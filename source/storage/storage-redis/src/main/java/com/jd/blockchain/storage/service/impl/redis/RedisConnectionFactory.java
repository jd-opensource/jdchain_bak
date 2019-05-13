package com.jd.blockchain.storage.service.impl.redis;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import com.jd.blockchain.storage.service.DbConnection;
import com.jd.blockchain.storage.service.DbConnectionFactory;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

public class RedisConnectionFactory implements DbConnectionFactory {

	public static final Pattern URI_PATTER = Pattern
			.compile("^\\w+\\://(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})\\:\\d+(/\\d*(/.*)*)?$");
	// public static final Pattern URI_PATTER = Pattern
	// .compile("^\\w+\\://(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})\\:\\d+(/\\d+)?(/.*)*$");

	private static final Map<String, DbConnection> connections = new ConcurrentHashMap<>();

	@Override
	public DbConnection connect(String dbUri) {
		return connect(dbUri, null);
	}

	@Override
	public synchronized DbConnection connect(String dbConnectionString, String password) {

		if (connections.containsKey(dbConnectionString)) {
			// 暂不处理密码变化问题
			return connections.get(dbConnectionString);
		}

		URI dbUri = URI.create(dbConnectionString);
		if (!(dbUri.getScheme().equalsIgnoreCase("redis"))) {
			throw new IllegalArgumentException(
					String.format("Not supported db connection string with scheme \"%s\"!", dbUri.getScheme()));
		}

		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(500);
		config.setMaxIdle(500);
		config.setMinIdle(50);
		config.setMaxWaitMillis(1000 * 30);
		config.setTestOnBorrow(false);

		String host = dbUri.getHost();
		int port = dbUri.getPort();
		int dbId = retriveDbIdFromPath(dbUri.getPath());
		JedisPool pool = new JedisPool(config, host, port, Protocol.DEFAULT_TIMEOUT, password, dbId, false);
		JedisConnection jedisConnection = new JedisConnection(pool);
		connections.put(dbConnectionString, jedisConnection);
		return jedisConnection;
	}

	/**
	 * 从 URI 路径检索数据库 ID ； <br>
	 * 预期路径参数的样式为“/{id}”开头，如果忽略路径之后加入的其它节； <br>
	 * 如果没有定义数据库ID，或者不符合样式，则返回默认的数据库ID ({@link Protocol#DEFAULT_DATABASE})；
	 * 
	 * @param uriPath
	 * @return
	 */
	private int retriveDbIdFromPath(String uriPath) {
		if (uriPath == null || uriPath.length() == 0) {
			return Protocol.DEFAULT_DATABASE;
		}
		int secondIndex = uriPath.indexOf('/', 1);
		String idStr;
		if (secondIndex < 0) {
			idStr = uriPath.substring(1).trim();
		} else {
			idStr = uriPath.substring(1, secondIndex).trim();
		}
		int dbId = Integer.parseInt(idStr);
		if (dbId < 0) {
			return Protocol.DEFAULT_DATABASE;
		}
		return dbId;
	}

	@Override
	public String dbPrefix() {
		return "redis://";
	}

	@Override
	public boolean support(String scheme) {
		return RedisConsts.URI_SCHEME.equalsIgnoreCase(scheme);
	}

	@Override
	public void close() {
		// TODO:  未实现连接池的关闭；
		if (!connections.isEmpty()) {
			for (Map.Entry<String, DbConnection> entry : connections.entrySet()) {
				try {
					entry.getValue().close();
				} catch (IOException e) {
					// 打印关闭异常日志
				}
			}
		}
	}
}
