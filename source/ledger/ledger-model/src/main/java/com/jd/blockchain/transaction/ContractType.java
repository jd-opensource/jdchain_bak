package com.jd.blockchain.transaction;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.SortedMap;

public class ContractType {

	private String name;

	private SortedMap<String, Method> events;

	private SortedMap<Method, String> handleMethods;

	/**
	 * 返回声明的所有事件；
	 * 
	 * @return
	 */
	public Set<String> getEvents() {
		return events.keySet();
	}

	/**
	 * 返回指定方法声明的事件；<br>
	 * 
	 * 如果不存在，则返回 null；
	 * 
	 * @param method
	 * @return
	 */
	public String getEvent(Method method) {
		return handleMethods.get(method);
	}

	/**
	 * 返回事件的处理方法；<br>
	 * 
	 * 如果不存在，则返回 null；
	 * 
	 * @param event
	 * @return
	 */
	public Method getHandleMethod(String event) {
		return events.get(event);
	}

	private ContractType() {
	}

	// public static ContractType resolve(Class<?> contractIntf) {
	//
	// }

}
