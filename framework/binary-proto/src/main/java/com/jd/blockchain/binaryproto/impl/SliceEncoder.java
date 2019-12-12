package com.jd.blockchain.binaryproto.impl;

import com.jd.blockchain.binaryproto.BinarySliceSpec;
import com.jd.blockchain.utils.io.BytesInputStream;
import com.jd.blockchain.utils.io.BytesOutputBuffer;
import com.jd.blockchain.utils.io.BytesSlices;

/**
 * 分段编码器；
 * 
 * @author huanghaiquan
 *
 */
public interface SliceEncoder {

	BinarySliceSpec getSliceSpecification();

	/**
	 * 将此编码器表示的数据契约分段输出的指定的缓冲区；
	 * 
	 * @param dataContract
	 *            数据契约的实例；当前编码器从中读取分段的值；
	 * @param buffer
	 *            要写入的缓冲区；调用者需要确保
	 * @param offset
	 *            缓冲区的写入起始位置；
	 * @return 写入的字节数；
	 */
	int encode(Object dataContract, BytesOutputBuffer buffer);

//	/**
//	 * 从指定的数据契约的数据中读取当前编码器表示的分段的数据；
//	 * 
//	 * @param dataContractBytes
//	 * @return
//	 */
//	BytesSlices decode(BytesSlice dataContractBytes, int offset);
	
	/**
	 * 从指定的数据契约的数据中读取当前编码器表示的分段的数据；
	 * 
	 * @param dataContractBytes
	 * @return
	 */
	BytesSlices decode(BytesInputStream bytesStream);
	
//	/**
//	 * 从指定的数据契约的数据中读取当前编码器表示的分段的数据；
//	 * 
//	 * @param dataContractBytes
//	 * @return
//	 */
//	BytesSlices decode(byte[] dataContractBytes, int offset);

}
