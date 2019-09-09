package com.jd.blockchain.peer.consensus;

import java.util.Arrays;

import com.jd.blockchain.ledger.ParticipantNode;
import com.jd.blockchain.peer.ConsensusRealm;
import com.jd.blockchain.utils.Bytes;

public class ConsensusRealmImpl implements ConsensusRealm {

	private ParticipantNode[] nodes;

	private Bytes setting;

	private int hashCode;

	public ConsensusRealmImpl(ParticipantNode[] nodeList) {
		this.nodes = nodeList;
		Bytes[] addrs = new Bytes[nodes.length];
		int i = 0;
		for (ParticipantNode n : nodes) {
			addrs[i++] = n.getAddress();
		}
		this.hashCode = Arrays.hashCode(addrs);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.jd.blockchain.peer.consensus.ConsensusRealm#getConsensusParticipants()
	 */
	@Override
	public ParticipantNode[] getNodes() {
		return Arrays.copyOf(nodes, nodes.length);
		// return participantNodes.toArray(new
		// ConsensusParticipant[participantNodes.size()]);
	}

	// public void addNode(ConsensusNode participantNode) {
	// participantNodes.add(participantNode);
	// }

	@Override
	public Bytes getSetting() {
		return setting;
	}

	public void setSetting(Bytes setting) {
		this.setting = setting;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.jd.blockchain.peer.consensus.ConsensusRealm#hasCommon(com.jd.blockchain.
	 * peer.consensus.ConsensusRealm)
	 */
	@Override
	public boolean hasIntersection(ConsensusRealm otherRealm) {
		// in case: different ledger ,same consensus realm
		if (this.equals(otherRealm)) {
			return true;
		}
		// in case: consensus realm has intersection
		ParticipantNode[] otherNodes = otherRealm.getNodes();
		for (ParticipantNode node : nodes) {
			for (ParticipantNode other : otherNodes) {
				if (node.getAddress().equals(other.getAddress())) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return hashCode;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (obj instanceof ConsensusRealmImpl) {
			ConsensusRealmImpl realm = (ConsensusRealmImpl) obj;
			return this.hashCode() == realm.hashCode();
		}
		return false;
	}
}
