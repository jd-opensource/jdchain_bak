package com.jd.blockchain.storage.service.impl.rocksdb;

import com.jd.blockchain.storage.service.ExPolicyKVStorage;
import com.jd.blockchain.utils.Bytes;

public class RocksDBExPolicyStorage implements ExPolicyKVStorage {
	
	private RocksDBVersioningStorage versioningStorage;

	public RocksDBExPolicyStorage(RocksDBVersioningStorage versioningStorage) {
		this.versioningStorage = versioningStorage;
	}

	@Override
	public byte[] get(Bytes key) {
		return versioningStorage.get(key, 0);
	}

	@Override
	public boolean exist(Bytes key) {
		long ver = versioningStorage.getVersion(key);
		if (ver < 1) {
			return ver == 0;
		}
		throw new IllegalStateException(
				"The version of keys managed by this RocksDBExPolicyStorage is great than expected max value '0'.");
	}

	@Override
	public boolean set(Bytes key, byte[] value, ExPolicy ex) {
		switch (ex) {
		case EXISTING:
			if (exist(key)) {
				versioningStorage.dbSetData(key, value, 0);
				return true;
			}
			return false;
		case NOT_EXISTING:
			long v = versioningStorage.set(key, value, -1);
			return v == 0;
		default:
			throw new IllegalArgumentException("Unsupported ExPolicy[" + ex.toString() + "]!");
		}
	}

	@Override
	public void batchBegin() {
		versioningStorage.batchBegin();
	}

	@Override
	public void batchCommit() {
		versioningStorage.batchCommit();
	}
}