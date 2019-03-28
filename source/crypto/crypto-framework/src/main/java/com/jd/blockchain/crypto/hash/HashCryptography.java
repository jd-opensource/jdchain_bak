package com.jd.blockchain.crypto.hash;

import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.CryptoException;

public interface HashCryptography {

	/**
	 * return HashFunction instance of the specified hash alg;
	 * 
	 * 
	 * if alg out of hash alg,then throws {@link IllegalArgumentException}
	 * 
	 * @param algorithm
	 * @return
	 */
	HashFunction getFunction(CryptoAlgorithm algorithm);

	/**
	 * 校验 hash 摘要与指定的数据是否匹配；
	 * 
	 * @param digestBytes
	 * @param data
	 * @return
	 */
	boolean verify(byte[] digestBytes, byte[] data);
	
	boolean verify(HashDigest digest, byte[] data);

	/**
	 * 解析指定的 hash 摘要； <br>
	 * 
	 * 如果不符合哈希摘要的编码格式，则引发 {@link CryptoException} 异常；
	 * 
	 * @param digestBytes
	 * @return
	 */
	HashDigest resolveHashDigest(byte[] digestBytes);

	/**
	 * 解析指定的 hash 摘要； <br>
	 * 
	 * 如果不符合哈希摘要的编码格式，则返回 null；
	 * 
	 * @param digestBytes
	 * @return
	 */
	HashDigest tryResolveHashDigest(byte[] digestBytes);

}
