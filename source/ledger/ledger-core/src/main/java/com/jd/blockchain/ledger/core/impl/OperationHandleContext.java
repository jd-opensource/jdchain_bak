package com.jd.blockchain.ledger.core.impl;

import com.jd.blockchain.ledger.Operation;

public interface OperationHandleContext {
	
	void handle(Operation operation);
	
}
