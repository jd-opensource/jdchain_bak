package com.jd.blockchain.utils;

/**
 * 数据迭代器；
 * 
 * @author huanghaiquan
 *
 * @param <K>
 * @param <V>
 */
public interface DataIterator<K, V> {

	void skip(long count);
	
	DataEntry<K, V> next();

	DataEntry<K, V>[] next(int count);
	
	boolean hasNext();
	
}
