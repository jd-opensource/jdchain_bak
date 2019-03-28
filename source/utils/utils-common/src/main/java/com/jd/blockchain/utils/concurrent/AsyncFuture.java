package com.jd.blockchain.utils.concurrent;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * 提供对异步操作的结果描述；
 * @param <V> class
 */
public interface AsyncFuture<V> {

	/**
	 * 返回异步操作的结果；<br>
	 * 
	 * 注：此方法将堵塞当前线程直至异步操作完成并返回结果；
	 * 
	 * @return v
	 */
	V get();

	V get(long timeout, TimeUnit unit);

	/**
	 * 操作是否已完成；
	 * 
	 * 当操作成功返回或者异常返回时，都表示为已完成；
	 * 
	 * @return boolean
	 */
	boolean isDone();

	/**
	 * 操作是否已成功；
	 * 
	 * @return boolean
	 */
	boolean isExceptionally();

	public AsyncFuture<V> thenAccept(Consumer<? super V> action);

	public AsyncFuture<V> thenAcceptAsync(Consumer<? super V> action);

	public AsyncFuture<V> thenAcceptAsync(Consumer<? super V> action, Executor executor);

	public AsyncFuture<V> thenRun(Runnable action);

	public AsyncFuture<V> thenRunAsync(Runnable action);

	public AsyncFuture<V> thenRunAsync(Runnable action, Executor executor);

	public AsyncFuture<V> whenComplete(AsyncHandle<? super V> action);

	public AsyncFuture<V> whenCompleteAsync(AsyncHandle<? super V> action);

	public AsyncFuture<V> whenCompleteAsync(AsyncHandle<? super V> action, Executor executor);

}
