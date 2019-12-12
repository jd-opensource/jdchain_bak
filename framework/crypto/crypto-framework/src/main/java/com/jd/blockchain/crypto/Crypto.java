package com.jd.blockchain.crypto;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jd.blockchain.provider.Provider;
import com.jd.blockchain.provider.ProviderManager;

/**
 * 密码服务提供者的管理器；
 * 
 * @author huanghaiquan
 *
 */
public final class Crypto {

	private static Logger LOGGER = LoggerFactory.getLogger(Crypto.class);

	private static Map<Short, CryptoFunction> functions = new ConcurrentHashMap<>();

	private static Map<Short, CryptoAlgorithm> algorithms = new ConcurrentHashMap<>();

	private static Map<String, Short> names = new ConcurrentHashMap<>();

	private static ProviderManager pm = new ProviderManager();

	static {
		loadDefaultProviders();
	}

	private static void loadDefaultProviders() {
		ClassLoader cl = Crypto.class.getClassLoader();
		pm.installAllProviders(CryptoService.class, cl);

		Iterable<Provider<CryptoService>> providers = pm.getAllProviders(CryptoService.class);
		for (Provider<CryptoService> provider : providers) {
			register(provider);
		}
	}

	private static void register(Provider<CryptoService> provider) {
		for (CryptoFunction cryptoFunction : provider.getService().getFunctions()) {

			String name = cryptoFunction.getAlgorithm().name().toUpperCase();
			short code = cryptoFunction.getAlgorithm().code();
			CryptoAlgorithm algorithm = new CryptoAlgorithmDefinition(name, code);
			if (CryptoAlgorithm.isRandomAlgorithm(algorithm) && !(cryptoFunction instanceof RandomFunction)) {
				LOGGER.error(String.format(
						"The random algorithm \"%s\" declared by provider[%s] does not implement the interface \"%s\"!",
						algorithm.toString(), provider.getFullName(), RandomFunction.class.getName()));
				continue;
			}
			if (CryptoAlgorithm.isAsymmetricEncryptionAlgorithm(algorithm)
					&& !(cryptoFunction instanceof AsymmetricEncryptionFunction)) {
				LOGGER.error(String.format(
						"The asymmetric encryption algorithm \"%s\" declared by the provider[%s] does not implement the interface \"%s\"!",
						algorithm.toString(), provider.getFullName(), AsymmetricEncryptionFunction.class.getName()));
				continue;
			}
			if (CryptoAlgorithm.isSignatureAlgorithm(algorithm) && !(cryptoFunction instanceof SignatureFunction)) {
				LOGGER.error(String.format(
						"The signature algorithm \"%s\" declared by the provider[%s] does not implement the interface \"%s\"!",
						algorithm.toString(), provider.getFullName(), SignatureFunction.class.getName()));
				continue;
			}
			if (CryptoAlgorithm.isSymmetricEncryptionAlgorithm(algorithm)
					&& !(cryptoFunction instanceof SymmetricEncryptionFunction)) {
				LOGGER.error(String.format(
						"The symmetric encryption algorithm \"%s\" declared by the provider[%s] does not implement the interface \"%s\"!",
						algorithm.toString(), provider.getFullName(), SymmetricEncryptionFunction.class.getName()));
				continue;
			}
			if (CryptoAlgorithm.isHashAlgorithm(algorithm) && !(cryptoFunction instanceof HashFunction)) {
				LOGGER.error(String.format(
						"The hash encryption algorithm \"%s\" declared by the provider[%s] does not implement the interface \"%s\"!",
						algorithm.toString(), provider.getFullName(), HashFunction.class.getName()));
				continue;
			}
			if (CryptoAlgorithm.isExtAlgorithm(algorithm) && (cryptoFunction instanceof RandomFunction
					|| cryptoFunction instanceof AsymmetricEncryptionFunction
					|| cryptoFunction instanceof SignatureFunction
					|| cryptoFunction instanceof SymmetricEncryptionFunction
					|| cryptoFunction instanceof HashFunction)) {
				LOGGER.error(String.format(
						"The ext algorithm \"%s\" declared by the provider[%s] can not implement the standard algorithm interface!",
						algorithm.toString(), provider.getFullName()));
				continue;
			}

			if (functions.containsKey(algorithm.code()) || names.containsKey(algorithm.name())) {
				LOGGER.error(String.format("The algorithm \"%s\" declared by the provider[%s] already exists!",
						algorithm.toString(), provider.getFullName()));
				continue;
			}

			functions.put(algorithm.code(), cryptoFunction);
			algorithms.put(algorithm.code(), algorithm);
			names.put(algorithm.name(), algorithm.code());
		}
	}

	private Crypto() {
	}

