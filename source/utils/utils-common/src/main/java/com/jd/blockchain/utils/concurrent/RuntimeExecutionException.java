package com.jd.blockchain.utils.concurrent;

/**
 * 未预期的中断异常；
 * 
 * @author haiq
 *
 */
public class RuntimeExecutionException extends RuntimeException {

	private static final long serialVersionUID = -4404758941888371638L;

	public RuntimeExecutionException() {
	}

	public RuntimeExecutionException(String message) {
		super(message);
	}

	public RuntimeExecutionException(String message, Throwable cause) {
		super(message, cause);
	}

}
