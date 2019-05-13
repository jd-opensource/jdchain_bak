package com.jd.blockchain.storage.service.impl.rocksdb;

import java.io.IOException;

import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

import com.jd.blockchain.storage.service.DbConnection;
import com.jd.blockchain.storage.service.KVStorageService;
import com.jd.blockchain.utils.io.FileUtils;

public class RocksDBConnection implements DbConnection {

	private Options options;

	private RocksDB db;

	private RocksDBStorageService storage;

	public RocksDBConnection(String dbPath, Options options) {
		try {
			String parentDir = FileUtils.getParent(dbPath);
			if (!FileUtils.existDirectory(parentDir)) {
				FileUtils.makeDirectory(parentDir);
			}
			this.db = RocksDB.open(options, dbPath);
		} catch (RocksDBException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
		this.storage = new RocksDBStorageService(db);
	}

	@Override
	public void close() throws IOException {
		// 假的释放；
		// TODO: 采用引用计数器进行优化；
		// db.close();
	}

	@Override
	public KVStorageService getStorageService() {
		return storage;
	}

	public void dbClose() {
		Options options = this.options;
		this.options = null;
		RocksDB db = this.db;
		this.db = null;

		if (options != null) {
			options.close();
		}
		if (db != null) {
			db.close();
		}
	}

}
