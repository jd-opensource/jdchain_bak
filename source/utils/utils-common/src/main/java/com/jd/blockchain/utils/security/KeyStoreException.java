package com.jd.blockchain.utils.security;

public class KeyStoreException extends RuntimeException {

	private static final long serialVersionUID = 1729427276871753983L;

	public KeyStoreException() {
	}
	
	public KeyStoreException(String message) {
		super(message);
	}
	
	public KeyStoreException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
