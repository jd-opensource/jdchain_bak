package com.jd.blockchain.storage.service;

import com.jd.blockchain.utils.Bytes;

/**
 * 支持存在性策略更新的 KV 存储；
 * 
 * @author huanghaiquan
 *
 */
public interface ExPolicyKVStorage extends BatchStorageService{

	/**
	 * 返回“键”对应的“值”；<br>
	 * 如果“键”不存在，则返回 null；
	 * 
	 * @param key
	 * @return
	 */
	byte[] get(Bytes key);
	
	/**
	 * 如果满足指定的存在性策略，则创建/更新指定的“键-值”；
	 * 
	 * @param key
	 *            键；
	 * @param value
	 *            值；
	 * @param ex
	 *            如果指定 {@link ExPolicy#EXISTING} ，则只有键存在时才更新； <br>
	 *            如果指定 {@link ExPolicy#NOT_EXISTING} ，则只有键不存在时才更新/创建；
	 * @return 如果符合存在性策略，并执行了创建/更新操作，则返回 true，否则返回 false；
	 */
	boolean set(Bytes key, byte[] value, ExPolicy ex);
	
	/**
	 * 指定的 key 是否存在；
	 * 
	 * @param key
	 * @return
	 */
	boolean exist(Bytes key);

	/**
	 * 存在性策略；
	 * 
	 * @author huanghaiquan
	 *
	 */
	public static enum ExPolicy {

		/**
		 * 已存在；
		 */
		EXISTING,

		/**
		 * 不存在；
		 */
		NOT_EXISTING
	}

}
