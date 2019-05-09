package com.jd.blockchain.binaryproto;

/**
 * 表示数据契约字段的格式标准；
 * 
 * @author huanghaiquan
 *
 */
public interface FieldSpec {

//	/**
//	 * 字段值的类型编码；
//	 * <p>
//	 * 
//	 * 该编码是对{@link #getPrimitiveType()}、{@link #getRefEnum()}、{@link #getRefContract()}
//	 * 3个属性的联合编码，通过首字节标识出类型为这3中类型中的一种；<br>
//	 * 其中：<br>
//	 * 1、首字节为 0 表示为基本类型 {@link #getPrimitiveType()}；<br>
//	 * 2、首字节为 1 表示为枚举类型 {@link #getRefEnum()}，紧接其后是枚举类型的编码<br>
//	 * 3、首字节为 2 表示为数据契约类型 {@link #getRefContract()}；<br>
//	 * 4、首字节为 3 表示为基本类型的数组类型 {@link #isRepeatable()} ()}；<br>
//	 * 5、首字节为 4 表示为PubKey类型 {@link #isRefPubKey()}；<br>
//	 * 6、首字节为 5 表示为PrivKey类型 {@link #isRefPrivKey()}；<br>
//	 * 7、首字节为 6 表示为HashDigest类型 {@link #isRefHashDigest()}；<br>
//	 * 8、首字节为 7 表示为数据契约类型数组 {@link #isList(), @link #getRefContract()}；<br>
//	 * 9、首字节为 8 表示为BlockChainIdentity数据类型 {@link #isRefIdentity()}
//	 * 10、首字节为9 表示为NetworkAddress数据类型 {@link #isRefNetworkAddr()}；<br>
//	 * @return
//	 */
//	long getTypeCode();

	/**
	 * 字段的值的类型；
	 * <p>
	 * 如果不是字段的值不是基本类型，则返回 null（即: {@link DataField#primitiveType()} 设置为
	 * {@link PrimitiveType#NIL}）；
	 * 
	 * @return
	 */
	PrimitiveType getPrimitiveType();

	/**
	 * 字段的值引用的枚举契约；
	 * <p>
	 * 如果字段的值不是枚举契约类型，则返回 null；
	 *
	 * @return
	 */
	EnumSpecification getRefEnum();

	/**
	 * 字段的值引用的数据契约；
	 * <p>
	 * 如果字段的值不是数据契约类型，则返回 null；
	 * 
	 * @return
	 */
	DataSpecification getRefContract();
	
	boolean isRepeatable();
	
//	/**
//	 * 字段的值引用的PubKey；
//	 * <p>
//	 * 如果字段的值不是PubKey，则返回 false；
//	 *
//	 * @return
//	 */
//    boolean isRefPubKey();
//	/**
//	 * 字段的值引用的PrivKey；
//	 * <p>
//	 * 如果字段的值不是PrivKey，则返回 false；
//	 *
//	 * @return
//	 */
//	boolean isRefPrivKey();
//	/**
//	 * 字段的值引用的HashDigest；
//	 * <p>
//	 * 如果字段的值不是HashDigest，则返回 false；
//	 *
//	 * @return
//	 */
//	boolean isRefHashDigest();
//	/**
//	 * 字段的值引用的SignatureDigest；
//	 * <p>
//	 * 如果字段的值不是SignatureDigest，则返回 false；
//	 *
//	 * @return
//	 */
//	boolean isRefSignatureDigest();
//	/**
//	 * 字段的值引用的BlockChainIdentity；
//	 * <p>
//	 * 如果字段的值不是HashDigest，则返回 false；
//	 *
//	 * @return
//	 */
//	boolean isRefIdentity();
//	/**
//	 * 字段的值引用的NetworkAddress；
//	 * <p>
//	 * 如果字段的值不是NetworkAddress，则返回 false；
//	 *
//	 * @return
//	 */
//	boolean isRefNetworkAddr();
	/**
	 * 最大长度；单位为“byte”；
	 * 
	 * @return
	 * @see {@link DataField#maxSize()}
	 */
	int getMaxSize();

	String getName();

	String getDescription();

	/**
	 * 是否引用了一个通用数据契约类型，实际的类型需要根据实际的对象实例来定；
	 * @return
	 */
	boolean isGenericContract();
//	Class<?> getContractTypeResolver();
}
