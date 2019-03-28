package com.jd.blockchain.ledger.data;

import com.jd.blockchain.ledger.LedgerInitOperation;
import com.jd.blockchain.ledger.LedgerInitSetting;

public interface LedgerInitOperationBuilder {

	/**
	 * 注册；
	 * 
	 * @param initSetting
	 *            账本初始化配置；
	 * @return LedgerInitOperation
	 */
	LedgerInitOperation create(LedgerInitSetting initSetting);

}
