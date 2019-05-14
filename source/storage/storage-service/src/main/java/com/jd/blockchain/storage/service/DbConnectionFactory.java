package com.jd.blockchain.storage.service;

import java.io.Closeable;

public interface DbConnectionFactory extends Closeable {

	/**
	 * 数据库连接前缀
	 * @return
	 */
	String dbPrefix();

	/**
	 * 是否支持指定 scheme 的连接字符串；
	 * @param scheme
	 * @return
	 */
	boolean support(String scheme);
	
	DbConnection connect(String dbConnectionString);
	
	DbConnection connect(String dbConnectionString, String password);
	
	@Override
	void close();
}
