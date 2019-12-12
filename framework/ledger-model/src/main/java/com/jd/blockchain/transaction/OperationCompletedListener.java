package com.jd.blockchain.transaction;

/**
 * 操作完成监听器；
 * 
 * @author huanghaiquan
 *
 */
public interface OperationCompletedListener {

	/**
	 * 当操作完成时发生；
	 * 
	 * @param retnValue 返回值；
	 * @param error     异常；如果值为非空，则表示由异常导致结束；
	 * @param context   上下文对象；
	 */
	void onCompleted(Object retnValue, Throwable error, OperationCompletedContext context);

}
