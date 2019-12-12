package com.jd.blockchain.binaryproto.impl;

import com.jd.blockchain.utils.io.BytesSlice;
import com.jd.blockchain.utils.io.BytesUtils;

public class Int64LongWrapperConverter implements FixedValueConverter{

	@Override
	public Class<?> getValueType() {
		return Long.class;
	}

	@Override
	public Object getDefaultValue() {
		return null;
	}

	@Override
	public int encodeValue(Object value, byte[] buffer, int offset) {
		BytesUtils.toBytes((long)value, buffer, offset);
		return 8;
	}

	@Override
	public Object decodeValue(BytesSlice dataSlice) {
		return dataSlice.getLong();
	}

}
