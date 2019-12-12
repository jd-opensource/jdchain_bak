package com.jd.blockchain.crypto;

public interface RandomGenerator {
	
	byte[] nextBytes(int size);
	
	void nextBytes(byte[] buffer);
	
}
