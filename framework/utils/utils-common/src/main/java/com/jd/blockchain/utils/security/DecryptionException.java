package com.jd.blockchain.utils.security;

/**
 * 解密异常；
 * 
 * @author haiq
 *
 */
public class DecryptionException extends RuntimeException {

	private static final long serialVersionUID = 2188866951704920121L;

	public DecryptionException(String message) {
		super(message);
	}

	public DecryptionException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
