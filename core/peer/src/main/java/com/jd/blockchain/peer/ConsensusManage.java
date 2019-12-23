package com.jd.blockchain.peer;

import com.jd.blockchain.consensus.service.NodeServer;

/**
 * 共识服务管理；
 * @author huanghaiquan
 *
 */
public interface ConsensusManage {
	
	/**
	 * @return
	 */
	ConsensusRealm[] getRealms();

	/**
	 * 启动参与的全部共识域的共识服务；
	 */
	void runAllRealms();

	/**
	 * 启动某个共识服务
	 * @param nodeServer
	 */
	void runRealm(NodeServer nodeServer);
	
	/**
	 * 停止共识服务；
	 */
	void closeAllRealms();
	
}
