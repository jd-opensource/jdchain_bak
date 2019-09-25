package com.jd.blockchain.utils;

/**
 * 版本化的键值数据项；
 * 
 * @author huanghaiquan
 *
 */
public interface VersioningKVEntry<K, V>{
	
	K getKey();
	
	long getVersion();

	V getValue();

}