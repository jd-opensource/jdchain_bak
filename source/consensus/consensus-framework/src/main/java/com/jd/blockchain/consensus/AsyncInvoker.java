package com.jd.blockchain.consensus;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.jd.blockchain.utils.concurrent.AsyncFuture;

public class AsyncInvoker {
	
	private static ThreadLocal<AsyncFuture<?>> resultHolder;
	
	static {
		resultHolder = new ThreadLocal<>();
	}
	

	@SuppressWarnings("unchecked")
	public static <T> T asynchorize(Class<T> serviceClazz, T serviceInstance) {
		if (serviceInstance instanceof AsyncService) {
			return (T) Proxy.newProxyInstance(serviceClazz.getClassLoader(), new Class<?>[] { serviceClazz },
					new AsyncInvocationHandle<T>(serviceClazz,  (AsyncService) serviceInstance));
		}
		throw new IllegalArgumentException("The specified service instance is not supported by this asynchronize util!");
	}
	
	@SuppressWarnings("unchecked")
	public static <T> AsyncFuture<T> call(T methodCall){
		AsyncFuture<T> result = (AsyncFuture<T>) resultHolder.get();
		resultHolder.set(null);
		return result;
	}
	
	

	private static class AsyncInvocationHandle<T> implements InvocationHandler {

		private Class<T> serviceClazz;
		private AsyncService serviceInstance;

		public AsyncInvocationHandle(Class<T> serviceClazz, AsyncService serviceInstance) {
			this.serviceInstance = serviceInstance;
			this.serviceClazz = serviceClazz;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			resultHolder.remove();
			if (method.getDeclaringClass() == serviceClazz) {
				//async invoke;
				AsyncFuture<Object> asyncResult = serviceInstance.invoke(method, args);
				resultHolder.set(asyncResult);
				return null;
			}
			return method.invoke(serviceInstance, args);
		}

	}
}
