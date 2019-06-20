package com.jd.blockchain.transaction;

import com.jd.blockchain.ledger.TransactionState;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.OperationResult;
import com.jd.blockchain.ledger.TransactionResponse;

/**
 * @author huanghaiquan
 *
 */
public class TxResponseMessage implements TransactionResponse {
	
	private HashDigest contentHash;
	
	private HashDigest blockHash;
	
	private long blockHeight;

	private TransactionState executionState;

	private OperationResult[] operationResults;
	
	public TxResponseMessage() {
	}

	// 重新包装operationResults
	public TxResponseMessage(TransactionResponse transactionResponse, OperationResult[] operationResults) {
		this.contentHash = transactionResponse.getContentHash();
		this.blockHash = transactionResponse.getBlockHash();
		this.blockHeight = transactionResponse.getBlockHeight();
		this.executionState = transactionResponse.getExecutionState();
		this.operationResults = operationResults;
	}
	
	public TxResponseMessage(HashDigest contentHash) {
		this.contentHash = contentHash;
	}
	
	@Override
	public HashDigest getContentHash() {
		return contentHash;
	}
	
	@Override
	public TransactionState getExecutionState() {
		return executionState;
	}

	public void setExecutionState(TransactionState executionState) {
		this.executionState = executionState;
	}

	@Override
	public HashDigest getBlockHash() {
		return blockHash;
	}

	public void setBlockHash(HashDigest blockHash) {
		this.blockHash = blockHash;
	}

	@Override
	public long getBlockHeight() {
		return blockHeight;
	}

	public void setBlockHeight(long blockHeight) {
		this.blockHeight = blockHeight;
	}

	public void setOperationResults(OperationResult[] operationResults) {
		this.operationResults = operationResults;
	}

	@Override
	public boolean isSuccess() {
		return blockHash != null & executionState == TransactionState.SUCCESS;
	}

	@Override
	public OperationResult[] getOperationResults() {
		return operationResults;
	}

}
