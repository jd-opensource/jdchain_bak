package com.jd.blockchain.utils.concurrent;

/**
 * 未预期的中断异常；
 * 
 * @author haiq
 *
 */
public class RuntimeInterruptedException extends RuntimeException {

	private static final long serialVersionUID = -4404758941888371638L;

	public RuntimeInterruptedException() {
	}

	public RuntimeInterruptedException(String message) {
		super(message);
	}

	public RuntimeInterruptedException(String message, Throwable cause) {
		super(message, cause);
	}

}
