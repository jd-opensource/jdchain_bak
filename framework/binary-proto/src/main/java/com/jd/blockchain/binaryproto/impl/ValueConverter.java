package com.jd.blockchain.binaryproto.impl;

public interface ValueConverter {

	Class<?> getValueType();

	/**
	 * 返回类型的默认初始值；
	 * 
	 * @return
	 */
	Object getDefaultValue();

}
