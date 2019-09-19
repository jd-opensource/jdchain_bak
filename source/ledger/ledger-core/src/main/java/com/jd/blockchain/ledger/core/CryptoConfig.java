package com.jd.blockchain.ledger.core;

import java.util.HashMap;

import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.CryptoProvider;
import com.jd.blockchain.ledger.CryptoSetting;
import com.jd.blockchain.ledger.LedgerException;

public class CryptoConfig implements CryptoSetting {

	private CryptoProvider[] cryptoProviders;

	private short hashAlgorithm;

	private boolean autoVerifyHash;

	HashMap<String, CryptoProvider> providers;
	HashMap<String, CryptoAlgorithm> nameAlgorithms;
	HashMap<Short, CryptoAlgorithm> codeAlgorithms;

	public CryptoConfig() {
	}

	public CryptoConfig(CryptoSetting setting) {
		setSupportedProviders(setting.getSupportedProviders());
		setHashAlgorithm(setting.getHashAlgorithm());
		this.autoVerifyHash = setting.getAutoVerifyHash();
	}

	@Override
	public CryptoProvider[] getSupportedProviders() {
		return cryptoProviders == null ? null : cryptoProviders.clone();
	}

	@Override
	public short getHashAlgorithm() {
		return hashAlgorithm;
	}

	@Override
	public boolean getAutoVerifyHash() {
		return autoVerifyHash;
	}

	public void setSupportedProviders(CryptoProvider[] supportedProviders) {
		HashMap<String, CryptoProvider> providers = new HashMap<String, CryptoProvider>();
		HashMap<String, CryptoAlgorithm> nameAlgorithms = new HashMap<String, CryptoAlgorithm>();
		HashMap<Short, CryptoAlgorithm> codeAlgorithms = new HashMap<Short, CryptoAlgorithm>();
		if (supportedProviders != null) {
			// 检查是否存在重复的提供者以及算法；
			for (CryptoProvider cryptoProvider : supportedProviders) {
				if (providers.containsKey(cryptoProvider.getName())) {
					throw new LedgerException("Duplicate crypto providers [" + cryptoProvider.getName() + "]!");
				}
				CryptoAlgorithm[] algorithms = cryptoProvider.getAlgorithms();
				for (CryptoAlgorithm alg : algorithms) {
					if (nameAlgorithms.containsKey(alg.name())) {
						throw new LedgerException("Duplicate crypto algorithms [" + alg.toString() + "] from provider "
								+ cryptoProvider.getName() + "!");
					}
					if (codeAlgorithms.containsKey(alg.code())) {
						throw new LedgerException("Duplicate crypto algorithms [" + alg.toString() + "] from provider"
								+ cryptoProvider.getName() + "!");
					}
					nameAlgorithms.put(alg.name(), alg);
					codeAlgorithms.put(alg.code(), alg);
				}
				providers.put(cryptoProvider.getName(), cryptoProvider);
			}
		}
		this.providers = providers;
		this.nameAlgorithms = nameAlgorithms;
		this.codeAlgorithms = codeAlgorithms;

		this.cryptoProviders = supportedProviders;
	}

	public void setHashAlgorithm(CryptoAlgorithm hashAlgorithm) {
		setHashAlgorithm(hashAlgorithm.code());
	}

	public void setHashAlgorithm(short hashAlgorithm) {
		if (codeAlgorithms == null || !codeAlgorithms.containsKey(hashAlgorithm)) {
			throw new LedgerException("Current CryptoConfig has no crypto provider!");
		}
		this.hashAlgorithm = hashAlgorithm;
	}

	public void setAutoVerifyHash(boolean autoVerifyHash) {
		this.autoVerifyHash = autoVerifyHash;
	}

}