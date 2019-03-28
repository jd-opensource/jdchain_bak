package com.jd.blockchain.contract;

import com.jd.blockchain.contract.model.ContractEventContext;

public interface ContractCode {

	String getAddress();
	
	long getVersion();

	void processEvent(ContractEventContext eventContext);

}
