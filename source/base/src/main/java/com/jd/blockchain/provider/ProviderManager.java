package com.jd.blockchain.provider;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The ProviderManager manages all serivce providers in the system.
 * <p>
 * 
 * The service is represented by an interface, and the provider is
 * implementation class of the service.
 * <p>
 * 
 * One service can have multiple providers in the system.
 * <p>
 * 
 * A provider must have a name, and implementor can use the annotation
 * {@link NamedProvider} to specify a short name, otherwise the system defaults
 * to the full name of the implementation class.
 * 
 * 
 * @author huanghaiquan
 *
 */
public final class ProviderManager {

	private final Logger LOGGER = LoggerFactory.getLogger(ProviderManager.class);

	private final Object mutex = new Object();

	private ConcurrentHashMap<Class<?>, NamedProviders<?>> serviceProviders = new ConcurrentHashMap<>();

	/**
	 * 返回指定提供者的服务；
	 * 
	 * @param serviceClazz
	 * @param providerName
	 * @return
	 */
	public <S> S getService(Class<S> serviceClazz, String providerName) {
		NamedProviders<S> providers = getNamedProviders(serviceClazz);
		return providers.getService(providerName);
	}
	
	public <S> Provider<S> getProvider(Class<S> serviceClazz, String providerName) {
		@SuppressWarnings("unchecked")
		NamedProviders<S> providers = (NamedProviders<S>) serviceProviders.get(serviceClazz);
		if (providers == null) {
			return null;
		}
		return providers.getProvider(providerName);
	}

	public <S> Collection<Provider<S>> getAllProviders(Class<S> serviceClazz) {
		@SuppressWarnings("unchecked")
		NamedProviders<S> providers = (NamedProviders<S>) serviceProviders.get(serviceClazz);
		if (providers == null) {
			return Collections.emptyList();
		}
		return providers.getProviders();
	}

	public <S> S installProvider(Class<S> serviceClazz, String providerFullName) {
		NamedProviders<S> providers = getNamedProviders(serviceClazz);
		return providers.install(providerFullName);
	}

	public <S> S installProvider(Class<S> service, String providerFullName, ClassLoader classLoader) {
		NamedProviders<S> providers = getNamedProviders(service);
		return providers.install(providerFullName, classLoader);
	}

	public <S> void installAllProviders(Class<S> serviceClazz, ClassLoader classLoader) {
		NamedProviders<S> providers = getNamedProviders(serviceClazz);
		providers.installAll(classLoader);
	}

	@SuppressWarnings("unchecked")
	private <S> NamedProviders<S> getNamedProviders(Class<S> serviceClazz) {
		NamedProviders<S> providers = (NamedProviders<S>) serviceProviders.get(serviceClazz);
		if (providers == null) {
			synchronized (mutex) {
				providers = (NamedProviders<S>) serviceProviders.get(serviceClazz);
				if (providers == null) {
					providers = new NamedProviders<S>(serviceClazz);
					serviceProviders.put(serviceClazz, providers);
				}
			}
		}
		return providers;
	}

	/**
	 * @author huanghaiquan
	 *
	 * @param <T>
	 *            Type of Service
	 */
	private class NamedProviders<S> {

		private Class<S> serviceClazz;

		private Map<String, String> shortNames = new HashMap<>();
		private Map<String, Provider<S>> namedProviders = new LinkedHashMap<>();

		private AccessControlContext acc;

		public NamedProviders(Class<S> serviceClazz) {
			this.serviceClazz = serviceClazz;
			this.acc = (System.getSecurityManager() != null) ? AccessController.getContext() : null;
			installAll();
		}

		public void installAll(ClassLoader classLoader) {
			ServiceLoader<S> sl = ServiceLoader.load(serviceClazz, classLoader);
			installAll(sl);
		}

		public void installAll() {
			// 默认采用线程上下文的类加载器；避免直接采用系统的类加载器: ClassLoader.getSystemClassLoader() ;
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			installAll(classLoader);
		}

