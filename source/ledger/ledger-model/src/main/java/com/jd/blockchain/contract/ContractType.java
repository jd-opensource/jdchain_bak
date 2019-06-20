package com.jd.blockchain.contract;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.jd.blockchain.ledger.BytesValueEncoding;
import com.jd.blockchain.utils.IllegalDataException;

public class ContractType {

	private String name;

	private Map<String, Method> events = new HashMap<>();

	private Map<Method, String> handleMethods = new HashMap<>();

	public String getName() {
		return name;
	}

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

	/**
	 * 解析合约的声明；
	 * 
	 * @param contractIntf 合约的声明接口，必须是 interface ；
	 * @return
	 */
	public static ContractType resolve(Class<?> contractIntf) {
		// TODO：方法会检查合约方法声明的类型和返回值类型；
		// 如果是Class则首先获取其接口
		if (!contractIntf.isInterface()) {
			Class<?> realIntf = null;
			Class<?>[] interfaces = contractIntf.getInterfaces();
			for (Class<?> intf : interfaces) {
				if (intf.isAnnotationPresent(Contract.class)) {
					realIntf = intf;
					break;
				}
			}
			if (realIntf == null) {
				throw new IllegalDataException(String
						.format("%s is not a Contract Type, because there is not @Contract !", contractIntf.getName()));
			}
			contractIntf = realIntf;
		}

		// 接口上必须有注解
		Contract contract = contractIntf.getAnnotation(Contract.class);
		if (contract == null) {
			throw new IllegalDataException("It is not a Contract Type, because there is not @Contract !");
		}

		Method[] classMethods = contractIntf.getDeclaredMethods();

		if (classMethods.length == 0) {
			throw new IllegalDataException("This interface have not any methods !");
		}

		ContractType contractType = new ContractType();

		// 设置合约显示名字为
		contractType.name = contract.name();

		for (Method method : classMethods) {

			// if current method contains @ContractEvent，then put it in this map;
			ContractEvent contractEvent = method.getAnnotation(ContractEvent.class);

			if (contractEvent != null) {
				String eventName = contractEvent.name();
				// if annoMethodMap has contained the eventName, too many same eventNames exists
				// probably, say NO!
				if (contractType.events.containsKey(eventName)) {
					throw new ContractException("there is repeat definition of contractEvent to @ContractEvent.");
				}
				// check param's type is fit for need.
				Class<?>[] paramTypes = method.getParameterTypes();
				for (Class<?> currParamType : paramTypes) {
					if (!BytesValueEncoding.supportType(currParamType)) {
						throw new IllegalStateException(
								String.format("Param Type = %s can not support !!!", currParamType.getName()));
					}
				}

				// 判断返回值是否可序列化
				Class<?> returnType = method.getReturnType();
				if (!BytesValueEncoding.supportType(returnType)) {
					throw new IllegalStateException(
							String.format("Return Type = %s can not support !!!", returnType.getName()));
				}

				contractType.events.put(eventName, method);
				contractType.handleMethods.put(method, eventName);
			}
		}
		// 最起码有一个ContractEvent
		if (contractType.events.isEmpty()) {
			throw new IllegalStateException(
					String.format("Contract Interface[%s] have none method for annotation[@ContractEvent] !", contractIntf.getName()));
		}

		return contractType;
	}

	@Override
	public String toString() {
		return "ContractType{" + "name='" + name + '\'' + ", events=" + events + ", handleMethods=" + handleMethods
				+ '}';
	}
}
