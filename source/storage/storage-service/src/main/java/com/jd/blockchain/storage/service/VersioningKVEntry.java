package com.jd.blockchain.storage.service;

import com.jd.blockchain.utils.Bytes;

/**
 * 版本化的键值数据项；
 * 
 * @author huanghaiquan
 *
 */
public interface VersioningKVEntry {
	
//	String getKey();
	Bytes getKey();
	
	long getVersion();

	byte[] getValue();

}