package com.jd.blockchain.utils.http.agent;

import java.util.Map;

/**
 * 路径参数解析器；
 * 
 * @author haiq
 *
 */
interface PathParamResolver {
	
	/**
	 * 将方法参数列表解析为路径参数的变量表；
	 * 
	 * @param args 方法参数列表；
	 * @return
	 */
	Map<String, String> resolve(Object[] args);
	
}
