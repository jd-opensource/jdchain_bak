package com.jd.blockchain.utils.security;

public interface SHA256Hash {

	/**
	 * 追加要一起计算哈希的数据；
	 * 
	 * @param bytes
	 */
	void update(byte[] bytes);
	
	/**
	 * 追加要一起计算哈希的数据；
	 * 
	 * @param bytes
	 */
	void update(byte[] bytes, int offset, int len);

	/**
	 * 完成哈希计算并返回结果；
	 * 
	 * @return
	 */
	byte[] complete();

}
