package com.jd.blockchain.sdk;

public interface BlockchainEventListener {
	
	public void onEvent(BlockchainEventMessage eventMessage, BlockchainEventHandle eventHandle);
	
}
