package com.jd.blockchain.crypto;

public class CryptoException extends RuntimeException {
	
	private static final long serialVersionUID = 1044893802336696205L;
	
	
	public CryptoException(String message) {
		super(message);
	}
	
	public CryptoException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
