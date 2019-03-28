package com.jd.blockchain.storage.service.impl.redis;

import redis.clients.util.SafeEncoder;

public interface RedisConsts {
	
	public static final String URI_SCHEME = "redis";

	public static final String OK = "OK";
	
	public static final String OK_MULTI = "+OK";

	byte[] XX = SafeEncoder.encode("XX");
	
	byte[] NX = SafeEncoder.encode("NX");
	
}
