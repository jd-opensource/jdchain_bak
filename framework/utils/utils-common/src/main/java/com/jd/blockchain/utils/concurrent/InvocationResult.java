package com.jd.blockchain.utils.concurrent;

public class InvocationResult<T> {

	private volatile T value;
	
	private volatile Exception error;

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public Exception getError() {
		return error;
	}

	public void setError(Exception error) {
		this.error = error;
	}

}