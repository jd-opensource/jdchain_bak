package com.jd.blockchain.ledger;

public class OperationResultData implements OperationResult {

	private int index;

	private byte[] result;

	public OperationResultData() {
	}

	public OperationResultData(int operationIndex, byte[] result) {
		this.index = operationIndex;
		this.result = result;
	}

	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public byte[] getResult() {
		return result;
	}
}
