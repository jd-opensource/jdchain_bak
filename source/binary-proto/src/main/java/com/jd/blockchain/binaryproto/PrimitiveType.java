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
	NIL((byte) 0x00),

	/**
	 * 布尔型；
	 */
	BOOLEAN((byte)  0x10),

	/**
	 * 数值型：
	 */
	INT8((byte) 0x11),

	INT16((byte) 0x12),

	INT32((byte) 0x13),

	INT64((byte) 0x14),

	/**
	 * 日期时间；
	 */
	DATETIME((byte) 0x15),

	/**
	 * 文本数据；
	 */
	TEXT((byte) 0x20),

	/**
	 * 文本数据；
	 */
	JSON((byte) 0x21),

	/**
	 * 文本数据；
	 */
	XML((byte) 0x22),

	/**
	 * 二进制数据；
	 */
	BYTES((byte) 0x40),

	/**
	 * 大整数；
	 */
	BIG_INT((byte) 0x41),

	/**
	 * 图片；
	 */
	IMG((byte) 0x42),

	/**
	 * 视频；
	 */
	VIDEO((byte) 0x43),

	/**
	 * 位置；
	 */
	LOCATION((byte) 0x44);
	

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
