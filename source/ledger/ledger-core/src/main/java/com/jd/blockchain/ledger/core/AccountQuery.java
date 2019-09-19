package com.jd.blockchain.ledger.core;

import com.jd.blockchain.ledger.AccountHeader;
import com.jd.blockchain.utils.Bytes;

public interface AccountQuery<T> extends MerkleProvable {

	AccountHeader[] getHeaders(int fromIndex, int count);

	/**
	 * 返回总数；
	 * 
	 * @return
	 */
	long getTotal();

	boolean contains(Bytes address);

	/**
	 * 返回账户实例；
	 * 
	 * @param address Base58 格式的账户地址；
	 * @return 账户实例，如果不存在则返回 null；
	 */
	T getAccount(String address);

	T getAccount(Bytes address);

	T getAccount(Bytes address, long version);

}