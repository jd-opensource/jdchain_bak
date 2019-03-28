package com.jd.blockchain.ledger.data;

import com.alibaba.fastjson.JSON;
import com.jd.blockchain.ledger.DataAccountKVSetOperation;
import com.jd.blockchain.ledger.DataType;
import com.jd.blockchain.utils.Bytes;

import java.util.Date;

public interface DataAccountKVSetOperationBuilder {

	/**
	 * 数据账户的KV写入操作；
	 * 
	 * @return
	 */
	DataAccountKVSetOperation getOperation();

	/**
	 * 写入键值；
	 * 
	 * @param key
	 *            键；
	 * @param value
	 *            值；byte[]格式
	 * @param expVersion
	 *            预期的当前版本；如果版本不匹配，则写入失败；
	 * @return
	 */
	DataAccountKVSetOperationBuilder set(String key, byte[] value, long expVersion);
	/**
	 * 写入键值；
	 *
	 * @param key
	 *            键；
	 * @param value
	 *            值；String格式
	 * @param expVersion
	 *            预期的当前版本；如果版本不匹配，则写入失败；
	 * @return
	 */
	DataAccountKVSetOperationBuilder set(String key, String value, long expVersion);
	/**
	 * 写入键值；
	 *
	 * @param key
	 *            键；
	 * @param value
	 *            值；Bytes格式
	 * @param expVersion
	 *            预期的当前版本；如果版本不匹配，则写入失败；
	 * @return
	 */
	DataAccountKVSetOperationBuilder set(String key, Bytes value, long expVersion);
	/**
	 * 写入键值；
	 *
	 * @param key
	 *            键；
	 * @param value
	 *            值；long格式
	 * @param expVersion
	 *            预期的当前版本；如果版本不匹配，则写入失败；
	 * @return
	 */
	DataAccountKVSetOperationBuilder set(String key, long value, long expVersion);

}
