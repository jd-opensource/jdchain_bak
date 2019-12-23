package com.jd.blockchain.storage.service.utils;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

import com.jd.blockchain.storage.service.ExPolicyKVStorage;
import com.jd.blockchain.storage.service.VersioningKVStorage;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.Transactional;
import com.jd.blockchain.utils.DataEntry;

/**
 * {@link BufferedKVStorage} 缓冲写入的KV存储；<br>
 * 
 * @author huanghaiquan
 *
 */
public class BufferedKVStorage implements VersioningKVStorage, ExPolicyKVStorage, Transactional {

	private static int MAX_PARALLEL_DB_WRITE_SIZE = 500;
	static {
		String strSize = System.getProperty("max-parallel-dbwrite-size");
		if (strSize != null) {
			try {
				MAX_PARALLEL_DB_WRITE_SIZE = Integer.parseInt(strSize);
			} catch (NumberFormatException e) {
				// 忽略格式错误；
				e.printStackTrace();
			}
		}
		System.out.println("------ [[ max-parallel-dbwrite-size=" + MAX_PARALLEL_DB_WRITE_SIZE + " ]] ------");
	}

	private boolean parallel;

	private VersioningKVStorage origVersioningStorage;

	private ExPolicyKVStorage origExistanceStorage;

	/**
	 * 版本化KV数据的缓冲区；
	 */
	private ConcurrentHashMap<Bytes, VersioningWritingSet> versioningCache = new ConcurrentHashMap<>();
	private Object versioningMutex = new Object();

	/**
	 * 存在性KV数据的缓冲区；
	 */
	private ConcurrentHashMap<Bytes, ExistanceWritingSet> existanceCache = new ConcurrentHashMap<>();
	private Object existanceMutex = new Object();

	/**
	 * 创建实例；
	 * 
	 * @param origExPolicyStorage
	 *            原始的存储；
	 * @param origVersioningStorage
	 *            原始的存储；
	 * @param parallel
	 *            是否并行写入；
	 */
	public BufferedKVStorage(ExPolicyKVStorage origExPolicyStorage, VersioningKVStorage origVersioningStorage,
			boolean parallel) {
		this.origExistanceStorage = origExPolicyStorage;
		this.origVersioningStorage = origVersioningStorage;
		this.parallel = parallel;
	}

	@Override
	public long getVersion(Bytes key) {
		VersioningWritingSet ws = versioningCache.get(key);
		if (ws == null) {
			return origVersioningStorage.getVersion(key);
		}
		return ws.getLatestVersion();
	}
	
	@Override
	public DataEntry<Bytes, byte[]> getEntry(Bytes key, long version) {
		VersioningWritingSet ws = versioningCache.get(key);
		if (ws == null) {
			return origVersioningStorage.getEntry(key, version);
		}
		long latestVersion = ws.getStartingVersion();
		if (version <= latestVersion) {
			// 值未
			return origVersioningStorage.getEntry(key, version < 0 ? latestVersion : version);
		}
		if (version > ws.getLatestVersion()) {
			return null;
		}
		// 返回缓冲的新数据；注：这些数据尚未提交到依赖的底层存储；
		return ws.getEntry(version);
	}

	@Override
	public byte[] get(Bytes key, long version) {
		VersioningWritingSet ws = versioningCache.get(key);
		if (ws == null) {
			return origVersioningStorage.get(key, version);
		}
		long latestVersion = ws.getStartingVersion();
		if (version <= latestVersion) {
			// 值未
			return origVersioningStorage.get(key, version < 0 ? latestVersion : version);
		}
		if (version > ws.getLatestVersion()) {
			return null;
		}
		// 返回缓冲的新数据；注：这些数据尚未提交到依赖的底层存储；
		return ws.get(version);
	}

