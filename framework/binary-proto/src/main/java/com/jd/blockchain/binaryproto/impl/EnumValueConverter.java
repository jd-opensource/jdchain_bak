package com.jd.blockchain.binaryproto.impl;

import com.jd.blockchain.binaryproto.DataContractException;
import com.jd.blockchain.binaryproto.PrimitiveType;
import com.jd.blockchain.utils.io.BytesSlice;

public class EnumValueConverter implements FixedValueConverter {

	private Class<?> enumType;

	private PrimitiveType codeType;

	private int[] values;

	private Object[] constants;

	private FixedValueConverter valueConverter;

	public EnumValueConverter(Class<?> enumType, PrimitiveType codeType, int[] values, Object[] constants, FixedValueConverter valueConverter) {
		this.enumType = enumType;
		this.values = values;
		this.constants = constants;
		this.valueConverter = valueConverter;
		this.codeType = codeType;

	}

	@Override
	public Class<?> getValueType() {
		return enumType;
	}

	@Override
	public Object getDefaultValue() {
		return null;
	}

	//lookup CODE value
	private Object getEnumCode(Object value, int[] codes, Object[] constants) {
		int codeIndex = 0;

		for (int i = 0; i < constants.length; i++) {
			if (value.toString().equals(constants[i].toString())) {
				codeIndex = i;
				break;
			}
		}
		switch (codeType) {
			case INT8:
				return (byte)codes[codeIndex];
			case INT16:
				return (short)codes[codeIndex];
			case INT32:
				return codes[codeIndex];
			default:
				throw new DataContractException(String.format("Enum code error!"));
		}
	}

	@Override
	public int encodeValue(Object value, byte[] buffer, int offset) {
		// 注：由于是通过反射调用的，已经在外围做了检查，此处不需要重复检查枚举值的范围；
		//首先把枚举常量转换成对应的CODE
		Object code = getEnumCode(value, values, constants);
		return valueConverter.encodeValue(code, buffer, offset);
	}

	@Override
	public Object decodeValue(BytesSlice dataSlice) {

		Object v = valueConverter.decodeValue(dataSlice);
		switch (codeType) {
			case INT8:
				for (int i = 0; i < values.length; i++) {
					if ((byte)values[i] == (byte)v) {
						return constants[i];
					}
				}
				throw new DataContractException(String.format(
						"The decoding value is out of all enum constants! --[value=%s][enum type=%s]", v, enumType.toString()));
			case INT16:
				for (int i = 0; i < values.length; i++) {
					if ((short)values[i] == (short)v) {
						return constants[i];
					}
				}
				throw new DataContractException(String.format(
						"The decoding value is out of all enum constants! --[value=%s][enum type=%s]", v, enumType.toString()));
			case INT32:
				for (int i = 0; i < values.length; i++) {
					if ((int)values[i] == (int)v) {
						return constants[i];
					}
				}
				throw new DataContractException(String.format(
						"The decoding value is out of all enum constants! --[value=%s][enum type=%s]", v, enumType.toString()));
			default:
				throw new DataContractException(String.format("Enum code error!"));

		}
	}

}
