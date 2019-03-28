package com.jd.blockchain.utils.io;

/**
 * @author huanghaiquan
 *
 */
public class BytesSlice implements BytesSerializable {

	public static final BytesSlice EMPTY = new BytesSlice(new byte[0], 0, 0);

	private byte[] bytes;

	private int dataOffset;

	private int size;

	public BytesSlice() {
	}

	public BytesSlice(byte[] bytes) {
		this(bytes, 0, bytes.length);
	}
	
	public BytesSlice(byte[] bytes, int offset) {
		this(bytes, offset, bytes.length - offset);
	}

	public BytesSlice(byte[] bytes, int offset, int size) {
		if (offset + size > bytes.length) {
			throw new IndexOutOfBoundsException();
		}
		this.bytes = bytes;
		this.dataOffset = offset;
		this.size = size;
	}

	public boolean isEmpty() {
		return size == 0;
	}

	public int getSize() {
		return size;
	}

	/**
	 * 返回首个字节；
	 * 
	 * @return byte
	 */
	public byte getByte() {
		return getByte(0);
	}

	/**
	 * 返回首个字节；
	 * @param offset offset
	 * @return byte
	 */
	public byte getByte(int offset) {
		int off = this.dataOffset + offset;
		checkBoundary(off, 1);
		return bytes[off];
	}

	public char getChar() {
		return getChar(0);
	}

	public char getChar(int offset) {
		int off = this.dataOffset + offset;
		checkBoundary(off, 2);
		return BytesUtils.toChar(bytes, off);
	}

	public short getShort() {
		return getShort(0);
	}

	public short getShort(int offset) {
		int off = this.dataOffset + offset;
		checkBoundary(off, 2);
		return BytesUtils.toShort(bytes, off);
	}

	public int getInt() {
		return getInt(0);
	}

	/**
	 * 从指定的偏移量开始，读取连续 4 个字节，转换为 int 类型的数值返回；
	 * 
	 * @param offset offset
	 * @return int
	 */
	public int getInt(int offset) {
		int off = this.dataOffset + offset;
		checkBoundary(off, 4);
		return BytesUtils.toInt(bytes, off);
	}

	public long getLong() {
		return getLong(0);
	}

	/**
	 * 从指定的偏移量开始，读取连续 8 个字节，转换为 long 类型的数值返回；
	 * 
	 * @param offset offset
	 * @return long
	 */
	public long getLong(int offset) {
		int off = this.dataOffset + offset;
		checkBoundary(off, 8);
		return BytesUtils.toLong(bytes, off);
	}

	public String getString() {
		return BytesUtils.toString(bytes, dataOffset, size);
	}

	public BytesInputStream getInputStream() {
		return getInputStream(0);
	}

	public BytesInputStream getInputStream(int offset) {
		int off = this.dataOffset + offset;
		int s = size;
		checkBoundary(off, s);
		return new BytesInputStream(bytes, off, s);
	}

	// public InputStream asInputStream() {
	// return new ByteArrayInputStream(bytes, dataOffset, size);
	// }

	public byte[] getBytesCopy() {
		if (size == 0) {
			return BytesUtils.EMPTY_BYTES;
		}
		byte[] copy = new byte[size];
		System.arraycopy(bytes, dataOffset, copy, 0, size);
		return copy;
	}
	
	public byte[] getBytesCopy(int offset) {
		return getBytesCopy(offset, getSize() - offset) ;
	}
	
	public byte[] getBytesCopy(int offset, int size) {
		int newOffset = dataOffset + offset;
		checkBoundary(newOffset, size);
		
		if (size == 0) {
			return BytesUtils.EMPTY_BYTES;
		}
		byte[] copy = new byte[size];
		System.arraycopy(bytes, newOffset, copy, 0, size);
		return copy;
	}

	protected byte[] getOriginBytes() {
		return bytes;
	}

	protected int getOriginOffset() {
		return dataOffset;
	}

	protected void checkBoundary(int offset, int len) {
		// assert offset >= dataOffset
		// && offset + len <= dataOffset + this.size : "The accessing index is out of
		// BytesSlice's bounds!";
		if (offset < dataOffset || offset + len > dataOffset + this.size) {
			throw new IndexOutOfBoundsException("The accessing index is out of BytesSlice's bounds!");
		}
	}

	public BytesSlice getSlice(int offset) {
		return getSlice(offset,  getSize() - offset);
	}
	
	public BytesSlice getSlice(int offset, int size) {
		int newOffset = dataOffset + offset;
		checkBoundary(newOffset, size);
		return new BytesSlice(bytes, newOffset, size);
	}

	@Override
	public byte[] toBytes() {
		return getBytesCopy();
	}

}
