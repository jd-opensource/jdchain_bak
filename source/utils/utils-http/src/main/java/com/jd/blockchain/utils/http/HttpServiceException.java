package com.jd.blockchain.utils.http;

/**
 * HTTP 服务异常；
 * 
 * 通过 HTTP Service Agent 调用产生的异常都将包装为 HttpServiceException 抛出；
 * 
 * 注：操作方法通过 throws 关键字声明的异常除外，这些异常将原样抛出；
 * 
 * @author haiq
 *
 */
public class HttpServiceException extends RuntimeException{

	private static final long serialVersionUID = 7316207586307377240L;

	public HttpServiceException() {
	}
	
	public HttpServiceException(String message) {
		super(message);
	}
	
	public HttpServiceException(String message, Throwable cause) {
		super(message, cause);
	}
}
