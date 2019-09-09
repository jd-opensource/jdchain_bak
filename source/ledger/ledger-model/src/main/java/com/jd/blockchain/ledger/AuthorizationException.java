package com.jd.blockchain.ledger;

public class AuthorizationException extends LedgerException {

	private static final long serialVersionUID = -4418553411943356320L;

	
	
	public AuthorizationException(String message) {
		super(message);
	}

	public AuthorizationException(String message, Throwable cause) {
		super(message, cause);
	}

}
