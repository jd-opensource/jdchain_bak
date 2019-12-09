package com.jd.blockchain.ledger.core;

import java.util.ArrayList;
import java.util.List;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.HashProof;

/**
 * 
 * @author huanghaiquan
 *
 */
public class HashDigestList implements HashProof {

	private List<HashDigest> proofs = new ArrayList<HashDigest>();

	public HashDigestList() {
	}

	public HashDigestList(HashProof proof) {
		concat(proof);
	}

	public void concat(HashProof proof) {
		int levels = proof.getLevels();
		for (int i = levels; i > -1; i--) {
			proofs.add(proof.getHash(i));
		}
	}

	@Override
	public int getLevels() {
		return proofs.size();
	}

	@Override
	public HashDigest getHash(int level) {
		return proofs.get(level);
	}

}
