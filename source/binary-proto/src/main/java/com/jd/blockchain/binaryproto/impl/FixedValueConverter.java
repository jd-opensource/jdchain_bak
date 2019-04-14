package com.jd.blockchain.binaryproto.impl;

import com.jd.blockchain.utils.io.BytesSlice;

public interface FixedValueConverter extends ValueConverter {

	/**
	 * 将把固定长度的值序列化到指定的缓冲区；
	 * <p>
	 * 
	 * 注：实现者应用确保写入的范围不要越界（超出 {@link #getSliceSpecification()} 属性指定的长度）；
	 * 
	 * @param value
	 *            要序列化的值；
	 * @param buffer
	 *            保存结果的缓冲区；
	 * @param offset
	 *            缓冲区的写入起始位置；
	 * @return 返回写入的长度；
	 */
	int encodeValue(Object value, byte[] buffer, int offset);

	Object decodeValue(BytesSlice dataSlice);

}
