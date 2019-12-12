package com.jd.blockchain.utils.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class RipeMD160Utils {

	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	public static byte[] hash(byte[] bytes) {
		try {
			MessageDigest md160 = MessageDigest.getInstance("RIPEMD160");
			return md160.digest(bytes);
		} catch (NoSuchAlgorithmException e) {
			throw new EncryptionException(e.getMessage(), e);
		}

	}

}
