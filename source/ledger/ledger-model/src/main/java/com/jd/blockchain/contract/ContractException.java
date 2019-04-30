package com.jd.blockchain.contract;

public class ContractException extends RuntimeException {
	
	public ContractException(String message) {
		super(message);
	}

	public ContractException(String message,ErrorCodeEnum errorCodeEnum) {
		super(message+","+errorCodeEnum.toString());
	}

	public ContractException(ErrorCodeEnum errorCodeEnum) {
		super(errorCodeEnum.toString());
	}
}
