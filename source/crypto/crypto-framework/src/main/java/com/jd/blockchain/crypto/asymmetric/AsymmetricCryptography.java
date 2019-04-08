//package com.jd.blockchain.crypto.asymmetric;
//
//import com.jd.blockchain.crypto.Ciphertext;
//import com.jd.blockchain.crypto.CryptoAlgorithm;
//import com.jd.blockchain.crypto.PrivKey;
//import com.jd.blockchain.crypto.PubKey;
//
//public interface AsymmetricCryptography {
//
//	/**
//	 * 生成密钥对；
//	 *
//	 * @param algorithm
//	 * @return
//	 */
//	CryptoKeyPair generateKeyPair(CryptoAlgorithm algorithm);
//
//	/**
//	 * 获取签名方法；
//	 *
//	 * @param algorithm
//	 * @return
//	 */
//	SignatureFunction getSignatureFunction(CryptoAlgorithm algorithm);
//
//	/**
//	 * 校验签名摘要和数据是否一致；
//	 *
//	 * @param digestBytes 签名摘要数据
//	 * @param pubKeyBytes 公钥数据
//	 * @param data 被签名数据
//	 * @return
//	 */
//	boolean verify(byte[] digestBytes, byte[] pubKeyBytes, byte[] data);
//
//	/**
//	 * 获取非对称加密方法；
//	 *
//	 * @param algorithm
//	 * @return
//	 */
//	AsymmetricEncryptionFunction getAsymmetricEncryptionFunction(CryptoAlgorithm algorithm);
//
//	/**
//	 * 解密；
//	 *
//	 * @param privKeyBytes
//	 * @param ciphertextBytes
//	 * @return
//	 */
//	byte[] decrypt(byte[] privKeyBytes, byte[] ciphertextBytes);
//
//
//	Ciphertext resolveCiphertext(byte[] ciphertextBytes);
//
//	Ciphertext tryResolveCiphertext(byte[] ciphertextBytes);
//
//	/**
//	 * @param digestBytes 待解析签名摘要
//	 * @return
//	 */
//	SignatureDigest resolveSignatureDigest(byte[] digestBytes);
//
//	SignatureDigest tryResolveSignatureDigest(byte[] digestBytes);
//
//	/**
//	 * 由私钥恢复公钥；
//	 *
//	 * @param privKeyBytes 包含算法标识、密钥掩码和私钥的字节数组
//	 * @return 包含算法标识、密钥掩码和公钥的字节数组
//	 */
//	byte[] retrievePubKey(byte[] privKeyBytes);
//
//	byte[] tryRetrievePubKeyBytes(byte[] privKeyBytes);
//
//	PubKey resolvePubKey(byte[] pubKeyBytes);
//
//	PubKey tryResolvePubKey(byte[] pubKeyBytes);
//
//	PrivKey resolvePrivKey(byte[] privKeyBytes);
//
//	PrivKey tryResolvePrivKey(byte[] privKeyBytes);
//
//}
