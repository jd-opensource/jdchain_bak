package com.jd.blockchain.consensus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.BeanUtils;
import org.springframework.util.ClassUtils;

public class ConsensusProviders {

	private static final Object mutex = new Object();

	private static Map<String, ConsensusProvider> providers = new ConcurrentHashMap<>();

	public static ConsensusProvider getProvider(String className) {
		ConsensusProvider provider = providers.get(className);
		if (provider == null) {
			synchronized (mutex) {
				provider = providers.get(className);
				if (provider == null) {
					provider = loadProvider(ConsensusProvider.class, className);
//					providers.put(className, provider);
					registerProvider(provider);
				}
			}
		}
		return provider;
	}

	private static <T> T loadProvider(Class<T> assignableTo, String implementClassName) {
		Class<?> providerClass = ClassUtils.resolveClassName(implementClassName,
				ConsensusProviders.class.getClassLoader());
		if (!assignableTo.isAssignableFrom(providerClass)) {
			throw new IllegalArgumentException(
					String.format("%s is not implement %s!", implementClassName, assignableTo.getName()));
		}
		return BeanUtils.instantiateClass(providerClass, assignableTo);
	}

	public static void registerProvider(ConsensusProvider provider) {
		providers.put(provider.getName(), provider);
	}

}
