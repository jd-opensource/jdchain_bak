package com.jd.blockchain.ledger;

public class LedgerPermissionException extends LedgerException {

	private static final long serialVersionUID = 6077975401474519117L;

	public LedgerPermissionException(String message) {
		super(message);
	}

	public LedgerPermissionException(String message, Throwable cause) {
		super(message, cause);
	}

}
