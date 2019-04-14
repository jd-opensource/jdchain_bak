package com.jd.blockchain.utils.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BytesEncoding {

	public static int writeInNormal(byte[] data, OutputStream out) {
		return write(data, NumberMask.NORMAL, out);
	}

	public static int writeInShort(byte[] data, OutputStream out) {
		return write(data, NumberMask.SHORT, out);
	}

	public static int writeInTiny(byte[] data, OutputStream out) {
		return write(data, NumberMask.TINY, out);
	}

	/**
	 * 写入字节数据；
	 * 
	 * 先写入字节长度的头部，再写入数据；
	 * 
	 * 如果字节数据的长度为 0，则只写入一个空的头部；
	 * 
	 * @param data data
	 * @param dataLengthMask dataLengthMask
	 * @param out out
	 * @return 返回写入的字节数；
	 */
	public static int write(byte[] data, NumberMask dataLengthMask, OutputStream out) {
		try {
			int s = dataLengthMask.writeMask(data == null ? 0 : data.length, out);
			if (data != null) {
				out.write(data, 0, data.length);
				s += data.length;
			}
			return s;
		} catch (IOException e) {
			throw new RuntimeIOException(e.getMessage(), e);
		}
	}

	/**
	 * getOutputSizeInNormal
	 * @param dataSize dataSize
	 * @return int
	 */
	public static int getOutputSizeInNormal(int dataSize) {
		return NumberMask.NORMAL.getMaskLength(dataSize) + dataSize;
	}

	public static int writeInNormal(byte[] data, byte[] buffer) {
		return write(NumberMask.NORMAL, data, 0, buffer, 0, data.length);
	}

	public static int writeInNormal(byte[] data, byte[] buffer, int bufferOffset) {
		return write(NumberMask.NORMAL, data, 0, buffer, bufferOffset, data.length);
	}

	public static int writeInNormal(byte[] data, byte[] buffer, int bufferOffset, int length) {
		return write(NumberMask.NORMAL, data, 0, buffer, bufferOffset, length);
	}

	public static int writeInNormal(byte[] data, int dataOffset, byte[] buffer, int bufferOffset, int length) {
		return write(NumberMask.NORMAL, data, dataOffset, buffer, bufferOffset, length);
	}

	public static int write(NumberMask dataLengthMask, byte[] data, byte[] buffer) {
		return write(dataLengthMask, data, 0, buffer, 0, data.length);
	}

	public static int write(NumberMask dataLengthMask, byte[] data, byte[] buffer, int bufferOffset) {
		return write(dataLengthMask, data, 0, buffer, bufferOffset, data.length);
	}

	public static int write(NumberMask dataLengthMask, byte[] data, byte[] buffer, int bufferOffset, int length) {
		return write(dataLengthMask, data, 0, buffer, bufferOffset, length);
	}

	public static int write(NumberMask dataLengthMask, byte[] data, int dataOffset, byte[] buffer, int bufferOffset,
			int length) {
		int s = dataLengthMask.writeMask(data == null ? 0 : data.length, buffer, bufferOffset);
		bufferOffset += s;
		if (data != null) {
			System.arraycopy(data, dataOffset, buffer, bufferOffset, length);
			s += length;
		}
		return s;
	}

	public static byte[] readInTiny(byte[] buffer, int offset) {
		return read(NumberMask.TINY, buffer, offset);
	}

	public static byte[] readInShort(byte[] buffer, int offset) {
		return read(NumberMask.SHORT, buffer, offset);
	}

	public static byte[] readInNormal(byte[] buffer, int offset) {
		return read(NumberMask.NORMAL, buffer, offset);
	}

	public static byte[] read(NumberMask dataLengthMask, byte[] buffer, int offset) {
		int size = dataLengthMask.resolveMaskedNumber(buffer, offset);
		int maskLen = dataLengthMask.resolveMaskLength(buffer[offset]);
		offset += maskLen;
		byte[] data = new byte[size];
		System.arraycopy(buffer, offset, data, 0, size);
		return data;
	}

	public static int write(ByteArray data, NumberMask dataLengthMask, OutputStream out) {
		int s = dataLengthMask.writeMask(data == null ? 0 : data.size(), out);
		if (data != null) {
			s += data.writeTo(out);
		}
		return s;
	}
	
	public static byte[] readInTiny(InputStream in) {
		return read(NumberMask.TINY, in);
	}
	
	public static byte[] readInNormal(InputStream in) {
		return read(NumberMask.NORMAL, in);
	}

	public static byte[] readInShort(InputStream in) {
		return read(NumberMask.SHORT, in);
	}

	/**
	 * 读取头部和内容；
	 * 如果头部标识的数据长度为 0，则返回一个长度为 0 的字节数组；
	 * @param dataLengthMask dataLengthMask
	 * @param in in
	 * @return byte[]
	 */
	public static byte[] read(NumberMask dataLengthMask, InputStream in) {
		try {
			int size = dataLengthMask.resolveMaskedNumber(in);
			if (size == 0) {
				return BytesUtils.EMPTY_BYTES;
			}
			byte[] data = new byte[size];
			int len = in.read(data, 0, size);
			if (len < size) {
				throw new IllegalArgumentException("No enough bytes was read as the size header indicated!");
			}
			return data;
		} catch (IOException e) {
			throw new RuntimeIOException(e.getMessage(), e);
		}
	}

//	/**
//	 * 读取头部和内容；
//	 * @param headerMask headerMask
//	 * @param in in
//	 * @return byteArray
//	 * @throws IOException exception
//	 */
//	public static ByteArray readAsByteArray(NumberMask headerMask, InputStream in) throws IOException {
//		byte[] data = read(headerMask, in);
//		return ByteArray.wrap(data);
//	}

}
