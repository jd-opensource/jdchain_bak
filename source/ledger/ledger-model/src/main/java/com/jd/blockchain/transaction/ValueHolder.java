package com.jd.blockchain.transaction;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ValueHolder<T> extends ValueHolderBase {

		ValueHolder(ContractInvocation invocation) {
			super(invocation);
		}

		/**
		 * 等待结果合约调用的结果返回；
		 * 
		 * @return
		 */
		@SuppressWarnings("unchecked")
		public T get() {
			return (T) super.getValue();
		}

		/**
		 * 等待结果合约调用的结果返回；
		 * 
		 * @param timeout
		 * @return
		 * @throws TimeoutException
		 */
		public T get(long timeout) throws TimeoutException {
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
		@SuppressWarnings("unchecked")
		public T get(long timeout, TimeUnit unit) throws TimeoutException {
			return (T) super.getValue(timeout, unit);
		}
	}