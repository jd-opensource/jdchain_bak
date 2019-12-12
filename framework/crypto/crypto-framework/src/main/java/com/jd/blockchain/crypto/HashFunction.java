package com.jd.blockchain.crypto;

public interface HashFunction extends CryptoFunction {

	/**
	 * 计算指定数据的 hash；
	 * 
	 * @param data
	 * @return
	 */
	HashDigest hash(byte[] data);
	
	/**
	 * 计算指定数据的 hash；
	 * 
	 * @param data
	 * @return
	 */
	HashDigest hash(byte[] data, int offset, int len);
	

	/**
	 * 校验 hash 摘要与指定的数据是否匹配；
	 * 
	 * @param digest
	 * @param data
	 * @return
	 */
	boolean verify(HashDigest digest, byte[] data);

	/**
	 * 校验字节数组形式的hash摘要的格式是否满足要求；
	 *
	 * @param digestBytes 包含算法标识和hash摘要的字节数组
	 * @return 是否满足指定算法的hash摘要格式
	 */
	boolean supportHashDigest(byte[] digestBytes);

	/**
	 * 将字节数组形式的hash摘要转换成HashDigest格式；
	 *
	 * @param digestBytes 包含算法标识和hash摘要的字节数组
	 * @return HashDigest形式的hash摘要
	 */
	HashDigest resolveHashDigest(byte[] digestBytes);
	
}
