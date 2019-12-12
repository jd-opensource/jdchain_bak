package com.jd.blockchain.utils.io;

public class SingleBytesSliceArray extends BytesSlice implements BytesSlices {

	private int totalSize;

	/**
	 * @param dataBytes
	 *            数据；
	 * @param offset
	 *            初始的偏移量；
	 * @param dataOffset
	 *            数据的起始偏移量；
	 * @param count
	 *            数据片段的总数；
	 * @param size
	 *            单个数据片段的大小；
	 */
	/**
	 * @param dataBytes
	 * @param offset
	 * @param dataOffset
	 * @param dataSize
	 */
	private SingleBytesSliceArray(byte[] dataBytes, int totalSize, int dataOffset, int dataSize) {
		super(dataBytes, dataOffset, dataSize);
		this.totalSize = totalSize;
	}

	@Override
	public int getTotalSize() {
		return totalSize;
	}

	@Override
	public int getCount() {
		return 1;
	}

	@Override
	public BytesSlice getDataSlice(int idx) {
		if (idx != 0) {
			throw new IllegalArgumentException("The specified idx is out of bound!");
		}
		return this;
	}

	public static SingleBytesSliceArray resolveDynamic(BytesInputStream bytesStream) {
		int p1  = bytesStream.getPosition();
		int size = NumberMask.NORMAL.resolveMaskedNumber(bytesStream);
		int dataOffset = bytesStream.getPosition();
		bytesStream.skip(size);
		int totalSize = bytesStream.getPosition() - p1;
		return new SingleBytesSliceArray(bytesStream.getOriginBytes(), totalSize, dataOffset, size);
	}
	
	public static SingleBytesSliceArray create(BytesInputStream bytesStream, int itemSize) {
		int offset = bytesStream.getPosition();
		bytesStream.skip(itemSize);
		return new SingleBytesSliceArray(bytesStream.getOriginBytes(), itemSize, offset, itemSize);
	}
	
	public static SingleBytesSliceArray create(byte[] dataBytes, int offset, int size) {
		return new SingleBytesSliceArray(dataBytes, size, offset, size);
	}

}
