package com.jd.blockchain.ledger;

public class ParticipantDoesNotExistException extends LedgerException {

	private static final long serialVersionUID = 397450363050148898L;

	public ParticipantDoesNotExistException() {
		this(TransactionState.PARTICIPANT_DOES_NOT_EXIST, null);
	}

	public ParticipantDoesNotExistException(String message) {
		this(TransactionState.PARTICIPANT_DOES_NOT_EXIST, message);
	}

	private ParticipantDoesNotExistException(TransactionState state, String message){
		super(message);
		assert TransactionState.SUCCESS != state;
		setState(state);
	}

	public ParticipantDoesNotExistException(String message, Throwable cause) {
		super(message, cause);
		this.setState(TransactionState.PARTICIPANT_DOES_NOT_EXIST);
	}

}
