package com.jd.blockchain.utils.security;

import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SignatureException;

import net.i2p.crypto.eddsa.EdDSAEngine;
import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable;
import net.i2p.crypto.eddsa.spec.EdDSAParameterSpec;
import net.i2p.crypto.eddsa.spec.EdDSAPrivateKeySpec;
import net.i2p.crypto.eddsa.spec.EdDSAPublicKeySpec;

public class Ed25519Utils {

	/**
	 * 用指定的私钥执行签名；
	 * 
	 * @param data
	 * @param privateKey
	 * @return 返回签名摘要；
	 */
	public static byte[] sign_512(byte[] data, byte[] privateKey) {
		return sign_512(ByteBuffer.wrap(data), privateKey);
	}

	/**
	 * 用指定的私钥执行签名；
	 * 
	 * @param data
	 * @param privateKey
	 * @return 返回签名摘要；
	 */
	public static byte[] sign_512(ByteBuffer data, byte[] privateKey) {
		try {
			java.security.Signature sgr = new EdDSAEngine(MessageDigest.getInstance("SHA-512"));
			EdDSAParameterSpec spec = EdDSANamedCurveTable.getByName(EdDSANamedCurveTable.CURVE_ED25519_SHA512);
			EdDSAPrivateKeySpec privateKeySpec = new EdDSAPrivateKeySpec(privateKey, spec);
			PrivateKey privKey = new EdDSAPrivateKey(privateKeySpec);
			sgr.initSign(privKey);
			sgr.update(data);
			return sgr.sign();
		} catch (InvalidKeyException e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e.getMessage(), e);
		} catch (SignatureException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	/**
	 * 验证签名；
	 * 
	 * @param plainData
	 *            明文；
	 * @param pubKeyBytes
	 *            公钥；
	 * @param signatureBytes
	 *            摘要；
	 * @return
	 */
	public static boolean verify(byte[] plainData, byte[] pubKeyBytes, byte[] signatureBytes) {
		try {
			java.security.Signature sgr = new EdDSAEngine(MessageDigest.getInstance("SHA-512"));
			EdDSAParameterSpec spec = EdDSANamedCurveTable.getByName("ed25519-sha-512");
			EdDSAPublicKeySpec pubKeySpec = new EdDSAPublicKeySpec(pubKeyBytes, spec);
			EdDSAPublicKey pubKey = new EdDSAPublicKey(pubKeySpec);
			sgr.initVerify(pubKey);
			sgr.update(plainData);
			return sgr.verify(signatureBytes);
		} catch (InvalidKeyException e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e.getMessage(), e);
		} catch (SignatureException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

}
