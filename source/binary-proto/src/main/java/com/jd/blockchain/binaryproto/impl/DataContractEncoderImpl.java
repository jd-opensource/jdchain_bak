package com.jd.blockchain.binaryproto.impl;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.jd.blockchain.binaryproto.DataContractEncoder;
import com.jd.blockchain.binaryproto.DataSpecification;
import com.jd.blockchain.utils.io.BytesInputStream;
import com.jd.blockchain.utils.io.BytesOutputBuffer;
import com.jd.blockchain.utils.io.BytesSlice;

public class DataContractEncoderImpl implements DataContractEncoder {

	private Class<?>[] contractTypeArray;

	private Class<?> contractType;

	private DataSpecification specification;

	private HeaderEncoder headEncoder;

	private FieldEncoder[] fieldEncoders;

	// 字段的 Get 方法与编码器的映射表；
	private Map<Method, Integer> fieldIndexMap;

	/**
	 * @param contractType
	 * @param specification
	 * @param headEncoder
	 *            头部编码器；
	 * @param fieldEncoders
	 *            按顺序排列的字段编码器列表；
	 */
	public DataContractEncoderImpl(Class<?> contractType, DataSpecification specification, HeaderEncoder headEncoder,
			FieldEncoder[] fieldEncoders) {
		this.contractType = contractType;
		this.contractTypeArray = new Class<?>[] { contractType };
		this.specification = specification;
		this.headEncoder = headEncoder;
		this.fieldEncoders = fieldEncoders;
		this.fieldIndexMap = new HashMap<>();
		int i = 0;
		for (FieldEncoder fieldEncoder : fieldEncoders) {
			fieldIndexMap.put(fieldEncoder.getReader(), i);
			i++;
		}
	}

	HeaderEncoder getHeaderEncoder() {
		return headEncoder;
	}

	Class<?>[] getContractTypeAsArray() {
		return contractTypeArray;
	}

	int getFieldCount() {
		return fieldEncoders.length;
	}

	FieldEncoder getFieldEncoder(int id) {
		return fieldEncoders[id];
	}

	/**
	 * 通过字段的声明方法返回字段的序号；
	 * 
	 * @param declaredMethod
	 *            声明并标注为数据契约字段的方法；注：不能是覆盖的非标注方法，也不能是实现方法；
	 * @return 字段序号； 如果不存在，则返回 -1；
	 */
	int getFieldId(Method declaredMethod) {
		Integer id = fieldIndexMap.get(declaredMethod);
		return id == null ? -1 : id.intValue();
	}

	/**
	 * 通过字段的声明方法返回字段的编码器；
	 * 
	 * @param declaredMethod
	 *            声明并标注为数据契约字段的方法；注：不能是覆盖的非标注方法，也不能是实现方法；
	 * @return
	 */
	FieldEncoder getFieldEncoder(Method declaredMethod) {
		Integer idx = fieldIndexMap.get(declaredMethod);
		if (idx == null) {
			return null;
		}
		return fieldEncoders[idx.intValue()];
	}

	@Override
	public DataSpecification getSepcification() {
		return specification;
	}

	@Override
	public Class<?> getContractType() {
		return contractType;
	}

	@Override
	public int encode(Object dataContract, BytesOutputBuffer buffer) {
		int size = 0;
		size += headEncoder.encode(dataContract, buffer);
		if (dataContract != null) {
			for (SliceEncoder sliceEncoder : fieldEncoders) {
				size += sliceEncoder.encode(dataContract, buffer);
			}
		}
		return size;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T decode(BytesInputStream bytesStream) {
		BytesSlice headerSlice = bytesStream.getSlice(HeaderEncoder.HEAD_BYTES);
		if (!headEncoder.verifyHeaders(headerSlice)) {
			throw new IllegalArgumentException(String.format(
					"The code and version resolved from bytes stream is not match this data contract encoder! --[expected=%s, %s][actual=%s, %s].",
					headEncoder.getCode(), headEncoder.getVersion(), HeaderEncoder.resolveCode(headerSlice),
					HeaderEncoder.resolveVersion(headerSlice)));
		}
		if (bytesStream.getSize() == HeaderEncoder.HEAD_BYTES) {
			// 只有头部，没有值，表示空值；
			return null;
		}
		return (T) DynamicDataContract.createContract(bytesStream, this);
	}

}
