package com.jd.blockchain.ledger.core;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.LedgerAdminInfo;
import com.jd.blockchain.ledger.LedgerAdminSettings;
import com.jd.blockchain.ledger.LedgerBlock;

public interface LedgerQuery {

	/**
	 * 账本哈希，这是账本的唯一标识；
	 * 
	 * @return
	 */
	HashDigest getHash();

	/**
	 * 最新区块高度；
	 * 
	 * @return
	 */
	long getLatestBlockHeight();

	/**
	 * 最新区块哈希；
	 * 
	 * @return
	 */
	HashDigest getLatestBlockHash();

	/**
	 * 最新区块；
	 * 
	 * @return
	 */
	LedgerBlock getLatestBlock();

	/**
	 * 指定高度的区块哈希；
	 * 
	 * @param height
	 * @return
	 */
	HashDigest getBlockHash(long height);

	/**
	 * 指定高度的区块；
	 * 
	 * @param height
	 * @return
	 */
	LedgerBlock getBlock(long height);

	LedgerAdminInfo getAdminInfo();

	LedgerAdminInfo getAdminInfo(LedgerBlock block);

	LedgerAdminSettings getAdminSettings();

	LedgerAdminSettings getAdminSettings(LedgerBlock block);

	LedgerBlock getBlock(HashDigest hash);

	/**
	 * 返回指定
	 * @param block
	 * @return
	 */
	LedgerDataQuery getLedgerData(LedgerBlock block);
	
	/**
	 * 返回最新区块对应的账本数据；
	 * 
	 * @return
	 */
	default LedgerDataQuery getLedgerData() {
		return getLedgerData(getLatestBlock());
	}

	TransactionQuery getTransactionSet(LedgerBlock block);

	UserAccountQuery getUserAccountSet(LedgerBlock block);

	DataAccountQuery getDataAccountSet(LedgerBlock block);

	ContractAccountQuery getContractAccountSet(LedgerBlock block);

	default TransactionQuery getTransactionSet() {
		return getTransactionSet(getLatestBlock());
	}

	default UserAccountQuery getUserAccountSet() {
		return getUserAccountSet(getLatestBlock());
	}

	default DataAccountQuery getDataAccountSet() {
		return getDataAccountSet(getLatestBlock());
	}

	default ContractAccountQuery getContractAccountset() {
		return getContractAccountSet(getLatestBlock());
	}

	/**
	 * 重新检索最新区块，同时更新缓存；
	 * 
	 * @return
	 */
	LedgerBlock retrieveLatestBlock();

	/**
	 * 重新检索最新区块，同时更新缓存；
	 * 
	 * @return
	 */
	long retrieveLatestBlockHeight();

	/**
	 * 重新检索最新区块哈希，同时更新缓存；
	 * 
	 * @return
	 */
	HashDigest retrieveLatestBlockHash();

}