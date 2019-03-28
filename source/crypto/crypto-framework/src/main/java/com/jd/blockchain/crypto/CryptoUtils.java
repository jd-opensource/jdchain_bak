//package com.jd.blockchain.crypto;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.jd.blockchain.crypto.asymmetric.AsymmetricCryptography;
//import com.jd.blockchain.crypto.asymmetric.AsymmetricEncryptionFunction;
//import com.jd.blockchain.crypto.asymmetric.SignatureFunction;
//import com.jd.blockchain.crypto.hash.HashCryptography;
//import com.jd.blockchain.crypto.hash.HashFunction;
//import com.jd.blockchain.crypto.symmetric.SymmetricCryptography;
//import com.jd.blockchain.crypto.symmetric.SymmetricEncryptionFunction;
//
//public class CryptoUtils {
//
//	private static Logger LOGGER = LoggerFactory.getLogger(CryptoUtils.class);
//
//	private static final Object MUTEX = new Object();
//
//	private static final String STD = "com.jd.blockchain.crypto.impl.CryptoFactoryImpl";
//
//	private static volatile CryptoFactory STD_FACTORY;
//
//	public static CryptoFactory crypto() {
//		if (STD_FACTORY == null) {
//			synchronized (MUTEX) {
//				if (STD_FACTORY == null) {
//					try {
//						Class<?> stdFactoryClass = Class.forName(STD);
//						STD_FACTORY = (CryptoFactory) stdFactoryClass.newInstance();
//					} catch (ClassNotFoundException e) {
//						LOGGER.error("STD crypto provider is not found!", e);
//						throw new CryptoException("STD crypto provider is not found!", e);
//					} catch (InstantiationException | IllegalAccessException e) {
//						LOGGER.error("Fail to init STD crypto provider!", e);
//						throw new CryptoException("Fail to init STD crypto provider!", e);
//					}
//				}
//				return STD_FACTORY;
//			}
//		}
//		return STD_FACTORY;
//	}
//
//	public static HashCryptography hashCrypto() {
//		return crypto().hashCryptography();
//	}
//
//	public static HashFunction hash(CryptoAlgorithm alg) {
//		return hashCrypto().getFunction(alg);
//	}
//
//	public static AsymmetricCryptography asymmCrypto() {
//		return crypto().asymmetricCryptography();
//	}
//
//	public static SignatureFunction sign(CryptoAlgorithm alg) {
//		return asymmCrypto().getSignatureFunction(alg);
//	}
//
//	public static AsymmetricEncryptionFunction asymmEncrypt(CryptoAlgorithm alg) {
//		return asymmCrypto().getAsymmetricEncryptionFunction(alg);
//	}
//
//	public static SymmetricCryptography symmCrypto() {
//		return crypto().symmetricCryptography();
//	}
//
//	public static SymmetricEncryptionFunction symmEncrypt(CryptoAlgorithm alg) {
//		return symmCrypto().getSymmetricEncryptionFunction(alg);
//	}
//
//}
