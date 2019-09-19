package com.jd.blockchain.ledger.core;

import com.jd.blockchain.crypto.SignatureDigest;

public class LedgerInitProposalData implements LedgerInitProposal {

	private int participantId;

	private SignatureDigest transactionSignature;

	/**
	 * a private contructor for deserialize;
	 */
	@SuppressWarnings("unused")
	private LedgerInitProposalData() {
	}

	public LedgerInitProposalData(int participantId, SignatureDigest initTxSignature) {
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
