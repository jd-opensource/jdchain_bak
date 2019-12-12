package com.jd.blockchain.ledger;

public class BlockRollbackException extends LedgerException {

	private static final long serialVersionUID = 3583192000738807503L;

	private TransactionState state;

	public BlockRollbackException(String message) {
		this(TransactionState.SYSTEM_ERROR, message);
	}

	public BlockRollbackException(TransactionState state, String message) {
		super(message);
		assert TransactionState.SUCCESS != state;
		this.state = state;
	}

	public BlockRollbackException(String message, Throwable cause) {
		this(TransactionState.SYSTEM_ERROR, message, cause);
	}

	public BlockRollbackException(TransactionState state, String message, Throwable cause) {
		super(message, cause);
		assert TransactionState.SUCCESS != state;
		this.state = state;
	}

	public TransactionState getState() {
		return state;
	}

}
