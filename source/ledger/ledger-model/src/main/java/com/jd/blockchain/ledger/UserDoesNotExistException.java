package com.jd.blockchain.ledger;

public class UserDoesNotExistException extends LedgerException {

	private static final long serialVersionUID = 397450363050148898L;

	public UserDoesNotExistException(String message) {
		super(message);
	}

	public UserDoesNotExistException(String message, Throwable cause) {
		super(message, cause);
	}

}
