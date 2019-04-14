package com.jd.blockchain.binaryproto.impl;

import com.jd.blockchain.utils.io.BytesSlice;
import com.jd.blockchain.utils.io.BytesUtils;

public class Int16ShortConverter implements FixedValueConverter{

	@Override
	public Class<?> getValueType() {
		return short.class;
	}

	@Override
	public Object getDefaultValue() {
		return 0;
	}

	@Override
	public int encodeValue(Object value, byte[] buffer, int offset) {
		BytesUtils.toBytes((short)value, buffer, offset);
		return 2;
	}

	@Override
	public Object decodeValue(BytesSlice dataSlice) {
		return dataSlice.getShort();
	}

}
