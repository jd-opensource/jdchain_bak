package com.jd.blockchain.ledger.core;

import com.jd.blockchain.crypto.hash.HashDigest;
import com.jd.blockchain.utils.Bytes;

public interface MerkleProvable {

	HashDigest getRootHash();

	/**
	 * Get the merkle proof of the latest version of specified key; <br>
	 * 
	 * The proof doesn't represent the latest changes until do
	 * committing({@link #commit()}).
	 * 
	 * @param key
	 * @return Return the {@link MerkleProof} instance, or null if the key doesn't
	 *         exist.
	 */
	MerkleProof getProof(Bytes key);

}