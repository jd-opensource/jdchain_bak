package com.jd.blockchain.utils.event;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultExceptionHandle<TListener> implements ExceptionHandle<TListener> {

	@Override
	public void handle(Exception ex, TListener listener, Method method, Object[] args) {
		String argsValue = null;
		if (args!=null) {
			StringBuilder argsFormat = new StringBuilder();
			for (int i = 0; i < args.length; i++) {
				argsFormat.append("arg[");
				argsFormat.append(i);
				argsFormat.append("]=%s;");
			}
			argsValue = String.format(argsFormat.toString(), args);
		}
		String message = String.format("Error occurred while firing event!--[listener.class=%s][method=%s][args=%s]--[%s]%s",
				listener.getClass().getName(), method.getName(), argsValue, ex.getClass().getName(), ex.getMessage());
		logger.error(message, ex);
	}
	private Logger logger = LoggerFactory.getLogger(DefaultExceptionHandle.class);
}
