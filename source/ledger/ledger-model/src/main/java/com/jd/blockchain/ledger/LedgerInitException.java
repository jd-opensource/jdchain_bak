package com.jd.blockchain.ledger;

public class LedgerInitException extends LedgerException{
	
	private static final long serialVersionUID = 103724923689027144L;

	public LedgerInitException(String message) {
		super(message);
	}

	public LedgerInitException(String message, Throwable cause) {
		super(message, cause);
	}

}
