package test.com.jd.blockchain.storage.service.impl.rocksdb;

import static com.jd.blockchain.storage.service.impl.rocksdb.RocksDBConnectionFactory.URI_PATTER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.jd.blockchain.storage.service.DbConnection;
import com.jd.blockchain.storage.service.DbConnectionFactory;
import com.jd.blockchain.storage.service.ExPolicyKVStorage;
import com.jd.blockchain.storage.service.ExPolicyKVStorage.ExPolicy;
import com.jd.blockchain.storage.service.VersioningKVStorage;
import com.jd.blockchain.storage.service.impl.rocksdb.RocksDBConnectionFactory;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.io.BytesUtils;
import com.jd.blockchain.utils.io.FileUtils;

public class RocksDBStorageTest {

	@Test
	public void test() {
		String dbUri = initEmptyDB("rocksdb_storage_test");
		long expectedVersion;
		try (DbConnectionFactory dbConnFactory = new RocksDBConnectionFactory();) {
			DbConnection conn = dbConnFactory.connect(dbUri);
			VersioningKVStorage verStorage = conn.getStorageService().getVersioningKVStorage();
			ExPolicyKVStorage exStorage = conn.getStorageService().getExPolicyKVStorage();

			expectedVersion = test(verStorage);

			test(exStorage);
		}

		try (DbConnectionFactory dbConnFactory = new RocksDBConnectionFactory();) {
			DbConnection conn = dbConnFactory.connect(dbUri);
			VersioningKVStorage verStorage = conn.getStorageService().getVersioningKVStorage();
			ExPolicyKVStorage exStorage = conn.getStorageService().getExPolicyKVStorage();

			testAfterReload(verStorage, expectedVersion);

			testAfterReload(exStorage);
		}

	}

	private void test(ExPolicyKVStorage exStorage) {
		Bytes key = Bytes.fromString("kex");
		assertFalse(exStorage.exist(key));

		byte[] data = exStorage.get(key);
		assertNull(data);

		data = BytesUtils.toBytes("data");
		assertFalse(exStorage.set(key, data, ExPolicy.EXISTING));

		assertTrue(exStorage.set(key, data, ExPolicy.NOT_EXISTING));
		assertTrue(exStorage.exist(key));

		assertFalse(exStorage.set(key, data, ExPolicy.NOT_EXISTING));

		assertTrue(exStorage.set(key, data, ExPolicy.EXISTING));
		assertTrue(exStorage.set(key, data, ExPolicy.EXISTING));
		assertFalse(exStorage.set(key, data, ExPolicy.NOT_EXISTING));

		assertTrue(exStorage.exist(key));

		byte[] reloadData = exStorage.get(key);
		assertTrue(BytesUtils.equals(data, reloadData));
	}

	private void testAfterReload(ExPolicyKVStorage exStorage) {
		Bytes key = Bytes.fromString("kex");
		assertTrue(exStorage.exist(key));

		byte[] data = exStorage.get(key);
		assertNotNull(data);

		assertEquals("data", BytesUtils.toString(data));

		assertFalse(exStorage.set(key, data, ExPolicy.NOT_EXISTING));
		assertFalse(exStorage.set(key, data, ExPolicy.NOT_EXISTING));

		assertTrue(exStorage.set(key, data, ExPolicy.EXISTING));
		assertTrue(exStorage.set(key, data, ExPolicy.EXISTING));
		assertFalse(exStorage.set(key, data, ExPolicy.NOT_EXISTING));

		assertTrue(exStorage.exist(key));

		byte[] reloadData = exStorage.get(key);
		assertEquals("data", BytesUtils.toString(data));
	}

	private long test(VersioningKVStorage verStorage) {
		Bytes key = Bytes.fromString("k1");
		long v = verStorage.getVersion(key);
		assertEquals(-1, v);
		byte[] data = verStorage.get(key, -1);
		assertNull(data);
		data = verStorage.get(key, 0);
		assertNull(data);
		data = verStorage.get(key, 1);
		assertNull(data);

		data = BytesUtils.toBytes("data");
		v = verStorage.set(key, data, -1);
		assertEquals(0, v);
		v = verStorage.set(key, data, -1);
		assertEquals(-1, v);
		v = verStorage.set(key, data, 0);
		assertEquals(1, v);
		return v;
	}

	private void testAfterReload(VersioningKVStorage verStorage, long expectedVersion) {
		Bytes key = Bytes.fromString("k1");
		long v = verStorage.getVersion(key);
		assertEquals(expectedVersion, v);
		byte[] data = verStorage.get(key, -1);
		String strData = BytesUtils.toString(data);
		assertEquals("data", strData);
	}

	private String initEmptyDB(String name) {
		String currDir = FileUtils.getCurrentDir();
		String dbDir = new File(currDir, name + ".db").getAbsolutePath();
		FileUtils.deleteFile(dbDir);
		String dbURI = "rocksdb://" + dbDir;
		return dbURI;
	}

	@Test
	// test rocksDB uri patter
	public void testRocksDBUriPatter() {
		Map<String, Boolean> cases = new HashMap<>();
		cases.put("rocksdb:///home/peer0/rocksdb", true);
		cases.put("rocksdb://D:\\home\\rocksdb", true);
		cases.put("rocksdb://\\home\\rocksdb", false);
		cases.put("rocksdb://:\\home\\rocksdb", false);
		cases.put("rocksdb://D:\\home\\", true);
		cases.put("rocksdb:///home/peer0/", true);
		for(Map.Entry<String, Boolean> entity : cases.entrySet()) {
			assertEquals(URI_PATTER.matcher(entity.getKey()).matches(), entity.getValue());
		}
	}
}
