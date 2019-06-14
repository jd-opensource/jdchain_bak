package com.jd.blockchain.contract;

import com.jd.blockchain.ledger.LedgerException;

public class ContractException extends LedgerException {

	private static final long serialVersionUID = -4643365435848655115L;

	public ContractException(String message) {
		super(message);
	}

	public ContractException(String message, Throwable cause) {
		super(message, cause);
	}

}
