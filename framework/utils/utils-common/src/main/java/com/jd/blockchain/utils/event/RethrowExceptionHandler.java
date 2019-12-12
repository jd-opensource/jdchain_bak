package com.jd.blockchain.utils.event;

import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;

import org.slf4j.Logger;

public class RethrowExceptionHandler<TListener> extends ExceptionLoggingHandle<TListener> {

	public RethrowExceptionHandler(Logger logger) {
		super(logger);
	}

	@Override
	public void handle(Exception ex, TListener listener, Method method, Object[] args) {
		super.handle(ex, listener, method, args);
		if (ex instanceof RuntimeException) {
			throw (RuntimeException)ex;
		}
		throw new UndeclaredThrowableException(ex);
	}

}
