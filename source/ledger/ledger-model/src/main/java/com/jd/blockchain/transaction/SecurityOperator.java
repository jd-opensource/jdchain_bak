package com.jd.blockchain.transaction;

/**
 * 与安全配置相关的操作门面；
 * 
 * <br>
 * 
 * 只能通过客户端接口直接操作；不支持通过合约操作；
 * 
 * @author huanghaiquan
 *
 */
public interface SecurityOperator {

	/**
	 * 注册账户操作；
	 * 
	 * @return
	 */

	SecurityOperationBuilder security();

}