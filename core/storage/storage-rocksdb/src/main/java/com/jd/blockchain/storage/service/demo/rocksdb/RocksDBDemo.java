package com.jd.blockchain.storage.service.demo.rocksdb;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.rocksdb.TransactionDB;
import org.rocksdb.TransactionDBOptions;

import com.jd.blockchain.utils.ConsoleUtils;
import com.jd.blockchain.utils.codec.Base58Utils;
import com.jd.blockchain.utils.io.BytesUtils;
import com.jd.blockchain.utils.io.FileUtils;
import com.jd.blockchain.utils.security.ShaUtils;

public class RocksDBDemo {

	private static String DB_PATH = "/Users/huanghaiquan/Documents/jd/projects/prototype/source/storage/storage-rocksdb/target/myrocks.db";
	private static String DB_0_PATH = "/Users/huanghaiquan/Documents/jd/projects/prototype/source/storage/storage-rocksdb/target/myrocks_0.db";
	private static String DB_1_PATH = "/Users/huanghaiquan/Documents/jd/projects/prototype/source/storage/storage-rocksdb/target/myrocks_1.db";

	static {
		RocksDB.loadLibrary();
	}

	private static Random rand = new Random();
	

	private static String initEmptyDB(String name) {
		String currDir = FileUtils.getCurrentDir();
		String dbDir = new File(currDir, name + ".db").getAbsolutePath();
		FileUtils.deleteFile(dbDir);
		return dbDir;
		
//		String dbURI = "rocksdb://" + dbDir;
//		return dbURI;
		
	}


	public static void main_test(String[] args) {
		
		DB_PATH = initEmptyDB("myrocks");

		// the Options class contains a set of configurable DB options
		// that determines the behaviour of the database.
		try (final Options options = new Options(); ) {
			options.setCreateIfMissing(true);
			// a factory method that returns a RocksDB instance
			try (final RocksDB db = RocksDB.open(options, DB_PATH)) {
			// try (final TransactionDB db =
			// TransactionDB.open(options, txnDbOptions, DB_PATH)) {

			// try (final RocksDB db0 = RocksDB.open(options, DB_0_PATH);
			// final RocksDB db1 = RocksDB.open(options, DB_1_PATH)) {
//			RocksDB[] dbs = { db0, db1 };
//			ConsoleUtils.info("simple test in db0");
//			simple_test(db0);
//			ConsoleUtils.info("simple test in db1");
//			simple_test(db1);
				
			RocksDB[] dbs = { db };
			simple_test(db);

			ConsoleUtils.info("Then, I will do performance test...");

			int count = 1000000;
			int valueSize = 1024;
			String keyPrefix = System.currentTimeMillis() + "_" + rand.nextInt() + "_";

			ConsoleUtils.info("Synchronize writting ...");
			perf_writing_test(dbs, count, keyPrefix, valueSize, false);

			ConsoleUtils.info("Parallel writting ...");
			perf_writing_test(dbs, count, keyPrefix, valueSize, true);

			ConsoleUtils.info("Reading ...");
			perf_reading_test(dbs, count, keyPrefix, valueSize);
		}
	}catch(

	Exception e)
	{
		// do some error handling
		e.printStackTrace();
	}

	}

	private static void simple_test(RocksDB db) throws RocksDBException {
		String strKey = "hello";
		byte[] key = BytesUtils.toBytes(strKey);
		byte[] value = db.get(key);
		if (value == null) {
			ConsoleUtils.info("Key[%s] doesn't exist! I will create it automactic.", strKey);
			value = new byte[32];
			rand.nextBytes(value);
			db.put(key, value);
		} else {
			ConsoleUtils.info("Key[%s] has been found! It's value is [%s].", strKey, Base58Utils.encode(value));
		}
	}

	private static void perf_reading_test(RocksDB[] dbs, int count, String keyPrefix, int valueSize)
			throws RocksDBException {
		byte[] value = new byte[valueSize];

		long startTs = System.currentTimeMillis();

		byte[] key;
		RocksDB db;
		for (int i = 0; i < count; i++) {
			key = BytesUtils.toBytes(keyPrefix + i);
			db = dbs[i % dbs.length];
			int len = db.get(key, value);
			if (len != value.length) {
				throw new IllegalStateException(String.format(
						"The size of value reloaded from rocksdb is out of expectation. [expected=%s][actual=%s]",
						value.length, len));
			}
		}
		long elapsedTs = System.currentTimeMillis() - startTs;

		double tps = count * 1000.0 / elapsedTs;
		ConsoleUtils.info(
				"============= perf_reading_test : total keys = %s; tps= %.2f; elapsed millis = %s; value bytes = %s; ",
				count, tps, elapsedTs, value.length);
	}

