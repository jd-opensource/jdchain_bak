package com.jd.blockchain.crypto.impl;

import com.jd.blockchain.crypto.CryptoFactory;
import com.jd.blockchain.crypto.asymmetric.AsymmetricCryptography;
import com.jd.blockchain.crypto.hash.HashCryptography;
import com.jd.blockchain.crypto.symmetric.SymmetricCryptography;

public class CryptoFactoryImpl implements CryptoFactory {

	//Field; 
	private static HashCryptography hashCryptography = new HashCryptographyImpl();
	private static AsymmetricCryptography asymmetricCryptography = new AsymmtricCryptographyImpl();
	private static SymmetricCryptography symmetricCryptography = new SymmetricCryptographyImpl();

	@Override
	public HashCryptography hashCryptography() {
		return hashCryptography;
	}

	@Override
	public AsymmetricCryptography asymmetricCryptography() {
		return asymmetricCryptography;
	}

	@Override
	public SymmetricCryptography symmetricCryptography() {
		return symmetricCryptography;
	}

}
