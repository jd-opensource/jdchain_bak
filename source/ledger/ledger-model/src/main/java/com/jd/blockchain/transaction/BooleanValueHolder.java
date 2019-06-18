package com.jd.blockchain.transaction;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class BooleanValueHolder extends ValueHolderBase {

	BooleanValueHolder(ContractInvocation invocation) {
		super(invocation);
	}

	/**
	 * 等待结果合约调用的结果返回；
	 * 
	 * @return
	 */
	public boolean get() {
		return (boolean) super.getValue();
	}

	/**
	 * 等待结果合约调用的结果返回；
	 * 
	 * @param timeout
	 * @return
	 * @throws TimeoutException
	 */
	public boolean get(long timeout) throws TimeoutException {
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
	public boolean get(long timeout, TimeUnit unit) throws TimeoutException {
		return (boolean) super.getValue(timeout, unit);
	}
}