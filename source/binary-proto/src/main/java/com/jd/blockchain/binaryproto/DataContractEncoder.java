package com.jd.blockchain.binaryproto;

import com.jd.blockchain.utils.io.BytesInputStream;
import com.jd.blockchain.utils.io.BytesOutputBuffer;

/**
 * 二进制编码器；
 * 
 * @author huanghaiquan
 *
 */
public interface DataContractEncoder {

	/**
	 * 数据契约的格式标准；
	 * 
	 * @return
	 */
	DataSpecification getSepcification();

	/**
	 * 数据契约的接口类型；
	 * 
	 * @return
	 */
	Class<?> getContractType();

	/**
	 * 按照数据格式标准序列化输出指定的数据对象；
	 * 
	 * @param dataContract
	 *            数据对象；
	 * @param buffer
	 *            要写入的缓冲区；
	 * @return 返回写入的字节数；
	 */
	int encode(Object dataContract, BytesOutputBuffer buffer);

	/**
	 * 按照数据格式标准将指定的二进制输入流反序列化生成数据对象；
	 * 
	 * @param bytesStream
	 * @return
	 */
	<T> T decode(BytesInputStream bytesStream);
}
