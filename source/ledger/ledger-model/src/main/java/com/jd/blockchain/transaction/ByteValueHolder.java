package com.jd.blockchain.transaction;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ByteValueHolder extends ValueHolderBase {

	ByteValueHolder(ContractInvocation invocation) {
		super(invocation);
	}

	/**
	 * 等待结果合约调用的结果返回；
	 * 
	 * @return
	 */
	public byte get() {
		return (byte) super.getValue();
	}

	/**
	 * 等待结果合约调用的结果返回；
	 * 
	 * @param timeout
	 * @return
	 * @throws TimeoutException
	 */
	public byte get(long timeout) throws TimeoutException {
		return get(timeout, TimeUnit.MILLISECONDS);
	}

	/**
	 * 等待结果合约调用的结果返回；
	 * 
	 * @param timeout
	 * @param unit
	 * @return
	 * @throws TimeoutException
	 */
	public byte get(long timeout, TimeUnit unit) throws TimeoutException {
		return (byte) super.getValue(timeout, unit);
	}
}