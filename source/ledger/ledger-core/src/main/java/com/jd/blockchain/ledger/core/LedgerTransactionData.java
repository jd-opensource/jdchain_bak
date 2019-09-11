package com.jd.blockchain.ledger.core;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.DigitalSignature;
import com.jd.blockchain.ledger.LedgerTransaction;
import com.jd.blockchain.ledger.OperationResult;
import com.jd.blockchain.ledger.TransactionContent;
import com.jd.blockchain.ledger.TransactionRequest;
import com.jd.blockchain.ledger.TransactionState;

public class LedgerTransactionData implements LedgerTransaction {

	private TransactionStagedSnapshot txSnapshot;

	private TransactionContent transactionContent;

	private DigitalSignature[] endpointSignatures;

	private DigitalSignature[] nodeSignatures;

	private TransactionState executionState;

	private HashDigest hash;

	private long blockHeight;

	private OperationResult[] operationResults;

	/**
	 * Declare a private no-arguments constructor for deserializing purpose；
	 */
	@SuppressWarnings("unused")
	private LedgerTransactionData() {
	}

	/**
	 * @param blockHeight 区块链高度；
	 * @param txReq       交易请求；
	 * @param execState   执行状态；
	 * @param txSnapshot  交易级的系统快照；
	 */
	public LedgerTransactionData(long blockHeight, TransactionRequest txReq, TransactionState execState,
			TransactionStagedSnapshot txSnapshot, OperationResult... opResults) {
		this.blockHeight = blockHeight;
		this.txSnapshot = txSnapshot;
		this.transactionContent = txReq.getTransactionContent();
		this.endpointSignatures = txReq.getEndpointSignatures();
		this.nodeSignatures = txReq.getNodeSignatures();
		this.executionState = execState;
		this.operationResults = opResults;
	}

	@Override
	public HashDigest getHash() {
		return this.hash;
	}

	@Override
	public long getBlockHeight() {
		return this.blockHeight;
	}

	@Override
	public TransactionState getExecutionState() {
		return executionState;
	}

	@Override
	public OperationResult[] getOperationResults() {
		return operationResults;
	}

	@Override
	public TransactionContent getTransactionContent() {
		return this.transactionContent;
	}

	@Override
	public DigitalSignature[] getEndpointSignatures() {
		return this.endpointSignatures;
	}

	@Override
	public DigitalSignature[] getNodeSignatures() {
		return nodeSignatures;
	}

	@Override
	public HashDigest getAdminAccountHash() {
		return txSnapshot == null ? null : txSnapshot.getAdminAccountHash();
	}

	@Override
	public HashDigest getUserAccountSetHash() {
		return txSnapshot == null ? null : txSnapshot.getUserAccountSetHash();
	}

	@Override
	public HashDigest getDataAccountSetHash() {
		return txSnapshot == null ? null : txSnapshot.getDataAccountSetHash();
	}

	@Override
	public HashDigest getContractAccountSetHash() {
		return txSnapshot == null ? null : txSnapshot.getContractAccountSetHash();
	}

	public void setTxSnapshot(TransactionStagedSnapshot txSnapshot) {
		if (txSnapshot == null) {
			throw new IllegalArgumentException("Transaction snapshot argument is null!");
		}
		this.txSnapshot = txSnapshot;
	}

	public void setTransactionContent(TransactionContent content) {
		this.transactionContent = content;
	}

	public void setEndpointSignatures(DigitalSignature[] participantSignatures) {
		this.endpointSignatures = participantSignatures;
//		int length = participantSignatures.length;
//		this.endpointSignatures = new DigitalSignature[length];
//		for (int i = 0; i < length; i++) {
//			this.endpointSignatures[i] = (DigitalSignature) participantSignatures[i];
//		}
	}

	public void setNodeSignatures(DigitalSignature[] nodeSignatures) {
		this.nodeSignatures = nodeSignatures;
//		int length = nodeSignatures.length;
//		this.nodeSignatures = new DigitalSignature[length];
//		for (int i = 0; i < length; i++) {
//			this.nodeSignatures[i] = (DigitalSignature) nodeSignatures[i];
//		}
	}

	public void setExecutionState(TransactionState executionState) {
		this.executionState = executionState;
	}

	public void setHash(HashDigest hash) {
		this.hash = hash;
	}

	public void setBlockHeight(long blockHeight) {
		this.blockHeight = blockHeight;
	}

	public void setAdminAccountHash(HashDigest adminAccountHash) {
		if (txSnapshot == null) {
			txSnapshot = new TransactionStagedSnapshot();
		}
		txSnapshot.setAdminAccountHash(adminAccountHash);
	}

	public void setUserAccountSetHash(HashDigest userAccountSetHash) {
		if (txSnapshot == null) {
			txSnapshot = new TransactionStagedSnapshot();
		}
		txSnapshot.setUserAccountSetHash(userAccountSetHash);
	}

	public void setDataAccountSetHash(HashDigest dataAccountSetHash) {
		if (txSnapshot == null) {
			txSnapshot = new TransactionStagedSnapshot();
		}
		txSnapshot.setDataAccountSetHash(dataAccountSetHash);
	}

	public void setContractAccountSetHash(HashDigest contractAccountSetHash) {
		if (txSnapshot == null) {
			txSnapshot = new TransactionStagedSnapshot();
		}
		txSnapshot.setContractAccountSetHash(contractAccountSetHash);
	}
}
