package com.jd.blockchain.utils.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.springframework.util.Base64Utils;

import com.jd.blockchain.utils.codec.Base58Utils;
import com.jd.blockchain.utils.codec.HexUtils;

/**
 * ByteArray 二进制字节块是对字节数组的包装，目的是提供一种不可变的二进制数据结构；
 * 
 * @author huanghaiquan
 *
 */
public class ByteArray implements Externalizable {

	public static final ByteArray EMPTY = ByteArray.wrap(new byte[0]);

	private byte[] bytes;
	
	private int hashCode;
	
	private ByteArray readonlyWrapper = null;

	private ByteArray(byte[] bytes) {
		this(bytes, false);
	}
	
	private ByteArray(byte[] bytes, boolean readonly) {
		this.bytes = bytes;
		this.hashCode = Arrays.hashCode(bytes);
//		this.readonly = readonly;
	}

	public int size() {
		return bytes.length;
	}

	public byte get(int i) {
		return bytes[i];
	}
	
//	public boolean isReadonly() {
//		return readonly;
//	}

	public void copy(int srcPos, byte[] dest, int destPos, int length) {
		System.arraycopy(bytes, srcPos, dest, destPos, length);
	}

	public ByteArray() {}

	@Override
	public boolean equals(Object obj) {
		if (super.equals(obj)) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ByteArray)) {
			return false;
		}
		ByteArray target = (ByteArray) obj;
		if (this.bytes == target.bytes) {
			return true;
		}
		return BytesUtils.equals(target.bytes, this.bytes);
	}
	
	@Override
	public int hashCode() {
		return Arrays.hashCode(bytes);
	}

	/**
	 * 对指定的字节数组的副本进行包装；
	 * 
	 * @param bytes bytes
	 * @return ByteArray
	 */
	public static ByteArray wrapCopy(byte[] bytes) {
		byte[] replica = Arrays.copyOf(bytes, bytes.length);
		return new ByteArray(replica);
	}

	/**
	 * 对指定的字节数组直接进行包装；
	 * 
	 * @param bytes bytes
	 * @return ByteArray
	 */
	public static ByteArray wrap(byte[] bytes) {
		return new ByteArray(bytes);
	}

	/**
	 * 对指定的字节数组直接进行包装；
	 * 
	 * @param oneByte oneByte
	 * @return ByteArray
	 */
	public static ByteArray wrap(byte oneByte) {
		return new ByteArray(new byte[] { oneByte });
	}
	
	/**
	 * 对指定的字节数组的副本进行包装；
	 * 
	 * @param bytes bytes
	 * @return ByteArray
	 */
	public static ByteArray wrapCopyReadonly(byte[] bytes) {
		byte[] replica = Arrays.copyOf(bytes, bytes.length);
		return new ByteArray(replica, true);
	}
	
	/**
	 * 对指定的字节数组直接进行包装；
	 * 
	 * @param bytes bytes
	 * @return ByteArray
	 */
	public static ByteArray wrapReadonly(byte[] bytes) {
		return new ByteArray(bytes, true);
	}
	
	/**
	 * 对指定的字节数组直接进行包装；
	 * 
	 * @param oneByte oneByte
	 * @return ByteArray
	 */
	public static ByteArray wrapReadonly(byte oneByte) {
		return new ByteArray(new byte[] { oneByte }, true);
	}

	/**
	 * 将字节内容完整写入指定的输出流；
	 * @param out out
	 * @return int
	 */
	public int writeTo(OutputStream out) {
		try {
			out.write(bytes);
			return bytes.length;
		} catch (IOException e) {
			throw new RuntimeIOException(e.getMessage(), e);
		}
	}

	public InputStream asInputStream() {
		return new ByteArrayInputStream(bytes);
	}

	public ByteBuffer asReadOnlyBuffer() {
		return ByteBuffer.wrap(bytes).asReadOnlyBuffer();
	}
	
