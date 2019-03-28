package com.jd.blockchain.tools.initializer.web;

import com.jd.blockchain.crypto.asymmetric.SignatureDigest;
import com.jd.blockchain.crypto.hash.HashDigest;
import com.jd.blockchain.ledger.core.LedgerInitDecision;

public class LedgerInitDecisionData implements LedgerInitDecision {

		private int participantId;

		private HashDigest ledgerHash;

		private SignatureDigest signature;

		@Override
		public int getParticipantId() {
			return participantId;
		}

		@Override
		public HashDigest getLedgerHash() {
			return ledgerHash;
		}

		@Override
		public SignatureDigest getSignature() {
			return signature;
		}

		public void setParticipantId(int participantId) {
			this.participantId = participantId;
		}

		public void setLedgerHash(HashDigest ledgerHash) {
			this.ledgerHash = ledgerHash;
		}

		public void setSignature(SignatureDigest signature) {
			this.signature = signature;
		}

	}