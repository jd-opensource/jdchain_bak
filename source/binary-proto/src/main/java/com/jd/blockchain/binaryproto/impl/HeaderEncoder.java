package com.jd.blockchain.binaryproto.impl;

import com.jd.blockchain.binaryproto.BinarySliceSpec;
import com.jd.blockchain.utils.io.BytesInputStream;
import com.jd.blockchain.utils.io.BytesOutputBuffer;
import com.jd.blockchain.utils.io.BytesSlice;
import com.jd.blockchain.utils.io.BytesSliceArrayWrapper;
import com.jd.blockchain.utils.io.BytesSlices;
import com.jd.blockchain.utils.io.BytesUtils;

/**
 * 分段编码器；
 * 
 * @author huanghaiquan
 *
 */
public class HeaderEncoder implements SliceEncoder {

	public static final int HEAD_BYTES = 12;

	private BinarySliceSpec sliceSpec;

	private int code;

	private long version;

	private String name;

	private String description;

	public int getCode() {
		return code;
	}

	public long getVersion() {
		return version;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public HeaderEncoder(BinarySliceSpec sliceSpec, int code, long version, String name, String description) {
		this.sliceSpec = sliceSpec;
		this.code = code;
		this.version = version;
		this.name = name;
		this.description = description;
	}

	/**
	 * 校验指定的数据契约字节列表的头部是否一致；
	 * 
	 * @param dataContractBytes
	 * @return
	 */
	boolean verifyHeaders(BytesSlice dataContractBytes) {
		int code = resolveCode(dataContractBytes);
		if (code != this.code) {
			return false;
		}
		long version = resolveVersion(dataContractBytes);
		return version == this.version;
	}
	
	public static int resolveCode(BytesSlice dataContractBytes) {
		return dataContractBytes.getInt();
	}
	
	public static long resolveVersion(BytesSlice dataContractBytes) {
		return dataContractBytes.getLong(4);
	}

	@Override
	public BinarySliceSpec getSliceSpecification() {
		return sliceSpec;
	}

	@Override
	public int encode(Object dataContract, BytesOutputBuffer buffer) {
		byte[] headBytes = new byte[HEAD_BYTES];
		BytesUtils.toBytes(code, headBytes);
		BytesUtils.toBytes(version, headBytes, 4);
		buffer.write(headBytes);
		return HEAD_BYTES;
	}

	@Override
	public BytesSlices decode(BytesInputStream bytesStream) {
		return BytesSliceArrayWrapper.wrap(bytesStream.readSlice(HEAD_BYTES));
	}

	// @Override
	// public BytesSlices decode(byte[] dataContractBytes, int offset) {
	// return SingleBytesSliceArray.create(dataContractBytes, offset, HEAD_BYTES);
	// }

}
