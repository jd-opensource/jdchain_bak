package com.jd.blockchain.binaryproto;

public class DataContractException extends RuntimeException {
	
	private static final long serialVersionUID = 5069307301932155810L;
	
	public DataContractException(String message) {
		super(message);
	}
	public DataContractException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
