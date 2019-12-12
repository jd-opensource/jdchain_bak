package com.jd.blockchain.ledger;

public class LedgerSecurityException extends RuntimeException {

	private static final long serialVersionUID = -4090881296855827888L;
	
	

	public LedgerSecurityException(String message) {
		super(message);
	}

	public LedgerSecurityException(String message, Throwable cause) {
		super(message, cause);
	}

}
