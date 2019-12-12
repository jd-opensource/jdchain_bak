package com.jd.blockchain.peer.consensus;

import java.io.Serializable;
import java.util.ArrayList;

import com.jd.blockchain.utils.net.NetworkAddress;

public class ConsensusViewDefinition implements Serializable {

	private static final long serialVersionUID = -201642288565436003L;

	private static int[] EMPTY_IDS = {};

	private ArrayList<NodeInfo> nodes = new ArrayList<>();

	public int getF() {
		return (int) ((getNodeCount() - 1) / 3);
	}

	/**
	 * 法定的数量；
	 * 
	 * @return
	 */
	public int getN() {
		return 3 * getF() + 1;
	}

	/**
	 * 实际的节点数量；
	 * 
	 * @return
	 */
	public int getNodeCount() {
		return nodes.size();
	}

	/**
	 * 返回法定数量的初始节点的 ID 列表；
	 * 
	 * @return
	 */
	public int[] getQuorumIDs() {
		if (getNodeCount() == 0) {
			return EMPTY_IDS;
		}
		int[] ids = new int[getN()];
		for (int i = 0; i < ids.length; i++) {
			ids[i] = nodes.get(i).getId();
		}
		return ids;
	}

	/**
	 * 加入节点信息；
	 * 
	 * @param networkAddress
	 * @return 返回分配给新节点的 ID （ID >= 0）；如果节点数量以达到 (3f+1) 的数量；
	 */
	public synchronized int addNode(NetworkAddress networkAddress) {
		for (NodeInfo ninf : nodes) {
			if (ninf.getNetworkAddress().equals(networkAddress)) {
				throw new IllegalArgumentException("Add node[" + networkAddress.toString() + "] reaptly!");
			}
		}
		NodeInfo nodeInfo = new NodeInfo(nodes.size(), networkAddress);
		nodes.add(nodeInfo);
		return nodeInfo.getId();
	}

	public static class NodeInfo implements Serializable {

		private static final long serialVersionUID = -9178639061945239622L;

		private int id;

		private NetworkAddress networkAddress;

		public NodeInfo(int id, NetworkAddress networkAddress) {
			this.id = id;
			this.networkAddress = networkAddress;
		}

		public int getId() {
			return id;
		}

		public NetworkAddress getNetworkAddress() {
			return networkAddress;
		}
	}

}
