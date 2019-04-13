package com.jd.blockchain.ledger.data;

import com.jd.blockchain.ledger.TransactionState;
import com.jd.blockchain.crypto.HashDigest;
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
	
	public TxResponseMessage() {
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

	/* (non-Javadoc)
	 * @see com.jd.blockchain.ledger.TransactionResponse#getBlockHash()
	 */
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
	
	@Override
	public boolean isSuccess() {
		return blockHash != null & executionState == TransactionState.SUCCESS;
	}

}
