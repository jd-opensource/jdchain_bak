package com.jd.blockchain.utils.io;

public class FixedBytesSliceArray implements BytesSlices {

	private byte[] dataBytes;

	private int totalSize;

	private int dataOffset;

	private int itemCount;

	private int itemSize;

	/**
	 * @param dataBytes
	 *            数据；
	 * @param totalSize
	 *            初始的偏移量；
	 * @param dataOffset
	 *            数据的起始偏移量；
	 * @param itemCount
	 *            数据片段的总数；
	 * @param itemSize
	 *            单个数据片段的大小；
	 */
	private FixedBytesSliceArray(byte[] dataBytes, int totalSize, int dataOffset, int itemCount, int itemSize) {
		if ((dataOffset + itemCount * itemSize) > dataBytes.length) {
			throw new IllegalArgumentException("The tail index of all slices is out of bound of data bytes!");
		}
		this.dataBytes = dataBytes;
		// this.length = (dataOffset + count * size) - totalSize;
		this.totalSize = totalSize;
		this.dataOffset = dataOffset;
		this.itemCount = itemCount;
		this.itemSize = itemSize;
	}

	@Override
	public int getTotalSize() {
		return totalSize;
	}

	@Override
	public int getCount() {
		return itemCount;
	}

	@Override
	public BytesSlice getDataSlice(int idx) {
		if (idx < 0 || idx >= itemCount) {
			throw new IllegalArgumentException("The specified idx is out of bound!");
		}
		return new BytesSlice(dataBytes, dataOffset + idx * itemSize, itemSize);
	}

	public static FixedBytesSliceArray resolve(BytesInputStream bytesStream, int itemSize) {
		int p1 = bytesStream.getPosition();

		int itemCount = NumberMask.NORMAL.resolveMaskedNumber(bytesStream);
		
		int dataOffset = bytesStream.getPosition();
		
		bytesStream.skip(itemCount * itemSize);
		int totalSize = bytesStream.getPosition() - p1;

		return new FixedBytesSliceArray(bytesStream.getOriginBytes(), totalSize, dataOffset, itemCount, itemSize);
	}

}
