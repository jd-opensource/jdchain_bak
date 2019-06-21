package com.jd.blockchain.consensus;

import com.jd.blockchain.ledger.ParticipantNode;

import java.util.Properties;

public interface ConsensusSettingsBuilder {

	/**
	 * 从属性表中解析生成共识网络的参数配置；
	 * 
	 * @param props
	 *            属性表；
	 * @param participantNodes
	 *            参与方列表；<br>
	 * @return
	 */
	ConsensusSettings createSettings(Properties props, ParticipantNode[] participantNodes);
	
	Properties createPropertiesTemplate();

	void writeSettings(ConsensusSettings settings, Properties props);
}
