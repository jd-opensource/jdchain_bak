package com.jd.blockchain.utils.security;

/**
 * 认证异常；
 * 
 * @author haiq
 *
 */
public class AuthenticationException extends RuntimeException {

	private static final long serialVersionUID = 2188866951704920121L;

	public AuthenticationException(String message) {
		super(message);
	}

	public AuthenticationException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
