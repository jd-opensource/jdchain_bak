package com.jd.blockchain.ledger;

public class DataVersionConflictException extends LedgerException {

	private static final long serialVersionUID = 3583192000738807503L;

	private TransactionState state;
	
	public DataVersionConflictException() {
		this(TransactionState.DATA_VERSION_CONFLICT, null);
	}

	public DataVersionConflictException(String message) {
		this(TransactionState.DATA_VERSION_CONFLICT, message);
	}

	private DataVersionConflictException(TransactionState state, String message) {
		super(message);
		assert TransactionState.SUCCESS != state;
		this.state = state;
	}

	public TransactionState getState() {
		return state;
	}

}
