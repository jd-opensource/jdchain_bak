package com.jd.blockchain.utils.net;

public class NetworkException extends RuntimeException{

	private static final long serialVersionUID = 2231122547918937867L;
	
	public NetworkException() {
	}
	public NetworkException(String message) {
		super(message);
	}
	public NetworkException(String message, Throwable cause) {
		super(message, cause);
	}

	
}
