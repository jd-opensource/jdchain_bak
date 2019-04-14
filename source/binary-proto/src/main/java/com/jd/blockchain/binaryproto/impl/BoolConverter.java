package com.jd.blockchain.binaryproto.impl;

import com.jd.blockchain.utils.io.BytesSlice;

public class BoolConverter implements FixedValueConverter{

	@Override
	public Class<?> getValueType() {
		return boolean.class;
	}

	@Override
	public Object getDefaultValue() {
		return false;
	}

	@Override
	public int encodeValue(Object value, byte[] buffer, int offset) {
		buffer[offset] = ((Boolean)value).booleanValue() ? (byte)1 : (byte)0;
		return 1;
	}

	@Override
	public Object decodeValue(BytesSlice dataSlice) {
		return dataSlice.getByte() == 1 ? Boolean.TRUE : Boolean.FALSE;
	}

}
