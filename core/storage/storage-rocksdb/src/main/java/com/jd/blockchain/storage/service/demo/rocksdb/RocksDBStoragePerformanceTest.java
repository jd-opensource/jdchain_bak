package com.jd.blockchain.storage.service.demo.rocksdb;

import java.io.File;
import java.util.Random;

import org.bouncycastle.util.Arrays;

import com.jd.blockchain.storage.service.DbConnection;
import com.jd.blockchain.storage.service.ExPolicyKVStorage;
import com.jd.blockchain.storage.service.ExPolicyKVStorage.ExPolicy;
import com.jd.blockchain.storage.service.KVStorageService;
import com.jd.blockchain.storage.service.VersioningKVStorage;
import com.jd.blockchain.storage.service.impl.rocksdb.RocksDBConnectionFactory;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.ConsoleUtils;
import com.jd.blockchain.utils.io.FileUtils;

public class RocksDBStoragePerformanceTest {

	public static void main(String[] args) {

		String uri = initEmptyDB("perf_rocksdb_storage_test");
		try (RocksDBConnectionFactory connFactory = new RocksDBConnectionFactory()) {
			DbConnection conn = connFactory.connect(uri);
			KVStorageService storageService = conn.getStorageService();

			int count = 1000000;
			int size = 1024;
			Bytes keyPrefix = Bytes.fromString("KEY-" + System.currentTimeMillis() + "_");
			test_versioning_writing("test_versioning_writing", keyPrefix, count, size,
					storageService.getVersioningKVStorage());
			Bytes exKeyPrefix = Bytes.fromString("EX").concat(keyPrefix);
			test_existance_writing("test_existance_writing", exKeyPrefix, count, size,
					storageService.getExPolicyKVStorage());
		}

	}

	private static void test_versioning_writing(String name, Bytes keyPrefix, int count, int valueSize,
			VersioningKVStorage storage) {
		byte[] value = new byte[valueSize];
		new Random().nextBytes(value);
		long startTs = System.currentTimeMillis();

		Bytes key;
		for (int i = 0; i < count; i++) {
			key = keyPrefix.concat(Bytes.fromInt(i));
			value = Arrays.copyOf(value, value.length);
			long v = storage.set(key, value, -1);
			if (v < 0) {
				throw new IllegalStateException(String.format(
						"The size of value reloaded from rocksdb is out of expectation. [expected=%s][actual=%s]", 0,
						v));
			}
		}
		long elapsedTs = System.currentTimeMillis() - startTs;

		double tps = count * 1000.0 / elapsedTs;
		ConsoleUtils.info("============= [%s] : total keys = %s; tps= %.2f; elapsed millis = %s; value bytes = %s; ",
				name, count, tps, elapsedTs, value.length);
	}

	private static void test_existance_writing(String name, Bytes keyPrefix, int count, int valueSize,
			ExPolicyKVStorage storage) {
		byte[] value = new byte[valueSize];
		new Random().nextBytes(value);
		long startTs = System.currentTimeMillis();

		Bytes key;
		for (int i = 0; i < count; i++) {
			key = keyPrefix.concat(Bytes.fromInt(i));
			boolean success = storage.set(key, value, ExPolicy.NOT_EXISTING);
			if (!success) {
				throw new IllegalStateException(String.format("Key already exist! --key=%s", key));
			}
		}
		long elapsedTs = System.currentTimeMillis() - startTs;

		double tps = count * 1000.0 / elapsedTs;
		ConsoleUtils.info("============= [%s] : total keys = %s; tps= %.2f; elapsed millis = %s; value bytes = %s; ",
				name, count, tps, elapsedTs, value.length);
	}

	private static String initEmptyDB(String name) {
		String currDir = FileUtils.getCurrentDir();
		String dbDir = new File(currDir, name + ".db").getAbsolutePath();
		FileUtils.deleteFile(dbDir);
		String dbURI = "rocksdb://" + dbDir;
		return dbURI;
	}

}
