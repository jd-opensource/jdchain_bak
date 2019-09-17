package com.jd.blockchain.binaryproto;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.jd.blockchain.binaryproto.impl.DataContractContext;
import com.jd.blockchain.binaryproto.impl.HeaderEncoder;
import com.jd.blockchain.utils.io.BytesOutputBuffer;
import com.jd.blockchain.utils.io.BytesSlice;
import com.jd.blockchain.utils.io.BytesUtils;

public class BinaryProtocol {
	

	public static void encode(Object data, Class<?> contractType, OutputStream out) {
		DataContractEncoder encoder = DataContractContext.resolve(contractType);
		if (encoder == null) {
			throw new IllegalArgumentException("Contract Type not exist!--" + contractType.getName());
		}
		BytesOutputBuffer buffer = new BytesOutputBuffer();
		encoder.encode(data, buffer);
		buffer.writeTo(out);
	}

	public static byte[] encode(Object data, Class<?> contractType) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		encode(data, contractType, out);
		return out.toByteArray();
	}

	public static <T> T decode(InputStream in) {
		byte[] bytes = BytesUtils.copyToBytes(in);
		return decode(bytes);
	}
	
	

	public static <T> T decode(byte[] dataSegment) {
		BytesSlice bytes = new BytesSlice(dataSegment, 0, dataSegment.length);
		int code = HeaderEncoder.resolveCode(bytes);
		long version = HeaderEncoder.resolveVersion(bytes);

		DataContractEncoder encoder = DataContractContext.ENCODER_LOOKUP.lookup(code, version);
		if (encoder == null) {
			throw new DataContractException(
					String.format("No data contract was registered with code[%s] and version[%s]!", code, version));
		}
		return encoder.decode(bytes.getInputStream());
	}

	
	
	public static <T> T decodeAs(byte[] dataSegment, Class<T> contractType) {
		DataContractEncoder encoder = DataContractContext.ENCODER_LOOKUP.lookup(contractType);
		if (encoder == null) {
			throw new DataContractException("Contract type is not registered! --" + contractType.toString());
		}
		BytesSlice bytes = new BytesSlice(dataSegment, 0, dataSegment.length);
		return encoder.decode(bytes.getInputStream());
	}
	
	
	
	

}
