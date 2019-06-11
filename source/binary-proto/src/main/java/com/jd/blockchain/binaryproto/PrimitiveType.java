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
	 * 数值型：
	 */
	INT8((byte) (BaseType.NUMERIC | 0x01)),

	INT16((byte) (BaseType.NUMERIC | 0x02)),

	INT32((byte) (BaseType.NUMERIC | 0x03)),

	INT64((byte) (BaseType.NUMERIC | 0x04)),

	/**
	 * 时间戳；
	 */
	TIMESTAMP((byte) (BaseType.NUMERIC | 0x08)),

	/**
	 * 文本数据；
	 */
	TEXT(BaseType.TEXT),

	/**
	 * 文本数据；
	 */
	JSON((byte) (BaseType.TEXT | 0x01)),

	/**
	 * 文本数据；
	 */
	XML((byte) (BaseType.TEXT | 0x02)),

	/**
	 * 二进制数据；
	 */
	BYTES(BaseType.BYTES),

	/**
	 * 大整数；
	 */
	BIG_INT((byte) (BaseType.BYTES | 0x01)),

	/**
	 * 图片；
	 */
	IMG((byte) (BaseType.BYTES | 0x02)),

	/**
	 * 视频；
	 */
	VIDEO((byte) (BaseType.BYTES | 0x03)),

	/**
	 * 位置坐标；
	 */
	LOCATION((byte) (BaseType.BYTES | 0x04)),
	
	/**
	 * 公钥；
	 */
	PUB_KEY((byte) (BaseType.BYTES | 0x05)),
	
	/**
	 * 签名摘要；
	 */
	SIGNATURE_DIGEST((byte) (BaseType.BYTES | 0x06)),
	
	/**
	 * 哈希摘要；
	 */
	HASH_DIGEST((byte) (BaseType.BYTES | 0x07)),
	
	/**
	 * 加密数据；
	 */
	ENCRYPTED_DATA((byte) (BaseType.BYTES | 0x08));

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
