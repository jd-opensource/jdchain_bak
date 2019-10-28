package com.jd.blockchain.binaryproto.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.jd.blockchain.utils.io.BytesInputStream;
import com.jd.blockchain.utils.io.BytesSlices;

class DynamicDataContract implements InvocationHandler {

	public static Method METHOD_GET_CLASS;

	static {
		try {
			METHOD_GET_CLASS = Object.class.getMethod("getClass");
		} catch (NoSuchMethodException | SecurityException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	private DataContractEncoderImpl contractEncoder;

	// 字段的数据片段列表，首个是 HeaderSlice，其次是按字段顺序排列的数据片段；
	private BytesSlices[] dataSlices;

	private DynamicDataContract(BytesInputStream bytesStream, DataContractEncoderImpl contractEncoder) {
		this.contractEncoder = contractEncoder;

		init(bytesStream);
	}

	private void init(BytesInputStream bytesStream) {
		// 解析出所有的数据片段；
		dataSlices = new BytesSlices[contractEncoder.getFieldCount() + 1];
		
		dataSlices[0] = contractEncoder.getHeaderEncoder().decode(bytesStream);

		for (int i = 1; i < dataSlices.length; i++) {
			dataSlices[i] = contractEncoder.getFieldEncoder(i - 1).decode(bytesStream);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T createContract(BytesInputStream bytesStream, DataContractEncoderImpl contractEncoder) {
		return (T) Proxy.newProxyInstance(contractEncoder.getContractType().getClassLoader(),
				contractEncoder.getContractTypeAsArray(), new DynamicDataContract(bytesStream, contractEncoder));
	}

	@SuppressWarnings("unchecked")
	public static <T> T createContract(byte[] contractBytes, DataContractEncoderImpl contractEncoder) {
		return (T) Proxy.newProxyInstance(contractEncoder.getContractType().getClassLoader(),
				contractEncoder.getContractTypeAsArray(),
				new DynamicDataContract(new BytesInputStream(contractBytes, 0, contractBytes.length), contractEncoder));
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		int fieldId = contractEncoder.getFieldId(method);
		if (fieldId > -1) {
			FieldEncoder encoder = contractEncoder.getFieldEncoder(fieldId);
			return encoder.decodeField(dataSlices[fieldId + 1]);
		}
		if (METHOD_GET_CLASS == method) {
			return contractEncoder.getContractType();
		}
		// invoke method declared in type Object;
		Object result;
		try {
			//for some special case, interface's method without annotation
			result = method.invoke(this, args);
		} catch (Exception e) {
			if (method.getReturnType().isPrimitive()) {
				result = 0;
			}
			else {
				result = null;
			}
			e.printStackTrace();
		}
		return result;
	}

}
