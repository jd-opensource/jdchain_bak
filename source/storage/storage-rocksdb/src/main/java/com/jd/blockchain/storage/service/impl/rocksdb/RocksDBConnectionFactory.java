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
		Cache cache = new LRUCache(512 * SizeUnit.MB, 64, false);

		final BlockBasedTableConfig tableOptions = new BlockBasedTableConfig()
				.setBlockCache(cache)
				.setMetadataBlockSize(4096)
				.setCacheIndexAndFilterBlocks(true) // 设置索引和布隆过滤器使用Block Cache内存
				.setCacheIndexAndFilterBlocksWithHighPriority(true)
				.setIndexType(IndexType.kTwoLevelIndexSearch) // 设置两级索引，控制索引占用内存
				.setPinTopLevelIndexAndFilter(false)
				.setBlockSize(4096)
				.setFilterPolicy(null) // 不设置布隆过滤器
				;

		Options options = new Options()
				// 最多占用256 * 6 + 512 = 2G内存
				.setWriteBufferSize(256 * SizeUnit.MB)
				.setMaxWriteBufferNumber(6)
				.setMinWriteBufferNumberToMerge(2)
				.setMaxOpenFiles(100) // 控制最大打开文件数量，防止内存持续增加
				.setAllowConcurrentMemtableWrite(true) //允许并行Memtable写入
				.setCreateIfMissing(true)
				.setTableFormatConfig(tableOptions)
				.setMaxBackgroundCompactions(5)
				.setMaxBackgroundFlushes(4)
				;
		return options;
	}

}
