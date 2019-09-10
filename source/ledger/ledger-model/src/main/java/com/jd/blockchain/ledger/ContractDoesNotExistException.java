package com.jd.blockchain.ledger;

public class ContractDoesNotExistException extends LedgerException {


	private static final long serialVersionUID = 8685914012112243771L;

	public ContractDoesNotExistException(String message) {
		super(message);
	}

	public ContractDoesNotExistException(String message, Throwable cause) {
		super(message, cause);
	}

}
