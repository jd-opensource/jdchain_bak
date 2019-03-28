package com.jd.blockchain.utils;

import java.util.Set;

/**
 * AttributeMap 定义字符串 key-value 属性表的通用访问接口；
 * 
 * @author haiq
 *
 */
public interface AttributeMap {

	/**
	 * 属性名称列表；
	 * 
	 * @return String's set
	 */
	public Set<String> getAttributeNames();

	/**
	 * 是否包含指定名称的属性；
	 * @param name name;
	 * @return boolean
	 */
	public boolean containAttribute(String name);

	/**
	 * 返回指定名称的属性值；
	 * 
	 * @param name name
	 * @return String
	 */
	public String getAttribute(String name);

}
