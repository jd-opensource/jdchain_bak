package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.PrimitiveType;
import com.jd.blockchain.binaryproto.EnumContract;
import com.jd.blockchain.binaryproto.EnumField;
import com.jd.blockchain.consts.DataCodes;

/**
 * 键值操作的数据类型；
 * 
 * @author huanghaiquan
 *
 */
@EnumContract(code = DataCodes.ENUM_TYPE_BYTES_VALUE_TYPE, name = "BytesValueType", decription = "")
public enum BytesValueType {

	/**
	 * 空；
	 */
	NIL(PrimitiveType.NIL.CODE),

	/**
	 * 布尔型；
	 */
	BOOLEAN(PrimitiveType.BOOLEAN.CODE),

	/**
	 * 数值型：
	 */

	INT8(PrimitiveType.INT8.CODE),

	INT16(PrimitiveType.INT16.CODE),

	INT32(PrimitiveType.INT32.CODE),

	INT64(PrimitiveType.INT64.CODE),

	/**
	 * 时间戳；
	 */
	TIMESTAMP(PrimitiveType.TIMESTAMP.CODE),

	/**
	 * 文本数据；
	 */
	TEXT(PrimitiveType.TEXT.CODE),

	/**
	 * 文本数据；
	 */
	JSON(PrimitiveType.JSON.CODE),

	/**
	 * 文本数据；
	 */
	XML(PrimitiveType.XML.CODE),

	/**
	 * 二进制数据；
	 */
	BYTES(PrimitiveType.BYTES.CODE),

	/**
	 * 大整数；
	 */
	BIG_INT(PrimitiveType.BIG_INT.CODE),

	/**
	 * 图片；
	 */
	IMG(PrimitiveType.IMG.CODE),

	/**
	 * 视频；
	 */
	VIDEO(PrimitiveType.VIDEO.CODE),

	/**
	 * 位置；
	 */
	LOCATION(PrimitiveType.LOCATION.CODE);

	@EnumField(type = PrimitiveType.INT8)
	public final byte CODE;

	private BytesValueType(byte code) {
		this.CODE = code;
	}

	public static BytesValueType valueOf(byte code) {
		for (BytesValueType dataType : BytesValueType.values()) {
			if (dataType.CODE == code) {
				return dataType;
			}
		}
		throw new IllegalArgumentException("Code [" + code + "] not supported by BytesValueType enum!");
	}

}
