package com.jd.blockchain.ledger.core;

import com.jd.blockchain.ledger.LedgerAdminSettings;
import com.jd.blockchain.ledger.ParticipantDataQuery;

public interface LedgerAdminDataQuery {
	
	LedgerAdminSettings getAdminInfo();

	ParticipantDataQuery getParticipantDataset();

}