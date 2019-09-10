package com.jd.blockchain.ledger.core;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.AccountHeader;
import com.jd.blockchain.ledger.MerkleProof;
import com.jd.blockchain.utils.Bytes;

public interface ContractAccountQuery {

	AccountHeader[] getAccounts(int fromIndex, int count);

	HashDigest getRootHash();

	/**
	 * 返回合约总数；
	 * 
	 * @return
	 */
	long getTotalCount();

	MerkleProof getProof(Bytes address);

	boolean contains(Bytes address);

	ContractAccount getContract(Bytes address);

	ContractAccount getContract(Bytes address, long version);

}