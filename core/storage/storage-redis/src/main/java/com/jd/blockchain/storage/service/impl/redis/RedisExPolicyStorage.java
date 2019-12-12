package com.jd.blockchain.storage.service.impl.redis;

import com.jd.blockchain.storage.service.ExPolicyKVStorage;
import com.jd.blockchain.utils.Bytes;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.util.SafeEncoder;

public class RedisExPolicyStorage implements ExPolicyKVStorage {

	private JedisPool jedisPool;

	public RedisExPolicyStorage(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}

	@Override
	public byte[] get(Bytes key) {
		try (Jedis jedis = jedisPool.getResource()) {
//			byte[] keyBytes = SafeEncoder.encode(key);
//			byte[] valueBytes = jedis.get(keyBytes);
			byte[] valueBytes = jedis.get(key.toBytes());
			return valueBytes;
		}
	}
	
	@Override
	public boolean exist(Bytes key) {
		try (Jedis jedis = jedisPool.getResource()) {
//			byte[] keyBytes = SafeEncoder.encode(key);
//			return jedis.exists(keyBytes);
			return jedis.exists(key.toBytes());
		}
	}

	@Override
	public boolean set(Bytes key, byte[] value, ExPolicy ex) {
		try (Jedis jedis = jedisPool.getResource()) {
			byte[] nxxx;
			switch (ex) {
			case EXISTING:
				nxxx = RedisConsts.XX;
				break;
			case NOT_EXISTING:
				nxxx = RedisConsts.NX;
				break;
			default:
				throw new IllegalArgumentException("Unsupported ExPolicy[" + ex.toString() + "]!");
			}
//			byte[] keyBytes = SafeEncoder.encode(key);
//			String retn = jedis.set(keyBytes, value, nxxx);
			String retn = jedis.set(key.toBytes(), value, nxxx);
			return RedisConsts.OK.equalsIgnoreCase(retn);
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
}