package com.jd.blockchain.utils.io;

public interface BytesSerializable {

	/**
	 * 以字节数组形式获取字节块的副本；
	 * @return byte[]
	 */
	byte[] toBytes();

}