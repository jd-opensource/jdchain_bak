package com.jd.blockchain.contract.engine;

import com.jd.blockchain.contract.ContractEventContext;
import com.jd.blockchain.ledger.BytesValue;
import com.jd.blockchain.utils.Bytes;

public interface ContractCode {

	Bytes getAddress();
	
	long getVersion();

	BytesValue processEvent(ContractEventContext eventContext);
}
