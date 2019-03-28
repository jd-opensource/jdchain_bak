package com.jd.blockchain.utils.serialize.json;

/**
 * 数组的合并策略；
 * 
 * @author haiq
 *
 */
public enum ArrayMergeStrategy {
	
	/**
	 * 替换策略：用新的数组替换掉原来的数组；
	 */
	REPLACE,
	
	/**
	 * 追加策略：把新数组的元素追加到原来的数组元素之后；
	 */
	APPEND,
	
	/**
	 * 深度合并策略：对于相同位置的元素，如果是原生值则进行替换，如果是JSON对象，则进行递归合并；新数组多出的元素则追加到原数组后面；
	 */
	DEEP_MERGE
	
}
