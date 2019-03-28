package com.jd.blockchain.utils.io;

import java.io.IOException;
import java.io.InputStream;

public class BytesInputStream extends InputStream {

	private byte[] bytes;

	private int posistion;

	private int tail;

	public BytesInputStream(byte[] bytes, int offset, int size) {
		this.bytes = bytes;
		this.posistion = offset;
		this.tail = offset + size;
	}

	protected int getPosition() {
		return posistion;
	}

	protected byte[] getOriginBytes() {
		return bytes;
	}

	/**
	 * 返回剩余的字节数；
	 * 
	 * @return int
	 */
	public int getSize() {
		return tail - posistion;
	}

	/**
	 * 读取接下来的 1 个字节并返回；
	 * 
	 * @return byte
	 */
	public byte readByte() {
		int off = this.posistion;
		checkBoundary(off, 1);
		posistion++;
		return bytes[off];
	}

	/**
	 * 读取接下来的 2 个字节并返回16位字符；
	 * 
	 * @return char
	 */
	public char readChar() {
		int off = posistion;
		checkBoundary(off, 2);
		char ch = BytesUtils.toChar(bytes, off);
		posistion += 2;
		return ch;
	}

	/**
	 * 读取接下来的 2 个字节并返回16位整数；
	 * 
	 * @return short
	 */
	public short readShort() {
		int off = posistion;
		checkBoundary(off, 2);
		short n = BytesUtils.toShort(bytes, off);
		posistion += 2;
		return n;
	}

	/**
	 * 读取接下来的 4 个字节并返回32位整数；
	 * 
	 * @return int
	 */
	public int readInt() {
		int off = posistion;
		checkBoundary(off, 4);
		int n = BytesUtils.toInt(bytes, off);
		posistion += 4;
		return n;
	}

	/**
	 * 读取接下来的 8 个字节并返回64位整数；
	 * 
	 * @return long
	 */
	public long readLong() {
		int off = posistion;
		checkBoundary(off, 8);
		long n = BytesUtils.toLong(bytes, off);
		posistion += 8;
		return n;
	}

	/**
	 * 读取指定数量的字节并返回对应的字符串；
	 * 
	 * @param size size
	 * @return String
	 */
	public String readString(int size) {
		int off = posistion;
		checkBoundary(off, size);
		String s = BytesUtils.toString(bytes, off, size);
		posistion += size;
		return s;
	}

	/**
	 * 读取指定数量的字节并返回对应的字符串；
	 * @param size size
	 * @param charset charset
	 * @return String
	 */
	public String readString(int size, String charset) {
		int off = posistion;
		checkBoundary(off, size);
		String s = BytesUtils.toString(bytes, off, size, charset);
		posistion += size;
		return s;
	}

	/**
	 * 读取指定数量的字节并返回;
	 * 
	 * @param size size
	 * @return 返回指定数量的字节拷贝；
	 */
	public byte[] readBytes(int size) {
		byte[] copy = new byte[size];
		readBytes(copy, 0, size);
		return copy;
	}

	public int readBytes(byte[] buffer, int offset, int size) {
		int off = posistion;
		int s = tail - posistion;
		s = s < size ? s : size;
		checkBoundary(off, s);
		System.arraycopy(bytes, off, buffer, offset, s);
		posistion += s;
		return s;
	}

	/**
	 * 从当前位置开始读取, 返回剩余字节的片段；；
	 * 注：此操作不影响游标位置；
	 * @return BytesSlice
	 */
	public BytesSlice getSlice() {
		if (tail == posistion) {
			return BytesSlice.EMPTY;
		}
		return getSlice(tail - posistion);
	}

	/**
	 * 从当前位置开始读取, 返回指定数量的字节片段；
	 * 
	 * <br>
	 * 注：此操作不影响游标位置；
	 * 
	 * @param size size
	 * @return byteSlice
	 */
	public BytesSlice getSlice(int size) {
		int off = posistion;
		checkBoundary(off, size);
		BytesSlice copy = new BytesSlice(bytes, off, size);
		return copy;
	}

	/**
	 * 从当前位置开始读取, 返回指定数量的字节片段；<br>
	 * 
	 * 注：此操作将数据游标位置向后移动指定数量的字节；；
	 * 
	 * @param size size
	 * @return byteSlice
	 */
	public BytesSlice readSlice(int size) {
		BytesSlice copy = getSlice(size);
		posistion += size;
		return copy;
	}

	public void skip(int size) {
		checkBoundary(posistion, size);
		posistion += size;
	}

	private void checkBoundary(int off, int len) {
		// assert off >= posistion && off + len <= tail : "The accessing index is out of
		// BytesInputStream's bounds!";
		if (off < posistion || off + len > tail) {
			throw new IndexOutOfBoundsException("The accessing index is out of BytesInputStream's bounds!");
		}
	}

	/**
	 * 注：遵循 JDK 接口要求，读取一个字节以 int 类型返回；
	 */
	@Override
	public int read() throws IOException {
		return readByte() & 0xFF;
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return readBytes(b, off, len);
	}

	@Override
	public int read(byte[] b) throws IOException {
		return read(b, 0, b.length);
	}

	@Override
	public int available() throws IOException {
		return getSize();
	}

	@Override
	public long skip(long n) throws IOException {
		if (n < 0) {
			throw new IllegalArgumentException("Specified a negative number of bytes to be skipped!");
		}
		if (n >= Integer.MAX_VALUE) {
			throw new IllegalArgumentException("The number of bytes to be skipped is out of max value!");
		}
		skip((int) n);
		return n;
	}
}
