package com.jd.blockchain.gateway;

import com.jd.blockchain.crypto.AsymmetricKeypair;
import com.jd.blockchain.utils.net.NetworkAddress;

import java.util.List;

public interface PeerConnector {
	
	NetworkAddress getPeerAddress();
	
	boolean isConnected();
	
	void connect(NetworkAddress peerAddress, AsymmetricKeypair defaultKeyPair, List<String> peerProviders);

	void reconnect();
	
	void close();
	
}
