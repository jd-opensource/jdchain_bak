package com.jd.blockchain.utils.io;

public class BytesSliceArrayWrapper implements BytesSlices {

	private BytesSlice slice;

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
	private BytesSliceArrayWrapper(BytesSlice slice) {
		this.slice = slice;
	}

	@Override
	public int getTotalSize() {
		return slice.getSize();
	}

	@Override
	public int getCount() {
		return 1;
	}

	@Override
	public BytesSlice getDataSlice(int id) {
		if (id != 0) {
			throw new IndexOutOfBoundsException("The specified idx is out of bound!");
		}
		return slice;
	}

	public static BytesSlices wrap(BytesSlice slice) {
		if (slice instanceof BytesSlices) {
			return (BytesSlices)slice;
		}
		return new BytesSliceArrayWrapper(slice);
	}

}
