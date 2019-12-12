package com.jd.blockchain.ledger.core;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.LedgerTransaction;
import com.jd.blockchain.ledger.TransactionState;

public interface TransactionQuery extends MerkleProvable {

	LedgerTransaction[] getTxs(int fromIndex, int count);

	byte[][] getValuesByIndex(int fromIndex, int count);

	long getTotalCount();

	/**
	 * @param txContentHash
	 *            Base58 编码的交易内容的哈希；
	 * @return
	 */
	LedgerTransaction get(HashDigest txContentHash);

	TransactionState getState(HashDigest txContentHash);

}