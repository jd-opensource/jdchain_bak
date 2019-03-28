package com.jd.blockchain.ledger.core;

import java.util.concurrent.atomic.AtomicLong;

import com.jd.blockchain.utils.Bytes;

public class MerkleSequenceSNGenerator implements SNGenerator {
	
	private AtomicLong sn;
	
	public MerkleSequenceSNGenerator(MerkleTree merkleTree) {
		this.sn = new AtomicLong(merkleTree.getMaxSn() + 1);
	}

	@Override
	public long generate(Bytes key) {
		return sn.getAndIncrement();
	}

}
