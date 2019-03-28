package com.jd.blockchain.ledger;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.Date;

import com.jd.blockchain.utils.ValueType;
import com.jd.blockchain.utils.io.ByteArray;
import com.jd.blockchain.utils.io.BytesUtils;

/**
 * KV数据项；
 * 
 * <p>
 * 
 * {@link KVDataObject} 被设计为只读对象；
 * 
 * @author huanghaiquan
 *
 */
public class KVDataObject implements KVDataEntry {

	private String key;

	private long version;

	private ValueType type;

	private byte[] bytesValue;

	public KVDataObject(String key, long version, ValueType type, byte[] bytesValue) {
		this.key = key;
		this.type = type;
		this.version = version < 0 ? -1 : version;
		this.bytesValue = bytesValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jd.blockchain.ledger.KVDataEntry#getKey()
	 */
	@Override
	public String getKey() {
		return key;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jd.blockchain.ledger.KVDataEntry#getVersion()
	 */
	@Override
	public long getVersion() {
		return version;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jd.blockchain.ledger.KVDataEntry#getType()
	 */
	@Override
	public ValueType getType() {
		return type;
	}

	@Override
	public Object getValue() {
		if (bytesValue == null) {
			return null;
		}

		try {
			switch (type) {
			case NIL:
				return null;
			case TEXT:
				return new String(bytesValue, "UTF-8");
			case BYTES:
				return ByteArray.toHex(bytesValue);
			case INT64:
				return BytesUtils.readLong(new ByteArrayInputStream(bytesValue));
			case JSON:
				return new String(bytesValue, "UTF-8");

			default:
				throw new IllegalStateException("Unsupported value type[" + type + "] to resolve!");
			}
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	/**
	 * 是否为空值；
	 * <p>
	 * 
	 * 仅当数据类型 {@link #getType()} 为 {@link ValueType#NIL} 时返回 true，其它情况返回 false；
	 * <p>
	 * 
	 * @return
	 */
	public boolean isNil() {
		return ValueType.NIL == type;
	}

	/**
	 * 字节数组形式的原始内容；
	 * 
	 * @return
	 */
	ByteArray bytesArray() {
		return ByteArray.wrapReadonly(bytesValue);
	}

	/**
	 * 返回 8 位整数值；
	 * <p>
	 * 
	 * 仅当数据类型 {@link #getType()} 为 {@link ValueType#INT8} 有效；
	 * <p>
	 * 
	 * 无效类型将引发 {@link IllegalStateException} 异常；
	 * 
	 * @return
	 */
	public byte tinyValue() {
		if (ValueType.INT8 == type) {
			return bytesValue[0];
		}
		throw new IllegalStateException(String.format("Expected type [%s], but [%s]", ValueType.INT8, type));
	}

	/**
	 * 返回 16 位整数值；
	 * <p>
	 * 
	 * 仅当数据类型 {@link #getType()} 为 {@link ValueType#INT16} 有效；
	 * <p>
	 * 
	 * 无效类型将引发 {@link IllegalStateException} 异常；
	 * 
	 * @return
	 */
	public short shortValue() {
		if (ValueType.INT16 == type) {
			return BytesUtils.toShort(bytesValue, 0);
		}
		throw new IllegalStateException(String.format("Expected type [%s], but [%s]", ValueType.INT16, type));
	}

	/**
	 * 返回 32 位整数值；
	 * <p>
	 * 
	 * 仅当数据类型 {@link #getType()} 为 {@link ValueType#INT32} 有效；
	 * <p>
	 * 
	 * 无效类型将引发 {@link IllegalStateException} 异常；
	 * 
	 * @return
	 */
	public int intValue() {
		if (ValueType.INT32 == type) {
			return BytesUtils.toInt(bytesValue, 0);
		}
		throw new IllegalStateException(String.format("Expected type [%s], but [%s]", ValueType.INT32, type));
	}

	/**
	 * 返回 64 位整数值；
	 * <p>
	 * 
	 * 仅当数据类型 {@link #getType()} 为 {@link ValueType#INT64} 有效；
	 * <p>
	 * 
	 * 无效类型将引发 {@link IllegalStateException} 异常；
	 * 
	 * @return
	 */
	public long longValue() {
		if (ValueType.INT64 == type) {
			return BytesUtils.toLong(bytesValue, 0);
		}
		throw new IllegalStateException(String.format("Expected type [%s], but [%s]", ValueType.INT64, type));

	}

	/**
	 * 返回大整数值；
	 * <p>
	 * 
	 * 仅当数据类型 {@link #getType()} 为 {@link ValueType#BIG_INT} 有效；
	 * <p>
	 * 
	 * 无效类型将引发 {@link IllegalStateException} 异常；
	 * 
	 * @return
	 */
	public BigInteger bigIntValue() {
		if (ValueType.BIG_INT == type) {
			return new BigInteger(bytesValue);
		}
		throw new IllegalStateException(String.format("Expected type [%s], but [%s]", ValueType.BIG_INT, type));
	}

	/**
	 * 返回布尔值；
	 * <p>
	 * 
	 * 仅当数据类型 {@link #getType()} 为 {@link ValueType#BIG_INT} 有效；
	 * <p>
	 * 
	 * 无效类型将引发 {@link IllegalStateException} 异常；
	 * 
	 * @return
	 */
	public boolean boolValue() {
		if (ValueType.BOOLEAN == type) {
			return bytesValue[0] != 0;
		}
		throw new IllegalStateException(String.format("Expected type [%s], but [%s]", ValueType.BOOLEAN, type));
	}

	/**
	 * 返回日期时间值；
	 * <p>
	 * 
	 * 仅当数据类型 {@link #getType()} 为 {@link ValueType#DATETIME} 有效；
	 * <p>
	 * 
	 * 无效类型将引发 {@link IllegalStateException} 异常；
	 * 
	 * @return
	 */
	public Date datetimeValue() {
		if (ValueType.DATETIME == type) {
			long ts = BytesUtils.toLong(bytesValue);
			return new Date(ts);
		}
		throw new IllegalStateException(String.format("Expected type [%s], but [%s]", ValueType.DATETIME, type));
	}

	/**
	 * 返回大整数值；
	 * <p>
	 * 
	 * 仅当数据类型 {@link #getType()} 为 {@link ValueType#TEXT} / {@link ValueType#JSON} /
	 * {@link ValueType#XML} 有效；
	 * <p>
	 * 
	 * 无效类型将引发 {@link IllegalStateException} 异常；
	 * 
	 * @return
	 */
	public String stringValue() {
		if (ValueType.TEXT == type || ValueType.JSON == type || ValueType.XML == type) {
			try {
				return new String(bytesValue, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new IllegalStateException(e.getMessage(), e);
			}
		}
		throw new IllegalStateException(String.format("Expected type [%s] or [%s] or [%s] , but [%s]", ValueType.TEXT,
				ValueType.JSON, ValueType.XML, type));
	}

//	// ----------------
//	public KVDataEntry nextVersionNil() {
//		return nilState(key, version + 1);
//	}
//
//	public KVDataEntry nextVersionBoolean(boolean value) {
//		return booleanState(key, version + 1, value);
//	}
//
//	public KVDataEntry nextVersionTiny(byte value) {
//		return tinyState(key, version + 1, value);
//	}
//
//	public KVDataEntry nextVersionShort(short value) {
//		return shortState(key, version + 1, value);
//	}
//
//	public KVDataEntry nextVersionInt(int value) {
//		return intState(key, version + 1, value);
//	}
//
//	public KVDataEntry nextVersionLong(long value) {
//		return longState(key, version + 1, value);
//	}
//
//	public KVDataEntry nextVersionDatetime(Date value) {
//		return datetimeState(key, version + 1, value);
//	}
//
//	public KVDataEntry nextVersionJson(String value) {
//		return jsonState(key, version + 1, value);
//	}
//
//	public KVDataEntry nextVersionXml(String value) {
//		return xmlState(key, version + 1, value);
//	}
//
//	public KVDataEntry nextVersionBigInt(BigInteger value) {
//		return bigIntState(key, version + 1, value);
//	}
//
//	public KVDataEntry nextVersionText(boolean encrypted, String value) {
//		return textState(key, version + 1, encrypted, value);
//	}
//
//	public KVDataEntry nextVersionBytes(boolean encrypted, byte[] value) {
//		return bytesState(key, version + 1, encrypted, value);
//	}
//
//	public KVDataEntry nextVersionImage(boolean encrypted, byte[] value) {
//		return imageState(key, version + 1, encrypted, value);
//	}
//
//	public KVDataEntry nextVersionVideo(boolean encrypted, byte[] value) {
//		return videoState(key, version + 1, encrypted, value);
//	}
//
//	public KVDataEntry nextVersionLocation(boolean encrypted, byte[] value) {
//		return locationState(key, version + 1, encrypted, value);
//	}
//	// ----------------
//
//	public KVDataEntry newNil() {
//		return nilState(key, version);
//	}
//
//	public KVDataEntry newBoolean(boolean value) {
//		return booleanState(key, version, value);
//	}
//
//	public KVDataEntry newTiny(byte value) {
//		return tinyState(key, version, value);
//	}
//
//	public KVDataEntry newShort(short value) {
//		return shortState(key, version, value);
//	}
//
//	public KVDataEntry newInt(int value) {
//		return intState(key, version, value);
//	}
//
//	public KVDataObject newLong(long value) {
//		return longState(key, version, value);
//	}
//
//	public KVDataEntry newDatetime(Date value) {
//		return datetimeState(key, version, value);
//	}
//
//	public KVDataEntry newJson(String value) {
//		return jsonState(key, version, value);
//	}
//
//	public KVDataEntry newXml(String value) {
//		return xmlState(key, version, value);
//	}
//
//	public KVDataEntry newBigInt(BigInteger value) {
//		return bigIntState(key, version, value);
//	}
//
//	public KVDataEntry newText(boolean encrypted, String value) {
//		return textState(key, version, encrypted, value);
//	}
//
//	public KVDataEntry newBytes(boolean encrypted, byte[] value) {
//		return bytesState(key, version, encrypted, value);
//	}
//
//	public KVDataEntry newImage(boolean encrypted, byte[] value) {
//		return imageState(key, version, encrypted, value);
//	}
//
//	public KVDataEntry newVideo(boolean encrypted, byte[] value) {
//		return videoState(key, version, encrypted, value);
//	}
//
//	public KVDataEntry newLocation(boolean encrypted, byte[] value) {
//		return locationState(key, version, encrypted, value);
//	}
//
//	// ----------------
//
//	public KVDataEntry newNil(long version) {
//		return nilState(key, version);
//	}
//
//	public KVDataEntry newBoolean(long version, boolean value) {
//		return booleanState(key, version, value);
//	}
//
//	public KVDataEntry newTiny(long version, byte value) {
//		return tinyState(key, version, value);
//	}
//
//	public KVDataEntry newShort(long version, short value) {
//		return shortState(key, version, value);
//	}
//
//	public KVDataEntry newInt(long version, int value) {
//		return intState(key, version, value);
//	}
//
//	public KVDataEntry newLong(long version, long value) {
//		return longState(key, version, value);
//	}
//
//	public KVDataEntry newDatetime(long version, Date value) {
//		return datetimeState(key, version, value);
//	}
//
//	public KVDataEntry newJson(long version, String value) {
//		return jsonState(key, version, value);
//	}
//
//	public KVDataEntry newXml(long version, String value) {
//		return xmlState(key, version, value);
//	}
//
//	public KVDataEntry newBigInt(long version, BigInteger value) {
//		return bigIntState(key, version, value);
//	}
//
//	public KVDataEntry newText(long version, boolean encrypted, String value) {
//		return textState(key, version, encrypted, value);
//	}
//
//	public KVDataEntry newBytes(long version, boolean encrypted, byte[] value) {
//		return bytesState(key, version, encrypted, value);
//	}
//
//	public KVDataEntry newImage(long version, boolean encrypted, byte[] value) {
//		return imageState(key, version, encrypted, value);
//	}
//
//	public KVDataEntry newVideo(long version, boolean encrypted, byte[] value) {
//		return videoState(key, version, encrypted, value);
//	}
//
//	public KVDataEntry newLocation(long version, boolean encrypted, byte[] value) {
//		return locationState(key, version, encrypted, value);
//	}
//
//	// ----------------
//
//	public static KVDataEntry booleanState(String key, boolean value) {
//		return booleanState(key, -1, value);
//	}
//
//	public static KVDataEntry tinyState(String key, byte value) {
//		return tinyState(key, -1, value);
//	}
//
//	public static KVDataEntry shortState(String key, short value) {
//		return shortState(key, -1, value);
//	}
//
//	public static KVDataEntry intState(String key, int value) {
//		return intState(key, -1, value);
//	}
//
//	public static KVDataEntry longState(String key, long value) {
//		return longState(key, -1, value);
//	}
//
//	public static KVDataEntry datetimeState(String key, Date value) {
//		return datetimeState(key, -1, value);
//	}
//
//	public static KVDataEntry jsonState(String key, String value) {
//		return jsonState(key, -1, value);
//	}
//
//	public static KVDataEntry xmlState(String key, String value) {
//		return xmlState(key, -1, value);
//	}
//
//	public static KVDataEntry bigIntState(String key, BigInteger value) {
//		return bigIntState(key, -1, value);
//	}
//
//	public static KVDataObject textState(String key, String value) {
//		return textState(key, -1, false, value);
//	}
//
//	public static KVDataEntry bytesState(String key, byte[] value) {
//		return bytesState(key, -1, false, value);
//	}
//
//	public static KVDataEntry imageState(String key, byte[] value) {
//		return imageState(key, -1, false, value);
//	}
//
//	public static KVDataEntry videoState(String key, byte[] value) {
//		return videoState(key, -1, false, value);
//	}
//
//	public static KVDataEntry locationState(String key, byte[] value) {
//		return locationState(key, -1, false, value);
//	}
//
//	// ----------------
//
//	public static KVDataEntry textState(String key, boolean encrypted, String value) {
//		return textState(key, -1, encrypted, value);
//	}
//
//	public static KVDataEntry bytesState(String key, boolean encrypted, byte[] value) {
//		return bytesState(key, -1, encrypted, value);
//	}
//
//	public static KVDataEntry imageState(String key, boolean encrypted, byte[] value) {
//		return imageState(key, -1, encrypted, value);
//	}
//
//	public static KVDataEntry videoState(String key, boolean encrypted, byte[] value) {
//		return videoState(key, -1, encrypted, value);
//	}
//
//	public static KVDataEntry locationState(String key, boolean encrypted, byte[] value) {
//		return locationState(key, -1, encrypted, value);
//	}
//
//	// ----------------------
//
//	public static KVDataEntry nilState(String key) {
//		return new KVDataObject(key, ValueType.NIL, -1, false, BytesUtils.EMPTY_BYTES);
//	}
//
//	public static KVDataEntry nilState(String key, long version) {
//		return new KVDataObject(key, ValueType.NIL, version, false, BytesUtils.EMPTY_BYTES);
//	}
//
//	public static KVDataEntry booleanState(String key, long version, boolean value) {
//		byte[] v = { value ? (byte) 1 : (byte) 0 };
//		return new KVDataObject(key, ValueType.BOOLEAN, version, false, v);
//	}
//
//	public static KVDataEntry tinyState(String key, long version, byte value) {
//		byte[] v = { value };
//		return new KVDataObject(key, ValueType.INT8, version, false, v);
//	}
//
//	public static KVDataEntry shortState(String key, long version, short value) {
//		byte[] v = BytesUtils.toBytes(value);
//		return new KVDataObject(key, ValueType.INT16, version, false, v);
//	}
//
//	public static KVDataEntry intState(String key, long version, int value) {
//		byte[] v = BytesUtils.toBytes(value);
//		return new KVDataObject(key, ValueType.INT32, version, false, v);
//	}
//
//	public static KVDataObject longState(String key, long version, long value) {
//		byte[] v = BytesUtils.toBytes(value);
//		return new KVDataObject(key, ValueType.INT64, version, false, v);
//	}
//
//	public static KVDataEntry datetimeState(String key, long version, Date value) {
//		byte[] v = BytesUtils.toBytes(value.getTime());
//		return new KVDataObject(key, ValueType.DATETIME, version, false, v);
//	}
//
//	public static KVDataObject textState(String key, long version, boolean encrypted, String value) {
//		try {
//			byte[] v = value.getBytes("UTF-8");
//			return new KVDataObject(key, ValueType.TEXT, version, encrypted, v);
//		} catch (UnsupportedEncodingException e) {
//			throw new IllegalStateException(e.getMessage(), e);
//		}
//	}
//
//	public static KVDataEntry jsonState(String key, long version, String value) {
//		try {
//			byte[] v = value.getBytes("UTF-8");
//			return new KVDataObject(key, ValueType.JSON, version, false, v);
//		} catch (UnsupportedEncodingException e) {
//			throw new IllegalStateException(e.getMessage(), e);
//		}
//	}
//
//	public static KVDataEntry xmlState(String key, long version, String value) {
//		try {
//			byte[] v = value.getBytes("UTF-8");
//			return new KVDataObject(key, ValueType.XML, version, false, v);
//		} catch (UnsupportedEncodingException e) {
//			throw new IllegalStateException(e.getMessage(), e);
//		}
//	}
//
//	public static KVDataEntry bigIntState(String key, long version, BigInteger value) {
//		byte[] v = value.toByteArray();
//		return new KVDataObject(key, ValueType.BIG_INT, version, false, v);
//	}
//
//	public static KVDataEntry bytesState(String key, long version, boolean encrypted, byte[] value) {
//		return new KVDataObject(key, ValueType.BYTES, version, encrypted, value);
//	}
//
//	public static KVDataEntry imageState(String key, long version, boolean encrypted, byte[] value) {
//		return new KVDataObject(key, ValueType.IMG, version, encrypted, value);
//	}
//
//	public static KVDataEntry videoState(String key, long version, boolean encrypted, byte[] value) {
//		return new KVDataObject(key, ValueType.VIDEO, version, encrypted, value);
//	}
//
//	public static KVDataEntry locationState(String key, long version, boolean encrypted, byte[] value) {
//		return new KVDataObject(key, ValueType.LOCATION, version, encrypted, value);
//	}

}