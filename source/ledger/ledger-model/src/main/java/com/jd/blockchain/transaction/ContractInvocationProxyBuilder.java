package com.jd.blockchain.transaction;

import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.jd.blockchain.contract.Contract;
import com.jd.blockchain.contract.ContractType;
import com.jd.blockchain.utils.Bytes;

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
		// TODO 检查返回值类型；

		ContractType contractType1 =  ContractType.resolve(contractIntf);
		contractTypes.put(contractIntf,contractType1);
		return contractType1;
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
