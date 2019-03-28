package com.jd.blockchain.sdk;

public class BlockchainException extends RuntimeException {
	
	private static final long serialVersionUID = 8228291068740022658L;

	public BlockchainException() {
	}
	
	public BlockchainException(String message) {
		super(message);
	}
	
	public BlockchainException(String message, Throwable cause) {
		super(message, cause);
	}
}
