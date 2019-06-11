package com.jd.blockchain.utils;

import java.io.IOException;
import java.io.OutputStream;

import com.jd.blockchain.utils.codec.Base58Utils;
import com.jd.blockchain.utils.io.BytesSerializable;
import com.jd.blockchain.utils.io.BytesUtils;
import com.jd.blockchain.utils.io.RuntimeIOException;

/**
 * Bytes 被设计为不可变对象；
 * 
 * @author huanghaiquan
 *
 */
public class Bytes implements BytesSerializable {

	public static final Bytes EMPTY = new Bytes(BytesUtils.EMPTY_BYTES);

	private static final int MAX_CACHE = 256;

	private static Bytes[] INT_BYTES;

	private static Bytes[] LONG_BYTES;

	static {
		INT_BYTES = new Bytes[MAX_CACHE];
		LONG_BYTES = new Bytes[MAX_CACHE];
		for (int i = 0; i < MAX_CACHE; i++) {
			INT_BYTES[i] = new Bytes(BytesUtils.toBytes((int) i));
			LONG_BYTES[i] = new Bytes(BytesUtils.toBytes((long) i));
		}
	}

	private final Bytes prefix;

	private final byte[] data;

	private final int hashCode;

	public int size() {
		return prefix == null ? data.length : prefix.size() + data.length;
	}

	public Bytes() {
		prefix = null;
		data = null;
		hashCode = hashCode(1);
	}

	public Bytes(byte[] data) {
		if (data == null) {
			throw new IllegalArgumentException("data is null!");
		}
		this.prefix = null;
		this.data = data;
		hashCode = hashCode(1);
	}

	public Bytes(Bytes prefix, byte[] data) {
		if (data == null) {
			throw new IllegalArgumentException("data is null!");
		}
		this.prefix = prefix;
		this.data = data;
		// setPrefix(prefix);
		hashCode = hashCode(1);
	}

	public Bytes(Bytes prefix, Bytes data) {
		// setData(data.toBytes());
		// setPrefix(prefix);
		if (data == null) {
			throw new IllegalArgumentException("data is null!");
		}
		this.prefix = prefix;
		this.data = data.toBytes();

		hashCode = hashCode(1);
	}

	// private void setData(byte[] data) {
	// if (data == null) {
	// throw new IllegalArgumentException("data is null!");
	// }
	// this.data = data;
	// }

	/**
	 * 返回当前的字节数组（不包含前缀对象）；
	 * 
	 * @return byte[]
	 */
	protected byte[] getDirectBytes() {
		return data;
	}

	public static Bytes fromString(String str) {
		return new Bytes(BytesUtils.toBytes(str));
	}

	public static Bytes fromBase58(String str) {
		return new Bytes(Base58Utils.decode(str));
	}

	// /**
	// * 连接指定的前缀后面；此操作并不会更改“prefix”参数；
	// *
	// * @param prefix
	// * @return
	// */
	// private Bytes setPrefix(Bytes prefix) {
	// this.prefix = prefix;
	// return this;
	// }

	public Bytes concat(Bytes key) {
		return new Bytes(this, key);
	}

	public Bytes concat(byte[] key) {
		return new Bytes(this, key);
	}

	public int writeTo(OutputStream out) {
		int size = 0;
		if (prefix != null) {
			size = prefix.writeTo(out);
		}
		try {
			out.write(data);
			size += data.length;
			return size;
		} catch (IOException e) {
			throw new RuntimeIOException(e.getMessage(), e);
		}
	}

	private int hashCode(int result) {
		if (prefix != null) {
			result = prefix.hashCode(result);
		}
		for (byte element : data) {
			result = 31 * result + element;
		}

		return result;
	}

	// private static int hashCode(byte a[], int offset, int len) {
	// if (a == null)
	// return 0;
	//
	// int result = 1;
	// for (int i = 0; i < len; i++) {
	// result = 31 * result + a[offset + i];
	// }
	//
	// return result;
	// }

	@Override
	public int hashCode() {
		return hashCode;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Bytes)) {
			return false;
		}
		Bytes oth = (Bytes) obj;
		if (this.hashCode != oth.hashCode) {
			return false;
		}
		boolean prefixIsEqual = false;
		if (this.prefix == null && oth.prefix == null) {
			prefixIsEqual = true;
		} else if (this.prefix == null) {
			prefixIsEqual = false;
		} else {
			prefixIsEqual = this.prefix.equals(oth.prefix);
		}
		if (!prefixIsEqual) {
			return false;
		}
		return BytesUtils.equals(this.data, oth.data);
	}

	public int copyTo(byte[] buffer, int offset, int len) {
		if (len < 0) {
			throw new IllegalArgumentException("Argument len is negative!");
		}
		if (len == 0) {
			return 0;
		}
		int s = 0;
		if (prefix != null) {
			s = prefix.copyTo(buffer, offset, len);
		}
		if (s < len) {
			int l = len - s;
			l = l < data.length ? l : data.length;
			System.arraycopy(data, 0, buffer, offset + s, l);
			s += l;
		}
		return s;
	}

	@Override
	public byte[] toBytes() {
		if (prefix == null || prefix.size() == 0) {
			return data;
		}
		int size = size();
		byte[] buffer = new byte[size];
		copyTo(buffer, 0, size);
		return buffer;
	}

	public String toBase58() {
		return Base58Utils.encode(toBytes());
	}

	public static Bytes fromInt(int value) {
		if (value > -1 && value < MAX_CACHE) {
			return INT_BYTES[value];
		}
		return new Bytes(BytesUtils.toBytes(value));
	}
	
	public String toUTF8String() {
		return BytesUtils.toString(toBytes());
	}

	public static Bytes fromLong(long value) {
		if (value > -1 && value < MAX_CACHE) {
			return LONG_BYTES[(int) value];
		}
		return new Bytes(BytesUtils.toBytes(value));
	}

	/**
	 * 返回 Base58 编码的字符；
	 */
	@Override
	public String toString() {
		return toBase58();
	}

}
