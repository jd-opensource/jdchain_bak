package com.jd.blockchain.utils.io;

public interface BytesDeserializable {

	/**
	 * 以字节数组形式获取字节块的副本；
	 * @param bytes butes
	 */
	void fromBytes(byte[] bytes);

}