	private static void perf_writing_test(RocksDB[] dbs, int count, String keyPrefix, int valueSize, boolean parallel)
			throws RocksDBException {
		byte[] value = new byte[valueSize];
		rand.nextBytes(value);

		long startTs = System.currentTimeMillis();

		if (parallel) {
			parallel_write(dbs, count, value, keyPrefix);
		} else {
			sync_write(dbs, count, value, keyPrefix);
		}

		long elapsedTs = System.currentTimeMillis() - startTs;

		double tps = count * 1000.0 / elapsedTs;
		ConsoleUtils.info(
				"============= perf_writing_test [parallel=%s]: total keys = %s; tps= %.2f; elapsed millis = %s; value bytes = %s; ",
				parallel, count, tps, elapsedTs, value.length);
	}

	private static void sync_write(RocksDB[] dbs, int count, byte[] value, String keyPrefix) throws RocksDBException {
		byte[] key;
		if (dbs.length > 16) {
			throw new IllegalArgumentException("Too many dbs!");
		}
		//  先按默认 2 个 db 进行分片处理；
		RocksDB db;
		for (int i = 0; i < count; i++) {
			key = BytesUtils.toBytes(keyPrefix + i);
			db = dbs[i % dbs.length];
			db.put(key, value);
		}
	}

	private static void parallel_write(RocksDB[] dbs, int count, byte[] value, String keyPrefix)
			throws RocksDBException {
		// WriteTask task = new WriteTask(0, count, keyPrefix, value, dbs);

		StepWriteTask task1 = new StepWriteTask(0, count, keyPrefix, value, dbs[0], 2);
		StepWriteTask task2 = new StepWriteTask(1, count, keyPrefix, value, dbs[0], 2);

		ForkJoinPool.commonPool().execute(task1);
		ForkJoinPool.commonPool().execute(task2);
		task1.join();
		task2.join();
	}

	private static class WriteTask extends RecursiveAction {

		private static final long serialVersionUID = -2609085082223653954L;

		private static int THRESHOLD = 2000;

		private String keyPrefix;

		private int offset;

		private int count;

		private byte[] value;

		private RocksDB[] dbs;

		public WriteTask(int offset, int count, String keyPrefix, byte[] value, RocksDB[] dbs) {
			this.offset = offset;
			this.count = count;
			this.keyPrefix = keyPrefix;
			this.value = value;
			this.dbs = dbs;
		}

		@Override
		protected void compute() {
			if (count > THRESHOLD) {
				// List<WriteTask> tasks = new LinkedList<>();
				// for (int i = 0; i < count;) {
				// int c = Math.min(THRESHOLD, count - i);
				// WriteTask task = new WriteTask(offset + i, c, keyPrefix, value, db);
				// tasks.add(task);
				// i += c;
				// }
				// ForkJoinTask.invokeAll(tasks);

				int count1 = count / 2;
				int count2 = count - count1;
				WriteTask task1 = new WriteTask(offset, count1, keyPrefix, value, dbs);
				WriteTask task2 = new WriteTask(offset + count1, count2, keyPrefix, value, dbs);
				ForkJoinTask.invokeAll(task1, task2);
			} else {
				byte[] key;
				RocksDB db;

				List<StepWriteTask> tasks = new LinkedList<>();
				for (int i = 0; i < dbs.length; i++) {
					StepWriteTask stepTask = new StepWriteTask(offset + i, count, keyPrefix, value, dbs[i], dbs.length);
					tasks.add(stepTask);
				}
				ForkJoinTask.invokeAll(tasks);
				// for (int i = 0; i < count; i++) {
				// key = BytesUtils.toBytes(keyPrefix + (offset + i));
				// db = dbs[(offset + i) % dbs.length];
				// db.put(key, value);
				// }
			}
		}

	}

	private static class StepWriteTask extends RecursiveAction {

		private static final long serialVersionUID = -6046220943323359514L;

		private String keyPrefix;

		private int offset;

		private int step;

		private int count;

		private byte[] value;

		private RocksDB db;

		public StepWriteTask(int offset, int count, String keyPrefix, byte[] value, RocksDB db, int step) {
			this.offset = offset;
			this.count = count;
			this.keyPrefix = keyPrefix;
			this.value = value;
			this.db = db;
			this.step = step;
		}

		@Override
		protected void compute() {
			try {
				byte[] key;
				for (int i = 0; i < count;) {
					key = BytesUtils.toBytes(keyPrefix + (offset + i));
					db.put(key, value);
					i += step;
				}
			} catch (RocksDBException e) {
				throw new IllegalStateException(e.getMessage(), e);
			}
		}
	}

}
