package com.jd.blockchain.sdk.service;

import com.jd.blockchain.consensus.GroupIndexer;
import com.jd.blockchain.ledger.TransactionRequest;

public class LedgerGroupIndexer implements GroupIndexer {

	@Override
	public byte[] getGroupId(Object[] messageObjects) {
		return ((TransactionRequest)messageObjects[0]).getTransactionContent().getLedgerHash().toBytes();
	}

}
