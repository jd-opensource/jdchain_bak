package com.jd.blockchain.crypto;

/**
 * 密钥；
 * 
 * @author huanghaiquan
 *
 */
public interface CryptoKey extends CryptoBytes {
	
	/**
	 * 密钥的类型；
	 * @return
	 */
	CryptoKeyType getKeyType();

	/**
	 * 原始的密钥数据；
	 *
	 * @return
	 */
	byte[] getRawKeyBytes();


}