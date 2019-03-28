package com.jd.blockchain.sdk.client;

import com.jd.blockchain.crypto.hash.HashDigest;
import com.jd.blockchain.ledger.data.TransactionService;
import com.jd.blockchain.sdk.BlockchainQueryService;
import com.jd.blockchain.sdk.proxy.BlockchainServiceProxy;

public class GatewayBlockchainServiceProxy extends BlockchainServiceProxy {

	private BlockchainQueryService queryService;

	private TransactionService txService;

	public GatewayBlockchainServiceProxy(TransactionService txService, BlockchainQueryService queryService) {
		this.txService = txService;
		this.queryService = queryService;
	}

	@Override
	public HashDigest[] getLedgerHashs() {
		return queryService.getLedgerHashs();
	}

	@Override
	protected TransactionService getTransactionService(HashDigest ledgerHash) {
		return txService;
	}

	@Override
	protected BlockchainQueryService getQueryService(HashDigest ledgerHash) {
		return queryService;
	}
}
