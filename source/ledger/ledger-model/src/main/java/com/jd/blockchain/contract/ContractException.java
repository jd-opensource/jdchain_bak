package com.jd.blockchain.contract;

public class ContractException extends RuntimeException {
	
	private static final long serialVersionUID = 4338023105616639257L;

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
