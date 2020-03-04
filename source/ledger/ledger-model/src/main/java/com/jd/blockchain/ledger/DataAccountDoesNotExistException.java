package com.jd.blockchain.ledger;

public class DataAccountDoesNotExistException extends LedgerException {

	
	private static final long serialVersionUID = -1889587937401974215L;

	public DataAccountDoesNotExistException(){
		this(TransactionState.DATA_ACCOUNT_DOES_NOT_EXIST,null);
	}

	public DataAccountDoesNotExistException(String message) {
		this(TransactionState.DATA_ACCOUNT_DOES_NOT_EXIST,message);
	}

	private DataAccountDoesNotExistException(TransactionState state, String message){
		super(message);
		assert TransactionState.SUCCESS != state;
		setState(state);
	}

	public DataAccountDoesNotExistException(String message, Throwable cause) {
		super(message, cause);
		setState(TransactionState.DATA_ACCOUNT_DOES_NOT_EXIST);
	}

}
