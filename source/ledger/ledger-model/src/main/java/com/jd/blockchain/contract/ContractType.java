package com.jd.blockchain.contract;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.jd.blockchain.contract.ContractSerializeUtils;
import com.jd.blockchain.utils.IllegalDataException;

public class ContractType {

	private String name;
	private Map<String, Method> events = new HashMap<>();
	private Map<Method, String> handleMethods = new HashMap<>();

	private Class<?> declaredClass;

	public String getName() {
		return name;
	}

	public Class<?> getDeclaredClass() {
		return declaredClass;
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
	 * @param delaredInterface 声明合约的接口类型；
	 * @return
	 */
	public static ContractType resolve(Class<?> contractIntf) {

		// 如果是Class则首先获取其接口
		if (!contractIntf.isInterface()) {
			Class<?> realIntf = null;
			Class<?>[] interfaces = contractIntf.getInterfaces();
			for (Class<?> intf: interfaces) {
				if (intf.isAnnotationPresent(Contract.class)) {
					realIntf = intf;
					break;
				}
			}
			if (realIntf == null) {
				throw new IllegalDataException(String.format(
						"%s is not a Contract Type, because there is not @Contract !", contractIntf.getName()));
			}
			contractIntf = realIntf;
		}

		// 接口上必须有注解
		if (!contractIntf.isAnnotationPresent(Contract.class)) {
			throw new IllegalDataException("It is not a Contract Type, because there is not @Contract !");
		}

		Method[] classMethods = contractIntf.getDeclaredMethods();

		if (classMethods.length == 0) {
			throw new IllegalDataException("This interface have not any methods !");
		}

		ContractType contractType = new ContractType();

		for (Method method : classMethods) {

			// if current method contains @ContractEvent，then put it in this map;
			ContractEvent contractEvent = method.getAnnotation(ContractEvent.class);

			if (contractEvent != null) {
				String eventName = contractEvent.name();
				//if annoMethodMap has contained the eventName, too many same eventNames exists probably, say NO!
				if(contractType.events.containsKey(eventName)){
					throw new ContractException("there is repeat definition of contractEvent to @ContractEvent.");
				}
				//check param's type is fit for need.
				Class<?>[] paramTypes = method.getParameterTypes();
				for(Class<?> currParamType : paramTypes) {
					if (!ContractSerializeUtils.support(currParamType)) {
						throw new IllegalStateException(String.format("Param Type = %s can not support !!!", currParamType.getName()));
					}
				}

				// 判断返回值是否可序列化
				Class<?> returnType = method.getReturnType();
				if (!ContractSerializeUtils.support(returnType)) {
					throw new IllegalStateException(String.format("Return Type = %s can not support !!!", returnType.getName()));
				}

				contractType.events.put(eventName, method);
				contractType.handleMethods.put(method, eventName);
			}
		}
		return contractType;
	}

	@Override
	public String toString() {
		return "ContractType{" +
				"name='" + name + '\'' +
				", events=" + events +
				", handleMethods=" + handleMethods +
				'}';
	}
}
