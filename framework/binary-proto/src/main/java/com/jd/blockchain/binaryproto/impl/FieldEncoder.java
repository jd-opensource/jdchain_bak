package com.jd.blockchain.binaryproto.impl;

import java.lang.reflect.Method;

import com.jd.blockchain.binaryproto.FieldSpec;
import com.jd.blockchain.utils.io.BytesSlices;

public interface FieldEncoder extends SliceEncoder {

	Method getReader();

	FieldSpec getFieldSpecification();

	Object decodeField(BytesSlices fieldBytes);

}
