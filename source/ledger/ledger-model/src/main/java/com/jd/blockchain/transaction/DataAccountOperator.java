package com.jd.blockchain.transaction;

import com.jd.blockchain.utils.Bytes;

public interface DataAccountOperator {

	/**
	 * 数据账户；
	 * 
	 * @return
	 */

	DataAccountRegisterOperationBuilder dataAccounts();

	/**
	 * 写入数据； <br>
	 * 
	 * @param accountAddress
	 * @return
	 */
	DataAccountKVSetOperationBuilder dataAccount(String accountAddress);

	/**
	 * 写入数据；
	 * 
	 * @param accountAddress
	 * @return
	 */
	DataAccountKVSetOperationBuilder dataAccount(Bytes accountAddress);
}