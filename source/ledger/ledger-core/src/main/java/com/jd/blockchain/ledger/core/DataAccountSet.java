package com.jd.blockchain.ledger.core;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.ledger.BlockchainIdentity;
import com.jd.blockchain.ledger.CryptoSetting;
import com.jd.blockchain.ledger.DigitalSignature;
import com.jd.blockchain.ledger.MerkleProof;
import com.jd.blockchain.storage.service.ExPolicyKVStorage;
import com.jd.blockchain.storage.service.VersioningKVStorage;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.Transactional;

public class DataAccountSet implements Transactional, DataAccountQuery {

	private MerkleAccountSet accountSet;

	public DataAccountSet(CryptoSetting cryptoSetting, String prefix, ExPolicyKVStorage exStorage,
			VersioningKVStorage verStorage, AccountAccessPolicy accessPolicy) {
		accountSet = new MerkleAccountSet(cryptoSetting, Bytes.fromString(prefix), exStorage, verStorage, accessPolicy);
	}

	public DataAccountSet(HashDigest dataRootHash, CryptoSetting cryptoSetting, String prefix,
			ExPolicyKVStorage exStorage, VersioningKVStorage verStorage, boolean readonly,
			AccountAccessPolicy accessPolicy) {
		accountSet = new MerkleAccountSet(dataRootHash, cryptoSetting, Bytes.fromString(prefix), exStorage, verStorage,
				readonly, accessPolicy);
	}

	@Override
	public BlockchainIdentity[] getHeaders(int fromIndex, int count) {
		return accountSet.getHeaders(fromIndex, count);
	}

	public boolean isReadonly() {
		return accountSet.isReadonly();
	}

	void setReadonly() {
		accountSet.setReadonly();
	}

	@Override
	public HashDigest getRootHash() {
		return accountSet.getRootHash();
	}

	@Override
	public long getTotal() {
		return accountSet.getTotal();
	}

	@Override
	public boolean contains(Bytes address) {
		return accountSet.contains(address);
	}

	/**
	 * 返回账户的存在性证明；
	 */
	@Override
	public MerkleProof getProof(Bytes address) {
		return accountSet.getProof(address);
	}

	public DataAccount register(Bytes address, PubKey pubKey, DigitalSignature addressSignature) {
		// TODO: 未实现对地址签名的校验和记录；
		CompositeAccount accBase = accountSet.register(address, pubKey);
		return new DataAccount(accBase);
	}

	@Override
	public DataAccount getAccount(String address) {
		return getAccount(Bytes.fromBase58(address));
	}

	/**
	 * 返回数据账户； <br>
	 * 如果不存在，则返回 null；
	 * 
	 * @param address
	 * @return
	 */
	@Override
	public DataAccount getAccount(Bytes address) {
		CompositeAccount accBase = accountSet.getAccount(address);
		if (accBase == null) {
			return null;
		}
		return new DataAccount(accBase);
	}

	@Override
	public DataAccount getAccount(Bytes address, long version) {
		CompositeAccount accBase = accountSet.getAccount(address, version);
		return new DataAccount(accBase);
	}

	@Override
	public boolean isUpdated() {
		return accountSet.isUpdated();
	}

	@Override
	public void commit() {
		accountSet.commit();
	}

	@Override
	public void cancel() {
		accountSet.cancel();
	}
}