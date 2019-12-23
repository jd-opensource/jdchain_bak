package com.jd.blockchain.utils.codec;

/**
 * 无效数据异常；
 * 
 * @author haiq
 *
 */
public class DataDecodeException extends RuntimeException{
	
	private static final long serialVersionUID = 5834019788898871654L;

	public DataDecodeException() {
	}
	
	public DataDecodeException(String message) {
		super(message);
	}
	
	public DataDecodeException(String message, Throwable cause) {
		super(message, cause);
	}
}
