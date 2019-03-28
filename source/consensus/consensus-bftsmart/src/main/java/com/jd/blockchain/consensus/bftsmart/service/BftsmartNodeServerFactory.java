package com.jd.blockchain.consensus.bftsmart.service;

import com.jd.blockchain.consensus.ConsensusSettings;
import com.jd.blockchain.consensus.NodeSettings;
import com.jd.blockchain.consensus.bftsmart.BftsmartConsensusSettings;
import com.jd.blockchain.consensus.bftsmart.BftsmartNodeSettings;
import com.jd.blockchain.consensus.service.MessageHandle;
import com.jd.blockchain.consensus.service.NodeServer;
import com.jd.blockchain.consensus.service.NodeServerFactory;
import com.jd.blockchain.consensus.service.ServerSettings;
import com.jd.blockchain.consensus.service.StateMachineReplicate;
import com.jd.blockchain.utils.net.NetworkAddress;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BftsmartNodeServerFactory implements NodeServerFactory {

	private static Map<String, NodeSettings[]> nodeServerMap = new ConcurrentHashMap<>();


	@Override
	public ServerSettings buildServerSettings(String realmName, ConsensusSettings consensusSetting, String currentNodeAddress) {

		NodeSettings serverNode = null;

		BftsmartServerSettingConfig serverSettings = new BftsmartServerSettingConfig();

		//find current node according to current address
		for (NodeSettings nodeSettings : consensusSetting.getNodes()) {
			if (nodeSettings.getAddress().equals(currentNodeAddress)) {
				serverNode = nodeSettings;
				break;
			}
		}

		if (serverNode == null) {
			throw new IllegalArgumentException();
		}

		//set server settings
		serverSettings.setRealmName(realmName);

		serverSettings.setReplicaSettings(serverNode);

		serverSettings.setConsensusSettings((BftsmartConsensusSettings) consensusSetting);

		return serverSettings;

	}

	@Override
	public NodeServer setupServer(ServerSettings serverSettings, MessageHandle messageHandler,
								  StateMachineReplicate stateMachineReplicator) {

		NodeSettings[] currNodeSettings = (((BftsmartServerSettings)serverSettings).getConsensusSettings()).getNodes();

		//check conflict realm
		if (!hasIntersection(currNodeSettings)) {
			BftsmartNodeServer nodeServer = new BftsmartNodeServer(serverSettings, messageHandler, stateMachineReplicator);
			nodeServerMap.put(serverSettings.getRealmName(), currNodeSettings);
			return nodeServer;
		}
		else {
			throw new IllegalArgumentException("setupServer serverSettings parameters error!");
		}
	}


	//check if consensus realm conflict, by this support multi ledgers
	private boolean hasIntersection(NodeSettings[] currNodeSettings) {

		int currHashCode = getHashcode(currNodeSettings);

		//first check if is same consensus realm
		for (NodeSettings[] exisitNodeSettings : nodeServerMap.values()) {
			if (currHashCode == getHashcode(exisitNodeSettings)) {
				return false;
			}
		}
		//check conflict
		for (NodeSettings[] exisitNodeSettings : nodeServerMap.values()) {
			for (NodeSettings curr : currNodeSettings) {
				for (NodeSettings exist : exisitNodeSettings) {
					if (((BftsmartNodeSettings)curr).getNetworkAddress().equals(((BftsmartNodeSettings)exist).getNetworkAddress())) {
						return true;
					}
				}
			}
		}

		return false;
	}

	//compute hashcode for consensus nodes
	private int getHashcode(NodeSettings[] nodeSettings) {

		int i = 0;
		NetworkAddress[] nodeAddrs = new NetworkAddress[nodeSettings.length];
		for (NodeSettings setting : nodeSettings) {

			nodeAddrs[i++] = ((BftsmartNodeSettings)setting).getNetworkAddress();
		}
		int hashCode = Arrays.hashCode(nodeAddrs);
		return hashCode;

	}

}
