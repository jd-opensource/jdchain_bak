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
	NIL(DataType.NIL),

	/**
	 * 布尔型；
	 */
	BOOLEAN(DataType.BOOLEAN),

	/**
	 * 数值型：
	 */
	INT8((byte) (DataType.NUMERIC | 0x01)),

	INT16((byte) (DataType.NUMERIC | 0x02)),

	INT32((byte) (DataType.NUMERIC | 0x03)),

	INT64((byte) (DataType.NUMERIC | 0x04)),

	/**
	 * 日期时间；
	 */
	DATETIME((byte) (DataType.NUMERIC | 0x08)),

	/**
	 * 文本数据；
	 */
	TEXT(DataType.TEXT),

	/**
	 * 文本数据；
	 */
	JSON((byte) (DataType.TEXT | 0x01)),

	/**
	 * 文本数据；
	 */
	XML((byte) (DataType.TEXT | 0x02)),

	/**
	 * 二进制数据；
	 */
	BYTES(DataType.BINARY),

	/**
	 * 大整数；
	 */
	BIG_INT((byte) (DataType.BINARY | 0x01)),

	/**
	 * 图片；
	 */
	IMG((byte) (DataType.BINARY | 0x02)),

	/**
	 * 视频；
	 */
	VIDEO((byte) (DataType.BINARY | 0x03)),

	/**
	 * 位置坐标；
	 */
	LOCATION((byte) (DataType.BINARY | 0x04));

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
