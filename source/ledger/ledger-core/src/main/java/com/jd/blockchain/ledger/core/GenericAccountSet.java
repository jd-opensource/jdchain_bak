//package com.jd.blockchain.ledger.core;
//
//import com.jd.blockchain.crypto.HashDigest;
//import com.jd.blockchain.ledger.AccountHeader;
//import com.jd.blockchain.ledger.CryptoSetting;
//import com.jd.blockchain.ledger.MerkleProof;
//import com.jd.blockchain.storage.service.ExPolicyKVStorage;
//import com.jd.blockchain.storage.service.VersioningKVStorage;
//import com.jd.blockchain.utils.Bytes;
//import com.jd.blockchain.utils.Transactional;
//
//public class GenericAccountSet<H extends AccountHeader, T extends GenericAccount<H>> implements AccountQuery<H, T>, Transactional {
//
//	private Class<H> headerType;
//	
//	private MerkleAccountSet merkleAccountSet;
//	
//	public GenericAccountSet(Class<H> headerType, CryptoSetting cryptoSetting, String keyPrefix, ExPolicyKVStorage exStorage,
//			VersioningKVStorage verStorage, AccountAccessPolicy accessPolicy) {
//		this(headerType, null, cryptoSetting, keyPrefix, exStorage, verStorage, false, accessPolicy);
//	}
//
//	public GenericAccountSet(Class<H> headerType, HashDigest rootHash, CryptoSetting cryptoSetting, String keyPrefix,
//			ExPolicyKVStorage exStorage, VersioningKVStorage verStorage, boolean readonly,
//			AccountAccessPolicy accessPolicy) {
//		this.headerType = headerType;
//		this.merkleAccountSet = new MerkleAccountSet(rootHash, cryptoSetting, keyPrefix, exStorage, verStorage, readonly, accessPolicy);
//	}
//
//	@Override
//	public MerkleProof getProof(Bytes address) {
//		return merkleAccountSet.getProof(address);
//	}
//
//	@Override
//	public HashDigest getRootHash() {
//		return merkleAccountSet.getRootHash();
//	}
//
//	@Override
//	public boolean isUpdated() {
//		return merkleAccountSet.isUpdated();
//	}
//
//	@Override
//	public void commit() {
//		merkleAccountSet.commit();
//	}
//
//	@Override
//	public void cancel() {
//		merkleAccountSet.cancel();
//	}
//
//	@Override
//	public H[] getHeaders(int fromIndex, int count) {
//		merkleAccountSet.getHeaders(fromIndex, count)
//		return null;
//	}
//
//	@Override
//	public long getTotal() {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//	@Override
//	public boolean contains(Bytes address) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public T getAccount(String address) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public T getAccount(Bytes address) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public T getAccount(Bytes address, long version) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//
//}
