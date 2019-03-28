package com.jd.blockchain.utils.http;

/**
 * 用于描述调用请求发生的 http 状态码400以上的错误；
 * 
 * @author haiq
 *
 */
public class HttpStatusException extends HttpServiceException {
	
	private static final long serialVersionUID = 9123750807777784421L;

	private int httpCode;
	
//	private String content;
//	
//	public HttpStatusException(int httpCode, String message) {
//		this(httpCode, message, null);
//	}
	
	public HttpStatusException(int httpCode, String message) {
		super(message);
		this.httpCode = httpCode;
//		this.content = content;
	}

	/**
	 * http 状态吗；
	 * @return
	 */
	public int getHttpCode() {
		return httpCode;
	}

//	public String getContent() {
//		return content;
//	}

}
