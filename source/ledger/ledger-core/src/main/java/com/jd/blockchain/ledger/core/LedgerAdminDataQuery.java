package com.jd.blockchain.ledger.core;

import com.jd.blockchain.ledger.LedgerAdminInfo;
import com.jd.blockchain.ledger.ParticipantDataQuery;

public interface LedgerAdminDataQuery {
	
	LedgerAdminInfo getAdminInfo();

	ParticipantDataQuery getParticipantDataset();

}