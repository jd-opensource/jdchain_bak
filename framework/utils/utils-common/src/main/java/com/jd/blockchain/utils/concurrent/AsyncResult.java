package com.jd.blockchain.utils.concurrent;
//package my.utils.concurrent;
//
//public interface AsyncResult<T> {
//
//	public T getResult();
//
//	/**
//	 * 操作是否已完成；
//	 * 
//	 * 当操作成功返回或者异常返回时，都表示为已完成；
//	 * 
//	 * @return
//	 */
//	public boolean isDone();
//
//	/**
//	 * 操作是否已成功；
//	 * 
//	 * @return
//	 */
//	public boolean isSuccess();
//
//	public String getErrorCode();
//
//	/**
//	 * 返回操作异常；
//	 * 
//	 * 当未完成(isDone方法返回false)或操作正常结束时，返回 null；
//	 * 
//	 * @return
//	 */
//	public Throwable getException();
//
//	/**
//	 * 等待异步操作完成后返回；
//	 * 
//	 * 等待过程不触发中断；
//	 */
//	public void awaitUninterruptibly();
//
//	/**
//	 * 等待异步操作完成后返回；
//	 * 
//	 * 等待过程不触发中断；
//	 * 
//	 * @param timeoutMillis
//	 *            超时毫秒数；
//	 * @return true 表示操作已完成； false 表示超时返回；
//	 */
//	public boolean awaitUninterruptibly(long timeoutMillis);
//
//	/**
//	 * 注册监听器；
//	 * 
//	 * @param listener
//	 */
//	public void addListener(AsyncCallback<T> callback);
//
//}