package com.jd.blockchain.ledger.data;

import com.jd.blockchain.ledger.LedgerInitOperation;
import com.jd.blockchain.ledger.LedgerInitSetting;

public class LedgerInitOperationBuilderImpl implements LedgerInitOperationBuilder {

	@Override
	public LedgerInitOperation create(LedgerInitSetting initSetting) {
		return new LedgerInitOpTemplate(initSetting);
	}

}
