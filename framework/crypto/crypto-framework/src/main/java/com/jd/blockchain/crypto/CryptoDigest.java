package com.jd.blockchain.crypto;

/**
 * 摘要；
 * 
 * @author huanghaiquan
 *
 */
public interface CryptoDigest extends CryptoBytes {

	/**
	 * 原始的摘要数据；
	 * 
	 * @return
	 */
	byte[] getRawDigest();

}
