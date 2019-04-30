package com.jd.blockchain.transaction;

import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.ledger.ParticipantNode;
import com.jd.blockchain.utils.net.NetworkAddress;

public class ConsensusParticipantData implements ParticipantNode {
	
		private int id;
		
		private String address;

		private String name;

		private PubKey pubKey;

		private NetworkAddress hostAddress;

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

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

	}