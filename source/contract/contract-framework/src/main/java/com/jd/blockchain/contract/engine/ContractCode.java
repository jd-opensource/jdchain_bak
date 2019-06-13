package com.jd.blockchain.contract.engine;

import com.jd.blockchain.contract.ContractEventContext;
import com.jd.blockchain.utils.Bytes;

public interface ContractCode {

	Bytes getAddress();
	
	long getVersion();

	byte[] processEvent(ContractEventContext eventContext);
}
