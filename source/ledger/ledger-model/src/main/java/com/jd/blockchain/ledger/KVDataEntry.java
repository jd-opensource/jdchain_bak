package com.jd.blockchain.ledger;

public interface KVDataEntry {

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
	BytesValueType getType();
	
	/**
	 * 值；
	 * @return
	 */
	Object getValue();
	
}