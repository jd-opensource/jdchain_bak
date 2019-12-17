package com.jd.blockchain.ledger.core;

import java.util.Map;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.CryptoSetting;
import com.jd.blockchain.ledger.MerkleSnapshot;
import com.jd.blockchain.storage.service.ExPolicyKVStorage;
import com.jd.blockchain.storage.service.VersioningKVStorage;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.Transactional;

public class MerkleDataCluster implements Transactional, MerkleSnapshot {

	private boolean readonly;

	private MerkleDataSet rootDS;

	private Map<Bytes, MerkleDataSet> partitions;

	/**
	 * Create an empty readable {@link MerkleDataCluster} instance;
	 */
	public MerkleDataCluster(CryptoSetting setting, Bytes keyPrefix, ExPolicyKVStorage exPolicyStorage,
			VersioningKVStorage versioningStorage) {
		this(null, setting, keyPrefix, exPolicyStorage, versioningStorage, false);
	}

	/**
	 * Create an {@link MerkleDataCluster} instance;
	 * 
	 * @param rootHash root hash of this {@link MerkleDataCluster} instance;
	 * @param readonly whether read only；
	 */
	public MerkleDataCluster(HashDigest rootHash, CryptoSetting setting, Bytes keyPrefix,
			ExPolicyKVStorage exPolicyStorage, VersioningKVStorage versioningStorage, boolean readonly) {
		this.rootDS = new MerkleDataSet(rootHash, setting, keyPrefix, exPolicyStorage, versioningStorage, readonly);
	}

	@Override
	public HashDigest getRootHash() {
		return rootDS.getRootHash();
	}

	@Override
	public boolean isUpdated() {
		return rootDS.isUpdated();
	}

//	public VersioningMap<Bytes, byte[]> getPartition(Bytes name) {
//		return getPartition(name, false);
//	}
//
//	public VersioningMap<Bytes, byte[]> getPartition(Bytes name, boolean create) {
//		
//	}
//
//	public VersioningMap<Bytes, byte[]> createPartition(Bytes name) {
//
//	}

	@Override
	public void commit() {
		// TODO Auto-generated method stub

	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub

	}

}
