package com.jd.blockchain.consensus.service;

import com.jd.blockchain.consensus.ConsensusSettings;

/**
 * 共识节点服务器的工厂；
 * 
 * @author huanghaiquan
 *
 */
public interface NodeServerFactory {

	/**
	 * 构建一个共识节点的参数配置；
	 * 
	 * @param realmName
	 *            共识域的名称；
	 * @param consensusSetting
	 *            共识配置；
	 * @param currentNodeAddress
	 *            共识节点的虚拟地址；必须是 {@link ConsensusSettings#getNodes()} 中的一项；
	 * @return 共识节点的参数配置；
	 */
	ServerSettings buildServerSettings(String realmName, ConsensusSettings consensusSetting, String currentNodeAddress);

	/**
	 * 创建一个节点服务器；
	 * 
	 * @param serverSettings
	 *            服务器配置；
	 * @param messageHandler
	 * @param stateMachineReplicator
	 * @return
	 */
	NodeServer setupServer(ServerSettings serverSettings, MessageHandle messageHandler,
			StateMachineReplicate stateMachineReplicator);

}