//	public ByteArray asReadonly() {
//		if (readonly) {
//			return this;
//		}
//		if (readonlyWrapper == null) {
//			readonlyWrapper = new ByteArray(bytes, true);
//		}
//		return readonlyWrapper;
//	}

	/**
	 * 返回原始的二进制数组的副本；<br>
	 * 
	 * 注：使用者要注意此操作代理的内存性能的影响；
	 * 
	 * @return byte array;
	 */
	public byte[] bytesCopy() {
		return Arrays.copyOf(bytes, bytes.length);
	}

	/**
	 * 返回原始的字节数组；
	 * 
	 * <br>
	 * 如果字节数组被标识为只读，调用此方法将引发异常 IllegalStateException；
	 * 
	 * 注：应谨慎操作此方法返回的字节数组，通常的原则是不应更改返回的字节数组，用于只读的情形；
	 * 
	 * @return byte array;
	 */
	public byte[] bytes() {
//		if (readonly) {
//			throw new IllegalStateException("This byte array is readonly!");
//		}
		return bytes;
	}

	public String toHex() {
		return toHex(bytes);
	}

	public String toBase64() {
		return toBase64(bytes);
	}

	public String toBase58() {
		return toBase58(bytes);
	}

	/**
	 * 返回 ByteArray 的 Base58 字符；
	 */
	@Override
	public String toString() {
		return toBase58();
	}

	public static ByteArray parseHex(String hexString) {
		return wrap(fromHex(hexString));
	}

	public static ByteArray parseBase58(String base58String) {
		return wrap(fromBase58(base58String));
	}

	public static ByteArray parseBase64(String base64String) {
		return wrap(fromBase64(base64String));
	}
	
	public static ByteArray parseString(String str, String charset) {
		return wrap(fromString(str, charset));
	}
	
	public static String toHex(byte[] bytes) {
		return HexUtils.encode(bytes);
	}

	public static String toBase58(byte[] bytes) {
		return Base58Utils.encode(bytes);
	}
	public static String toBase64(byte[] bytes) {
		return Base64Utils.encodeToUrlSafeString(bytes);
	}

	public String toString(String charset) {
		return BytesUtils.toString(bytes, charset);
	}
	
	public static byte[] fromHex(String hexString) {
		return HexUtils.decode(hexString);
	}
	
	public static byte[] fromBase58(String base58String) {
		return Base58Utils.decode(base58String);
	}

	public static byte[] fromBase64(String base64String) {
		return Base64Utils.decodeFromUrlSafeString(base64String);
	}
	
	public static byte[] fromString(String str, String charset) {
		return BytesUtils.toBytes(str, charset);
	}

	/**
	 * The object implements the writeExternal method to save its contents
	 * by calling the methods of DataOutput for its primitive values or
	 * calling the writeObject method of ObjectOutput for objects, strings,
	 * and arrays.
	 *
	 * @param out the stream to write the object to
	 * @throws IOException Includes any I/O exceptions that may occur
	 * @serialData Overriding methods should use this tag to describe
	 * the data layout of this Externalizable object.
	 * List the sequence of element types and, if possible,
	 * relate the element to a public/protected field and/or
	 * method of this Externalizable class.
	 */
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		writeTo(os);
		byte[] bts = os.toByteArray();
		out.writeInt(bts.length);
		out.write(bts);
	}

	/**
	 * The object implements the readExternal method to restore its
	 * contents by calling the methods of DataInput for primitive
	 * types and readObject for objects, strings and arrays.  The
	 * readExternal method must read the values in the same sequence
	 * and with the same types as were written by writeExternal.
	 *
	 * @param in the stream to read data from in order to restore the object
	 * @throws IOException            if I/O errors occur
	 * @throws ClassNotFoundException If the class for an object being
	 *                                restored cannot be found.
	 */
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		int len = in.readInt();
		byte[] bts = new byte[len];
		in.readFully(bts);

		this.bytes = bts;
	}
}
