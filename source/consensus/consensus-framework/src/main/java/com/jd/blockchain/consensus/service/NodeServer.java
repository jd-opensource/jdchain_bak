package com.jd.blockchain.consensus.service;

import com.jd.blockchain.consensus.ConsensusManageService;

public interface NodeServer {
	
	String getProviderName();
	
	ConsensusManageService getManageService();
	
	ServerSettings getSettings();
	
	boolean isRunning();
	
	void start();
	
	void stop();
	
}
