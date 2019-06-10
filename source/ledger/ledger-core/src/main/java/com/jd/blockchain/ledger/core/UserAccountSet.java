package com.jd.blockchain.ledger.core;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.ledger.AccountHeader;
import com.jd.blockchain.ledger.CryptoSetting;
import com.jd.blockchain.ledger.LedgerException;
import com.jd.blockchain.storage.service.ExPolicyKVStorage;
import com.jd.blockchain.storage.service.VersioningKVStorage;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.Transactional;

/**
 * @author huanghaiquan
 *
 */
public class UserAccountSet implements Transactional, MerkleProvable {

	private AccountSet accountSet;

	public UserAccountSet(CryptoSetting cryptoSetting, String keyPrefix, ExPolicyKVStorage simpleStorage,
			VersioningKVStorage versioningStorage, AccountAccessPolicy accessPolicy) {
		accountSet = new AccountSet(cryptoSetting, keyPrefix, simpleStorage, versioningStorage, accessPolicy);
	}

	public UserAccountSet(HashDigest dataRootHash, CryptoSetting cryptoSetting, String keyPrefix,
			ExPolicyKVStorage exStorage, VersioningKVStorage verStorage, boolean readonly,
			AccountAccessPolicy accessPolicy) {
		accountSet = new AccountSet(dataRootHash, cryptoSetting, keyPrefix, exStorage, verStorage, readonly,
				accessPolicy);
	}

	public AccountHeader[] getAccounts(int fromIndex, int count) {
		return accountSet.getAccounts(fromIndex,count);
	}

	/**
	 * 返回用户总数；
	 * 
	 * @return
	 */
	public long getTotalCount() {
		return accountSet.getTotalCount();
	}

	public boolean isReadonly() {
		return accountSet.isReadonly();
	}

	@Override
	public HashDigest getRootHash() {
		return accountSet.getRootHash();
	}

	@Override
	public MerkleProof getProof(Bytes key) {
		return accountSet.getProof(key);
	}
	
	public UserAccount getUser(String address) {
		return getUser(Bytes.fromBase58(address));
	}

	public UserAccount getUser(Bytes address) {
		BaseAccount baseAccount = accountSet.getAccount(address);
		return new UserAccount(baseAccount);
	}

	public boolean contains(Bytes address) {
		return accountSet.contains(address);
	}

	public UserAccount getUser(Bytes address, long version) {
		BaseAccount baseAccount = accountSet.getAccount(address, version);
		return new UserAccount(baseAccount);
	}

	/**
	 * 注册一个新用户； <br>
	 * 
	 * 如果用户已经存在，则会引发 {@link LedgerException} 异常； <br>
	 * 
	 * 如果指定的地址和公钥不匹配，则会引发 {@link LedgerException} 异常；
	 * 
	 * @param address
	 *            区块链地址；
	 * @param pubKey
	 *            公钥；
	 * @return 注册成功的用户对象；
	 */
	public UserAccount register(Bytes address, PubKey pubKey) {
		BaseAccount baseAccount = accountSet.register(address, pubKey);
		return new UserAccount(baseAccount);
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