package com.jd.blockchain.storage.service.utils;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.jd.blockchain.storage.service.VersioningKVStorage;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.DataEntry;
import com.jd.blockchain.utils.io.BytesMap;

public class VersioningKVStorageMap implements VersioningKVStorage, BytesMap<Bytes> {
	
//	private Map<String, AtomicLong> versions = new ConcurrentHashMap<>();
//
//	// 要维持键的写入顺序，并且最终以相同的顺序输出；
//	private Map<String, Object> cache;
	
	/**
	 * 版本化KV数据的缓冲区；
	 */
	private Map<Bytes, VersioningWritingSet> versioningCache = new ConcurrentHashMap<>();
	private Object versioningMutex = new Object();


	
	public VersioningKVStorageMap() {
	}
	
//	public VersioningKVStorageMap(Map<String, Object> external) {
//		cache = external;
//	}
	
	@Override
	public long getVersion(Bytes key) {
		VersioningWritingSet ws = versioningCache.get(key);
		if (ws == null) {
			return -1;
		}
		return ws.getLatestVersion();
	}

	@Override
	public DataEntry getEntry(Bytes key, long version) {
		VersioningWritingSet ws = versioningCache.get(key);
		if (ws == null) {
			return null;
		}
		long latestVersion = ws.getLatestVersion();
		if (version > ws.getLatestVersion()) {
			return null;
		}
		if (version < 0) {
			version = latestVersion;
		}
		// 返回缓冲的新数据；注：这些数据尚未提交到依赖的底层存储；
		return ws.getEntry(version);
	}

	@Override
	public byte[] get(Bytes key, long version) {
		VersioningWritingSet ws = versioningCache.get(key);
		if (ws == null) {
			return null;
		}
		long latestVersion = ws.getLatestVersion();
		if (version > ws.getLatestVersion()) {
			return null;
		}
		if (version < 0) {
			version = latestVersion;
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
			long latestVersion = -1;
			synchronized (versioningMutex) {
				ws = versioningCache.get(key);
				if (ws == null) {
					if (version == latestVersion) {
						ws = new VersioningWritingSet(key, latestVersion, value);
						versioningCache.put(key, ws);
						return version+1;
					}
					// 指定的版本不是最新版本；
					return -1;
				}
				// 存在并发写，退出同步之后由该 key 的 VersioningWritingSet 来控制写入；
			}
		}
		return ws.set(value, version);
	}

	@Override
	public Set<Bytes> keySet() {
		return versioningCache.keySet();
	}

	@Override
	public byte[] getValue(Bytes key) {
		return get(key, getVersion(key));
	}
	
	public int getCount() {
		return versioningCache.size();
	}

	@Override
	public void batchBegin() {
		// un support!!!
	}

	@Override
	public void batchCommit() {
		// un support!!!
	}


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

		public DataEntry getEntry(long version) {
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
	
//	private static class CachedSetEntry implements VersioningKVEntry {
//
//		private String key;
//
//		private long version;
//
//		private byte[] value;
//
//		/**
//		 * 
//		 * @param key
//		 *            键；
//		 * @param version
//		 *            新的版本；
//		 * @param value
//		 *            值；
//		 */
//		private CachedSetEntry(String key, long version, byte[] value) {
//			this.key = key;
//			this.version = version;
//			this.value = value;
//		}
//
//		@Override
//		public String getKey() {
//			return key;
//		}
//
//		@Override
//		public long getVersion() {
//			return version;
//		}
//
//		@Override
//		public byte[] getValue() {
//			return value;
//		}
//
//	}
	
	
}
