package com.jd.blockchain.utils.concurrent;
//package my.utils.concurrent;
//
///**
// * 用于适配同步操作的 AsyncFuture 实现；
// * 
// * @author haiq
// *
// * @param <TSource>
// */
//public class SyncFutureAdaptor<TSource> implements AsyncFuture<TSource> {
//
//	private TSource source;
//
//	private boolean success = false;
//	private Throwable exception;
//	private String errorCode;
//
//	/**
//	 * 创建 SyncFutureAdaptor 实例；
//	 * 
//	 * @param source
//	 *            操作对象；
//	 * @param exception
//	 *            操作完成后引发的异常；如果指定为 null 则表示操作成功返回而没有引发异常；
//	 * @param errorCode 错误码
//	 */
//	private SyncFutureAdaptor(TSource source, Throwable exception, String errorCode) {
//		this.source = source;
//		this.success = exception == null;
//		this.exception = exception;
//		this.errorCode = errorCode;
//	}
//	
//	/**
//	 * 创建表示成功完成操作的 AsyncFuture 实例；
//	 * 
//	 * @param source
//	 *            执行操作的对象；
//	 * @return AsyncFuture 实例；
//	 */
//	public static <T> AsyncFuture<T> createSuccessFuture(T source) {
//		return new SyncFutureAdaptor<T>(source, null, null);
//	}
//
//	/**
//	 * 创建表示操作引发异常返回的 AsyncFuture 实例；
//	 * 
//	 * @param source
//	 *            执行操作的对象；
//	 * @param exception
//	 *            操作引发的异常；不允许为 null;
//	 * @return AsyncFuture 实例；
//	 */
//	public static <T> AsyncFuture<T> createErrorFuture(T source, Throwable exception) {
//		if (exception == null) {
//			throw new IllegalArgumentException("Exception is null!");
//		}
//		return new SyncFutureAdaptor<T>(source, exception, null);
//	}
//	
//	/**
//	 * 创建表示操作引发异常返回的 AsyncFuture 实例；
//	 * 
//	 * @param source
//	 *            执行操作的对象；
//	 * @param errorCode
//	 *            操作引发的错误代码;
//	 * @return AsyncFuture 实例；
//	 */
//	public static <T> AsyncFuture<T> createErrorFuture(T source, String errorCode) {
//		if (errorCode == null || errorCode.length() == 0) {
//			throw new IllegalArgumentException("ErrorCode is empty!");
//		}
//		return new SyncFutureAdaptor<T>(source, null, errorCode);
//	}
//
//	@Override
//	public TSource get() {
//		return source;
//	}
//
//	@Override
//	public boolean isDone() {
//		return true;
//	}
//
//	@Override
//	public boolean isSuccess() {
//		return success;
//	}
//
//	@Override
//	public Throwable getException() {
//		return exception;
//	}
//	
//	// @Override
//	// public void addListener(AsyncFutureListener<AsyncFuture<TSource>>
//	// listener) {
//	//
//	// }
//
//	@Override
//	public void await() throws InterruptedException {
//		return;
//	}
//
//	@Override
//	public boolean await(long timeoutMillis) throws InterruptedException {
//		return true;
//	}
//
//	@Override
//	public void awaitUninterruptibly() {
//		return;
//	}
//
//	@Override
//	public boolean awaitUninterruptibly(long timeoutMillis) {
//		return true;
//	}
//
//	@Override
//	public void addListener(AsyncFutureListener<TSource> listener) {
//		// 同步操作已经完成；
//		listener.complete(this);
//	}
//
//	@Override
//	public String getErrorCode() {
//		return this.errorCode;
//	}
//
//}
