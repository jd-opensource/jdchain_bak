package com.jd.blockchain.crypto;

/**
 * 密文；
 * 
 * @author huanghaiquan
 *
 */
public interface Ciphertext extends CryptoBytes {

	/**
	 * 原始的密文数据；
	 *
	 * @return
	 */
	byte[] getRawCiphertext();

}
