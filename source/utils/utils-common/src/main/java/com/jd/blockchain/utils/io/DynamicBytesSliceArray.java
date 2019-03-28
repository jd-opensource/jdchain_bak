package com.jd.blockchain.utils.io;

public class DynamicBytesSliceArray implements BytesSlices {
	
	private byte[] dataBytes;

	private int totalSize;
	
	private int[] offsets;

	private int[] sizes;
	
	private DynamicBytesSliceArray(byte[] dataBytes, int totalSize, int[] offsets, int[] sizes) {
		this.dataBytes = dataBytes;
		this.totalSize = totalSize;
		this.offsets = offsets;
		this.sizes = sizes;
	}

	@Override
	public int getTotalSize() {
		return totalSize;
	}

	@Override
	public int getCount() {
		return sizes.length;
	}

	@Override
	public BytesSlice getDataSlice(int idx) {
		return new BytesSlice(dataBytes, offsets[idx], sizes[idx]);
	}

//	public static DynamicBytesSliceArray resolve(byte[] dataBytes, int offset) {
//		return resolve(new BytesSlice(dataBytes, 0, dataBytes.length), 0);
//	}
	
	public static DynamicBytesSliceArray resolve(BytesInputStream bytesStream) {
		int p1 = bytesStream.getPosition();
		int count = NumberMask.NORMAL.resolveMaskedNumber(bytesStream);
		
		int[] offsets = new int[count];
		int[] sizes = new int[count];
		
		int size;
		for (int i = 0; i < count; i++) {
			size = NumberMask.NORMAL.resolveMaskedNumber(bytesStream);
			sizes[i] = size;
			offsets[i] = bytesStream.getPosition();
			bytesStream.skip(size);
		}
		int totalSize = bytesStream.getPosition() -  p1;
		return new DynamicBytesSliceArray(bytesStream.getOriginBytes(), totalSize, offsets, sizes);
	}

}
