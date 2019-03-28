package com.jd.blockchain.utils.event;

import java.lang.reflect.Method;

/**
 * ExceptionHandle 定义了在对事件监听器进行事件通知时出现的异常的处理接口；
 * 
 * @author haiq
 *
 * @param <TListener> TListener
 */
public interface ExceptionHandle<TListener> {

	/**
	 * 处理监听器异常；
	 * 
	 * @param ex
	 *            异常；
	 * @param listener
	 *            发生异常的监听器实例；
	 * @param method
	 *            发生异常的方法；
	 * @param args
	 *            方法参数；
	 */
	public void handle(Exception ex, TListener listener, Method method, Object[] args);
}
