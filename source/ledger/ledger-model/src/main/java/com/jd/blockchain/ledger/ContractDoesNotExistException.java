package com.jd.blockchain.ledger;

public class ContractDoesNotExistException extends LedgerException {


	private static final long serialVersionUID = 8685914012112243771L;

	public ContractDoesNotExistException() {
		this(TransactionState.CONTRACT_DOES_NOT_EXIST, null);
	}

	public ContractDoesNotExistException(String message) {
		this(TransactionState.CONTRACT_DOES_NOT_EXIST, message);
	}

	private ContractDoesNotExistException(TransactionState state, String message){
		super(message);
		assert TransactionState.SUCCESS != state;
		setState(state);
	}

	public ContractDoesNotExistException(String message, Throwable cause) {
		super(message, cause);
		setState(TransactionState.CONTRACT_DOES_NOT_EXIST);
	}

}
