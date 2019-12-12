package com.jd.blockchain.ledger;

public class ValueTypeCastException extends LedgerException {


	private static final long serialVersionUID = 6641080037721006099L;
	

	public ValueTypeCastException(String message) {
		super(message);
	}

	public ValueTypeCastException(String message, Throwable cause) {
		super(message, cause);
	}

}
