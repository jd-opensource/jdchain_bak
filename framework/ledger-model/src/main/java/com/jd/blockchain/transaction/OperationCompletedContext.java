package com.jd.blockchain.transaction;

import com.jd.blockchain.ledger.BytesValue;

public class OperationCompletedContext {

	private int operationIndex;

	private BytesValue returnBytesValue;

	public OperationCompletedContext(int operationIndex, BytesValue returnBytesValue) {
		this.operationIndex = operationIndex;
		this.returnBytesValue = returnBytesValue;
	}

	public int getOperationIndex() {
		return operationIndex;
	}

	public BytesValue getReturnBytesValue() {
		return returnBytesValue;
	}

}
