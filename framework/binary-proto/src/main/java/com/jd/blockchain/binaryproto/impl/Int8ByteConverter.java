package com.jd.blockchain.binaryproto.impl;

import com.jd.blockchain.utils.io.BytesSlice;

public class Int8ByteConverter implements FixedValueConverter{

	@Override
	public Class<?> getValueType() {
		return byte.class;
	}

	@Override
	public Object getDefaultValue() {
		return 0;
	}

	@Override
	public int encodeValue(Object value, byte[] buffer, int offset) {
		buffer[offset] = (byte)value;
		return 1;
	}

	@Override
	public Object decodeValue(BytesSlice dataSlice) {
		return dataSlice.getByte();
	}

}
