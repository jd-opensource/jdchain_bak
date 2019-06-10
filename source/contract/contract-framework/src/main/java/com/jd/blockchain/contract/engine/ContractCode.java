package com.jd.blockchain.contract.engine;

import com.jd.blockchain.contract.ContractEventContext;
import com.jd.blockchain.utils.Bytes;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public interface ContractCode {

	Bytes getAddress();
	
	long getVersion();

	void processEvent(ContractEventContext eventContext, CompletableFuture<String> execReturn);
}
