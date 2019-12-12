package com.jd.blockchain.ledger;

public interface TypedKVEntry {

	/**
	 * 键名；
	 * 
	 * @return
	 */
	String getKey();

	/**
	 * 版本；
	 * <p>
	 * 有效的版本大于等于 0 ；
	 * <p>
	 * 如果返回 -1 ，则表示此项数据无效；
	 * 
	 * @return
	 */
	long getVersion();

	/**
	 * 数据类型；
	 * 
	 * @return
	 */
	DataType getType();
	
	/**
	 * 值；
	 * @return
	 */
	Object getValue();
	
	default long longValue() {
		if (getType() == DataType.INT64) {
			Object value = getValue();
			return value == null ? 0 : (long) value;
		}
		throw new IllegalStateException(String.format("Expected type [%s], but [%s]", DataType.INT64, getType()));
	}
	
}