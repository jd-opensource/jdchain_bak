package com.jd.blockchain.ledger;

public class ParticipantDoesNotExistException extends LedgerException {

	private static final long serialVersionUID = 397450363050148898L;

	public ParticipantDoesNotExistException(String message) {
		super(message);
	}

	public ParticipantDoesNotExistException(String message, Throwable cause) {
		super(message, cause);
	}

}
