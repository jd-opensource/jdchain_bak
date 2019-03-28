package com.jd.blockchain.utils.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * {@link NumberMask} 数值掩码； <br>
 * 
 * {@link NumberMask} 定义了使用有限的字节表示一个特定范围的正整数的格式；<br>
 * 
 * {@link NumberMask} 用于表示数值的字节长度是动态的，根据数值的范围而定；<br>
 * 
 * 这个特点使得 {@link NumberMask} 适用于表示小数据片的头部尺寸；尤其当一个数据块是由大量表示不同属性的小数据片构成时，使用
 * {@link NumberMask} 可以得到更紧凑的字节流；
 * 
 * <p>
 * 注：{@link NumberMask} 处理的数值范围处于 32 位整数(int)的范围，不处理 64 位整数(long)的情况; <br>
 * 这样的设计一方面是因为 {@link NumberMask#NORMAL} 已经可以表示通常情况下的足够大的数值范围(1G)，而 64
 * 位整数(long)表示的长度已经远远超出单台计算机能够处理的内存数量；<br>
 * 另一方面是，采用嵌套的方式使用{@link NumberMask}也可以达到表示无限大小的数据的目的（如：使用 {@link NumberMask}
 * 表示数据片数量，对每一个数据片再用一个{@link NumberMask}表示其数据长度）；
 * 
 * @author huanghaiquan
 *
 */
public enum NumberMask {

	/**
	 * 最短的头部，占用1字节；<br>
	 * 
	 * 表示字节内容的长度小于 256 (2^8)；
	 */
	TINY((byte) 0),

	/**
	 * 短的头部，最多占用2字节；<br>
	 * 
	 * <pre>
	 * 记录字节数据大小的头部占用的字节数是动态的，根据数据的大小而定，最少1个字节，最大2个字节；
	 * 
	 * 使用首个字节的最高两位作为标识位指示头部的长度；
	 * 
	 * 当字节内容的长度小于 128 (2^7)，则头部占用1个字节，最高位标识为 0 ;
	 * 
	 * 当字节内容的长度小于 32768 (2^15, 32KB)，则头部占用2个字节，最高位标识为 1 ;
	 * 
	 * 
	 * </pre>
	 */
	SHORT((byte) 1),

	/**
	 * 短的头部，最多占用4字节； <br>
	 * 
	 * <pre>
	 * 记录字节数据大小的头部占用的字节数是动态的，根据数据的大小而定，最少1个字节，最大4个字节；
	 * 
	 * 使用首个字节的最高两位作为标识位指示头部的长度；
	 * 
	 * 当字节内容的长度小于 64 (2^6)，则头部占用1个字节，最高位标识为 0 ;
	 * 
	 * 当字节内容的长度小于 16384 (2^14, 16KB)，则头部占用2个字节，最高位标识为 1 ;
	 * 
	 * 当字节内容的长度小于 4194304 (2^22, 4MB)，则头部占用3个字节，最高位标识为 2 ;
	 * 
	 * 当字节内容的长度小于 1073741824 (2^30, 1GB)，则头部占用4个字节，最高位标识为 3 ;
	 * 
	 * </pre>
	 */
	NORMAL((byte) 2);

	// 不考虑 long 的情况，因为 long 的数值表示的长度已经远远超出单台计算机能够处理的内存数量；

	// /**
	// * 短的头部，最多占用8字节；
	// */
	// LONG((byte) 3);

	/**
	 * 掩码位的个数；
	 */
	public final byte BIT_COUNT;

	/**
	 * 头部长度的最大值；
	 */
	public final int MAX_HEADER_LENGTH;

	public final int MAX_BOUNDARY_SIZE;

	/**
	 * 此常量对于 TINY、SHORT、NORMAL 有效；
	 */
	public final int BOUNDARY_SIZE_0;
	public final int BOUNDARY_SIZE_1;
	public final int BOUNDARY_SIZE_2;
	public final int BOUNDARY_SIZE_3;

	 private int[] boundarySizes;

	private NumberMask(byte bitCount) {
		this.BIT_COUNT = bitCount;
		this.MAX_HEADER_LENGTH = 1 << bitCount;
		this.boundarySizes = new int[MAX_HEADER_LENGTH];
		for (byte i = 0; i < MAX_HEADER_LENGTH; i++) {
			boundarySizes[i] = computeBoundarySize((byte) (i + 1));
		}

		this.MAX_BOUNDARY_SIZE = boundarySizes[MAX_HEADER_LENGTH - 1];
		if (bitCount == 0) {
			// TINY;
			BOUNDARY_SIZE_0 = boundarySizes[0];
			BOUNDARY_SIZE_1 = -1;
			BOUNDARY_SIZE_2 = -1;
			BOUNDARY_SIZE_3 = -1;
		} else if (bitCount == 1) {
			// SHORT;
			BOUNDARY_SIZE_0 = boundarySizes[0];
			BOUNDARY_SIZE_1 = boundarySizes[1];
			BOUNDARY_SIZE_2 = -1;
			BOUNDARY_SIZE_3 = -1;
		} else if (bitCount == 2) {
			// NORMAL;
			BOUNDARY_SIZE_0 = boundarySizes[0];
			BOUNDARY_SIZE_1 = boundarySizes[1];
			BOUNDARY_SIZE_2 = boundarySizes[2];
			BOUNDARY_SIZE_3 = boundarySizes[3];
		} else {
			throw new IllegalArgumentException("Illegal bitCount!");
		}
	}

	/**
	 * 在指定的头部长度下能够表示的数据大小的临界值（不含）；
	 *
	 * @param headerLength
	 *            值范围必须大于 0 ，且小于等于 {@link #MAX_HEADER_LENGTH}
	 * @return
	 */
	public int getBoundarySize(int headerLength) {
		return boundarySizes[headerLength - 1];
	}

	private int computeBoundarySize(int headerLength) {
		// 不考虑 long 的情况；
		// long boundarySize = 1L << (headerLength * 8 - BIT_COUNT);

		int boundarySize = 1 << (headerLength * 8 - BIT_COUNT);
		return boundarySize;
	}

	/**
	 * 获取能够表示指定的数值的掩码长度，即掩码所需的字节数；<br>
	 * 
	 * @param number
	 *            要表示的数值；如果值范围超出掩码的有效范围，将抛出 {@link IllegalArgumentException} 异常；
	 * @return
	 */
	public int getMaskLength(int number) {
		if (number > -1) {
			if (number < BOUNDARY_SIZE_0) {
				return 1;
			}
			if (number < BOUNDARY_SIZE_1) {
				return 2;
			}
			if (number < BOUNDARY_SIZE_2) {
				return 3;
			}
			if (number < BOUNDARY_SIZE_3) {
				return 4;
			}
		}
		throw new IllegalArgumentException("Number is out of the illegal range! --[number=" + number + "]");
	}

	/**
	 * 生成指定数值的掩码；
	 * 
	 * @param number
	 *            要表示的数值；如果值范围超出掩码的有效范围，将抛出 {@link IllegalArgumentException} 异常；
	 * @return
	 */
	public byte[] generateMask(int number) {
		// 计算掩码占用的字节长度；
		int maskLen = getMaskLength(number);
		byte[] maskBytes = new byte[maskLen];
		writeMask(number, maskLen, maskBytes, 0);
		return maskBytes;
	}

	public int writeMask(int number, byte[] buffer, int offset) {
		// 计算掩码占用的字节长度；
		int maskLen = getMaskLength(number);
		return writeMask(number, maskLen, buffer, offset);
	}

	private int writeMask(int number, int maskLen, byte[] buffer, int offset) {
		// 计算掩码占用的字节长度；
		for (int i = maskLen; i > 0; i--) {
			buffer[offset + i - 1] = (byte) ((number >>> 8 * (maskLen - i)) & 0xFF);
		}

		// 计算头字节的标识位；
		byte indicatorByte = (byte) ((maskLen - 1) << (8 - BIT_COUNT));
		// 设置标识位；
		buffer[offset] = (byte) (indicatorByte | buffer[offset]);
		return maskLen;
	}

	/**
	 * 生成指定数值的掩码并写入到指定的输出流；
	 * 
	 * @param number
	 * @param out
	 * @return 写入的字节数；
	 */
	public int writeMask(int number, OutputStream out) {
		// 生成数据尺寸掩码；
		byte[] maskBytes = generateMask(number);

		try {
			out.write(maskBytes);
			return maskBytes.length;
		} catch (IOException e) {
			throw new RuntimeIOException(e.getMessage(), e);
		}
	}

	/**
	 * 解析掩码的头字节获得该掩码实例的完整长度；
	 * 
	 * @param bytes
	 *            掩码的头字节；即掩码的字节序列的首个字节；
	 * @return 返回掩码实例的完整长度；<br>
	 *         注：在字节流中，对首字节解析获取该值后减 1，可以得到该掩码后续要读取的字节长度；
	 */
	public int resolveMaskLength(BytesSlice bytes) {
		return resolveMaskLength(bytes.getByte());
	}

	public int resolveMaskLength(BytesSlice bytes, int offset) {
		return resolveMaskLength(bytes.getByte(offset));
	}

	/**
	 * 解析掩码的头字节获得该掩码实例的完整长度；
	 * 
	 * @param headByte
	 *            掩码的头字节；即掩码的字节序列的首个字节；
	 * @return 返回掩码实例的完整长度；<br>
	 *         注：在字节流中，对首字节解析获取该值后减 1，可以得到该掩码后续要读取的字节长度；
	 */
	public int resolveMaskLength(byte headByte) {
		int len = ((headByte & 0xFF) >>> (8 - BIT_COUNT)) + 1;
		if (len < 1) {
			throw new IllegalArgumentException(
					"Illegal length [" + len + "] was resolved from the head byte of NumberMask!");
		}
		if (len > MAX_HEADER_LENGTH) {
			throw new IllegalArgumentException(
					"Illegal length [" + len + "] was resolved from the head byte of NumberMask!");
		}
		return len;
	}

	public int resolveMaskedNumber(byte[] markBytes) {
		return resolveMaskedNumber(markBytes, 0);
	}

	/**
	 * 从字节中解析掩码表示的数值；
	 * 
	 * @param markBytes
	 * @param headPos
	 * @return
	 */
	public int resolveMaskedNumber(byte[] markBytes, int headPos) {
		int maskLen = resolveMaskLength(markBytes[headPos]);

		// 清除首字节的标识位；
		byte numberHead = (byte) (markBytes[headPos] & (0xFF >>> BIT_COUNT));

		// 转换字节大小；
		int number = numberHead & 0xFF;
		for (int i = 1; i < maskLen; i++) {
			number = (number << 8) | (markBytes[headPos + i] & 0xFF);
		}

		return number;
	}

	/**
	 * 从字节中解析掩码表示的数值；
	 * @param bytes bytes
	 * @return int
	 */
	public int resolveMaskedNumber(BytesSlice bytes) {
		return resolveMaskedNumber(bytes, 0);
	}

	/**
	 * 从字节中解析掩码表示的数值；
	 * @param bytes bytes
	 * @param offset offset
	 * @return int
	 */
	public int resolveMaskedNumber(BytesSlice bytes, int offset) {
		byte headByte = bytes.getByte(offset);
		int maskLen = resolveMaskLength(headByte);

		// 清除首字节的标识位；
		byte numberHead = (byte) (headByte & (0xFF >>> BIT_COUNT));

		// 转换字节大小；
		int number = numberHead & 0xFF;
		for (int i = 1; i < maskLen; i++) {
			number = (number << 8) | (bytes.getByte(offset + i) & 0xFF);
		}

		return number;
	}

	/**
	 * 从字节中解析掩码表示的数值；
	 * @param bytesStream
	 * @return int
	 */
	public int resolveMaskedNumber(BytesInputStream bytesStream) {
		byte headByte = bytesStream.readByte();
		int maskLen = resolveMaskLength(headByte);

		// 清除首字节的标识位；
		byte numberHead = (byte) (headByte & (0xFF >>> BIT_COUNT));

		// 转换字节大小；
		int number = numberHead & 0xFF;
		for (int i = 1; i < maskLen; i++) {
			number = (number << 8) | (bytesStream.readByte() & 0xFF);
		}

		return number;
	}

	/**
	 * 从字节流解析掩码表示的数值；
	 * 
	 * @param in
	 * @return
	 */
	public int resolveMaskedNumber(InputStream in) {
		try {
			byte[] buff = new byte[MAX_HEADER_LENGTH];
			// 解析头字节；
			int len = in.read(buff, 0, 1);
			if (len < 1) {
				throw new IllegalArgumentException("No enough bytes for the size header's indicator byte!");
			}
			int maskLen = resolveMaskLength(buff[0]);
			if (maskLen > 1) {
				in.read(buff, 1, maskLen - 1);
			}

			return resolveMaskedNumber(buff, 0);
		} catch (IOException e) {
			throw new RuntimeIOException(e.getMessage(), e);
		}
	}

}