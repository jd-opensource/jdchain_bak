package com.jd.blockchain.storage.service.impl.rocksdb;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import javax.annotation.PreDestroy;

import org.rocksdb.*;

import com.jd.blockchain.storage.service.DbConnection;
import com.jd.blockchain.storage.service.DbConnectionFactory;
import org.rocksdb.util.SizeUnit;

public class RocksDBConnectionFactory implements DbConnectionFactory {

	static {
		RocksDB.loadLibrary();
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
		final Filter bloomFilter = new BloomFilter(32);
		final BlockBasedTableConfig tableOptions = new BlockBasedTableConfig()
				.setFilter(bloomFilter)
				.setBlockSize(4 * SizeUnit.KB)
				.setBlockSizeDeviation(10)
				.setBlockCacheSize(64 * SizeUnit.GB)
				.setNoBlockCache(false)
				.setCacheIndexAndFilterBlocks(true)
				.setBlockRestartInterval(16)
				;
		final List<CompressionType> compressionLevels = new ArrayList<>();
		compressionLevels.add(CompressionType.NO_COMPRESSION); // 0-1
		compressionLevels.add(CompressionType.SNAPPY_COMPRESSION); // 1-2
		compressionLevels.add(CompressionType.SNAPPY_COMPRESSION); // 2-3
		compressionLevels.add(CompressionType.SNAPPY_COMPRESSION); // 3-4
		compressionLevels.add(CompressionType.SNAPPY_COMPRESSION); // 4-5
		compressionLevels.add(CompressionType.SNAPPY_COMPRESSION); // 5-6
		compressionLevels.add(CompressionType.SNAPPY_COMPRESSION); // 6-7

		Options options = new Options()
				.setAllowConcurrentMemtableWrite(true)
				.setEnableWriteThreadAdaptiveYield(true)
				.setCreateIfMissing(true)
				.setMaxWriteBufferNumber(3)
				.setTableFormatConfig(tableOptions)
				.setMaxBackgroundCompactions(10)
				.setMaxBackgroundFlushes(4)
				.setBloomLocality(10)
				.setMinWriteBufferNumberToMerge(4)
				.setCompressionPerLevel(compressionLevels)
				.setNumLevels(7)
				.setCompressionType(CompressionType.SNAPPY_COMPRESSION)
				.setCompactionStyle(CompactionStyle.UNIVERSAL)
				.setMemTableConfig(new SkipListMemTableConfig())
				;
		return options;
	}

}
