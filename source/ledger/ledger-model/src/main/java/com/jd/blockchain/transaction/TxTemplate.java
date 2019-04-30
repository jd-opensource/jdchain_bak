package com.jd.blockchain.transaction;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.PreparedTransaction;
import com.jd.blockchain.ledger.TransactionRequestBuilder;
import com.jd.blockchain.ledger.TransactionTemplate;
import com.jd.blockchain.utils.Bytes;

public class TxTemplate implements TransactionTemplate {

	private TxBuilder txBuilder;

	private TransactionService txService;

	public TxTemplate(HashDigest ledgerHash, TransactionService txService) {
		this.txBuilder = new TxBuilder(ledgerHash);
		this.txService = txService;
	}

	@Override
	public HashDigest getLedgerHash() {
		return txBuilder.getLedgerHash();
	}

	@Override
	public PreparedTransaction prepare() {
		TransactionRequestBuilder txReqBuilder = txBuilder.prepareRequest();
		return new PreparedTx(txReqBuilder, txService);
	}

	@Override
	public UserRegisterOperationBuilder users() {
		return txBuilder.users();
	}

	@Override
	public DataAccountRegisterOperationBuilder dataAccounts() {
		return txBuilder.dataAccounts();
	}
	
	@Override
	public DataAccountKVSetOperationBuilder dataAccount(String accountAddress) {
		return txBuilder.dataAccount(accountAddress);
	}

	@Override
	public DataAccountKVSetOperationBuilder dataAccount(Bytes accountAddress) {
		return txBuilder.dataAccount(accountAddress);
	}

	@Override
	public ContractCodeDeployOperationBuilder contracts() {
		return txBuilder.contracts();
	}

	@Override
	public ContractEventSendOperationBuilder contractEvents() {
		return txBuilder.contractEvents();
	}
	
	@Override
	public <T> T contract(String address, Class<T> contractIntf) {
		return txBuilder.contract(address, contractIntf);
	}

}
