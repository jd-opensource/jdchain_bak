package com.jd.blockchain.ledger.core;

/**
 * 多重身份的校验策略；
 * 
 * @author huanghaiquan
 *
 */
public enum MultiIDsPolicy {

	/**
	 * 至少有一个都能通过；
	 */
	AT_LEAST_ONE,

	/**
	 * 每一个都能通过；
	 */
	ALL

}