	@Override
	public long set(Bytes key, byte[] value, long version) {
		if (value == null) {
			throw new IllegalArgumentException("Value is null!");
		}
		if (version < -1) {
			version = -1;
		}
		VersioningWritingSet ws = versioningCache.get(key);
		if (ws == null) {
			long latestVersion = origVersioningStorage.getVersion(key);
			if (version < latestVersion) {
				return -1;
			}
			synchronized (versioningMutex) {
				ws = versioningCache.get(key);
				if (ws == null) {
					if (version == latestVersion) {
						ws = new VersioningWritingSet(key, latestVersion, value);
						versioningCache.put(key, ws);
						return version + 1;
					}
					// 指定的版本不是最新版本；
					return -1;
				}
				// 存在并发写，退出同步之后由该 key 的 VersioningWritingSet 来控制写入；
			}
		}
		return ws.set(value, version);
	}

	/**
	 * 输出已缓冲的所有写入数据到原始存储，并清空缓冲区；
	 */
	public void flush() {
		if (parallel) {
			parallelFlush();
		} else {
			syncFlush();
		}

		clear();
	}

	private void parallelFlush() {
		// 不必在“版本”和“存在性”这两类存储接口之间保证向下写入的顺序，也不必保证不同 key 向下写入的顺序；
		ParallelVersioningWritingTask versioningWritingTask = null;
		if (versioningCache.size() > 0) {
			VersioningWritingSet[] wss = versioningCache.values()
					.toArray(new VersioningWritingSet[versioningCache.size()]);
			versioningWritingTask = new ParallelVersioningWritingTask(wss, 0, wss.length, origVersioningStorage);
			ForkJoinPool.commonPool().execute(versioningWritingTask);
		}

		ParallelExistanceWritingTask existanceWritingTask = null;
		if (existanceCache.size() > 0) {
			ExistanceWritingSet[] wss = existanceCache.values().toArray(new ExistanceWritingSet[existanceCache.size()]);
			existanceWritingTask = new ParallelExistanceWritingTask(wss, 0, wss.length, origExistanceStorage);
			ForkJoinPool.commonPool().execute(existanceWritingTask);
		}
		if (versioningWritingTask != null) {
			versioningWritingTask.join();
		}
		if (existanceWritingTask != null) {
			existanceWritingTask.join();
		}
	}

    @Override
    public void batchBegin() {
        // un support!!!
    }

    @Override
    public void batchCommit() {
        // un support!!!
    }

    private static class ParallelVersioningWritingTask extends RecursiveTask<Boolean> {

		private static final long serialVersionUID = -2603448698013687038L;

		private VersioningWritingSet[] wss;
		private int offset;
		private int count;
		private VersioningKVStorage storage;

		public ParallelVersioningWritingTask(VersioningWritingSet[] wss, int offset, int count,
				VersioningKVStorage storage) {
			this.wss = wss;
			this.offset = offset;
			this.count = count;
			this.storage = storage;
		}

		/**
		 * 返回错误任务的计数器；
		 */
		@Override
		protected Boolean compute() {
			if (count > MAX_PARALLEL_DB_WRITE_SIZE) {
				int count1 = count / 2;
				int count2 = count - count1;
				ParallelVersioningWritingTask task1 = new ParallelVersioningWritingTask(wss, offset, count1, storage);
				ParallelVersioningWritingTask task2 = new ParallelVersioningWritingTask(wss, offset + count1, count2,
						storage);
				ForkJoinTask.invokeAll(task1, task2);
				boolean success = task1.join();
				success = task2.join() & success;
				return success;
			} else {
				for (int i = 0; i < count; i++) {
					wss[offset + i].flushTo(storage);
				}
				return true;
			}
		}
	}

	private static class ParallelExistanceWritingTask extends RecursiveTask<Boolean> {

		private static final long serialVersionUID = -7101095718404738821L;

		private ExistanceWritingSet[] wss;
		private int offset;
		private int count;
		private ExPolicyKVStorage storage;

		public ParallelExistanceWritingTask(ExistanceWritingSet[] wss, int offset, int count,
				ExPolicyKVStorage storage) {
			this.wss = wss;
			this.offset = offset;
			this.count = count;
			this.storage = storage;
		}

