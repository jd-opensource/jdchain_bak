//package com.jd.blockchain.ledger.core;
//
//import com.jd.blockchain.crypto.hash.HashDigest;
//import com.jd.blockchain.ledger.data.DigitalSignatureBlob;
//
//import my.utils.io.ExistentialKVStorage;
//import my.utils.io.VersioningKVStorage;
//
//public class PrivilegeDataSet extends GenericMerkleDataSet<Authorization, AuthorizationVO> {
//
//	public PrivilegeDataSet(CryptoSetting setting, ExistentialKVStorage merkleTreeStorage, VersioningKVStorage dataStorage) {
//		this(null, setting, merkleTreeStorage, dataStorage, false);
//	}
//
//	public PrivilegeDataSet(HashDigest rootHash, CryptoSetting setting, ExistentialKVStorage merkleTreeStorage,
//			VersioningKVStorage dataStorage, boolean readonly) {
//		super(rootHash, setting, merkleTreeStorage, dataStorage, readonly, Authorization.class, AuthorizationVO.class,
//				DigitalSignatureBlob.class);
//	}
//
//}