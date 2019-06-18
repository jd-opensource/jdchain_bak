package com.jd.blockchain.transaction;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

class ValueHolderBase {
		private ContractInvocation invocation;

		protected ValueHolderBase(ContractInvocation invocation) {
			this.invocation = invocation;
		}

		/**
		 * 等待结果合约调用的结果返回；
		 * 
		 * @return
		 */
		protected Object getValue() {
			try {
				return invocation.getReturnValue().get();
			} catch (InterruptedException | ExecutionException e) {
				throw new IllegalStateException(e.getMessage(), e);
			}
		}

		/**
		 * 等待结果合约调用的结果返回；
		 * 
		 * @param timeout
		 * @param unit
		 * @return
		 * @throws TimeoutException
		 */
		protected Object getValue(long timeout, TimeUnit unit) throws TimeoutException {
			try {
				return invocation.getReturnValue().get(timeout, unit);
			} catch (InterruptedException | ExecutionException e) {
				throw new IllegalStateException(e.getMessage(), e);
			}
		}
	}