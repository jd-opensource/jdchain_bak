package com.jd.blockchain.transaction;

import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.ledger.CryptoSetting;
import com.jd.blockchain.ledger.LedgerInitSetting;
import com.jd.blockchain.ledger.ParticipantNode;
import com.jd.blockchain.ledger.UserAuthInitSettings;
import com.jd.blockchain.utils.Bytes;

public class LedgerInitData implements LedgerInitSetting {

	static {
		DataContractRegistry.register(LedgerInitSetting.class);
	}

	private byte[] ledgerSeed;

	private ParticipantNode[] consensusParticipants;

	private CryptoSetting cryptoSetting;

	private String consensusProvider;

	private Bytes consensusSettings;

	private long createdTime;

	@Override
	public byte[] getLedgerSeed() {
		return ledgerSeed;
	}

	@Override
	public ParticipantNode[] getConsensusParticipants() {
		return consensusParticipants;
	}

	@Override
	public CryptoSetting getCryptoSetting() {
		return cryptoSetting;
	}

	@Override
	public Bytes getConsensusSettings() {
		return consensusSettings;
	}

	public void setLedgerSeed(byte[] ledgerSeed) {
		this.ledgerSeed = ledgerSeed;
	}

	public void setConsensusParticipants(ParticipantNode[] consensusParticipants) {
		this.consensusParticipants = consensusParticipants;
	}

	public void setCryptoSetting(CryptoSetting cryptoSetting) {
		this.cryptoSetting = cryptoSetting;
	}

	public void setConsensusSettings(Bytes consensusSettings) {
		this.consensusSettings = consensusSettings;
	}

	public void setConsensusSettings(byte[] consensusSettings) {
		this.consensusSettings = new Bytes(consensusSettings);
	}

	@Override
	public String getConsensusProvider() {
		return consensusProvider;
	}

	public void setConsensusProvider(String consensusProvider) {
		this.consensusProvider = consensusProvider;
	}

	@Override
	public long getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}
}