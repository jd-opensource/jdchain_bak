package com.jd.blockchain.utils;

/**
 * Versioning Key-Value data entry；
 * 
 * @author huanghaiquan
 *
 */
public interface DataEntry<K, V> {

	public K getKey();

	public long getVersion();

	public V getValue();

}