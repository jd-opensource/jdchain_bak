package com.jd.blockchain.consensus.service;

import com.jd.blockchain.consensus.NodeSettings;

/**
 * Replica 服务器的本地配置；
 * 
 * @author huanghaiquan
 *
 */
public interface ServerSettings {

	String getRealmName();

	NodeSettings getReplicaSettings();

}