		private synchronized void installAll(ServiceLoader<S> sl) {
			for (S provider : sl) {
				install(provider);
			}
		}

		/**
		 * 安装指定的服务提供者；<br>
		 * 
		 * 如果同名的服务提供者已经存在（包括ShortName和FullName任意之一存在），则返回 false；<br>
		 * 
		 * 如果同名的服务提供者不存在，则返回 false；
		 * 
		 * @param service
		 *            提供者的服务实现；
		 * @return
		 */
		private synchronized boolean install(S service) {
			String fullName = service.getClass().getName();
			if (namedProviders.containsKey(fullName)) {
				LOGGER.warn(String.format("The provider[%s] already exists.", fullName));
				return false;
			}
			String shortName = null;
			NamedProvider annoNP = service.getClass().getAnnotation(NamedProvider.class);
			if (annoNP != null && annoNP.value() != null) {
				String n = annoNP.value().trim();
				if (n.length() > 0) {
					shortName = n;
				}
			}
			if (shortName != null && shortNames.containsKey(shortName)) {
				return false;
			}
			ProviderInfo<S> provider = new ProviderInfo<>(shortName, fullName, service);
			if (shortName != null) {
				shortNames.put(shortName, fullName);
			}
			namedProviders.put(fullName, provider);
			return true;
		}

		public S install(String providerFullName) {
			return install(providerFullName, null);
		}

		public S install(String providerFullName, ClassLoader classLoader) {
			// 默认采用线程上下文的类加载器；避免直接采用系统的类加载器: ClassLoader.getSystemClassLoader() ;
			ClassLoader cl = (classLoader == null) ? Thread.currentThread().getContextClassLoader() : classLoader;
			S p = null;
			if (acc == null) {
				p = instantiate(providerFullName, cl);
			} else {
				PrivilegedAction<S> action = new PrivilegedAction<S>() {
					public S run() {
						return instantiate(providerFullName, cl);
					}
				};
				p = AccessController.doPrivileged(action, acc);
			}
			if (!install(p)) {
				throw new ProviderException(
						"[" + serviceClazz.getName() + "] Provider " + providerFullName + " already exist!");
			}
			return p;
		}

		public Collection<Provider<S>> getProviders() {
			return namedProviders.values();
		}
		

		public Provider<S> getProvider(String providerFullName) {
			return namedProviders.get(providerFullName);
		}

		public S getService(String name) {
			String fullName = shortNames.get(name);
			if (fullName == null) {
				fullName = name;
			}
			Provider<S> pd = namedProviders.get(fullName);
			return pd == null ? null : pd.getService();
		}

		private S instantiate(String className, ClassLoader classLoader) {
			Class<?> c = null;
			try {
				c = Class.forName(className, false, classLoader);
			} catch (ClassNotFoundException x) {
				throw new ProviderException("[" + serviceClazz.getName() + "] Provider " + className + " not found");
			}
			if (!serviceClazz.isAssignableFrom(c)) {
				throw new ProviderException(
						"[" + serviceClazz.getName() + "] Provider " + className + " not a subtype");
			}
			try {
				S provider = serviceClazz.cast(c.newInstance());
				return provider;
			} catch (Throwable e) {
				throw new ProviderException("[" + serviceClazz.getName() + "] Provider " + className
						+ " could not be instantiated! --" + e.getMessage());
			}
		}
	}

	private static class ProviderInfo<S> implements Provider<S> {

		private final String shortName;

		private final String fullName;

		private final S service;

		public ProviderInfo(String shortName, String fullName, S service) {
			this.shortName = shortName;
			this.fullName = fullName;
			this.service = service;
		}

		@Override
		public String getShortName() {
			return shortName;
		}

		@Override
		public String getFullName() {
			return fullName;
		}

		@Override
		public S getService() {
			return service;
		}

	}
}
