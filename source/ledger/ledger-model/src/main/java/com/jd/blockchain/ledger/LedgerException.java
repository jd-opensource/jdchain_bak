package com.jd.blockchain.ledger;

public class LedgerException extends RuntimeException {

	private static final long serialVersionUID = -4090881296855827888L;

	private TransactionState state = TransactionState.LEDGER_ERROR;
	

	public LedgerException(String message) {
		super(message);
	}

	public LedgerException(String message, Throwable cause) {
		super(message, cause);
	}

	public TransactionState getState() {
		return state;
	}

	public void setState(TransactionState state) {
		this.state = state;
	}
}
