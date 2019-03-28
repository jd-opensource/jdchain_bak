package com.jd.blockchain.ledger;

public class TransactionException extends Exception {
	
	private static final long serialVersionUID = 3583192000738807503L;
	
	private TransactionState state;

	public TransactionException(TransactionState state) {
		this.state = state;
	}
	
	public TransactionException(TransactionState state, String message) {
		super(message);
		this.state = state;
	}

	public TransactionState getState() {
		return state;
	}

}
