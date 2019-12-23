package com.jd.blockchain.crypto;

public interface RandomFunction extends CryptoFunction {
	
	RandomGenerator generate(byte[] seed);
	
}
