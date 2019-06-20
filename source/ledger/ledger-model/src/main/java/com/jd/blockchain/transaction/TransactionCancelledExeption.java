package com.jd.blockchain.transaction;

public class TransactionCancelledExeption extends RuntimeException {

	private static final long serialVersionUID = -2577951411093171806L;

	public TransactionCancelledExeption(String message) {
		super(message);
	}

	public TransactionCancelledExeption(String message, Throwable cause) {
		super(message, cause);
	}

}