		/**
		 * 返回错误任务的计数器；
		 */
		@Override
		protected Boolean compute() {
			if (count > MAX_PARALLEL_DB_WRITE_SIZE) {
				int count1 = count / 2;
				int count2 = count - count1;
				ParallelExistanceWritingTask task1 = new ParallelExistanceWritingTask(wss, offset, count1, storage);
				ParallelExistanceWritingTask task2 = new ParallelExistanceWritingTask(wss, offset + count1, count2,
						storage);
				ForkJoinTask.invokeAll(task1, task2);
				boolean success = task1.join();
				success = task2.join() & success;
				return success;
			} else {
				for (int i = 0; i < count; i++) {
					wss[offset + i].flushTo(storage);
				}
				return true;
			}
		}
	}

	private void syncFlush() {
		// 不必在“版本”和“存在性”这两类存储接口之间保证向下写入的顺序，也不必保证不同 key 向下写入的顺序；
        if (versioningCache.isEmpty() && existanceCache.isEmpty()) {
            return;
        }
        origVersioningStorage.batchBegin();
		for (VersioningWritingSet ws : versioningCache.values()) {
			ws.flushTo(origVersioningStorage);
		}
        origVersioningStorage.batchCommit();
        origExistanceStorage.batchBegin();
		for (ExistanceWritingSet ws : existanceCache.values()) {
			ws.flushTo(origExistanceStorage);
		}
        origExistanceStorage.batchCommit();
	}

	private void clear() {
		versioningCache.clear();
		existanceCache.clear();
	}

	/**
	 * 清空缓冲的数据；
	 */
	public void cancel() {
		clear();
	}

	@Override
	public boolean isUpdated() {
		return versioningCache.size() > 0 || existanceCache.size() > 0;
	}

