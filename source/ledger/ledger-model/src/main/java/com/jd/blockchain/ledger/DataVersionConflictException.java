package com.jd.blockchain.ledger;

public class DataVersionConflictException extends LedgerException {

	private static final long serialVersionUID = 3583192000738807503L;
	
	public DataVersionConflictException() {
		this(TransactionState.DATA_VERSION_CONFLICT, null);
	}

	public DataVersionConflictException(String message) {
		this(TransactionState.DATA_VERSION_CONFLICT, message);
	}

	private DataVersionConflictException(TransactionState state, String message) {
		super(message);
		assert TransactionState.SUCCESS != state;
		setState(state);
	}
}
