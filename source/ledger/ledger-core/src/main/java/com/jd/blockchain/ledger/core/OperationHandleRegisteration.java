package com.jd.blockchain.ledger.core;

import com.jd.blockchain.ledger.Operation;

public interface OperationHandleRegisteration {

	OperationHandle getHandle(Class<? extends Operation> operationType);

}