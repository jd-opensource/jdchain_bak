package com.jd.blockchain.ledger.core;

import com.jd.blockchain.ledger.CryptoSetting;
import com.jd.blockchain.ledger.LedgerSettings;
import com.jd.blockchain.utils.Bytes;

public class LedgerConfiguration implements LedgerSettings {

	private String consensusProvider;

	private Bytes consensusSetting;

	private CryptoConfig cryptoSetting;

	public LedgerConfiguration() {
		this.cryptoSetting = new CryptoConfig();
	}

	public LedgerConfiguration(LedgerSettings origSetting) {
		if (origSetting != null) {
			this.consensusProvider = origSetting.getConsensusProvider();
			this.consensusSetting = origSetting.getConsensusSetting();
			this.cryptoSetting = new CryptoConfig(origSetting.getCryptoSetting());
		} else {
			this.cryptoSetting = new CryptoConfig();
		}
	}

	public LedgerConfiguration(String consensusProvider, Bytes consensusSetting, CryptoSetting cryptoSetting) {
		this.consensusProvider = consensusProvider;
		this.consensusSetting = consensusSetting;
		this.cryptoSetting = new CryptoConfig(cryptoSetting);
	}

	@Override
	public Bytes getConsensusSetting() {
		return consensusSetting;
	}

	public void setConsensusSetting(Bytes consensusSetting) {
		this.consensusSetting = consensusSetting;
	}

	@Override
	public CryptoConfig getCryptoSetting() {
		return cryptoSetting;
	}

	@Override
	public String getConsensusProvider() {
		return consensusProvider;
	}

	public void setConsensusProvider(String consensusProvider) {
		this.consensusProvider = consensusProvider;
	}

}
