package com.jd.blockchain.crypto;

import com.jd.blockchain.utils.io.BytesSerializable;

/**
 * {@link CryptoBytes} 表示与特定密码算法相关的编码数据；
 * 
 * @author huanghaiquan
 *
 */
public interface CryptoBytes extends BytesSerializable {

	/**
	 * 算法标识符的长度；
	 */
	int ALGORYTHM_CODE_SIZE = CryptoAlgorithm.CODE_SIZE;

	/**
	 * 算法；
	 * 
	 * @return
	 */
	short getAlgorithm();

	/**
	 * 返回编码后的摘要信息；<br>
	 * 
	 * 这是算法标识 {@link #getAlgorithm()} 与原始的摘要数据
	 * 按照特定的编码方式合并后的结果；
	 */
	@Override
	byte[] toBytes();
	
	

}
