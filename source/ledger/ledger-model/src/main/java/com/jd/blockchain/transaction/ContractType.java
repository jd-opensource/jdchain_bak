package com.jd.blockchain.transaction;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jd.blockchain.contract.Contract;
import com.jd.blockchain.contract.ContractEvent;
import com.jd.blockchain.contract.ContractException;
import com.jd.blockchain.utils.IllegalDataException;

public class ContractType {

	private String name;
	private Map<String, Method> events = new HashMap<>();
	private Map<Method, String> handleMethods = new HashMap<>();
//	private Map<Method, List<DataContract>> dataContractMap = new HashMap<>();

	/**
	 * 返回声明的所有事件；
	 * 
	 * @return
	 */
	public Set<String> getEvents() {
		return events.keySet();
	}

//	public Map<Method, List<DataContract>> getDataContractMap() {
//		return dataContractMap;
//	}

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

	public static ContractType resolve(Class<?> contractIntf){
		ContractType contractType = new ContractType();

		Annotation annotation = contractIntf.getDeclaredAnnotation(Contract.class);

		//contains: @Contract?
		boolean isContractType = annotation != null ? true : false;
		if(!isContractType){
			throw new IllegalDataException("is not Contract Type, becaust there is not @Contract.");
		}

		//contractIntf contains @Contract and @ContractEvent;
		Method[] classMethods = contractIntf.getDeclaredMethods();
		for (Method method : classMethods) {
			// if current method contains @ContractEvent，then put it in this map;
			ContractEvent contractEvent = method.getAnnotation(ContractEvent.class);
			if (contractEvent != null) {
				String eventName_ = contractEvent.name();
				//if annoMethodMap has contained the eventName, too many same eventNames exists probably, say NO!
				if(contractType.events.containsKey(eventName_)){
					throw new ContractException("there is repeat definition of contractEvent to @ContractEvent.");
				}
				//check param's type is fit for need.
				Class<?>[] paramTypes = method.getParameterTypes();
				List dataContractList = new ArrayList();
				for(Class<?> curParamType : paramTypes){
					throw new IllegalStateException("Not implemented!");
//					DataContract dataContract = ContractSerializeUtils.parseDataContract(curParamType);
//					dataContractList.add(dataContract);
				}
//				if(dataContractList.size()>0){
//					contractType.dataContractMap.put(method,dataContractList);
//				}

				contractType.events.put(eventName_, method);
				contractType.handleMethods.put(method,eventName_);
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
