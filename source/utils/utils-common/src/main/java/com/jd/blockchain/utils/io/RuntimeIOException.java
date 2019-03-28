package com.jd.blockchain.utils.io;

public class RuntimeIOException extends RuntimeException{

	private static final long serialVersionUID = 6863237161295632635L;
	
	public RuntimeIOException(String message) {
		super(message);
	}
	
	public RuntimeIOException(String message, Throwable cause) {
		super(message, cause);
	}

}
