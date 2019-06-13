package com.jd.blockchain.runtime;

import java.io.InputStream;
import java.util.concurrent.Callable;

import com.jd.blockchain.utils.concurrent.AsyncFuture;

public interface Module {

	String getName();

	Class<?> loadClass(String className);

	InputStream loadResourceAsStream(String name);

	String getMainClass();

//	Module getParent();

	/**
	 * Run in this module's ClassLoader context;
	 * 
	 * @param runnable
	 */
	void execute(Runnable runnable);

	/**
	 * Run asynchronize in this module's ClassLoader context;
	 * 
	 * @param runnable
	 * @return
	 */
	AsyncFuture<Void> executeAsync(Runnable runnable);

	<V> V call(Callable<V> callable);

	<V> AsyncFuture<V> callAsync(Callable<V> callable);

}