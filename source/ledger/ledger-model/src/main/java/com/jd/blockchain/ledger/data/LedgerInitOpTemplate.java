package com.jd.blockchain.ledger.data;

import com.jd.blockchain.binaryproto.DConstructor;
import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.binaryproto.FieldSetter;
import com.jd.blockchain.ledger.LedgerInitOperation;
import com.jd.blockchain.ledger.LedgerInitSetting;

public class LedgerInitOpTemplate implements LedgerInitOperation {
	static {
		DataContractRegistry.register(LedgerInitOperation.class);
	}
	
	private LedgerInitSetting initSetting;

	public LedgerInitOpTemplate() {
	}

	@DConstructor(name="LedgerInitOpTemplate")
	public LedgerInitOpTemplate(@FieldSetter(name="getInitSetting", type="LedgerInitSetting") LedgerInitSetting initSetting) {
		this.initSetting = initSetting;
	}
	
	@Override
	public LedgerInitSetting getInitSetting() {
		return initSetting;
	}

}
