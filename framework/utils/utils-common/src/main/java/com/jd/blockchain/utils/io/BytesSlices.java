package com.jd.blockchain.utils.io;

public interface BytesSlices {
	
	/**
	 * 总的字节数；包含各个子数据片段之间间隔的头部字节；
	 * @return int
	 */
	int getTotalSize();

	/**
	 * 包含的子片段的数量；
	 * 
	 * @return int
	 */
	int getCount();
	
	/**
	 * 返回一个子数据片段；
	 * @param idx 子数据片段的编号；大于等于 0 ，小于总数 {@link #getCount()}；
	 * @return bytesSlice
	 */
	BytesSlice getDataSlice(int idx);

}
