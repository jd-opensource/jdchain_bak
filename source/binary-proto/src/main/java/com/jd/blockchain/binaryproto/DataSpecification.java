package com.jd.blockchain.binaryproto;

import java.util.List;

/**
 * {@link DataSpecification} 表示数据契约的格式标准；
 * <p>
 * {@link DataSpecification} 提供了数据契约的格式描述（通过属性 {@link #getCode()}、
 * {@link #getVersion()}、
 * {@link #getFields()}），以及二进制数据片段序列的格式描述（{@link #getSlices()}）；
 * 
 * <p>
 * 其中，数据片段列表（{@link #getSlices()}）反映了数据契约实际输出的二进制序列：<br>
 * 1、首个数据片段是数据契约的类型编码（{@link #getCode()}）；<br>
 * 2、接下来是数据契约的版本（{@link #getVersion()}）；<br>
 * 3、再接下来是与字段列表（{@link #getFields()}）一一对应的数据分片；<p>
 * 
 * <p>
 * 
 * 
 * @author huanghaiquan
 *
 */
public interface DataSpecification {

	/**
	 * 数据契约的类型编码；
	 * 
	 * @return
	 */
	int getCode();

	/**
	 * 数据契约的版本；
	 * <p>
	 * 
	 * 由类型编码{@link #getCode()}和字段列表{@link #getFields()} 进行哈希生成的 64 位整数；
	 * 
	 * @return
	 */
	long getVersion();

	String getName();

	String getDescription();

	/**
	 * 按定义顺序排列的字段格式标准的列表；
	 * <p>
	 * 字段的顺序由 {@link DataField#order()} 定义；
	 * 
	 * @return
	 */
	List<FieldSpec> getFields();

	/**
	 * 按顺序定义的二进制数据片段的格式标准的列表；
	 * 
	 * @return
	 */
	List<BinarySliceSpec> getSlices();

	String toHtml();

}
