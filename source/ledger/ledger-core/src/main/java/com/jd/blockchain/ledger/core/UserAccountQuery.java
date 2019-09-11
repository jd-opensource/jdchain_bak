package com.jd.blockchain.ledger.core;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.AccountHeader;
import com.jd.blockchain.ledger.MerkleProof;
import com.jd.blockchain.utils.Bytes;

public interface UserAccountQuery {

	AccountHeader[] getAccounts(int fromIndex, int count);

	/**
	 * 返回用户总数；
	 * 
	 * @return
	 */
	long getTotalCount();

	HashDigest getRootHash();

	MerkleProof getProof(Bytes key);

	UserAccount getUser(String address);

	UserAccount getUser(Bytes address);

	boolean contains(Bytes address);

	UserAccount getUser(Bytes address, long version);

}