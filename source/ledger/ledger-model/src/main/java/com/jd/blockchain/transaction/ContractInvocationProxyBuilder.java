package com.jd.blockchain.transaction;

import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.jd.blockchain.contract.EventResult;
import com.jd.blockchain.contract.ContractType;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.IllegalDataException;

/**
 * 合约调用代理的构建器；
 * 
 * @author huanghaiquan
 *
 */
public class ContractInvocationProxyBuilder {

	private Map<Class<?>, ContractType> contractTypes = new ConcurrentHashMap<>();

//	private Map<Object, Integer> contractOperations = new ConcurrentHashMap<>();

	public <T> T create(String address, Class<T> contractIntf, ContractEventSendOperationBuilder contractEventBuilder) {
		return create(Bytes.fromBase58(address), contractIntf, contractEventBuilder);
	}

	@SuppressWarnings("unchecked")
	public <T> T create(Bytes address, Class<T> contractIntf, ContractEventSendOperationBuilder contractEventBuilder) {
		ContractType contractType = resolveContractType(contractIntf);

		ContractInvocationHandler proxyHandler = new ContractInvocationHandler(address, contractType, contractEventBuilder);

		T proxy = (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
				new Class<?>[] { contractIntf }, proxyHandler);
//		// 创建关联关系
//		contractOperations.put(proxy, proxyHandler.opIndex());
		return proxy;
	}

//	public <T> EventResult<T> execute(ContractEventExecutor execute) {
//		Object contractProxy = execute.execute();
//		if (contractProxy == null) {
//			// 该方法执行必须要有返回值
//			throw new IllegalStateException(
//					String.format("ContractEventExecutor [%s] 's return must be not empty !!!", execute.toString()));
//		}
//		if (!(contractProxy instanceof Proxy)) {
//			throw new IllegalDataException(
//					String.format("ContractEventExecutor [%s] 's return must from TxTemplate.contract()'s result !!!",
//							execute.toString()));
//		}
//
//		Integer opIndex = contractOperations.get(contractProxy);
//		if (opIndex != null && opIndex > -1) {
//			return new EventResult<>(opIndex);
//		}
//		return null;
//	}

	private ContractType resolveContractType(Class<?> contractIntf) {
		ContractType contractType = contractTypes.get(contractIntf);
		if (contractType != null) {
			return contractType;
		}
		ContractType ct = ContractType.resolve(contractIntf);
		contractTypes.put(contractIntf, ct);
		return ct;
	}
}
