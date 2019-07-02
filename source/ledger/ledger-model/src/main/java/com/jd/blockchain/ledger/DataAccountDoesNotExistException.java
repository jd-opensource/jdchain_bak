package com.jd.blockchain.ledger;

public class DataAccountDoesNotExistException extends LedgerException {

	
	private static final long serialVersionUID = -1889587937401974215L;

	public DataAccountDoesNotExistException(String message) {
		super(message);
	}

	public DataAccountDoesNotExistException(String message, Throwable cause) {
		super(message, cause);
	}

}
