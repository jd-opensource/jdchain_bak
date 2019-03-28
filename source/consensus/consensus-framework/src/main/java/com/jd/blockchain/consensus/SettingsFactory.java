package com.jd.blockchain.consensus;

import com.jd.blockchain.utils.io.BytesEncoder;

/**
 * 配置参数的编码器；
 * 
 * @author huanghaiquan
 *
 */
public interface SettingsFactory {

	ConsensusSettingsBuilder getConsensusSettingsBuilder();

	BytesEncoder<ConsensusSettings> getConsensusSettingsEncoder();

	BytesEncoder<ClientIncomingSettings> getIncomingSettingsEncoder();
	
	
	
}
