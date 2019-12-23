package com.jd.blockchain.ledger.core;

import com.jd.blockchain.ledger.ParticipantNode;

/**
 * @author hhq
 * @version 1.0
 * @created 14-6��-2018 12:13:33
 */
public class Peer extends Node {

	public ParticipantNode m_Participant;

	public Peer(){

	}

	public void finalize() throws Throwable {
		super.finalize();
	}

}