	public static CryptoProvider[] getProviders() {
		Collection<Provider<CryptoService>> providers = pm.getAllProviders(CryptoService.class);
		CryptoProvider[] infos = new CryptoProvider[providers.size()];

		int i = 0;
		for (Provider<CryptoService> pd : providers) {
			CryptoProviderInfo info = getProviderInfo(pd);
			infos[i] = info;
		}

		return infos;
	}

	private static CryptoProviderInfo getProviderInfo(Provider<CryptoService> pd) {
		Collection<CryptoFunction> functions = pd.getService().getFunctions();
		CryptoAlgorithm[] algorithms = new CryptoAlgorithm[functions.size()];
		int i = 0;
		for (CryptoFunction function : functions) {
			algorithms[i] = function.getAlgorithm();
			i++;
		}
		return new CryptoProviderInfo(pd.getFullName(), algorithms);
	}

	/**
	 * 返回指定名称的密码服务提供者；如果不存在，则返回 null ；
	 * 
	 * @param providerFullName
	 * @return
	 */
	public static CryptoProvider getProvider(String providerFullName) {
		Provider<CryptoService> pd = pm.getProvider(CryptoService.class, providerFullName);
		if (pd == null) {
			throw new CryptoException("Crypto service provider named [" + providerFullName + "] does not exist!");
		}
		return getProviderInfo(pd);
	}

	public static Collection<CryptoAlgorithm> getAllAlgorithms() {
		return algorithms.values();
	}

	/**
	 * 返回指定编码的算法； <br>
	 * 如果不存在，则返回 null；
	 * 
	 * @param code
	 * @return
	 */
	public static CryptoAlgorithm getAlgorithm(short code) {
		return algorithms.get(code);
	}

	/**
	 * Return the CryptoAlogrithm object of the specified name ,or null if none;
	 * 
	 * @param name
	 * @return
	 */
	public static CryptoAlgorithm getAlgorithm(String name) {
		Short code = names.get(name.toUpperCase());
		return code == null ? null : algorithms.get(code);
	}

	public static RandomFunction getRandomFunction(short algorithmCode) {
		CryptoAlgorithm algorithm = getAlgorithm(algorithmCode);
		if (algorithm == null) {
			throw new CryptoException("Algorithm [code:" + algorithmCode + "] has no service provider!");
		}
		return getRandomFunction(algorithm);
	}

	public static RandomFunction getRandomFunction(String algorithmName) {
		CryptoAlgorithm algorithm = getAlgorithm(algorithmName);
		if (algorithm == null) {
			throw new CryptoException("Algorithm " + algorithmName + " has no service provider!");
		}
		return getRandomFunction(algorithm);
	}

	public static RandomFunction getRandomFunction(CryptoAlgorithm algorithm) {
		if (!CryptoAlgorithm.isRandomAlgorithm(algorithm)) {
			throw new CryptoException("The specified algorithm " + algorithm.name() + "[" + algorithm.code()
					+ "] is not a random function!");
		}
		CryptoFunction func = functions.get(algorithm.code());
		if (func == null) {
			throw new CryptoException(
					"Algorithm " + algorithm.name() + "[" + algorithm.code() + "] has no service provider!");
		}

		return (RandomFunction) func;
	}

	public static HashFunction getHashFunction(short algorithmCode) {
		CryptoAlgorithm algorithm = getAlgorithm(algorithmCode);
		if (algorithm == null) {
			throw new CryptoException("Algorithm [code:" + algorithmCode + "] has no service provider!");
		}
		return getHashFunction(algorithm);
	}

	public static HashFunction getHashFunction(String algorithmName) {
		CryptoAlgorithm algorithm = getAlgorithm(algorithmName);
		if (algorithm == null) {
			throw new CryptoException("Algorithm " + algorithmName + " has no service provider!");
		}
		return getHashFunction(algorithm);
	}

	public static HashFunction getHashFunction(CryptoAlgorithm algorithm) {
		if (!CryptoAlgorithm.isHashAlgorithm(algorithm)) {
			throw new CryptoException("The specified algorithm " + algorithm.name() + "[" + algorithm.code()
					+ "] is not a hash function!");
		}
		CryptoFunction func = functions.get(algorithm.code());
		if (func == null) {
			throw new CryptoException(
					"Algorithm " + algorithm.name() + "[" + algorithm.code() + "] has no service provider!");
		}

		return (HashFunction) func;
	}

	public static AsymmetricEncryptionFunction getAsymmetricEncryptionFunction(short algorithmCode) {
		CryptoAlgorithm algorithm = getAlgorithm(algorithmCode);
		if (algorithm == null) {
			throw new CryptoException("Algorithm [code:" + algorithmCode + "] has no service provider!");
		}
		return getAsymmetricEncryptionFunction(algorithm);
	}

