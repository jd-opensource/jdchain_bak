package com.jd.blockchain.ledger.core;

import com.jd.blockchain.crypto.asymmetric.PubKey;
import com.jd.blockchain.crypto.hash.HashDigest;
import com.jd.blockchain.ledger.AccountHeader;
import com.jd.blockchain.ledger.BlockchainIdentity;
import com.jd.blockchain.ledger.BlockchainIdentityData;
import com.jd.blockchain.ledger.CryptoSetting;
import com.jd.blockchain.storage.service.ExPolicyKVStorage;
import com.jd.blockchain.storage.service.VersioningKVStorage;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.Transactional;

/**
 * 事务性的基础账户；
 * 
 * @author huanghaiquan
 *
 */
public class BaseAccount implements AccountHeader, MerkleProvable, Transactional {

	private BlockchainIdentity bcid;

	protected MerkleDataSet dataset;

	private AccountAccessPolicy accessPolicy;

	/**
	 * Create a new Account with the specified address and pubkey; <br>
	 *
	 * At the same time, a empty merkle dataset is also created for this account,
	 * which is used for storing data of this account.<br>
	 *
	 * Note that, the blockchain identity of the account is not stored in the
	 * account's merkle dataset, but is stored by the outer invoker;
	 * 
	 * @param address
	 * @param pubKey
	 */
	public BaseAccount(Bytes address, PubKey pubKey, CryptoSetting cryptoSetting, String keyPrefix,
			ExPolicyKVStorage exStorage, VersioningKVStorage verStorage, AccountAccessPolicy accessPolicy) {
		this(address, pubKey, null, cryptoSetting, keyPrefix, exStorage, verStorage, false, accessPolicy);
	}

	/**
	 * Create a new Account with the specified address and pubkey; <br>
	 *
	 * At the same time, a empty merkle dataset is also created for this account,
	 * which is used for storing data of this account.<br>
	 *
	 * Note that, the blockchain identity of the account is not stored in the
	 * account's merkle dataset, but is stored by the outer invoker;
	 *
	 * @param bcid
	 * @param cryptoSetting
	 * @param exStorage
	 * @param verStorage
	 * @param accessPolicy
	 */
	public BaseAccount(BlockchainIdentity bcid, CryptoSetting cryptoSetting, String keyPrefix,
			ExPolicyKVStorage exStorage, VersioningKVStorage verStorage, AccountAccessPolicy accessPolicy) {
		this(bcid, null, cryptoSetting, keyPrefix, exStorage, verStorage, false, accessPolicy);
	}

	/**
	 * Create a account instance with the specified address and pubkey and load it's
	 * merkle dataset with the specified root hash. which is used for storing data
	 * of this account.<br>
	 *
	 * @param address
	 * @param pubKey
	 * @param dataRootHash
	 *            merkle root hash of account's data; if null be set, create a new
	 *            empty merkle dataset;
	 * @param cryptoSetting
	 * @param exStorage
	 * @param verStorage
	 * @param readonly
	 * @param accessPolicy
	 */
	public BaseAccount(Bytes address, PubKey pubKey, HashDigest dataRootHash, CryptoSetting cryptoSetting,
			String keyPrefix, ExPolicyKVStorage exStorage, VersioningKVStorage verStorage, boolean readonly,
			AccountAccessPolicy accessPolicy) {
		this(new BlockchainIdentityData(address, pubKey), dataRootHash, cryptoSetting, keyPrefix, exStorage, verStorage,
				readonly, accessPolicy);
	}

	public BaseAccount(BlockchainIdentity bcid, HashDigest dataRootHash, CryptoSetting cryptoSetting, String keyPrefix,
			ExPolicyKVStorage exStorage, VersioningKVStorage verStorage, boolean readonly,
			AccountAccessPolicy accessPolicy) {
		this.bcid = bcid;
		this.dataset = new MerkleDataSet(dataRootHash, cryptoSetting, keyPrefix, exStorage, verStorage, readonly);
		this.accessPolicy = accessPolicy;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jd.blockchain.ledger.core.AccountDataSet#getAddress()
	 */
	@Override
	public Bytes getAddress() {
		return bcid.getAddress();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jd.blockchain.ledger.core.AccountDataSet#getPubKey()
	 */
	@Override
	public PubKey getPubKey() {
		return bcid.getPubKey();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jd.blockchain.ledger.core.AccountDataSet#getRootHash()
	 */
	@Override
	public HashDigest getRootHash() {
		return dataset.getRootHash();
	}

	@Override
	public MerkleProof getProof(Bytes key) {
		return dataset.getProof(key);
	}

	/**
	 * 是否只读；
	 * 
	 * @return
	 */
	public boolean isReadonly() {
		return dataset.isReadonly();
	}

	/**
	 * Create or update the value associated the specified key if the version
	 * checking is passed.<br>
	 * 
	 * The value of the key will be updated only if it's latest version equals the
	 * specified version argument. <br>
	 * If the key doesn't exist, the version checking will be ignored, and key will
	 * be created with a new sequence number as id. <br>
	 * It also could specify the version argument to -1 to ignore the version
	 * checking.
	 * <p>
	 * If updating is performed, the version of the key increase by 1. <br>
	 * If creating is performed, the version of the key initialize by 0. <br>
	 * 
	 * @param key
	 *            The key of data;
	 * @param value
	 *            The value of data;
	 * @param version
	 *            The expected version of the key.
	 * @return The new version of the key. <br>
	 *         If the key is new created success, then return 0; <br>
	 *         If the key is updated success, then return the new version;<br>
	 *         If this operation fail by version checking or other reason, then
	 *         return -1;
	 */
	public long setBytes(Bytes key, byte[] value, long version) {
		// TODO: 支持多种数据类型；
		return dataset.setValue(key, value, version);
	}

	/**
	 * Return the latest version entry associated the specified key; If the key
	 * doesn't exist, then return -1;
	 * 
	 * @param key
	 * @return
	 */
	public long getKeyVersion(Bytes key) {
		return dataset.getVersion(key);
	}

	/**
	 * return the latest version's value;
	 * 
	 * @param key
	 * @return return null if not exist;
	 */
	public byte[] getBytes(Bytes key) {
		return dataset.getValue(key);
	}

	/**
	 * Return the specified version's value;
	 * 
	 * @param key
	 * @param version
	 * @return return null if not exist;
	 */
	public byte[] getBytes(Bytes key, long version) {
		return dataset.getValue(key, version);
	}

	@Override
	public boolean isUpdated() {
		return dataset.isUpdated();
	}

	@Override
	public void commit() {
		if (!accessPolicy.checkCommitting(this)) {
			throw new LedgerException("Account Committing was rejected for the access policy!");
		}

		dataset.commit();
	}

	@Override
	public void cancel() {
		dataset.cancel();
	}

}