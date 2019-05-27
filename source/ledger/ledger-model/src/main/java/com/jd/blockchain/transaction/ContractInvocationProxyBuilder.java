package com.jd.blockchain.transaction;

import com.jd.blockchain.contract.Contract;
import com.jd.blockchain.contract.ContractEvent;
import com.jd.blockchain.utils.BaseConstant;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.IllegalDataException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ContractInvocationProxyBuilder {

	private Map<Class<?>, ContractType> contractTypes = new ConcurrentHashMap<>();

	public <T> T create(String address, Class<T> contractIntf, ContractEventSendOperationBuilder contractEventBuilder) {
		return create(Bytes.fromBase58(address), contractIntf, contractEventBuilder);
	}

	@SuppressWarnings("unchecked")
	public <T> T create(Bytes address, Class<T> contractIntf, ContractEventSendOperationBuilder contractEventBuilder) {
		ContractType contractType = resolveContractType(contractIntf);

		ContractInvocationProxy proxyHandler = new ContractInvocationProxy(address, contractType,
				contractEventBuilder);
		T proxy = (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
				new Class<?>[] { contractIntf }, proxyHandler);

		return (T) proxy;
	}

	private ContractType resolveContractType(Class<?> contractIntf) {
		ContractType contractType = contractTypes.get(contractIntf);
		if (contractType != null) {
			return contractType;
		}
		
		// 判断是否是标注了合约的接口类型；
		if (!isContractType(contractIntf)){
			throw new IllegalDataException("is not Contract Type, becaust there is not @Contract.");
		}

		// 解析合约事件处理方法，检查是否有重名；
		if(!isUniqueEvent(contractIntf)){
			throw new IllegalDataException("there is repeat definition of contractEvent to @ContractEvent.");
		}

		// TODO 检查是否不支持的参数类型；

		// TODO 检查返回值类型；

		ContractType contractType1 =  ContractType.resolve(contractIntf);
		contractTypes.put(contractIntf,contractType1);
		return contractType1;
	}


	private boolean isUniqueEvent(Class<?> contractIntf) {
		boolean isUnique = true;
		Method[] classMethods = contractIntf.getMethods();
		Map<Method, Annotation[]> methodAnnoMap = new HashMap<Method, Annotation[]>();
		Map<String, Method> annoMethodMap = new HashMap<String, Method>();
		for (int i = 0; i < classMethods.length; i++) {
			Annotation[] annotations = classMethods[i].getDeclaredAnnotations();
			methodAnnoMap.put(classMethods[i], annotations);
			// if current method contains @ContractEvent，then put it in this map;
			Method curMethod = classMethods[i];
			ContractEvent contractEvent = curMethod.getAnnotation(ContractEvent.class);
			if (contractEvent != null) {
				Object obj = classMethods[i].getAnnotation(ContractEvent.class);
				String annoAllName = obj.toString();
				// format:@com.jd.blockchain.contract.model.ContractEvent(name=transfer-asset)
				String eventName_ = contractEvent.name();
				//if annoMethodMap has contained the eventName, too many same eventNames exists probably, say NO!
				if(annoMethodMap.containsKey(eventName_)){
					isUnique = false;
				}
				annoMethodMap.put(eventName_, classMethods[i]);
			}
		}

		return isUnique;
	}

	/**
	 * is contractType really?  identified by @Contract;
	 * @param contractIntf
	 * @return
	 */
	private boolean isContractType(Class<?> contractIntf) {
		Annotation annotation = contractIntf.getDeclaredAnnotation(Contract.class);
		return annotation != null ? true : false;
	}
}
