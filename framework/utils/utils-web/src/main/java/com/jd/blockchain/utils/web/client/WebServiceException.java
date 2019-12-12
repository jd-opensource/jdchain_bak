package com.jd.blockchain.utils.web.client;

import com.jd.blockchain.utils.http.HttpServiceException;

public class WebServiceException extends HttpServiceException {

	private static final long serialVersionUID = -4869903115201215122L;

	private int errorCode;

	public int getErrorCode() {
		return errorCode;
	}
	
	public WebServiceException() {
	}

	public WebServiceException(int errorCode) {
		super("Error code[" + errorCode + "]!");
		this.errorCode = errorCode;
	}

	public WebServiceException(int errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	public WebServiceException(int errorCode, String message, Throwable cause) {
		super(message, cause);
		this.errorCode = errorCode;
	}

}
