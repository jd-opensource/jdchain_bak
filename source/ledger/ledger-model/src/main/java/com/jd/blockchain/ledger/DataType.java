package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.BaseType;
import com.jd.blockchain.binaryproto.EnumContract;
import com.jd.blockchain.binaryproto.EnumField;
import com.jd.blockchain.binaryproto.PrimitiveType;
import com.jd.blockchain.consts.DataCodes;

/**
 * 键值操作的数据类型；
 * 
 * @author huanghaiquan
 *
 */
@EnumContract(code = DataCodes.ENUM_TYPE_BYTES_VALUE_TYPE)
public enum DataType {

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
	 * 文本数据；
	 */
	TEXT(PrimitiveType.TEXT.CODE),

	/**
	 * 二进制数据；
	 */
	BYTES(PrimitiveType.BYTES.CODE),

	/**
	 * 时间戳；
	 */
	TIMESTAMP((byte) (BaseType.INTEGER | 0x08)),

	/**
	 * 文本数据；
	 */
	JSON((byte) (BaseType.TEXT | 0x01)),

	/**
	 * 文本数据；
	 */
	XML((byte) (BaseType.TEXT | 0x02)),

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
	ENCRYPTED_DATA((byte) (BaseType.BYTES | 0x08)),

	/**
	 * DataContract 数据；
	 */
	DATA_CONTRACT((byte) (BaseType.EXT | 0x01));
	
	
	
	public static final boolean BOOLEAN_DEFAULT_VALUE = false;
	
	public static final byte INT8_DEFAULT_VALUE = 0;
	
	public static final short INT16_DEFAULT_VALUE = 0;
	
	public static final int INT32_DEFAULT_VALUE = 0;
	
	public static final long INT64_DEFAULT_VALUE = 0;
	
	

	@EnumField(type = PrimitiveType.INT8)
	public final byte CODE;

	private DataType(byte code) {
		this.CODE = code;
	}

	/**
	 * 是否表示“文本类型”或“文本衍生类型”；
	 * 
	 * @return
	 */
	public boolean isText() {
		return BaseType.TEXT == (BaseType.TEXT & CODE);
	}

	/**
	 * 是否表示“字节类型”或“字节衍生类型”；
	 * 
	 * @return
	 */
	public boolean isBytes() {
		return BaseType.BYTES == (BaseType.BYTES & CODE);
	}

	/**
	 * 是否表示“整数类型”或“整数衍生类型”；
	 * 
	 * @return
	 */
	public boolean isInteger() {
		return BaseType.INTEGER == (BaseType.INTEGER & CODE);
	}
	
	/**
	 * 是否表示“布尔类型”；
	 * 
	 * @return
	 */
	public boolean isBoolean() {
		return BaseType.BOOLEAN == (BaseType.BOOLEAN & CODE);
	}
	
	/**
	 * 是否表示“扩展类型”；
	 * 
	 * @return
	 */
	public boolean isExt() {
		return BaseType.EXT == (BaseType.EXT & CODE);
	}

	public static DataType valueOf(byte code) {
		for (DataType dataType : DataType.values()) {
			if (dataType.CODE == code) {
				return dataType;
			}
		}
		throw new IllegalArgumentException("Code [" + code + "] not supported by BytesValueType enum!");
	}

}
