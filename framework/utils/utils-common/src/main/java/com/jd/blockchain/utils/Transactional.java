package com.jd.blockchain.utils;

/**
 * {@link Transactional} 表示一种事务特性，即对一个对象的状态的更改是临时性的，直到成功地调用 {@link #commit()}
 * 方法提交变更，或者调用 {@link #cancel()} 方法取消变更；
 * 
 * @author huanghaiquan
 *
 */
public interface Transactional {

	/**
	 * 是否发生了变更；<br>
	 * 
	 * 自上一次调用 {@link #commit()} 或 {@link #cancel()} 之后，对象的状态如果发生了变更，则返回 true，否则返回
	 * false；
	 * 
	 * @return boolean
	 */
	boolean isUpdated();

	/**
	 * 提交所有变更；<br>
	 * 
	 * 如果执行前 {@link #isUpdated()} 返回 true，则提交之后 {@link #isUpdated()} 返回 false；
	 */
	void commit();

	/**
	 * 取消所有变更；<br>
	 * 
	 * 如果执行前 {@link #isUpdated()} 返回 true，则取消之后 {@link #isUpdated()} 返回 false；
	 */
	void cancel();

}
