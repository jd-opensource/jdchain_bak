package com.jd.blockchain.ledger.core;

public interface OperationHandleRegisteration {

	OperationHandle getHandle(Class<?> operationType);

}