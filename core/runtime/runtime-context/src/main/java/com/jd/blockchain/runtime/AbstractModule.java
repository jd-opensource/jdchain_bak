package com.jd.blockchain.runtime;

import java.io.InputStream;
import java.util.concurrent.Callable;

import com.jd.blockchain.utils.concurrent.AsyncFuture;
import com.jd.blockchain.utils.concurrent.CompletableAsyncFuture;

public abstract class AbstractModule implements Module {
	
	
	protected abstract ClassLoader getModuleClassLoader();

	@Override
	public String getMainClass(){return null;}

	@Override
	public Class<?> loadClass(String className) {
		try {
			return getModuleClassLoader().loadClass(className);
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}
	
	public InputStream loadResourceAsStream(String name) {
		return getModuleClassLoader().getResourceAsStream(name);
	}

	@Override
	public void execute(Runnable runnable) {
		ClassLoader origClassLoader = Thread.currentThread().getContextClassLoader();
		ClassLoader moduleClassLoader = getModuleClassLoader();
		if (origClassLoader != moduleClassLoader) {
			Thread.currentThread().setContextClassLoader(moduleClassLoader);
		}
		try {
			runnable.run();
		} finally {
			if (origClassLoader != Thread.currentThread().getContextClassLoader()) {
				Thread.currentThread().setContextClassLoader(origClassLoader);
			}
		}
	}

	@Override
	public AsyncFuture<Void> executeAsync(Runnable runnable) {
		ClassLoader origClassLoader = Thread.currentThread().getContextClassLoader();
		ClassLoader moduleClassLoader = getModuleClassLoader();
		if (origClassLoader != moduleClassLoader) {
			Thread.currentThread().setContextClassLoader(moduleClassLoader);
		}
		return CompletableAsyncFuture.runAsync(() -> {
			try {
				runnable.run();
			} finally {
				if (origClassLoader != Thread.currentThread().getContextClassLoader()) {
					Thread.currentThread().setContextClassLoader(origClassLoader);
				}
			}
		});

	}

	@Override
	public <V> AsyncFuture<V> callAsync(Callable<V> callable) {
		ClassLoader origClassLoader = Thread.currentThread().getContextClassLoader();
		ClassLoader moduleClassLoader = getModuleClassLoader();
		if (origClassLoader != moduleClassLoader) {
			Thread.currentThread().setContextClassLoader(moduleClassLoader);
		}
		return CompletableAsyncFuture.callAsync(new Callable<V>() {
			@Override
			public V call() throws Exception {
				try {
					return callable.call();
				} finally {
					if (origClassLoader != Thread.currentThread().getContextClassLoader()) {
						Thread.currentThread().setContextClassLoader(origClassLoader);
					}
				}
			}
		});
	}

	@Override
	public <V> V call(Callable<V> callable) {
		ClassLoader origClassLoader = Thread.currentThread().getContextClassLoader();
		ClassLoader moduleClassLoader = getModuleClassLoader();
		if (origClassLoader != moduleClassLoader) {
			Thread.currentThread().setContextClassLoader(moduleClassLoader);
		}
		try {
			return callable.call();
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		} finally {
			if (origClassLoader != Thread.currentThread().getContextClassLoader()) {
				Thread.currentThread().setContextClassLoader(origClassLoader);
			}
		}
	}

}