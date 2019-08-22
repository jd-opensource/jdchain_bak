package com.jd.blockchain.ledger.core;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.ledger.AccountHeader;
import com.jd.blockchain.ledger.CryptoSetting;
import com.jd.blockchain.ledger.DigitalSignature;
import com.jd.blockchain.storage.service.ExPolicyKVStorage;
import com.jd.blockchain.storage.service.VersioningKVStorage;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.Transactional;

public class DataAccountSet implements MerkleProvable, Transactional {

	private AccountSet accountSet;

	public DataAccountSet(CryptoSetting cryptoSetting, String prefix, ExPolicyKVStorage exStorage,
			VersioningKVStorage verStorage, AccountAccessPolicy accessPolicy) {
		accountSet = new AccountSet(cryptoSetting, prefix, exStorage, verStorage, accessPolicy);
	}

	public DataAccountSet(HashDigest dataRootHash, CryptoSetting cryptoSetting, String prefix,
			ExPolicyKVStorage exStorage, VersioningKVStorage verStorage, boolean readonly,
			AccountAccessPolicy accessPolicy) {
		accountSet = new AccountSet(dataRootHash, cryptoSetting, prefix, exStorage, verStorage, readonly, accessPolicy);
	}

	public AccountHeader[] getAccounts(int fromIndex, int count) {
		return accountSet.getAccounts(fromIndex, count);
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

	public long getTotalCount() {
		return accountSet.getTotalCount();
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
		BaseAccount accBase = accountSet.register(address, pubKey);
		return new DataAccount(accBase);
	}

	/**
	 * 返回数据账户； <br>
	 * 如果不存在，则返回 null；
	 * 
	 * @param address
	 * @return
	 */
	public DataAccount getDataAccount(Bytes address) {
		BaseAccount accBase = accountSet.getAccount(address);
		if (accBase == null) {
			return null;
		}
		return new DataAccount(accBase);
	}

	public DataAccount getDataAccount(Bytes address, long version) {
		BaseAccount accBase = accountSet.getAccount(address, version);
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