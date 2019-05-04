package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.DataType;
import com.jd.blockchain.binaryproto.EnumContract;
import com.jd.blockchain.binaryproto.EnumField;
import com.jd.blockchain.consts.DataCodes;

/**
 * 键值操作的数据类型；
 * 
 * @author huanghaiquan
 *
 */
@EnumContract(code = DataCodes.ENUM_TYPE_DATA_TYPE, name = "DataType", decription = "")
public enum BytesValueType {

	/**
	 * 空；
	 */
	NIL(DataType.NIL.CODE),

	/**
	 * 布尔型；
	 */
	BOOLEAN((byte) 0x10),

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

	// /**
	// * 引用； <br>
	// *
	// * 表示引用区块链系统中的某一个特定的对象，用以下形式的 URI 表示；
	// *
	// * state://ledger/account/key/version <br>
	// * 或 <br>
	// * proof:state://account_merkle_path/key_merkle_path
	// *
	// * proof:tx://
	// *
	// */
	// REFERENCE((byte) 0x80);
	@EnumField(type = DataType.INT8)
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
		throw new IllegalArgumentException("Unsupported code[" + code + "] of DataType!");
	}

}
