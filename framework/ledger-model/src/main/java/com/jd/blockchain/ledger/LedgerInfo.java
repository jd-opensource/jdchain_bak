package com.jd.blockchain.ledger;

import com.jd.blockchain.crypto.HashDigest;

public class LedgerInfo {

	/**
	 * 账本哈希，这是账本的唯一标识；
	 * 
	 * @return
	 */
	private HashDigest hash;

	private HashDigest latestBlockHash;

	private long latestBlockHeight;

	public HashDigest getHash() {
		return hash;
	}

	public void setHash(HashDigest hash) {
		this.hash = hash;
	}

	public HashDigest getLatestBlockHash() {
		return latestBlockHash;
	}

	public void setLatestBlockHash(HashDigest latestBlockHash) {
		this.latestBlockHash = latestBlockHash;
	}

	public long getLatestBlockHeight() {
		return latestBlockHeight;
	}

	public void setLatestBlockHeight(long latestBlockHeight) {
		this.latestBlockHeight = latestBlockHeight;
	}
	
}
