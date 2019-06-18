package com.jd.blockchain.transaction;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ContractReturns {

	/**
	 * 解析合约方法调用的返回值；
	 * <p>
	 * 用法示例：<br>
	 * <code>
	 * ReturnValue<String> retnHolder = decode(contract.issue(assetKey, amount));
	 * 
	 * PreparedTransaction prepTx = tx.prepare();
	 * prepTx.sign(userKey);
	 * prepTx.commit()
	 * 
	 * String retnValue = retnHolder.get(); //这是同步方法，会阻塞当前线程等待交易提交后返回结果；
	 * </code>
	 * 
	 * @param <T>
	 * @param call
	 * @return
	 */
	public static <T> ReturnValue<T> decode(T call) {
		ContractInvocation invocation = ContractInvocationStub.take();
		return new ReturnValue<T>(invocation);
	}

	/**
	 * 解析合约方法调用的返回值；
	 * <p>
	 * 用法示例：<br>
	 * <code>
	 * ReturnLongValue retnHolder = decode(contract.issue(assetKey, amount));
	 * 
	 * PreparedTransaction prepTx = tx.prepare();
	 * prepTx.sign(userKey);
	 * prepTx.commit()
	 * 
	 * long retnValue = retnHolder.get(); //这是同步方法，会阻塞当前线程等待交易提交后返回结果；
	 * </code>
	 * 
	 * @param call
	 * @return
	 */
	public static ReturnLongValue decode(long call) {
		ContractInvocation invocation = ContractInvocationStub.take();
		return new ReturnLongValue(invocation);
	}

	/**
	 * 解析合约方法调用的返回值；
	 * <p>
	 * 用法示例：<br>
	 * <code>
	 * ReturnLongValue retnHolder = decode(contract.issue(assetKey, amount));
	 * 
	 * PreparedTransaction prepTx = tx.prepare();
	 * prepTx.sign(userKey);
	 * prepTx.commit()
	 * 
	 * int retnValue = retnHolder.get(); //这是同步方法，会阻塞当前线程等待交易提交后返回结果；
	 * </code>
	 * 
	 * @param call
	 * @return
	 */
	public static ReturnIntValue decode(int call) {
		ContractInvocation invocation = ContractInvocationStub.take();
		return new ReturnIntValue(invocation);
	}

	/**
	 * 解析合约方法调用的返回值；
	 * <p>
	 * 用法示例：<br>
	 * <code>
	 * ReturnLongValue retnHolder = decode(contract.issue(assetKey, amount));
	 * 
	 * PreparedTransaction prepTx = tx.prepare();
	 * prepTx.sign(userKey);
	 * prepTx.commit()
	 * 
	 * short retnValue = retnHolder.get(); //这是同步方法，会阻塞当前线程等待交易提交后返回结果；
	 * </code>
	 * 
	 * @param call
	 * @return
	 */
	public static ReturnShortValue decode(short call) {
		ContractInvocation invocation = ContractInvocationStub.take();
		return new ReturnShortValue(invocation);
	}

	/**
	 * 解析合约方法调用的返回值；
	 * <p>
	 * 用法示例：<br>
	 * <code>
	 * ReturnLongValue retnHolder = decode(contract.issue(assetKey, amount));
	 * 
	 * PreparedTransaction prepTx = tx.prepare();
	 * prepTx.sign(userKey);
	 * prepTx.commit()
	 * 
	 * byte retnValue = retnHolder.get(); //这是同步方法，会阻塞当前线程等待交易提交后返回结果；
	 * </code>
	 * 
	 * @param call
	 * @return
	 */
	public static ReturnByteValue decode(byte call) {
		ContractInvocation invocation = ContractInvocationStub.take();
		return new ReturnByteValue(invocation);
	}
	
	/**
	 * 解析合约方法调用的返回值；
	 * <p>
	 * 用法示例：<br>
	 * <code>
	 * ReturnLongValue retnHolder = decode(contract.issue(assetKey, amount));
	 * 
	 * PreparedTransaction prepTx = tx.prepare();
	 * prepTx.sign(userKey);
	 * prepTx.commit()
	 * 
	 * boolean retnValue = retnHolder.get(); //这是同步方法，会阻塞当前线程等待交易提交后返回结果；
	 * </code>
	 * 
	 * @param call
	 * @return
	 */
	public static ReturnBooleanValue decode(boolean call) {
		ContractInvocation invocation = ContractInvocationStub.take();
		return new ReturnBooleanValue(invocation);
	}

	
	//----------------------- 内部类型 -----------------------
	
	
	private static class ReturnValueBase {
		private ContractInvocation invocation;

		private ReturnValueBase(ContractInvocation invocation) {
			this.invocation = invocation;
		}

		/**
		 * 等待结果合约调用的结果返回；
		 * 
		 * @return
		 */
		private Object get() {
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
		private Object get(long timeout, TimeUnit unit) throws TimeoutException {
			try {
				return invocation.getReturnValue().get(timeout, unit);
			} catch (InterruptedException | ExecutionException e) {
				throw new IllegalStateException(e.getMessage(), e);
			}
		}
	}

	public static class ReturnValue<T> extends ReturnValueBase {

		private ReturnValue(ContractInvocation invocation) {
			super(invocation);
		}

		/**
		 * 等待结果合约调用的结果返回；
		 * 
		 * @return
		 */
		@SuppressWarnings("unchecked")
		public T get() {
			return (T) super.get();
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
			return (T) super.get(timeout, unit);
		}
	}

	public static class ReturnLongValue extends ReturnValueBase {

		private ReturnLongValue(ContractInvocation invocation) {
			super(invocation);
		}

		/**
		 * 等待结果合约调用的结果返回；
		 * 
		 * @return
		 */
		public long get() {
			return (long) super.get();
		}

		/**
		 * 等待结果合约调用的结果返回；
		 * 
		 * @param timeout
		 * @return
		 * @throws TimeoutException
		 */
		public long get(long timeout) throws TimeoutException {
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
		public long get(long timeout, TimeUnit unit) throws TimeoutException {
			return (long) super.get(timeout, unit);
		}
	}

	public static class ReturnIntValue extends ReturnValueBase {

		private ReturnIntValue(ContractInvocation invocation) {
			super(invocation);
		}

		/**
		 * 等待结果合约调用的结果返回；
		 * 
		 * @return
		 */
		public int get() {
			return (int) super.get();
		}

		/**
		 * 等待结果合约调用的结果返回；
		 * 
		 * @param timeout
		 * @return
		 * @throws TimeoutException
		 */
		public int get(long timeout) throws TimeoutException {
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
		public int get(long timeout, TimeUnit unit) throws TimeoutException {
			return (int) super.get(timeout, unit);
		}
	}

	public static class ReturnShortValue extends ReturnValueBase {

		private ReturnShortValue(ContractInvocation invocation) {
			super(invocation);
		}

		/**
		 * 等待结果合约调用的结果返回；
		 * 
		 * @return
		 */
		public short get() {
			return (short) super.get();
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
			return (short) super.get(timeout, unit);
		}
	}

	public static class ReturnByteValue extends ReturnValueBase {

		private ReturnByteValue(ContractInvocation invocation) {
			super(invocation);
		}

		/**
		 * 等待结果合约调用的结果返回；
		 * 
		 * @return
		 */
		public byte get() {
			return (byte) super.get();
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
			return (byte) super.get(timeout, unit);
		}
	}

	public static class ReturnBooleanValue extends ReturnValueBase {

		private ReturnBooleanValue(ContractInvocation invocation) {
			super(invocation);
		}

		/**
		 * 等待结果合约调用的结果返回；
		 * 
		 * @return
		 */
		public boolean get() {
			return (boolean) super.get();
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
			return (boolean) super.get(timeout, unit);
		}
	}
}
