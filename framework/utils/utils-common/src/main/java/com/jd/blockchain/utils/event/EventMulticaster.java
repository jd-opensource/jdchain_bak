package com.jd.blockchain.utils.event;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import com.jd.blockchain.utils.Disposable;

/**
 * 将针对一个特定的 listener 的方法调用事件广播到多个 listener对应的方法；
 * 
 * 只支持无返回值的，非 Object 类的方法；
 * 
 * @author haiq
 *
 * @param <TListener> TListener
 */
public class EventMulticaster<TListener> implements Disposable {

	private TListener listenerProxy;

	private Method[] supportedMethods;

	private List<TListener> listeners = new CopyOnWriteArrayList<TListener>();

	private ExceptionHandle<TListener> exHandle;

	public EventMulticaster(Class<TListener> listenerClass) {
		this(listenerClass, (ExceptionHandle<TListener>) null);
	}

	public EventMulticaster(Class<TListener> listenerClass, Logger errorLogger) {
		this(listenerClass, new RethrowExceptionHandler<TListener>(errorLogger));
	}

	@SuppressWarnings("unchecked")
	public EventMulticaster(Class<TListener> listenerClass, ExceptionHandle<TListener> exHandle) {
		if (!listenerClass.isInterface()) {
			throw new IllegalArgumentException("The specified class of listener does not represent an interface!");
		}
		// 初始化错误处理器；
		this.exHandle = exHandle == null
				? new RethrowExceptionHandler<TListener>(LoggerFactory.getLogger(EventMulticaster.class))
				: exHandle;

		// 解析出不支持的方法；
		Method[] methods = ReflectionUtils.getAllDeclaredMethods(listenerClass);
		List<Method> supMths = new ArrayList<Method>();
		for (Method method : methods) {
			if (method.getDeclaringClass() == Object.class) {
				// 不支持 Object 方法；
				continue;
			}
			if (method.getReturnType() != void.class) {
				// 不支持带返回值的方法；
				continue;
			}
			supMths.add(method);
		}
		supportedMethods = supMths.toArray(new Method[supMths.size()]);

		// 生成代理类；
		listenerProxy = (TListener) Proxy.newProxyInstance(listenerClass.getClassLoader(),
				new Class<?>[] { listenerClass }, new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						multicast(proxy, method, args);
						return null;
					}
				});
	}

	private void multicast(Object proxy, Method method, Object[] args) {
		boolean supported = false;
		for (Method mth : supportedMethods) {
			if (mth.equals(method)) {
				supported = true;
				break;
			}
		}
		if (supported) {
			doNotify(listeners, method, args);
		} else {
			// 调用了不支持的方法；
			throw new UnsupportedOperationException("Unsupported method for event multicasting!");
		}
	}

	protected void doNotify(List<TListener> listeners, Method method, Object[] args) {
		for (TListener listener : listeners) {
			doNotifySingle(listener, method, args);
		}
	}

	protected void doNotifySingle(TListener listener, Method method, Object[] args) {
		try {
			ReflectionUtils.invokeMethod(method, listener, args);
		} catch (Exception e) {
			exHandle.handle(e, listener, method, args);
		}
	}

	public void addListener(TListener listener) {
		listeners.add(listener);
	}

	public void removeListener(TListener listener) {
		listeners.remove(listener);
	}

	public TListener getBroadcaster() {
		return listenerProxy;
	}

	@Override
	public void dispose() {
		listeners.clear();
		listeners = null;
		listenerProxy = null;
		supportedMethods = null;
		exHandle = null;
	}

}
