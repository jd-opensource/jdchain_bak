package com.jd.blockchain.binaryproto.impl;

import com.jd.blockchain.binaryproto.DataContractEncoder;

public interface DataContractEncoderLookup {

	/**
	 * 检索指定类型的编码器；
	 * 
	 * @param contractType
	 * @return
	 */
	DataContractEncoder lookup(Class<?> contractType);

	/**
	 * 检索指定 code 和 version 的编码器；
	 * 
	 * @param contractType
	 * @return
	 */
	DataContractEncoder lookup(int code, long version);

}
