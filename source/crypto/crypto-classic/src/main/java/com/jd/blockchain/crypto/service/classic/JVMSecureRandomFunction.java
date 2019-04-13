package com.jd.blockchain.crypto.service.classic;

import java.security.SecureRandom;

import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.RandomFunction;
import com.jd.blockchain.crypto.RandomGenerator;

public class JVMSecureRandomFunction implements RandomFunction {

	private static final CryptoAlgorithm JVM_SECURE_RANDOM = ClassicAlgorithm.JVM_SECURE_RANDOM;


	JVMSecureRandomFunction() {
	}

	@Override
	public CryptoAlgorithm getAlgorithm() {
		return JVM_SECURE_RANDOM;
	}

	@Override
	public RandomGenerator generate(byte[] seed) {
		return new SecureRandomGenerator(seed);
	}


	private static class SecureRandomGenerator implements RandomGenerator{
		
		private SecureRandom sr;
		
		public SecureRandomGenerator(byte[] seed) {
			if (seed == null || seed.length == 0) {
				// 随机；
				sr = new SecureRandom();
			} else {
				sr = new SecureRandom(seed);
			}
		}

		@Override
		public byte[] nextBytes(int size) {
			byte[] randomBytes = new byte[size];
			sr.nextBytes(randomBytes);
			return randomBytes;
		}

		@Override
		public void nextBytes(byte[] buffer) {
			sr.nextBytes(buffer);
		}

	}
}
