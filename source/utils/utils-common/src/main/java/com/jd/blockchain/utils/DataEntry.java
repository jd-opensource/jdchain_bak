package com.jd.blockchain.utils;

/**
 * 版本化的键值数据项；
 * 
 * @author huanghaiquan
 *
 */
public interface DataEntry<K, V> {

	public K getKey();

	public long getVersion();

	public V getValue();

}