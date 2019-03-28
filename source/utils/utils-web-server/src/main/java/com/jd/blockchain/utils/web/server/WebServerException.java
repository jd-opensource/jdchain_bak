package com.jd.blockchain.utils.web.server;

public class WebServerException extends RuntimeException{
	
	private static final long serialVersionUID = -9177534295689026560L;

	public WebServerException() {
	}
	
	public WebServerException(String message) {
		super(message);
	}
	
	public WebServerException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