	@Override
	public void commit() {
		flush();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.jd.blockchain.storage.service.ExPolicyKVStorage#get(java.lang.String)
	 */
	@Override
	public byte[] get(Bytes key) {
		// 从“存在性KV存储”读取值；
		ExistanceWritingSet ws = existanceCache.get(key);
		if (ws == null) {
			return origExistanceStorage.get(key);
		}
		return ws.get();
	}

	@Override
	public boolean set(Bytes key, byte[] value, ExPolicy ex) {
		if (value == null) {
			throw new IllegalArgumentException("Value is null!");
		}
		switch (ex) {
		case EXISTING:
			return setEx(key, value);
		case NOT_EXISTING:
			return setNx(key, value);
		default:
			throw new IllegalArgumentException("Unsupported ExistancePolicy[" + ex + "]!");
		}
	}

	@Override
	public boolean exist(Bytes key) {
		ExistanceWritingSet ws = existanceCache.get(key);
		if (ws == null) {
			return origExistanceStorage.exist(key);
		}
		return true;
	}

	/**
	 * 当 key 不存在时写入新的键值；
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	private boolean setNx(Bytes key, byte[] value) {
		// 从“存在性KV存储”读取值；
		ExistanceWritingSet ws = existanceCache.get(key);
		if (ws == null) {
			boolean exist = origExistanceStorage.exist(key);
			if (exist) {
				return false;
			}
			synchronized (existanceMutex) {
				ws = existanceCache.get(key);
				if (ws == null) {
					ws = new ExistanceWritingSet(key, value, ExPolicy.NOT_EXISTING);
					existanceCache.put(key, ws);
					return true;
				}
				// 并发写，已经存在；
				return false;
			}
		}
		// 已经存在；
		return false;
	}

	/**
	 * 当 key 已经存在时更新值；
	 *
	 * @param key
	 * @param value
	 * @return
	 */
	private boolean setEx(Bytes key, byte[] value) {
		// 从“存在性KV存储”读取值；
		ExistanceWritingSet ws = existanceCache.get(key);
		if (ws == null) {
			boolean exist = origExistanceStorage.exist(key);
			if (!exist) {
				// key 不存在；
				return false;
			}
			synchronized (existanceMutex) {
				ws = existanceCache.get(key);
				if (ws == null) {
					// 初始化，缓存首个更新值以及更新条件；
					ws = new ExistanceWritingSet(key, value, ExPolicy.EXISTING);
					existanceCache.put(key, ws);
					return true;
				}
				// 并发写，已经存在；
			}
		}
		// 更新值；
		ws.set(value);
		return true;
	}

	// =============================================================

	/**
	 * 记录在最新版本之上新写入但未保存的最新的数据版本序列；
	 * 
	 * @author huanghaiquan
	 *
	 */
	private static class VersioningWritingSet {

		private Bytes key;

		private long startingVersion;

		private ArrayList<byte[]> values;

		/**
		 * 
		 * @param key
		 *            键；
		 * @param version
		 *            新的版本；
		 * @param value
		 *            值；
		 */
		private VersioningWritingSet(Bytes key, long startingVersion, byte[] firstValue) {
			this.key = key;
			this.startingVersion = startingVersion;
			this.values = new ArrayList<>(1);
			this.values.add(firstValue);
		}

		public byte[] get(long version) {
			long idx = version - startingVersion - 1;
			if (idx < 0 || idx >= values.size()) {
				return null;
			}
			return values.get((int) idx);
		}

		public synchronized long set(byte[] value, long version) {
			if (getLatestVersion() == version) {
				this.values.add(value);
				return version + 1;
			}
			return -1;
		}

		/**
		 * 当前写入序列的最新版本；<br>
		 * 
		 * 此版本是最新的尚未写入底层存储的数据的版本；
		 * 
		 * @return
		 */
		public long getLatestVersion() {
			return startingVersion + values.size();
		}

		/**
		 * 当前写入序列的起始版本；<br>
		 * 同时也是底层存储的最新版本；
		 * 
		 * @return
		 */
		public long getStartingVersion() {
			return startingVersion;
		}

		public DataEntry<Bytes, byte[]> getEntry(long version) {
			byte[] value = get(version);
			if (value == null) {
				return null;
			}
			return new VersioningKVData(key, version, value);
		}

		public void flushTo(VersioningKVStorage storage) {
			long expVersion = startingVersion;
			for (byte[] value : values) {
				if (storage.set(key, value, expVersion) < 0) {
					throw new IllegalStateException(String.format(
							"Fail on flushing data to original storage! Expected version doesn't match! --[KEY=%s][EXPECTED_VERSION=%s]",
							key, expVersion));
				}
				expVersion++;
			}
		}
	}

	private static class VersioningKVData implements DataEntry<Bytes, byte[]> {

		private Bytes key;

		private long version;

		private byte[] value;

		public VersioningKVData(Bytes key, long version, byte[] value) {
			this.key = key;
			this.version = version;
			this.value = value;
		}

		@Override
		public Bytes getKey() {
			return key;
		}

		@Override
		public long getVersion() {
			return version;
		}

		@Override
		public byte[] getValue() {
			return value;
		}

	}

	private static class ExistanceWritingSet {

		private Bytes key;

		private volatile byte[] value;

		private ExPolicy initPolicy;

		/**
		 * 
		 * @param key
		 *            键；
		 * @param value
		 *            值；
		 * @param initPolicy
		 *            初始的写入策略；
		 */
		private ExistanceWritingSet(Bytes key, byte[] value, ExPolicy initPolicy) {
			this.key = key;
			this.value = value;
			this.initPolicy = initPolicy;
		}

		public void flushTo(ExPolicyKVStorage origExistanceStorage) {
			if (!origExistanceStorage.set(key, value, initPolicy)) {
				throw new IllegalStateException(String.format(
						"Fail on flushing data to original storage! The existance policy doesn't match --[KEY=%s][POLICY=%s]",
						key, initPolicy));
			}
		}

		public void set(byte[] value) {
			this.value = value;
		}

		public byte[] get() {
			return value;
		}

	}

}