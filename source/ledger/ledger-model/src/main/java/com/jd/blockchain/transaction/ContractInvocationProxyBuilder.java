package com.jd.blockchain.transaction;

import java.lang.reflect.Proxy;
import java.util.Map;

import com.jd.blockchain.utils.Bytes;

public class ContractInvocationProxyBuilder {

	private Map<Class<?>, ContractType> contractTypes;

	public <T> T create(String address, Class<T> contractIntf, BlockchainOperationFactory opFactory) {
		return create(Bytes.fromBase58(address), contractIntf, opFactory);
	}

	@SuppressWarnings("unchecked")
	public <T> T create(Bytes address, Class<T> contractIntf, BlockchainOperationFactory opFactory) {
		ContractType contractType = resolveContractType(contractIntf);

		ContractInvocationProxy proxyHandler = new ContractInvocationProxy(address, contractType,
				opFactory.contractEvents());
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

		// 解析合约事件处理方法，检查是否有重名；

		// TODO 检查是否不支持的参数类型；

		// TODO 检查返回值类型；

		return null;
	}

}
