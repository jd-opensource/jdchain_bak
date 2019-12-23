package com.jd.blockchain.binaryproto.impl;

import com.jd.blockchain.utils.io.BytesOutputBuffer;
import com.jd.blockchain.utils.io.BytesSlice;

public interface DynamicValueConverter extends ValueConverter {

	/**
	 * 写入一个动态长度的值；以一个头部的长度字节开始
	 * @param value
	 * @param buffer
	 * @return
	 */
	int encodeDynamicValue(Object value, BytesOutputBuffer buffer);

	Object decodeValue(BytesSlice dataSlice);

}
