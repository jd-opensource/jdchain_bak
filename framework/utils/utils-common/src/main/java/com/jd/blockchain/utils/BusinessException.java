package com.jd.blockchain.utils;

public class BusinessException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1934407327912767401L;

	private Integer errorCode; // 错误代码

	public BusinessException() {
		super();
	}
	
	public BusinessException(int errorCode) {
		super("");
		this.errorCode = errorCode;
	}

	public BusinessException(String message) {
		super(message);
	}

	public BusinessException(String message, Throwable throwable) {
		super(message, throwable);
	}

	public BusinessException(int code, String message) {
		super(message);
		this.errorCode = code;
	}

	public BusinessException(int code, String message, Throwable throwable) {
		super(message, throwable);
		this.errorCode = code;
	}

	public Integer getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

}
