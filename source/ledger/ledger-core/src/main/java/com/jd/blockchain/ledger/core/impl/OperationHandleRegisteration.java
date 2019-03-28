package com.jd.blockchain.ledger.core.impl;

import com.jd.blockchain.ledger.core.OperationHandle;

public interface OperationHandleRegisteration {

	OperationHandle getHandle(Class<?> operationType);

}