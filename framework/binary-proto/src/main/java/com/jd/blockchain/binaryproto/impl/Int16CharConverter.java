package com.jd.blockchain.binaryproto.impl;

import com.jd.blockchain.utils.io.BytesSlice;
import com.jd.blockchain.utils.io.BytesUtils;

public class Int16CharConverter implements FixedValueConverter{
	
	@Override
	public Class<?> getValueType() {
		return char.class;
	}

	@Override
	public Object getDefaultValue() {
		return '\u0000';
	}

	@Override
	public int encodeValue(Object value, byte[] buffer, int offset) {
		BytesUtils.toBytes((char)value, buffer, offset);
		return 2;
	}

	@Override
	public Object decodeValue(BytesSlice dataSlice) {
		return dataSlice.getChar();
	}

}
