package com.jd.blockchain.transaction;

public interface LedgerInitOperator {

	/**
	 * 注册账户操作；
	 * 
	 * @return
	 */

	LedgerInitOperationBuilder ledgers();

}