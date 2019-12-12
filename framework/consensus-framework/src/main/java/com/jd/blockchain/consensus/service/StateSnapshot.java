package com.jd.blockchain.consensus.service;

/**
 * 状态快照；
 * 
 * @author huanghaiquan
 *
 */
public interface StateSnapshot {

	/**
	 * 状态的唯一编号；
	 * 
	 * @return
	 */
	long getId();

	/**
	 * 状态的快照数据；
	 * 
	 * @return
	 */
	byte[] getSnapshot();
}
