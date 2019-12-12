package com.jd.blockchain.storage.service.impl.redis;

import com.jd.blockchain.storage.service.DbConnection;
import com.jd.blockchain.storage.service.KVStorageService;

import redis.clients.jedis.JedisPool;

public class JedisConnection implements DbConnection {
	
	private JedisPool jedisPool;
	
	private RedisStorageService storage;
	
	public JedisConnection(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
		this.storage = new RedisStorageService(jedisPool);
	}

	@Override
	public void close() {
//		jedisPool.close();
	}

	@Override
	public KVStorageService getStorageService() {
		return storage;
	}

}
