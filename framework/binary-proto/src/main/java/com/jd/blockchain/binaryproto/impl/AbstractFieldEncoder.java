package com.jd.blockchain.binaryproto.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.jd.blockchain.binaryproto.BinarySliceSpec;
import com.jd.blockchain.binaryproto.FieldSpec;

public abstract class AbstractFieldEncoder implements FieldEncoder {

	protected BinarySliceSpec sliceSpec;

	protected FieldSpec fieldSpec;

	protected Method reader;

	public AbstractFieldEncoder(BinarySliceSpec sliceSpec, FieldSpec fieldSpec, Method reader) {
		this.sliceSpec = sliceSpec;
		this.fieldSpec = fieldSpec;
		this.reader = reader;
	}

	@Override
	public BinarySliceSpec getSliceSpecification() {
		return sliceSpec;
	}

	@Override
	public FieldSpec getFieldSpecification() {
		return fieldSpec;
	}

	@Override
	public Method getReader() {
		return reader;
	}


	protected Object readValue(Object dataContract) {
		try {
			return reader.invoke(dataContract);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	protected Object[] readArrayValue(Object dataContract) {
		return (Object[]) readValue(dataContract);
	}
}
