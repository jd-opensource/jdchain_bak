package com.jd.blockchain.ledger;

/**
 * 强类型的“键-值”数据对象；
 * 
 * <p>
 * 
 * {@link TypedKVData} 被设计为只读对象；
 * 
 * @author huanghaiquan
 *
 */
public class TypedKVData implements TypedKVEntry {

	private String key;

	private long version;

	private DataType type;

	private Object value;

	public TypedKVData(String key, long version, DataType type, Object value) {
		this.key = key;
		this.version = version;
		this.type = type;
		this.value = value;
	}

	public TypedKVData(String key, long version, BytesValue bytesValue) {
		this.key = key;
		this.version = version;
		TypedValue typedValue;
		if (bytesValue != null && bytesValue instanceof TypedValue) {
			typedValue = (TypedValue) bytesValue;
		} else {
			typedValue = TypedValue.wrap(bytesValue);
		}
		this.type = typedValue.getType();
		this.value = typedValue.getValue();
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
	public DataType getType() {
		return type;
	}

	@Override
	public Object getValue() {
		return value;
	}

}