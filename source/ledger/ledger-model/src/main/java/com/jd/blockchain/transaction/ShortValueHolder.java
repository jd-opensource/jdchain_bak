package com.jd.blockchain.transaction;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public  class ShortValueHolder extends ValueHolderBase {

	ShortValueHolder(ContractInvocation invocation) {
		super(invocation);
	}

	/**
	 * 等待结果合约调用的结果返回；
	 * 
	 * @return
	 */
	public short get() {
		return (short) super.getValue();
	}

	/**
	 * 等待结果合约调用的结果返回；
	 * 
	 * @param timeout
	 * @return
	 * @throws TimeoutException
	 */
	public short get(long timeout) throws TimeoutException {
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
	public short get(long timeout, TimeUnit unit) throws TimeoutException {
		return (short) super.getValue(timeout, unit);
	}
}