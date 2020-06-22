package com.jd.blockchain.storage.service.impl.rocksdb;

import com.jd.blockchain.storage.service.DbConnection;
import com.jd.blockchain.storage.service.DbConnectionFactory;
import org.rocksdb.BlockBasedTableConfig;
import org.rocksdb.BloomFilter;
import org.rocksdb.Cache;
import org.rocksdb.IndexType;
import org.rocksdb.LRUCache;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.util.SizeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class RocksDBConnectionFactory implements DbConnectionFactory {
	private Logger logger = LoggerFactory.getLogger(RocksDBConnectionFactory.class);

	private static final String DB_CONFIG_ARG = "-rb";

	private static Properties dbConfigProperties = null;

	static {
		RocksDB.loadLibrary();
		init();
	}

	public static final String URI_SCHEME = "rocksdb";

	public static final Pattern URI_PATTER = Pattern
			.compile("^\\w+\\://(/)?\\w+(\\:)?([/\\\\].*)*$");

	private Map<String, RocksDBConnection> connections = new ConcurrentHashMap<>();

	@Override
	public DbConnection connect(String dbUri) {
		return connect(dbUri, null);
	}

	@Override
	public synchronized DbConnection connect(String dbConnectionString, String password) {
		if (!URI_PATTER.matcher(dbConnectionString).matches()) {
			throw new IllegalArgumentException("Illegal format of rocksdb connection string!");
		}
		URI dbUri = URI.create(dbConnectionString.replace("\\", "/"));
		if (!support(dbUri.getScheme())) {
			throw new IllegalArgumentException(
					String.format("Not supported db connection string with scheme \"%s\"!", dbUri.getScheme()));
		}

		String uriHead = dbPrefix();
		int beginIndex = dbConnectionString.indexOf(uriHead);
		String dbPath = dbConnectionString.substring(beginIndex + uriHead.length());

		RocksDBConnection conn = connections.get(dbPath);
		if (conn != null) {
			return conn;
		}

		Options options = initOptions();

		conn = new RocksDBConnection(dbPath, options);
		connections.put(dbPath, conn);

		return conn;
	}


	@Override
	public String dbPrefix() {
		return URI_SCHEME + "://";
	}

	@Override
	public boolean support(String scheme) {
		return URI_SCHEME.equalsIgnoreCase(scheme);
	}

	@PreDestroy
	@Override
	public void close() {
		RocksDBConnection[] conns = connections.values().toArray(new RocksDBConnection[connections.size()]);
		connections.clear();
		for (RocksDBConnection conn : conns) {
			conn.dbClose();
		}
	}

	private Options initOptions() {
		return initOptionsByProperties(dbConfigProperties);
	}

	private Options initOptionsByProperties(Properties dbProperties) {
		long cacheCapacity = getLong(dbProperties, "cache.capacity", 1024 * SizeUnit.MB);
		int cacheNumShardBits = getInt(dbProperties, "cache.numShardBits", 128);

		long tableBlockSize = getLong(dbProperties, "table.blockSize", 4 * SizeUnit.KB);
		long tableMetadataBlockSize = getLong(dbProperties, "table.metadata.blockSize", 4 * SizeUnit.KB);
		int tableBloomBitsPerKey = getInt(dbProperties, "table.bloom.bitsPerKey", -1);

		long optionWriteBufferSize = getLong(dbProperties, "option.writeBufferSize", 256 * SizeUnit.MB);
		int optionMaxWriteBufferNumber = getInt(dbProperties, "option.maxWriteBufferNumber", 7);
		int optionMinWriteBufferNumberToMerge = getInt(dbProperties, "option.minWriteBufferNumberToMerge", 2);
		int optionMaxOpenFiles = getInt(dbProperties, "option.maxOpenFiles", -1);
		int optionMaxBackgroundCompactions = getInt(dbProperties, "option.maxBackgroundCompactions", 5);
		int optionMaxBackgroundFlushes = getInt(dbProperties, "option.maxBackgroundFlushes", 4);

		logger.info("initOptionsByProperties,[cacheCapacity={}],[cacheNumShardBits={}],[tableBlockSize={}]," +
						"[tableMetadataBlockSize={}],[tableBloomBitsPerKey={}],[optionWriteBufferSize={}]," +
						"[optionMaxWriteBufferNumber={}],[optionMinWriteBufferNumberToMerge={}]," +
						"[optionMaxOpenFiles={}],[optionMaxBackgroundCompactions={}],[optionMaxBackgroundFlushes={}]",
				cacheCapacity,cacheNumShardBits,tableBlockSize,tableMetadataBlockSize,
				tableBloomBitsPerKey, optionWriteBufferSize,optionMaxWriteBufferNumber,optionMinWriteBufferNumberToMerge,
				optionMaxOpenFiles,optionMaxBackgroundCompactions,optionMaxBackgroundFlushes);

		Cache cache = new LRUCache(cacheCapacity, cacheNumShardBits, false);
        BloomFilter bloomFilter = tableBloomBitsPerKey <= 0 ? null : new BloomFilter(tableBloomBitsPerKey);

		final BlockBasedTableConfig tableOptions = new BlockBasedTableConfig()
				.setBlockCache(cache)
				.setMetadataBlockSize(tableMetadataBlockSize)
				.setCacheIndexAndFilterBlocks(true) // 设置索引和布隆过滤器使用Block Cache内存
				.setCacheIndexAndFilterBlocksWithHighPriority(true)
				.setIndexType(IndexType.kTwoLevelIndexSearch) // 设置两级索引，控制索引占用内存
				.setPinTopLevelIndexAndFilter(false)
				.setBlockSize(tableBlockSize)
				.setFilterPolicy(bloomFilter) // 设置布隆过滤器
				;

		Options options = new Options()
				// 最多占用256 * 6 + 512 = 2G内存
				.setWriteBufferSize(optionWriteBufferSize)
				.setMaxWriteBufferNumber(optionMaxWriteBufferNumber)
				.setMinWriteBufferNumberToMerge(optionMinWriteBufferNumberToMerge)
				.setMaxOpenFiles(optionMaxOpenFiles) // 控制最大打开文件数量，防止内存持续增加
				.setAllowConcurrentMemtableWrite(true) //允许并行Memtable写入
				.setCreateIfMissing(true)
				.setTableFormatConfig(tableOptions)
				.setMaxBackgroundCompactions(optionMaxBackgroundCompactions)
				.setMaxBackgroundFlushes(optionMaxBackgroundFlushes)
				;
		return options;
	}

	/**
	 * 初始化参数配置
	 *
	 */
	private static void init() {
		String dbConfigPath = System.getProperty(DB_CONFIG_ARG);
		if (dbConfigPath != null && dbConfigPath.length() > 0) {
			File dbConfigFile = new File(dbConfigPath);
			try {
				dbConfigProperties = new Properties();
				dbConfigProperties.load(new FileInputStream(dbConfigFile));
			} catch (Exception e) {
				throw new IllegalStateException(String.format("Load rocksdb.config %s error !!!", dbConfigPath), e);
			}
		}
	}

	private long getLong(Properties properties, String key, long defaultVal) {
		if (properties == null || properties.isEmpty()) {
			return defaultVal;
		}
		String prop = properties.getProperty(key);
		if (prop == null || prop.length() == 0) {
			return defaultVal;
		} else {
			return Long.parseLong(prop);
		}
	}

	private int getInt(Properties properties, String key, int defaultVal) {
		if (properties == null || properties.isEmpty()) {
			return defaultVal;
		}
		String prop = properties.getProperty(key);
		if (prop == null || prop.length() == 0) {
			return defaultVal;
		} else {
			return Integer.parseInt(prop);
		}
	}
}
