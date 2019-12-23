package com.jd.blockchain.ledger.core;
//package com.jd.blockchain.ledger.core.impl;
//
//import com.jd.blockchain.storage.service.ExPolicyKVStorage;
//import com.jd.blockchain.storage.service.VersioningKVEntry;
//import com.jd.blockchain.storage.service.VersioningKVStorage;
//import com.jd.blockchain.storage.service.ExPolicyKVStorage.ExPolicy;
//
///**
// * 账本存储代理；<br>
// * 
// * @author huanghaiquan
// *
// */
//class GenesisLedgerStorageProxy implements VersioningKVStorage, ExPolicyKVStorage {
//
//	private VersioningKVStorage versioningStorage;
//
//	private ExPolicyKVStorage exPolicyStorage;
//
//	@Override
//	public long getVersion(String key) {
//		// Storage of genesis ledger is totally empty;
//		return -1;
//	}
//
//	@Override
//	public VersioningKVEntry getEntry(String key, long version) {
//		return null;
//	}
//
//	@Override
//	public byte[] get(String key, long version) {
//		return null;
//	}
//
//	@Override
//	public long set(String key, byte[] value, long version) {
//		if (versioningStorage == null) {
//			throw new IllegalStateException("The persistent storage of ledger is not ready!");
//		}
//		return versioningStorage.set(key, value, version);
//	}
//
//	@Override
//	public byte[] get(String key) {
//		// Storage of genesis ledger is totally empty;
//		return null;
//	}
//	
//	@Override
//	public boolean exist(String key) {
//		return false;
//	}
//
//	@Override
//	public boolean set(String key, byte[] value, ExPolicy ex) {
//		if (exPolicyStorage == null) {
//			throw new IllegalStateException("The persistent storage of ledger is not ready!");
//		}
//		return exPolicyStorage.set(key, value, ex);
//	}
//
//	public void setPersistentStorage(ExPolicyKVStorage exPolicyStorage, VersioningKVStorage persistentStorage) {
//		this.exPolicyStorage = exPolicyStorage;
//		this.versioningStorage = persistentStorage;
//	}
//}