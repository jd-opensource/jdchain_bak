package com.jd.blockchain.ledger;

public class IllegalTransactionException extends RuntimeException {

	private static final long serialVersionUID = 6348921847690512944L;

	private TransactionState txState;

	public IllegalTransactionException(String message) {
		super(message);
		this.txState = TransactionState.SYSTEM_ERROR;
	}
	
	public IllegalTransactionException(String message, TransactionState txState) {
		super(message);
		assert TransactionState.SUCCESS != txState;
		this.txState = txState;
	}

	public IllegalTransactionException(String message, Throwable cause) {
		super(message, cause);
		this.txState = TransactionState.SYSTEM_ERROR;
	}

	public IllegalTransactionException(String message, Throwable cause, TransactionState txState) {
		super(message, cause);
		assert TransactionState.SUCCESS != txState;
		this.txState = txState;
	}

	public TransactionState getTxState() {
		return txState;
	}

}
