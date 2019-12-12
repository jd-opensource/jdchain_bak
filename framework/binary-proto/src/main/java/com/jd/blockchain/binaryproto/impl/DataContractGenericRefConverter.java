package com.jd.blockchain.binaryproto.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataContractEncoder;
import com.jd.blockchain.binaryproto.DataContractException;
import com.jd.blockchain.utils.io.BytesOutputBuffer;
import com.jd.blockchain.utils.io.BytesSlice;

public class DataContractGenericRefConverter extends AbstractDynamicValueConverter {

	private final Object mutex = new Object();

	private DataContractEncoderLookup encoderLookup;

	private Map<Class<?>, DataContractEncoder> encoderCache;

	public DataContractGenericRefConverter(Class<?> baseType, DataContractEncoderLookup encoderLookup) {
		super(baseType);
		this.encoderLookup = encoderLookup;
		this.encoderCache = new ConcurrentHashMap<>();
	}

	private DataContractEncoder lookupEncoder(Class<?> dataObjectType) {
		DataContractEncoder encoder = encoderCache.get(dataObjectType);
		if (encoder != null) {
			return encoder;
		}
		synchronized (mutex) {
			encoder = encoderCache.get(dataObjectType);
			if (encoder != null) {
				return encoder;
			}
			Class<?>[] intfs = dataObjectType.getInterfaces();
			Class<?> contractType = null;
			DataContract anno = null;
			for (Class<?> itf : intfs) {
				anno = itf.getAnnotation(DataContract.class);
				if (anno != null) {
					if (contractType == null) {
						contractType = itf;
					} else {
						throw new DataContractException(String.format(
								"Data object implements more than one DataContract interface! --[DataObject=%s]",
								dataObjectType.toString()));
					}
				}
			}
			if (contractType == null) {
				throw new DataContractException(
						String.format("Data object doesn't implement any DataContract interface! --[DataObject=%s]",
								dataObjectType.toString()));
			}

			encoder = encoderLookup.lookup(contractType);
			if (encoder == null) {
				throw new DataContractException(String.format(
						"DataContract of the specified data object hasn't been registered! --[DataContract=%s][DataObject=%s]",
						contractType.toString(), dataObjectType.toString()));
			}
			encoderCache.put(dataObjectType, encoder);
		}

		return encoder;
	}

	@Override
	public int encodeDynamicValue(Object value, BytesOutputBuffer buffer) {
		DataContractEncoder contractEncoder = lookupEncoder(value.getClass());

		BytesOutputBuffer contractBuffer = new BytesOutputBuffer();
		int size = contractEncoder.encode(value, contractBuffer);

		size += writeSize(size, buffer);

		buffer.write(contractBuffer);
		return size;
	}

	@Override
	public Object decodeValue(BytesSlice dataSlice) {
		int code = HeaderEncoder.resolveCode(dataSlice);
		long version = HeaderEncoder.resolveVersion(dataSlice);
		DataContractEncoder contractEncoder = encoderLookup.lookup(code, version);
		if (contractEncoder == null) {
			throw new DataContractException(
					String.format("No data contract was registered with code[%s] and version[%s]!", code, version));
		}
		return contractEncoder.decode(dataSlice.getInputStream());
	}

}
