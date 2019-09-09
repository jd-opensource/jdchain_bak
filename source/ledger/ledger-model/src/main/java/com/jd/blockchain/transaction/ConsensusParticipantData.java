package com.jd.blockchain.transaction;

import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.ledger.ParticipantNode;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.net.NetworkAddress;
import com.jd.blockchain.ledger.ParticipantNodeState;

public class ConsensusParticipantData implements ParticipantNode {
	
		private int id;
		
		private Bytes address;

		private String name;

		private PubKey pubKey;

		private NetworkAddress hostAddress;

	    private ParticipantNodeState participantNodeState;


		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public NetworkAddress getConsensusAddress() {
			return hostAddress;
		}

		public void setHostAddress(NetworkAddress hostAddress) {
			this.hostAddress = hostAddress;
		}

		public PubKey getPubKey() {
			return pubKey;
		}

		public void setPubKey(PubKey pubKey) {
			this.pubKey = pubKey;
		}

		public Bytes getAddress() {
			return address;
		}

		public void setAddress(Bytes address) {
			this.address = address;
		}

	    public ParticipantNodeState getParticipantNodeState() {
		return participantNodeState;
	}

	    public void setParticipantState(ParticipantNodeState participantNodeState) {
			this.participantNodeState = participantNodeState;
		}

	}