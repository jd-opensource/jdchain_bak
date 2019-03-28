package com.jd.blockchain.utils.concurrent;

public class InvocationResult<T> {

	private volatile T result;
	
	private volatile Exception error;

	public T getResult() {
		return result;
	}

	public void setResult(T result) {
		this.result = result;
	}

	public Exception getError() {
		return error;
	}

	public void setError(Exception error) {
		this.error = error;
	}

}