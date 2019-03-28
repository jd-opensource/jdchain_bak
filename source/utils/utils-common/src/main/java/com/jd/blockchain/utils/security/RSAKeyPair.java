package com.jd.blockchain.utils.security;

import java.nio.ByteBuffer;

import com.jd.blockchain.utils.codec.Base58Utils;

public class RSAKeyPair {
	
	private byte[] publicKey;
	
	private String publicKey_Base58;
	
	private byte[] privateKey;
	
	private String privateKey_Base58;
	
	public RSAKeyPair(byte[] publicKey, byte[] privateKey) {
		this.publicKey = publicKey;
		this.privateKey = privateKey;
		
		this.publicKey_Base58 = Base58Utils.encode(publicKey);
		this.privateKey_Base58 = Base58Utils.encode(privateKey);
	}

	public ByteBuffer getPublicKey() {
		return ByteBuffer.wrap(publicKey).asReadOnlyBuffer();
	}

	public ByteBuffer getPrivateKey() {
		return ByteBuffer.wrap(privateKey).asReadOnlyBuffer();
	}

	public String getPublicKey_Base58() {
		return publicKey_Base58;
	}

	public String getPrivateKey_Base58() {
		return privateKey_Base58;
	}

}
