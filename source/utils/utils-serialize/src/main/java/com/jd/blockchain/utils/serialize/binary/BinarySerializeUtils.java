package com.jd.blockchain.utils.serialize.binary;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import com.jd.blockchain.utils.io.BytesUtils;
import com.jd.blockchain.utils.io.RuntimeIOException;

public class BinarySerializeUtils {

	public static byte[] serialize(Object object) {
		if (object == null) {
			return BytesUtils.EMPTY_BYTES;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		serialize(object, out);
		return out.toByteArray();
	}

	public static void serialize(Object object, OutputStream out) {
		if (object == null) {
			return;
		}
		try {
			ObjectOutputStream objOut = new ObjectOutputStream(out);
			objOut.writeObject(object);
			objOut.flush();
		} catch (IOException e) {
			throw new RuntimeIOException(e.getMessage(), e);
		}
	}

	public static <T> T deserialize(byte[] bytes) {
		if (bytes == null || bytes.length == 0) {
			return null;
		}
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		return deserialize(in);
	}

	@SuppressWarnings("unchecked")
	public static <T> T deserialize(InputStream in) {
		try {
			try(FilteredObjectInputStream objIn = new FilteredObjectInputStream(in)){
				Object obj = objIn.readObject();
				return (T) obj;
			}
		} catch (IOException e) {
			throw new RuntimeIOException(e.getMessage(), e);
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	public static <T> T copyOf(T obj) {
		byte[] bts = serialize(obj);
		return deserialize(bts);
	}
}
