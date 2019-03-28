package com.jd.blockchain.storage.service.impl.composite;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.jd.blockchain.storage.service.DbConnection;
import com.jd.blockchain.storage.service.DbConnectionFactory;
import com.jd.blockchain.storage.service.impl.redis.JedisConnection;
import com.jd.blockchain.storage.service.impl.redis.RedisConnectionFactory;
import com.jd.blockchain.storage.service.impl.rocksdb.RocksDBConnection;
import com.jd.blockchain.storage.service.impl.rocksdb.RocksDBConnectionFactory;
//import com.jd.blockchain.storage.service.utils.MemoryBasedDb;
import com.jd.blockchain.storage.service.utils.MemoryDBConn;
import com.jd.blockchain.storage.service.utils.MemoryDBConnFactory;


public class CompositeConnectionFactory implements DbConnectionFactory {

	private static final RocksDBConnectionFactory rocksDBConnFactory = new RocksDBConnectionFactory();

	private static final RedisConnectionFactory redisConnFactory = new RedisConnectionFactory();

	private static final MemoryDBConnFactory memoryConnFactory = new MemoryDBConnFactory();

	private static final String CONN_PREFIX_REDIS = "redis://";

	private static final String CONN_PREFIX_ROCKSDB = "rocksdb://";

	private static final String CONN_PREFIX_MEMORY = "memory://";

	private final Map<String, DbConnection> connections = new ConcurrentHashMap<>();

	@Override
	public DbConnection connect(String dbUri) {
		return connect(dbUri, null);
	}

	@Override
	public DbConnection connect(String dbConnectionString, String password) {
		if (!dbConnectionString.startsWith(CONN_PREFIX_REDIS) &&
				!dbConnectionString.startsWith(CONN_PREFIX_ROCKSDB) &&
				!dbConnectionString.startsWith(CONN_PREFIX_MEMORY)){
			throw new IllegalArgumentException("Illegal format of composite db connection string!");
		}

		if (dbConnectionString.startsWith(CONN_PREFIX_REDIS)) {
			return redisConnFactory.connect(dbConnectionString, password);
		} else if (dbConnectionString.startsWith(CONN_PREFIX_ROCKSDB)) {
			return rocksDBConnFactory.connect(dbConnectionString, password);
		} else if (dbConnectionString.startsWith(CONN_PREFIX_MEMORY)) {
			return memoryConnFactory.connect(dbConnectionString, password);
		}
		return null;
	}

	@Override
	public void close() {
		for (DbConnection dbConnection : connections.values()) {
			if (dbConnection.getClass().equals(JedisConnection.class)) {
				((JedisConnection)dbConnection).close();
			}
			else if (dbConnection.getClass().equals(RocksDBConnection.class)) {
				((RocksDBConnection)dbConnection).dbClose();
			}
			else if (dbConnection.getClass().equals(MemoryDBConn.class)) {
				((MemoryDBConn)dbConnection).close();
			}
		}
		connections.clear();
	}

	@Override
	public boolean support(String scheme) {
		return rocksDBConnFactory.support(scheme) || redisConnFactory.support(scheme) || memoryConnFactory.support(scheme);
	}
}
