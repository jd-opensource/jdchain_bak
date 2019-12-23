package com.jd.blockchain.utils.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class CompletableAsyncFuture<T> implements AsyncFuture<T> {

	private CompletableFuture<T> cf;

	public CompletableAsyncFuture() {
		this.cf = new CompletableFuture<T>();
	}

	private CompletableAsyncFuture(CompletableFuture<T> cf) {
		this.cf = cf;
	}

	public static <T> CompletableAsyncFuture<T> completeFuture(T value) {
		CompletableFuture<T> cf = CompletableFuture.completedFuture(value);
		return new CompletableAsyncFuture<>(cf);
	}
	
	public static CompletableAsyncFuture<Void> runAsync(Runnable runnable){
		CompletableFuture<Void> cf = CompletableFuture.runAsync(runnable);
		return new CompletableAsyncFuture<>(cf);
	}
	
	public static CompletableAsyncFuture<Void> runAsync(Runnable runnable, Executor executor){
		CompletableFuture<Void> cf = CompletableFuture.runAsync(runnable, executor);
		return new CompletableAsyncFuture<>(cf);
	}
	
	public static <T> CompletableAsyncFuture<T> callAsync(Callable<T> callable){
		CompletableFuture<T> cf = CompletableFuture.supplyAsync(new Supplier<T>() {
			@Override
			public T get() {
				try {
					return callable.call();
				} catch (Exception e) {
					throw new RuntimeExecutionException(e.getMessage(), e);
				}
			}
		});
		return new CompletableAsyncFuture<>(cf);
	}
	
	public static <T> CompletableAsyncFuture<T> callAsync(Callable<T> callable, Executor executor){
		CompletableFuture<T> cf = CompletableFuture.supplyAsync(new Supplier<T>() {
			@Override
			public T get() {
				try {
					return callable.call();
				} catch (Exception e) {
					throw new RuntimeExecutionException(e.getMessage(), e);
				}
			}
		}, executor);
		return new CompletableAsyncFuture<>(cf);
	}
	
	public static <T> CompletableAsyncFuture<T> callAsync(Supplier<T> supplier){
		CompletableFuture<T> cf = CompletableFuture.supplyAsync(supplier);
		return new CompletableAsyncFuture<>(cf);
	}
	
	public static <T> CompletableAsyncFuture<T> callAsync(Supplier<T> supplier, Executor executor){
		CompletableFuture<T> cf = CompletableFuture.supplyAsync(supplier, executor);
		return new CompletableAsyncFuture<>(cf);
	}

	/**
	 * 如果尚未完成，则设置 {@link #get()} 方法的返回值，并置为已完成状态；
	 * 
	 * @param value
	 *            正常返回的结果；
	 * @return true - 表示此操作使得状态从“未完成”状态转为“已完成”状态；
	 */
	public boolean complete(T value) {
		return cf.complete(value);
	}
	
	public boolean error(Throwable ex) {
		return cf.completeExceptionally(ex);
	}

	@Override
	public T get() {
		try {
			return cf.get();
		} catch (InterruptedException e) {
			throw new RuntimeInterruptedException(e.getMessage(), e);
		} catch (ExecutionException e) {
			Throwable cause = e.getCause();
			if (cause == null) {
				cause = e;
			}
			throw new RuntimeExecutionException(cause.getMessage(), cause);
		}
	}

	@Override
	public T get(long timeout, TimeUnit unit) {
		try {
			return cf.get(timeout, unit);
		} catch (InterruptedException e) {
			throw new RuntimeInterruptedException(e.getMessage(), e);
		} catch (ExecutionException e) {
			Throwable cause = e.getCause();
			if (cause == null) {
				cause = e;
			}
			throw new RuntimeExecutionException(cause.getMessage(), cause);
		} catch (TimeoutException e) {
			throw new RuntimeTimeoutException(e.getMessage(), e);
		}
	}

	@Override
	public boolean isDone() {
		return cf.isDone();
	}

	@Override
	public boolean isExceptionally() {
		return cf.isCompletedExceptionally();
	}

	@Override
	public AsyncFuture<T> thenAccept(Consumer<? super T> action) {
		cf.thenAccept(action);
		return this;
	}

	@Override
	public AsyncFuture<T> thenAcceptAsync(Consumer<? super T> action) {
		cf.thenAcceptAsync(action);
		return this;
	}

	@Override
	public AsyncFuture<T> thenAcceptAsync(Consumer<? super T> action, Executor executor) {
		cf.thenAcceptAsync(action, executor);
		return this;
	}

	@Override
	public AsyncFuture<T> thenRun(Runnable action) {
		cf.thenRun(action);
		return this;
	}

	@Override
	public AsyncFuture<T> thenRunAsync(Runnable action) {
		cf.thenRunAsync(action);
		return this;
	}

	@Override
	public AsyncFuture<T> thenRunAsync(Runnable action, Executor executor) {
		cf.thenRunAsync(action, executor);
		return this;
	}

	@Override
	public AsyncFuture<T> whenComplete(AsyncHandle<? super T> action) {
		cf.whenComplete(action);
		return this;
	}

	@Override
	public AsyncFuture<T> whenCompleteAsync(AsyncHandle<? super T> action) {
		cf.whenCompleteAsync(action);
		return this;
	}

	@Override
	public AsyncFuture<T> whenCompleteAsync(AsyncHandle<? super T> action, Executor executor) {
		cf.whenCompleteAsync(action, executor);
		cf.whenCompleteAsync((a, b) -> action.accept(a, b), executor);
		return this;
	}

}
