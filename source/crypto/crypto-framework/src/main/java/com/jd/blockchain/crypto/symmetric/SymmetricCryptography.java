package com.jd.blockchain.crypto.symmetric;

import com.jd.blockchain.crypto.Ciphertext;
import com.jd.blockchain.crypto.CryptoAlgorithm;

public interface SymmetricCryptography {

	/**
	 * 生成秘钥对；
	 * 
	 * @param algorithm
	 * @return
	 */
	SymmetricKey generateKey(CryptoAlgorithm algorithm);

	/**
	 * 获取签名方法；
	 * 
	 * @param algorithm
	 * @return
	 */
	SymmetricEncryptionFunction getSymmetricEncryptionFunction(CryptoAlgorithm algorithm);

	byte[] decrypt(byte[] symmetricKeyBytes,byte[] ciphertextBytes);
	
	Ciphertext resolveCiphertext(byte[] ciphertextBytes);
	
	Ciphertext tryResolveCiphertext(byte[] ciphertextBytes);

	SymmetricKey resolveSymmetricKey(byte[] symmetricKeyBytes);

	SymmetricKey tryResolveSymmetricKey(byte[] symmetricKeyBytes);

}
