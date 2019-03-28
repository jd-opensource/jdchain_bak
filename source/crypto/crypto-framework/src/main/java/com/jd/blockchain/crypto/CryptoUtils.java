package com.jd.blockchain.crypto;

import com.jd.blockchain.crypto.asymmetric.AsymmetricCryptography;
import com.jd.blockchain.crypto.asymmetric.AsymmetricEncryptionFunction;
import com.jd.blockchain.crypto.asymmetric.SignatureFunction;
import com.jd.blockchain.crypto.hash.HashCryptography;
import com.jd.blockchain.crypto.hash.HashFunction;
import com.jd.blockchain.crypto.impl.CryptoFactoryImpl;
import com.jd.blockchain.crypto.symmetric.SymmetricCryptography;
import com.jd.blockchain.crypto.symmetric.SymmetricEncryptionFunction;

public class CryptoUtils {

	private static CryptoFactory CRYPTO_FACTORY = new CryptoFactoryImpl();

	public static CryptoFactory crypto() {
		return CRYPTO_FACTORY;
	}


	public static HashCryptography hashCrypto() {
		return crypto().hashCryptography();
	}

	public static HashFunction hash(CryptoAlgorithm alg) {
		return hashCrypto().getFunction(alg);
	}


	public static AsymmetricCryptography asymmCrypto() {
		return crypto().asymmetricCryptography();
	}

	public static SignatureFunction sign(CryptoAlgorithm alg) {
		return asymmCrypto().getSignatureFunction(alg);
	}
	
	public static AsymmetricEncryptionFunction asymmEncrypt(CryptoAlgorithm alg) {
		return asymmCrypto().getAsymmetricEncryptionFunction(alg);
	}


	public static SymmetricCryptography symmCrypto() {
		return crypto().symmetricCryptography();
	}

	public static SymmetricEncryptionFunction symmEncrypt(CryptoAlgorithm alg) {
		return symmCrypto().getSymmetricEncryptionFunction(alg);
	}

}
