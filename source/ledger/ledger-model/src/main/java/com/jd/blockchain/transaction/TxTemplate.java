package com.jd.blockchain.transaction;

import java.io.IOException;
import java.util.Collection;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.PreparedTransaction;
import com.jd.blockchain.ledger.TransactionRequestBuilder;
import com.jd.blockchain.ledger.TransactionTemplate;
import com.jd.blockchain.utils.Bytes;

public class TxTemplate implements TransactionTemplate {

	private TxBuilder txBuilder;

	private TransactionService txService;

	private TxStateManager stateManager;

	public TxTemplate(HashDigest ledgerHash, TransactionService txService) {
		this.stateManager = new TxStateManager();
		this.txBuilder = new TxBuilder(ledgerHash);
		this.txService = txService;
	}

	@Override
	public HashDigest getLedgerHash() {
		return txBuilder.getLedgerHash();
	}

	@Override
	public PreparedTransaction prepare() {
		stateManager.prepare();
		TransactionRequestBuilder txReqBuilder = txBuilder.prepareRequest();
		return new PreparedTx(stateManager, txReqBuilder, txService, txBuilder.getReturnValuehandlers());
	}

	@Override
	public SecurityOperationBuilder security() {
		stateManager.operate();
		return txBuilder.security();
	}

	@Override
	public UserRegisterOperationBuilder users() {
		stateManager.operate();
		return txBuilder.users();
	}

	@Override
	public DataAccountRegisterOperationBuilder dataAccounts() {
		stateManager.operate();
		return txBuilder.dataAccounts();
	}

	@Override
	public DataAccountKVSetOperationBuilder dataAccount(String accountAddress) {
		stateManager.operate();
		return txBuilder.dataAccount(accountAddress);
	}

	@Override
	public DataAccountKVSetOperationBuilder dataAccount(Bytes accountAddress) {
		stateManager.operate();
		return txBuilder.dataAccount(accountAddress);
	}

	@Override
	public ContractCodeDeployOperationBuilder contracts() {
		stateManager.operate();
		return txBuilder.contracts();
	}

	@Override
	public ParticipantRegisterOperationBuilder participants() {
		stateManager.operate();
		return txBuilder.participants();
	}

	@Override
	public ParticipantStateUpdateOperationBuilder states() {
		stateManager.operate();
		return txBuilder.states();
	}

	@Override
	public <T> T contract(Bytes address, Class<T> contractIntf) {
		stateManager.operate();
		return txBuilder.contract(address, contractIntf);
	}

	@Override
	public <T> T contract(String address, Class<T> contractIntf) {
		stateManager.operate();
		return txBuilder.contract(address, contractIntf);
	}

	@Override
	public void close() throws IOException {
		if (!stateManager.close()) {
			Collection<OperationResultHandle> handlers = txBuilder.getReturnValuehandlers();
			if (handlers.size() > 0) {
				TransactionCancelledExeption error = new TransactionCancelledExeption(
						"Transaction template has been cancelled!");
				for (OperationResultHandle handle : handlers) {
					handle.complete(error);
				}
			}
		}
	}
}
