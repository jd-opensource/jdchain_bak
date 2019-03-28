package com.jd.blockchain.consensus.service;

public interface StateHandle {
	
	byte[] takeSnapshot();
	
	void installSnapshot(byte[] snapshot);
	
}
