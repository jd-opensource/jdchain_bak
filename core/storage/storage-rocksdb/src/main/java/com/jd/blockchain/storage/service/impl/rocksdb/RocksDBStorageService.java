package com.jd.blockchain.storage.service.impl.rocksdb;

import org.rocksdb.RocksDB;

import com.jd.blockchain.storage.service.ExPolicyKVStorage;
import com.jd.blockchain.storage.service.KVStorageService;
import com.jd.blockchain.storage.service.VersioningKVStorage;

public class RocksDBStorageService implements KVStorageService {

	private ExPolicyKVStorage exStorage;

	private VersioningKVStorage verStorage;

	public RocksDBStorageService(RocksDB db) {
		this.verStorage = new RocksDBVersioningStorage(db);
		this.exStorage = new RocksDBExPolicyStorage(new RocksDBVersioningStorage(db));
	}

	@Override
	public ExPolicyKVStorage getExPolicyKVStorage() {
		return exStorage;
	}

	@Override
	public VersioningKVStorage getVersioningKVStorage() {
		return verStorage;
	}

}
