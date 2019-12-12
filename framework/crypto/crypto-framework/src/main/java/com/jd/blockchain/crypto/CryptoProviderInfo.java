package com.jd.blockchain.crypto;

class CryptoProviderInfo implements CryptoProvider {

	private String name;

	private CryptoAlgorithm[] algorithms;

	public CryptoProviderInfo(String name, CryptoAlgorithm[] algorithms) {
		this.name = name;
		this.algorithms = algorithms;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public CryptoAlgorithm[] getAlgorithms() {
		return algorithms.clone();
	}

}
