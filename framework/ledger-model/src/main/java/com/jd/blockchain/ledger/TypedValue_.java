//package com.jd.blockchain.ledger;
//
//import java.math.BigInteger;
//import java.util.Date;
//
//import com.jd.blockchain.binaryproto.PrimitiveType;
//import com.jd.blockchain.crypto.HashDigest;
//import com.jd.blockchain.crypto.PubKey;
//import com.jd.blockchain.crypto.SignatureDigest;
//import com.jd.blockchain.utils.Bytes;
//import com.jd.blockchain.utils.io.BytesUtils;
//
//public class TypedValue_ {
//
//	private BytesValue bytesValue;
//
//	public TypedValue_(BytesValue bytesValue) {
//		this.bytesValue = bytesValue;
//	}
//
//	public DataType getType() {
//		return bytesValue == null ? DataType.NIL : bytesValue.getType();
//	}
//
//	public Object getValue() {
//		if (isNil()) {
//			return null;
//		}
//		switch (bytesValue.getType()) {
//		case BOOLEAN:
//			return toBoolean();
//		case INT8:
//			return toInt8();
//		case INT16:
//			return toInt16();
//		case INT32:
//			return toInt32();
//		case INT64:
//			return toInt64();
//		case BIG_INT:
//			return toBigInteger();
//		case TIMESTAMP:
//			return toDatetime();
//		case TEXT:
//		case JSON:
//		case XML:
//			return toText();
//
//		case BYTES:
//		case VIDEO:
//		case IMG:
//		case LOCATION:
//		case ENCRYPTED_DATA:
//			return toBytesArray();
//
//		case HASH_DIGEST:
//			return toHashDegist();
//		case PUB_KEY:
//			return toPubKey();
//		case SIGNATURE_DIGEST:
//			return toSignatureDigest();
//
//		case DATA_CONTRACT:
//			return toBytesArray();
//		default:
//			throw new IllegalStateException(String.format("Type [%s] has not be supported!", bytesValue.getType()));
//		}
//	}
//
//	/**
//	 * 是否为空值；
//	 * <p>
//	 * 
//	 * 仅当数据类型 {@link #getType()} 为 {@link PrimitiveType#NIL} 时返回 true，其它情况返回 false；
//	 * <p>
//	 * 
//	 * @return
//	 */
//	public boolean isNil() {
//		return bytesValue == null || DataType.NIL == bytesValue.getType();
//	}
//
//	/**
//	 * 返回 8 位整数值；
//	 * <p>
//	 * 
//	 * 仅当数据类型 {@link #getType()} 为 {@link PrimitiveType#INT8} 有效；
//	 * <p>
//	 * 
//	 * 无效类型将引发 {@link IllegalStateException} 异常；
//	 * 
//	 * @return
//	 */
//	public byte tinyValue() {
//		if (isNil()) {
//			return DataType.INT8_DEFAULT_VALUE;
//		}
//		if (DataType.INT8 == getType()) {
//			return toInt8();
//		}
//		throw new IllegalStateException(String.format("Type [%s] cannot be convert to Int8!", bytesValue.getType()));
//	}
//
//	private byte toInt8() {
//		return bytesValue.getValue().toBytes()[0];
//	}
//
//	/**
//	 * 返回 16 位整数值；
//	 * <p>
//	 * 
//	 * 仅当数据类型 {@link #getType()} 为 {@link PrimitiveType#INT16} 有效；
//	 * <p>
//	 * 
//	 * 无效类型将引发 {@link IllegalStateException} 异常；
//	 * 
//	 * @return
//	 */
//	public short shortValue() {
//		if (isNil()) {
//			return DataType.INT16_DEFAULT_VALUE;
//		}
//		if (DataType.INT16 == getType()) {
//			return toInt16();
//		}
//		throw new IllegalStateException(String.format("Type [%s] cannot be convert to Int16!", bytesValue.getType()));
//	}
//
//	private short toInt16() {
//		return BytesUtils.toShort(bytesValue.getValue().toBytes(), 0);
//	}
//
//	/**
//	 * 返回 32 位整数值；
//	 * <p>
//	 * 
//	 * 仅当数据类型 {@link #getType()} 为 {@link PrimitiveType#INT32} 有效；
//	 * <p>
//	 * 
//	 * 无效类型将引发 {@link IllegalStateException} 异常；
//	 * 
//	 * @return
//	 */
//	public int intValue() {
//		if (isNil()) {
//			return DataType.INT32_DEFAULT_VALUE;
//		}
//		if (DataType.INT32 == getType()) {
//			return toInt32();
//		}
//		throw new IllegalStateException(String.format("Type [%s] cannot be convert to Int32!", bytesValue.getType()));
//	}
//
//	private int toInt32() {
//		return BytesUtils.toInt(bytesValue.getValue().toBytes(), 0);
//	}
//
//	/**
//	 * 返回 64 位整数值；
//	 * <p>
//	 * 
//	 * 仅当数据类型 {@link #getType()} 为 {@link PrimitiveType#INT64} 有效；
//	 * <p>
//	 * 
//	 * 无效类型将引发 {@link IllegalStateException} 异常；
//	 * 
//	 * @return
//	 */
//	public long longValue() {
//		if (isNil()) {
//			return DataType.INT64_DEFAULT_VALUE;
//		}
//		if (DataType.INT64 == bytesValue.getType()) {
//			return toInt64();
//		}
//		throw new IllegalStateException(String.format("Type [%s] cannot be convert to Int64!", bytesValue.getType()));
//
//	}
//
//	private long toInt64() {
//		return BytesUtils.toLong(bytesValue.getValue().toBytes(), 0);
//	}
//
//	/**
//	 * 返回大整数值；
//	 * <p>
//	 * 
//	 * 仅当数据类型 {@link #getType()} 为 {@link PrimitiveType#BIG_INT} 有效；
//	 * <p>
//	 * 
//	 * 无效类型将引发 {@link IllegalStateException} 异常；
//	 * 
//	 * @return
//	 */
//	public BigInteger bigIntValue() {
//		if (isNil()) {
//			return null;
//		}
//		if (DataType.BIG_INT == bytesValue.getType()) {
//			return toBigInteger();
//		}
//		throw new IllegalStateException(
//				String.format("Type [%s] cannot be convert to BigInteger!", bytesValue.getType()));
//	}
//
//	private BigInteger toBigInteger() {
//		return new BigInteger(bytesValue.getValue().toBytes());
//	}
//
//	/**
//	 * 返回布尔值；
//	 * <p>
//	 * 
//	 * 仅当数据类型 {@link #getType()} 为 {@link PrimitiveType#BIG_INT} 有效；
//	 * <p>
//	 * 
//	 * 无效类型将引发 {@link IllegalStateException} 异常；
//	 * 
//	 * @return
//	 */
//	public boolean boolValue() {
//		if (isNil()) {
//			return DataType.BOOLEAN_DEFAULT_VALUE;
//		}
//		if (DataType.BOOLEAN == bytesValue.getType()) {
//			return toBoolean();
//		}
//		throw new IllegalStateException(String.format("Type [%s] cannot be convert to boolean!", bytesValue.getType()));
//	}
//
//	private boolean toBoolean() {
//		return BytesUtils.toBoolean(bytesValue.getValue().toBytes()[0]);
//	}
//
//	/**
//	 * 返回日期时间值；
//	 * <p>
//	 * 
//	 * 仅当数据类型 {@link #getType()} 为 {@link PrimitiveType#TIMESTAMP} 有效；
//	 * <p>
//	 * 
//	 * 无效类型将引发 {@link IllegalStateException} 异常；
//	 * 
//	 * @return
//	 */
//	public Date datetimeValue() {
//		if (isNil()) {
//			return null;
//		}
//		if (DataType.TIMESTAMP == bytesValue.getType()) {
//			return toDatetime();
//		}
//		throw new IllegalStateException(
//				String.format("Type [%s] cannot be convert to datetime!", bytesValue.getType()));
//	}
//
//	private Date toDatetime() {
//		long ts = BytesUtils.toLong(bytesValue.getValue().toBytes());
//		return new Date(ts);
//	}
//
//	/**
//	 * 返回文本值；
//	 * <p>
//	 * 
//	 * 仅当数据类型 {@link #getType()} 为“文本类型”或“文本衍生类型”时有效；
//	 * <p>
//	 * 
//	 * 无效类型将引发 {@link IllegalStateException} 异常；
//	 * 
//	 * @return
//	 */
//	public String stringValue() {
//		if (isNil()) {
//			return null;
//		}
//		DataType type = bytesValue.getType();
//		if (type.isText()) {
//			return toText();
//		}
//		throw new IllegalStateException(String.format("Type [%s] cannot be convert to text!", type));
//	}
//
//	private String toText() {
//		return bytesValue.getValue().toUTF8String();
//	}
//
//	/**
//	 * 返回字节数组的值；
//	 * <p>
//	 * 
//	 * 仅当数据类型 {@link #getType()} 为“字节类型”或“字节衍生类型”时有效；
//	 * <p>
//	 * 
//	 * 无效类型将引发 {@link IllegalStateException} 异常；
//	 * 
//	 * @return
//	 */
//	public byte[] bytesValue() {
//		if (isNil()) {
//			return null;
//		}
//		DataType type = bytesValue.getType();
//		if (type.isBytes()) {
//			return toBytesArray();
//		}
//		throw new IllegalStateException(String.format("Type [%s] cannot be convert to bytes!", type));
//	}
//
//	private byte[] toBytesArray() {
//		return bytesValue.getValue().toBytes();
//	}
//
//	public HashDigest hashDigestValue() {
//		if (isNil()) {
//			return null;
//		}
//		if (DataType.HASH_DIGEST == bytesValue.getType()) {
//			return toHashDegist();
//		}
//		throw new IllegalStateException(
//				String.format("Type [%s] cannot be convert to hash digest!", bytesValue.getType()));
//	}
//
//	private HashDigest toHashDegist() {
//		return new HashDigest(toBytesArray());
//	}
//
//	public PubKey pubKeyValue() {
//		if (isNil()) {
//			return null;
//		}
//		if (DataType.PUB_KEY == bytesValue.getType()) {
//			return toPubKey();
//		}
//		throw new IllegalStateException(String.format("Type [%s] cannot be convert to pub key!", bytesValue.getType()));
//	}
//
//	private PubKey toPubKey() {
//		return new PubKey(toBytesArray());
//	}
//
//	public SignatureDigest signatureDigestValue() {
//		if (isNil()) {
//			return null;
//		}
//		if (DataType.SIGNATURE_DIGEST == bytesValue.getType()) {
//			return toSignatureDigest();
//		}
//		throw new IllegalStateException(
//				String.format("Type [%s] cannot be convert to signature digest!", bytesValue.getType()));
//	}
//
//	private SignatureDigest toSignatureDigest() {
//		return new SignatureDigest(toBytesArray());
//	}
//
//	public BytesValue convertToBytesValue() {
//		return bytesValue == null ? TypedBytesValue.NIL : bytesValue;
//	}
//
//	public static TypedBytesValue fromText(String key, String value, long version) {
//		BytesValue bytesValue = TypedBytesValue.fromText(value);
//		return new TypedBytesValue(bytesValue);
//	}
//
//	public static TypedBytesValue fromBoolean(String key, boolean value, long version) {
//		BytesValue bytesValue = TypedBytesValue.fromBoolean(value);
//		return new TypedBytesValue(bytesValue);
//	}
//
//	public static TypedBytesValue fromInt8(String key, byte value, long version) {
//		BytesValue bytesValue = TypedBytesValue.fromInt8(value);
//		return new TypedBytesValue(bytesValue);
//	}
//
//	public static TypedBytesValue fromInt16(String key, short value, long version) {
//		BytesValue bytesValue = TypedBytesValue.fromInt16(value);
//		return new TypedBytesValue(bytesValue);
//	}
//
//	public static TypedBytesValue fromInt32(String key, int value, long version) {
//		BytesValue bytesValue = TypedBytesValue.fromInt32(value);
//		return new TypedBytesValue(bytesValue);
//	}
//
//	public static TypedBytesValue fromInt64(String key, long value, long version) {
//		BytesValue bytesValue = TypedBytesValue.fromInt64(value);
//		return new TypedBytesValue(bytesValue);
//	}
//
//	public static TypedBytesValue fromBytes(String key, byte[] value, long version) {
//		BytesValue bytesValue = TypedBytesValue.fromBytes(value);
//		return new TypedBytesValue(bytesValue);
//	}
//
//	public static TypedBytesValue fromTimestamp(String key, long value, long version) {
//		BytesValue bytesValue = TypedBytesValue.fromTimestamp(value);
//		return new TypedBytesValue(bytesValue);
//	}
//
//	public static TypedBytesValue fromJSON(String key, String value, long version) {
//		BytesValue bytesValue = TypedBytesValue.fromJSON(value);
//		return new TypedBytesValue(bytesValue);
//	}
//
//	public static TypedBytesValue fromXML(String key, String value, long version) {
//		BytesValue bytesValue = TypedBytesValue.fromXML(value);
//		return new TypedBytesValue(bytesValue);
//	}
//
//	public static TypedBytesValue fromImage(String key, byte[] value, long version) {
//		BytesValue bytesValue = TypedBytesValue.fromImage(value);
//		return new TypedBytesValue(bytesValue);
//	}
//
//	public static TypedBytesValue fromText(Bytes key, String value, long version) {
//		BytesValue bytesValue = TypedBytesValue.fromText(value);
//		return new TypedBytesValue(bytesValue);
//	}
//
//	public static TypedBytesValue fromBoolean(Bytes key, boolean value, long version) {
//		BytesValue bytesValue = TypedBytesValue.fromBoolean(value);
//		return new TypedBytesValue(bytesValue);
//	}
//
//	public static TypedBytesValue fromInt8(Bytes key, byte value, long version) {
//		BytesValue bytesValue = TypedBytesValue.fromInt8(value);
//		return new TypedBytesValue(bytesValue);
//	}
//
//	public static TypedBytesValue fromInt16(Bytes key, short value, long version) {
//		BytesValue bytesValue = TypedBytesValue.fromInt16(value);
//		return new TypedBytesValue(bytesValue);
//	}
//
//	public static TypedBytesValue fromInt32(Bytes key, int value, long version) {
//		BytesValue bytesValue = TypedBytesValue.fromInt32(value);
//		return new TypedBytesValue(bytesValue);
//	}
//
//	public static TypedBytesValue fromInt64(Bytes key, long value, long version) {
//		BytesValue bytesValue = TypedBytesValue.fromInt64(value);
//		return new TypedBytesValue(bytesValue);
//	}
//
//	public static TypedBytesValue fromBytes(Bytes key, byte[] value, long version) {
//		BytesValue bytesValue = TypedBytesValue.fromBytes(value);
//		return new TypedBytesValue(bytesValue);
//	}
//
//	public static TypedBytesValue fromTimestamp(Bytes key, long value, long version) {
//		BytesValue bytesValue = TypedBytesValue.fromTimestamp(value);
//		return new TypedBytesValue(bytesValue);
//	}
//
//	public static TypedBytesValue fromJSON(Bytes key, String value, long version) {
//		BytesValue bytesValue = TypedBytesValue.fromJSON(value);
//		return new TypedBytesValue(bytesValue);
//	}
//
//	public static TypedBytesValue fromXML(Bytes key, String value, long version) {
//		BytesValue bytesValue = TypedBytesValue.fromXML(value);
//		return new TypedBytesValue(bytesValue);
//	}
//
//	public static TypedBytesValue fromImage(Bytes key, byte[] value, long version) {
//		BytesValue bytesValue = TypedBytesValue.fromImage(value);
//		return new TypedBytesValue(bytesValue);
//	}
//
//}
