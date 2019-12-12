package com.jd.blockchain.binaryproto;

/**
 * 键值操作的数据类型；
 * 
 * @author huanghaiquan
 *
 */
public enum PrimitiveType {

	/**
	 * 空；
	 */
	NIL(BaseType.NIL),

	/**
	 * 布尔型；
	 */
	BOOLEAN(BaseType.BOOLEAN),

	/**
	 * 8位的整数：
	 */
	INT8(BaseType.INT8),

	/**
	 * 16位整数；
	 */
	INT16(BaseType.INT16),

	/**
	 * 32位整数；
	 */
	INT32(BaseType.INT32),

	/**
	 * 64位整数；
	 */
	INT64(BaseType.INT64),

	/**
	 * 文本；
	 */
	TEXT(BaseType.TEXT),

	/**
	 * 二进制数据；
	 */
	BYTES(BaseType.BYTES);

	public final byte CODE;

	private PrimitiveType(byte code) {
		this.CODE = code;
	}

	public static PrimitiveType valueOf(byte code) {
		for (PrimitiveType dataType : PrimitiveType.values()) {
			if (dataType.CODE == code) {
				return dataType;
			}
		}
		throw new IllegalArgumentException("Code[" + code + "] not suppported by PrimitiveType!");
	}

}
