package com.jd.blockchain.consensus.client;

import com.jd.blockchain.consensus.ConsensusSettings;
import com.jd.blockchain.crypto.PubKey;

/**
 * 共识客户端的配置参数；
 * 
 * @author huanghaiquan
 *
 */
public interface ClientSettings {

	/**
	 * 客户端ID；
	 * 
	 * @return
	 */
	int getClientId();

	/**
	 * 客户端的公钥；
	 * 
	 * @return
	 */
	PubKey getClientPubKey();

	/**
	 * 共识网络的配置参数；
	 * 
	 * @return
	 */
	ConsensusSettings getConsensusSettings();

}
