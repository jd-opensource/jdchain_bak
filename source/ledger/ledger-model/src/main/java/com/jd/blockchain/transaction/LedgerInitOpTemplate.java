package com.jd.blockchain.transaction;

import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.ledger.LedgerInitOperation;
import com.jd.blockchain.ledger.LedgerInitSetting;

public class LedgerInitOpTemplate implements LedgerInitOperation {
	static {
		DataContractRegistry.register(LedgerInitOperation.class);
	}

	private LedgerInitSetting initSetting;

	public LedgerInitOpTemplate() {
	}

	public LedgerInitOpTemplate(LedgerInitSetting initSetting) {
		this.initSetting = initSetting;
	}

	@Override
	public LedgerInitSetting getInitSetting() {
		return initSetting;
	}

}
