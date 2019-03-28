package com.jd.blockchain.utils.event;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jd.blockchain.utils.console.CommandConsole;

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
		String message = String.format("Error occurred on firing event!--[listener.class=%s][method=%s][args=%s]--[%s]%s",
				listener.getClass().getName(), method.getName(), argsValue, ex.getClass().getName(), ex.getMessage());
//		System.err.println(message);
		logger.error(message, ex);
	}
	private Logger logger = LoggerFactory.getLogger(DefaultExceptionHandle.class);
}