	public static AsymmetricEncryptionFunction getAsymmetricEncryptionFunction(String algorithmName) {
		CryptoAlgorithm algorithm = getAlgorithm(algorithmName);
		if (algorithm == null) {
			throw new CryptoException("Algorithm " + algorithmName + " has no service provider!");
		}
		return getAsymmetricEncryptionFunction(algorithm);
	}

	public static AsymmetricEncryptionFunction getAsymmetricEncryptionFunction(CryptoAlgorithm algorithm) {
		if (!CryptoAlgorithm.isAsymmetricEncryptionAlgorithm(algorithm)) {
			throw new CryptoException("The specified algorithm " + algorithm.name() + "[" + algorithm.code()
					+ "] is not a asymmetric encryption function!");
		}
		CryptoFunction func = functions.get(algorithm.code());
		if (func == null) {
			throw new CryptoException(
					"Algorithm " + algorithm.name() + "[" + algorithm.code() + "] has no service provider!");
		}

		return (AsymmetricEncryptionFunction) func;
	}

	public static SignatureFunction getSignatureFunction(Short algorithmCode) {
		CryptoAlgorithm algorithm = getAlgorithm(algorithmCode);
		if (algorithm == null) {
			throw new CryptoException("Algorithm [code:" + algorithmCode + "] has no service provider!");
		}
		return getSignatureFunction(algorithm);
	}

	public static SignatureFunction getSignatureFunction(String algorithmName) {
		CryptoAlgorithm algorithm = getAlgorithm(algorithmName);
		if (algorithm == null) {
			throw new CryptoException("Algorithm " + algorithmName + " has no service provider!");
		}
		return getSignatureFunction(algorithm);
	}

	public static SignatureFunction getSignatureFunction(CryptoAlgorithm algorithm) {
		if (!CryptoAlgorithm.isSignatureAlgorithm(algorithm)) {
			throw new CryptoException("The specified algorithm " + algorithm.name() + "[" + algorithm.code()
					+ "] is not a signature function!");
		}
		CryptoFunction func = functions.get(algorithm.code());
		if (func == null) {
			throw new CryptoException(
					"Algorithm " + algorithm.name() + "[" + algorithm.code() + "] has no service provider!");
		}

		return (SignatureFunction) func;
	}

	public static SymmetricEncryptionFunction getSymmetricEncryptionFunction(short algorithmCode) {
		CryptoAlgorithm algorithm = getAlgorithm(algorithmCode);
		if (algorithm == null) {
			throw new CryptoException("Algorithm [code:" + algorithmCode + "] has no service provider!");
		}
		return getSymmetricEncryptionFunction(algorithm);
	}

	public static SymmetricEncryptionFunction getSymmetricEncryptionFunction(String algorithmName) {
		CryptoAlgorithm algorithm = getAlgorithm(algorithmName);
		if (algorithm == null) {
			throw new CryptoException("Algorithm " + algorithmName + " has no service provider!");
		}
		return getSymmetricEncryptionFunction(algorithm);
	}

	public static SymmetricEncryptionFunction getSymmetricEncryptionFunction(CryptoAlgorithm algorithm) {
		if (!CryptoAlgorithm.isSymmetricEncryptionAlgorithm(algorithm)) {
			throw new CryptoException("The specified algorithm " + algorithm.name() + "[" + algorithm.code()
					+ "] is not a symmetric encryption function!");
		}
		CryptoFunction func = functions.get(algorithm.code());
		if (func == null) {
			throw new CryptoException(
					"Algorithm " + algorithm.name() + "[" + algorithm.code() + "] has no service provider!");
		}

		return (SymmetricEncryptionFunction) func;
	}

	public static CryptoFunction getCryptoFunction(short algorithmCode) {
		CryptoAlgorithm algorithm = getAlgorithm(algorithmCode);
		if (algorithm == null) {
			throw new CryptoException("Algorithm [code:" + algorithmCode + "] has no service provider!");
		}
		return getCryptoFunction(algorithm);
	}

	public static CryptoFunction getCryptoFunction(String algorithmName) {
		CryptoAlgorithm algorithm = getAlgorithm(algorithmName);
		if (algorithm == null) {
			throw new CryptoException("Algorithm " + algorithmName + " has no service provider!");
		}
		return getCryptoFunction(algorithm);
	}

	public static CryptoFunction getCryptoFunction(CryptoAlgorithm algorithm) {
		CryptoFunction func = functions.get(algorithm.code());
		if (func == null) {
			throw new CryptoException(
					"Algorithm " + algorithm.name() + "[" + algorithm.code() + "] has no service provider!");
		}

		return func;
	}

}
