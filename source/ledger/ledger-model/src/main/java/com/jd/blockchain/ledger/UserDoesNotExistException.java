package com.jd.blockchain.ledger;

public class UserDoesNotExistException extends LedgerException {

	private static final long serialVersionUID = 397450363050148898L;

	public UserDoesNotExistException() {
		this(TransactionState.USER_DOES_NOT_EXIST, null);
	}

	public UserDoesNotExistException(String message) {
		this(TransactionState.USER_DOES_NOT_EXIST,message);
	}

	private UserDoesNotExistException(TransactionState state, String message){
		super(message);
		assert TransactionState.SUCCESS != state;
		setState(state);
	}

	public UserDoesNotExistException(String message, Throwable cause) {
		super(message, cause);
		setState(TransactionState.USER_DOES_NOT_EXIST);
	}

}
