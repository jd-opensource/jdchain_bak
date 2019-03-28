package com.jd.blockchain.ledger.core;

import com.jd.blockchain.ledger.ParticipantNode;

public interface LedgerAdministration {

	LedgerMetadata getMetadata();
	
	long getParticipantCount();

//	ParticipantNode getParticipant(int id);
	
	ParticipantNode[] getParticipants();
	
}