package com.jd.blockchain.ump.model.web;

/**
 * 错误代码；
 */
public enum ErrorCode {

	UNEXPECTED(5000),
	;

	private int value;

	ErrorCode(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}