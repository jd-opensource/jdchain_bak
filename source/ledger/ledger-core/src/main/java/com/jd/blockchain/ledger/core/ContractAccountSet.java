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

public class ContractAccountSet implements MerkleProvable, Transactional {

	private AccountSet accountSet;

	public ContractAccountSet(CryptoSetting cryptoSetting, String prefix, ExPolicyKVStorage exStorage,
			VersioningKVStorage verStorage, AccountAccessPolicy accessPolicy) {
		accountSet = new AccountSet(cryptoSetting, prefix, exStorage, verStorage, accessPolicy);
	}

	public ContractAccountSet(HashDigest dataRootHash, CryptoSetting cryptoSetting, String prefix,
			ExPolicyKVStorage exStorage, VersioningKVStorage verStorage, boolean readonly,
			AccountAccessPolicy accessPolicy) {
		accountSet = new AccountSet(dataRootHash, cryptoSetting, prefix, exStorage, verStorage, readonly, accessPolicy);
	}

	public AccountHeader[] getAccounts(int fromIndex, int count) {
		return accountSet.getAccounts(fromIndex,count);
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

	/**
	 * 返回合约总数；
	 * 
	 * @return
	 */
	public long getTotalCount() {
		return accountSet.getTotalCount();
	}

	@Override
	public MerkleProof getProof(Bytes address) {
		return accountSet.getProof(address);
	}

	public boolean contains(Bytes address) {
		return accountSet.contains(address);
	}

	public ContractAccount getContract(Bytes address) {
		BaseAccount accBase = accountSet.getAccount(address);
		return new ContractAccount(accBase);
	}

	public ContractAccount getContract(Bytes address, long version) {
		BaseAccount accBase = accountSet.getAccount(address, version);
		return new ContractAccount(accBase);
	}

	/**
	 * 部署一项新的合约链码；
	 * 
	 * @param address
	 *            合约账户地址；
	 * @param pubKey
	 *            合约账户公钥；
	 * @param addressSignature
	 *            地址签名；合约账户的私钥对地址的签名；
	 * @param chaincode
	 *            链码内容；
	 * @return 合约账户；
	 */
	public ContractAccount deploy(Bytes address, PubKey pubKey, DigitalSignature addressSignature, byte[] chaincode) {
		// TODO: 校验和记录合约地址签名；
		BaseAccount accBase = accountSet.register(address, pubKey);
		ContractAccount contractAcc = new ContractAccount(accBase);
		contractAcc.setChaincode(chaincode, -1);
		return contractAcc;
	}

	/**
	 * 更新指定账户的链码；
	 * 
	 * @param address
	 *            合约账户地址；
	 * @param chaincode
	 *            链码内容；
	 * @param version
	 *            链码版本；
	 * @return 返回链码的新版本号；
	 */
	public long update(Bytes address, byte[] chaincode, long version) {
		BaseAccount accBase = accountSet.getAccount(address);
		ContractAccount contractAcc = new ContractAccount(accBase);
		return contractAcc.setChaincode(chaincode, version);
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