package com.jd.blockchain.ledger.core;

import com.jd.blockchain.crypto.asymmetric.SignatureDigest;

public class LedgerInitPermissionData implements LedgerInitPermission {

	private int participantId;

	private SignatureDigest transactionSignature;

	/**
	 * a private contructor for deserialize;
	 */
	private LedgerInitPermissionData() {
	}

	public LedgerInitPermissionData(int participantId, SignatureDigest initTxSignature) {
		this.participantId = participantId;
		this.transactionSignature = initTxSignature;
	}

	@Override
	public int getParticipantId() {
		return participantId;
	}

	@Override
	public SignatureDigest getTransactionSignature() {
		return transactionSignature;
	}

}
