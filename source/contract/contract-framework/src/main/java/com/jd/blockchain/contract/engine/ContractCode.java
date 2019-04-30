package com.jd.blockchain.contract.engine;

import com.jd.blockchain.contract.ContractEventContext;

public interface ContractCode {

	String getAddress();
	
	long getVersion();

	void processEvent(ContractEventContext eventContext);

}
