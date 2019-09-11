package com.jd.blockchain.ledger.core;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.AccountHeader;
import com.jd.blockchain.ledger.MerkleProof;
import com.jd.blockchain.utils.Bytes;

public interface DataAccountQuery {

	AccountHeader[] getAccounts(int fromIndex, int count);

	HashDigest getRootHash();

	long getTotalCount();

	/**
	 * 返回账户的存在性证明；
	 */
	MerkleProof getProof(Bytes address);

	/**
	 * 返回数据账户； <br>
	 * 如果不存在，则返回 null；
	 * 
	 * @param address
	 * @return
	 */
	DataAccount getDataAccount(Bytes address);

	DataAccount getDataAccount(Bytes address, long version);

}