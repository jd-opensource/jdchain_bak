package com.jd.blockchain.crypto;

/**
 * CryptoFunction represents the cryptographic function of a particular
 * algorithm；
 * 
 * @author huanghaiquan
 *
 */
public interface CryptoFunction {

	/**
	 * The cryptographic algorithm supported by this CryptoFunction;
	 * 
	 * @return
	 */
	CryptoAlgorithm getAlgorithm();

//	/**
//	 * Resolve ciphertext from byte array to CyptoBytes object, and check if its
//	 * algorithm matches this function.
//	 * 
//	 * @param bytes
//	 *            ciphertext
//	 * @return Return {@link CryptoBytes} object, or throw {@link CryptoException}
//	 *         if its algorithm does not match.
//	 */
//	CryptoBytes resolveCryptoBytes(byte[] bytes);

}
