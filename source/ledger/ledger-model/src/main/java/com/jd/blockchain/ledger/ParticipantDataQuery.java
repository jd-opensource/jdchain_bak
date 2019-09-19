package com.jd.blockchain.ledger;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.utils.Bytes;

public interface ParticipantDataQuery {

	HashDigest getRootHash();

	MerkleProof getProof(Bytes key);

	long getParticipantCount();

	boolean contains(Bytes address);

	/**
	 * 返回指定地址的参与方凭证；
	 * 
	 * <br>
	 * 如果不存在，则返回 null；
	 * 
	 * @param address
	 * @return
	 */
	ParticipantNode getParticipant(Bytes address);

	ParticipantNode[] getParticipants();

}