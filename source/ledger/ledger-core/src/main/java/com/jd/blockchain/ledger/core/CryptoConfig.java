package com.jd.blockchain.ledger.core;

import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.ledger.CryptoSetting;

public class CryptoConfig implements CryptoSetting {

	private CryptoAlgorithm hashAlgorithm;

	private boolean autoVerifyHash;
	
	public CryptoConfig() {
	}
	
	public CryptoConfig(CryptoSetting setting) {
		this.hashAlgorithm = setting.getHashAlgorithm();
		this.autoVerifyHash = setting.getAutoVerifyHash();
	}
	

	@Override
	public CryptoAlgorithm getHashAlgorithm() {
		return hashAlgorithm;
	}

	@Override
	public boolean getAutoVerifyHash() {
		return autoVerifyHash;
	}

	public void setHashAlgorithm(CryptoAlgorithm hashAlgorithm) {
		this.hashAlgorithm = hashAlgorithm;
	}

	public void setAutoVerifyHash(boolean autoVerifyHash) {
		this.autoVerifyHash = autoVerifyHash;
	}


}