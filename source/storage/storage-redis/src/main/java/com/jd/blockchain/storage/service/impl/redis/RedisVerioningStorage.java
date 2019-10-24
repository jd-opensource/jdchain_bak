package com.jd.blockchain.storage.service.impl.redis;

import com.jd.blockchain.storage.service.VersioningKVStorage;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.DataEntry;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.util.SafeEncoder;

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
public class RedisVerioningStorage implements VersioningKVStorage {

	private JedisPool jedisPool;

	public RedisVerioningStorage(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}

	@Override
	public long getVersion(Bytes key) {
		try (Jedis jedis = jedisPool.getResource()) {
//			byte[] keyBytes = SafeEncoder.encode(key);
//			return jedis.hlen(keyBytes) - 1;
			return jedis.hlen(key.toBytes()) - 1;
		}
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
		try (Jedis jedis = jedisPool.getResource()) {
			long ver = version;
			if (ver < 0) {
				//查询最新；
				ver = getVersion(key);
			}
			if (ver < 0) {
				return null;
			}
//			byte[] keyBytes = SafeEncoder.encode(key);
//			byte[] verBytes = encodeVersionKey(ver);
//			byte[] value = jedis.hget(keyBytes, verBytes);
			byte[] verBytes = encodeVersionKey(ver);
			byte[] value = jedis.hget(key.toBytes(), verBytes);
			return value;
		}
	}

	@Override
	public long set(Bytes key, byte[] value, long version) {
		try (Jedis jedis = jedisPool.getResource()) {
//			byte[] keyBytes = SafeEncoder.encode(key);
			long ver = version < 0 ? 0 : version + 1;
			byte[] verBytes = encodeVersionKey(ver);
			// 如果不存在，则写入；由于 Redis 特性的限制，此处无法原子性地校验是 version 参数否存在跳空增长，
			// 默认在外部调用已经校验了 version 为最新版本;
			Long r = jedis.hsetnx(key.toBytes(), verBytes, value);
			return r.longValue() == 0 ? -1 : ver;
		}
	}

	private byte[] encodeVersionKey(long version) {
		return SafeEncoder.encode("" + version);
	}

	@Override
	public void batchBegin() {
		// un support!!!
	}

	@Override
	public void batchCommit() {
		// un support!!!
	}


	private static class VersioningKVData implements DataEntry{

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