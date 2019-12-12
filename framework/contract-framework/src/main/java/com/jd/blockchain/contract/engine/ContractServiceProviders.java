package com.jd.blockchain.contract.engine;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.BeanUtils;
import org.springframework.util.ClassUtils;

public class ContractServiceProviders {

	private static final Object mutex = new Object();
	private static Map<String, ContractServiceProvider> providers = new ConcurrentHashMap<>();

	public static ContractServiceProvider getProvider(String className) {
		ContractServiceProvider provider = providers.get(className);
		if (provider == null) {
			synchronized (mutex) {
				provider = providers.get(className);
				if (provider == null) {
					provider = loadProvider(ContractServiceProvider.class, className);
					providers.put(className, provider);
				}
			}
		}
		return provider;
	}

	private static <T> T loadProvider(Class<T> assignableTo, String implementClassName) {
		Class<?> providerClass = ClassUtils.resolveClassName(implementClassName,
				ContractServiceProvider.class.getClassLoader());
		if (!assignableTo.isAssignableFrom(providerClass)) {
			throw new IllegalArgumentException(
					String.format("%s is not implement %s!", implementClassName, assignableTo.getName()));
		}
		return BeanUtils.instantiateClass(providerClass, assignableTo);
	}

	public static void registerProvider(ContractServiceProvider provider) {
		providers.put(provider.getName(), provider);
	}
}
