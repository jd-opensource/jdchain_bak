package com.jd.blockchain.ledger;

public class TransactionRollbackException extends RuntimeException {


	private static final long serialVersionUID = -1223140447229570029L;

	public TransactionRollbackException(String message) {
		super(message);
	}

	public TransactionRollbackException(String message, Throwable cause) {
		super(message, cause);
	}

}
