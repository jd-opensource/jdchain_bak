package com.jd.blockchain.utils.concurrent;

/**
 * 未预期的超时异常；
 * 
 * @author haiq
 *
 */
public class RuntimeTimeoutException extends RuntimeException {

	private static final long serialVersionUID = -4404758941888371638L;

	public RuntimeTimeoutException() {
	}

	public RuntimeTimeoutException(String message) {
		super(message);
	}

	public RuntimeTimeoutException(String message, Throwable cause) {
		super(message, cause);
	}

}
