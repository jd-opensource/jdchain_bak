package com.jd.blockchain.utils.security;

public class KeyGenerationException extends RuntimeException {

	private static final long serialVersionUID = 1729427276871753983L;

	public KeyGenerationException() {
	}
	
	public KeyGenerationException(String message) {
		super(message);
	}
	
	public KeyGenerationException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
