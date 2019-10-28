package com.jd.blockchain.storage.service.impl.rocksdb;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.collections4.map.LRUMap;
import org.rocksdb.*;

import com.jd.blockchain.storage.service.VersioningKVStorage;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.DataEntry;
import com.jd.blockchain.utils.io.BytesUtils;

/**
 * 基于 Redis 实现的版本化KV存储；
 * 
 * <p>
 * 
 * 版本化KV存储要求实现几个关键特性： <br>
 * 1、每个Key都有唯一的版本序列，版本序列从 0 开始；<br>
 * 2、对 Key 值的每一次更改都导致版本增长 1 ；<br>
 * 3、对“写入”和“版本增长”两个操作一起是原子性的；<br>
 * 4、“版本增长”必须以 1 顺序递增，不允许跳空递增；<br>
 * 
 * <p>
 * 由于 Redis 缺少事务特性，并且无法很好地自定义组合的原子操作（ MULTI-EXEC 不适用于集群模式下），所以在此实现中只能做到前面 3
 * 点。<br>
 * 第 4 点由调用方（账本操作层）在调用前做了版本校验，所以从整个系统来看也可以保证以上的4点要求。
 * 
 * @author huanghaiquan
 *
 */
public class RocksDBVersioningStorage implements VersioningKVStorage {

	private final ThreadLocal<WriteBatch> writeBatchThreadLocal = new ThreadLocal<>();

	private static Bytes VERSION_PREFIX = Bytes.fromString("V");

	private static Bytes DATA_PREFIX = Bytes.fromString("D");

	private final ReentrantLock lock = new ReentrantLock();

	private final WriteOptions writeOptions = new WriteOptions();

	private final ReadOptions readOptions = new ReadOptions()
			.setFillCache(true)
			.setVerifyChecksums(false)
			;
	// put、get操作都在当前对象中，处理过程已加锁，不再需要线程安全对象
	private Map<Bytes, AtomicLong> versions = new LRUMap<>(1024 * 128);
//	private Map<Bytes, AtomicLong> versions = new LRUMap<>(1024);

	private RocksDB db;

	public RocksDBVersioningStorage(RocksDB db) {
		this.db = db;
	}

	protected static Bytes encodeVersionKey(Bytes dataKey) {
		return VERSION_PREFIX.concat(dataKey);
	}

	protected static Bytes encodeDataKey(Bytes dataKey, long version) {
		return DATA_PREFIX.concat(Bytes.fromLong(version)).concat(dataKey);
	}

	private byte[] dbGet(Bytes key) {
		try {
			byte[] keyBytes = key.toBytes();
			return db.get(readOptions, keyBytes);
		} catch (RocksDBException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	protected void dbSet(Bytes key, byte[] value) {
		byte[] keyBytes = key.toBytes();

		WriteBatch writeBatch = writeBatchThreadLocal.get();
		if (writeBatch != null) {
			// 表示批量
			try {
				writeBatch.put(keyBytes, value);
			} catch (RocksDBException e) {
				throw new IllegalStateException(e.getMessage(), e);
			}
		} else {
			try {
				this.db.put(keyBytes, value);
			} catch (RocksDBException e) {
				throw new IllegalStateException(e.getMessage(), e);
			}
		}
	}

	@Override
	public long getVersion(Bytes key) {
		return innerGetVersion(key).get();
	}

	protected AtomicLong innerGetVersion(Bytes key) {
		AtomicLong ver;
		try {
			lock.lock();
			ver = versions.get(key);
			if (ver == null) {
				Bytes vkey = encodeVersionKey(key);
				byte[] verBytes = dbGet(vkey);
				if (verBytes == null) {
					// TODO: 未处理无效 key 的释放；
					ver = new AtomicLong(-1);
				} else {
					long v = BytesUtils.toLong(verBytes);
					if (v < 0) {
						throw new IllegalStateException(
								String.format("Illegal format of version bytes in rocks db! --[key=%s]", key));
					}
					ver = new AtomicLong(v);
				}
				versions.put(key, ver);
			}
		} finally {
			lock.unlock();
		}
		return ver;
	}

	@Override
	public DataEntry getEntry(Bytes key, long version) {
		byte[] value = get(key, version);
		if (value == null) {
			return null;
		}
		return new VersioningKVData(key, version, value);
	}

	@Override
	public byte[] get(Bytes key, long version) {
		long latestVersion = getVersion(key);
		if (latestVersion < 0) {
			return null;
		}
		if (version > latestVersion) {
			return null;
		}
		long targetVersion = version < 0 ? latestVersion : version;
		Bytes dKey = encodeDataKey(key, targetVersion);
		byte[] value = dbGet(dKey);
		return value;
	}

	@Override
	public synchronized long set(Bytes key, byte[] value, long version) {
		AtomicLong ver = innerGetVersion(key);
		long newVer = version + 1;
		if (ver.compareAndSet(version, newVer)) {
			// updateIfLatest 为 false 有利于同一个 key 的多版本并发写入；
			dbSetToVersion(key, value, newVer);
			return newVer;
		}

		return -1;
	}

	/**
	 * 向数据库更新键值到指定版本； <br>
	 * 
	 * 操作将把最新的版本号以及对应的数据存储到数据库；<br>
	 * 
	 * @param key
	 *            要写入的键；
	 * @param value
	 *            要写入的值；
	 * @param version
	 *            要写入的新版本号；将做并发检查，如果指定的值已经不是最新版本，则不会写入到数据库；
	 * @return 返回最新的版本号；
	 */
	private long dbSetToVersion(Bytes key, byte[] value, long version) {
		long latestVersion;
		try {
			lock.lock();
			// 同步地写入版本号；
			// 判断此版本是否已被并发更新为更高的值；如果版本已经不一致，则当前值已不必写入,避免数据库中新版本被错误地覆盖为低的版本；
			latestVersion = innerGetVersion(key).get();
			if (version == latestVersion) {
				Bytes vkey = encodeVersionKey(key);
				byte[] verBytes = BytesUtils.toBytes(version);
				dbSet(vkey, verBytes);
			}
		} finally {
			lock.unlock();
		}
		// 写入数据；
		dbSetData(key, value, version);
		return latestVersion;
	}

	protected void dbSetData(Bytes key, byte[] value, long version) {
		// 写入数据；
		Bytes dkey = encodeDataKey(key, version);
		dbSet(dkey, value);
	}

	@Override
	public void batchBegin() {
		writeBatchThreadLocal.set(new WriteBatch());
	}

	@Override
	public void batchCommit() {
		WriteBatch writeBatch = writeBatchThreadLocal.get();
		if (writeBatch != null) {
			writeBatch(writeBatch);
			writeBatchThreadLocal.remove();
		}
	}

	private void writeBatch(WriteBatch writeBatch) {
		try {
			db.write(writeOptions, writeBatch);
		} catch (RocksDBException e) {
			throw new IllegalStateException(e.getMessage(), e);
		} finally {
			writeBatch.close();
		}
	}

	private static class VersioningKVData implements DataEntry {

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
}