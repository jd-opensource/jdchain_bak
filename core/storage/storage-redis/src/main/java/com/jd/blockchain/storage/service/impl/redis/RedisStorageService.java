package com.jd.blockchain.storage.service.impl.redis;

import com.jd.blockchain.storage.service.ExPolicyKVStorage;
import com.jd.blockchain.storage.service.KVStorageService;
import com.jd.blockchain.storage.service.VersioningKVStorage;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisStorageService implements KVStorageService {

	private RedisExPolicyStorage exStorage;

	private RedisVerioningStorage verStorage;

	private JedisPool jedisPool;

	public RedisStorageService(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
		this.exStorage = new RedisExPolicyStorage(jedisPool);
		this.verStorage = new RedisVerioningStorage(jedisPool);
	}

	@Override
	public ExPolicyKVStorage getExPolicyKVStorage() {
		return exStorage;
	}

	@Override
	public VersioningKVStorage getVersioningKVStorage() {
		return verStorage;
	}

	public void clearDB() {
		try (Jedis jedis = jedisPool.getResource()) {
			jedis.flushDB();
		}
	}
}
