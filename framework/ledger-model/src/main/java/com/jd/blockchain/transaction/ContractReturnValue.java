package com.jd.blockchain.transaction;

public class ContractReturnValue {
	
	private ContractReturnValue() {
	}

	/**
	 * 解析合约方法调用的返回值；
	 * <p>
	 * 用法示例：<br>
	 * <code>
	 * import static com.jd.blockchain.transaction.ContractReturnValue.*; <p><p><p>
	 * 
	 * 
	 * ValueHolder<String retnHolder = decode(contract.issue(assetKey, amount)); <br>
	 * 
	 * PreparedTransaction prepTx = tx.prepare();<br>
	 * prepTx.sign(userKey);<br>
	 * prepTx.commit()<br><br>
	 * 
	 * String retnValue = retnHolder.get(); //这是同步方法，会阻塞当前线程等待交易提交后返回结果；<br>
	 * </code>
	 * 
	 * @param <T>
	 * @param call
	 * @return
	 */
	public static <T> GenericValueHolder<T> decode(T call) {
		ContractInvocation invocation = ContractInvocationStub.take();
		return new GenericValueHolder<T>(invocation);
	}

	/**
	 * 解析合约方法调用的返回值；
	 * <p>
	 * 用法示例：<br>
	 * <code>
	 * import static com.jd.blockchain.transaction.ContractReturnValue.*; <p><p><p>
	 * 
	 * 
	 * LongValueHolder retnHolder = decode(contract.issue(assetKey, amount)); <br>
	 * 
	 * PreparedTransaction prepTx = tx.prepare();<br>
	 * prepTx.sign(userKey);<br>
	 * prepTx.commit()<br><br>
	 * 
	 * long retnValue = retnHolder.get(); //这是同步方法，会阻塞当前线程等待交易提交后返回结果；<br>
	 * </code>
	 * 
	 * @param call
	 * @return
	 */
	public static LongValueHolder decode(long call) {
		ContractInvocation invocation = ContractInvocationStub.take();
		return new LongValueHolder(invocation);
	}

	/**
	 * 解析合约方法调用的返回值；
	 * <p>
	 * 用法示例：<br>
	 * <code>
	 * import static com.jd.blockchain.transaction.ContractReturnValue.*; <p><p><p>
	 * 
	 * 
	 * IntValueHolder retnHolder = decode(contract.issue(assetKey, amount)); <br>
	 * 
	 * PreparedTransaction prepTx = tx.prepare();<br>
	 * prepTx.sign(userKey);<br>
	 * prepTx.commit()<br><br>
	 * 
	 * int retnValue = retnHolder.get(); //这是同步方法，会阻塞当前线程等待交易提交后返回结果；<br>
	 * </code>
	 * 
	 * @param call
	 * @return
	 */
	public static IntValueHolder decode(int call) {
		ContractInvocation invocation = ContractInvocationStub.take();
		return new IntValueHolder(invocation);
	}

	/**
	 * 解析合约方法调用的返回值；
	 * <p>
	 * 用法示例：<br>
	 * <code>
	 * import static com.jd.blockchain.transaction.ContractReturnValue.*; <p><p><p>
	 * 
	 * 
	 * ShortValueHolder retnHolder = decode(contract.issue(assetKey, amount)); <br>
	 * 
	 * PreparedTransaction prepTx = tx.prepare();<br>
	 * prepTx.sign(userKey);<br>
	 * prepTx.commit()<br><br>
	 * 
	 * short retnValue = retnHolder.get(); //这是同步方法，会阻塞当前线程等待交易提交后返回结果；<br>
	 * </code>
	 * 
	 * @param call
	 * @return
	 */
	public static ShortValueHolder decode(short call) {
		ContractInvocation invocation = ContractInvocationStub.take();
		return new ShortValueHolder(invocation);
	}

	/**
	 * 解析合约方法调用的返回值；
	 * <p>
	 * 用法示例：<br>
	 * <code>
	 * import static com.jd.blockchain.transaction.ContractReturnValue.*; <p><p><p>
	 * 
	 * 
	 * ByteValueHolder retnHolder = decode(contract.issue(assetKey, amount)); <br>
	 * 
	 * PreparedTransaction prepTx = tx.prepare();<br>
	 * prepTx.sign(userKey);<br>
	 * prepTx.commit()<br><br>
	 * 
	 * byte retnValue = retnHolder.get(); //这是同步方法，会阻塞当前线程等待交易提交后返回结果；<br>
	 * </code>
	 * 
	 * @param call
	 * @return
	 */
	public static ByteValueHolder decode(byte call) {
		ContractInvocation invocation = ContractInvocationStub.take();
		return new ByteValueHolder(invocation);
	}
	
	/**
	 * 解析合约方法调用的返回值；
	 * <p>
	 * 用法示例：<br>
	 * <code>
	 * import static com.jd.blockchain.transaction.ContractReturnValue.*; <p><p><p>
	 * 
	 * 
	 * BooleanValueHolder retnHolder = decode(contract.issue(assetKey, amount)); <br>
	 * 
	 * PreparedTransaction prepTx = tx.prepare();<br>
	 * prepTx.sign(userKey);<br>
	 * prepTx.commit()<br><br>
	 * 
	 * boolean retnValue = retnHolder.get(); //这是同步方法，会阻塞当前线程等待交易提交后返回结果；<br>
	 * </code>
	 * 
	 * @param call
	 * @return
	 */
	public static BooleanValueHolder decode(boolean call) {
		ContractInvocation invocation = ContractInvocationStub.take();
		return new BooleanValueHolder(invocation);
	}

	
	//----------------------- 内部类型 -----------------------
	


}
