package com.jd.blockchain.utils.security;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.jd.blockchain.utils.codec.Base58Utils;

/**
 * RSA 加密算法的工具类；
 * 
 * @author haiq
 *
 */
public class RSAUtils {
	public static final String ALG_RSA = "RSA";
	public static final String KEYPAIR_PUBKEY = "pubKey";
	public static final String KEYPAIR_PRIKEY = "priKey";

	public static RSAKeyPair generateKey512() {
		try {
			KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(ALG_RSA);
			keyPairGen.initialize(512, new SecureRandom());
			KeyPair keyPair = keyPairGen.generateKeyPair();
			RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
			RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

			return new RSAKeyPair(publicKey.getEncoded(), privateKey.getEncoded());
		} catch (NoSuchAlgorithmException e) {
			throw new KeyGenerationException(e.getMessage(), e);
		}
	}
	
	public static RSAKeyPair generateKey2048() {
		try {
			KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(ALG_RSA);
			keyPairGen.initialize(2048, new SecureRandom());
			KeyPair keyPair = keyPairGen.generateKeyPair();
			RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
			RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
			
			return new RSAKeyPair(publicKey.getEncoded(), privateKey.getEncoded());
		} catch (NoSuchAlgorithmException e) {
			throw new KeyGenerationException(e.getMessage(), e);
		}
	}

	public static byte[] encryptByPublicKey_Base58(byte[] data, String publicKey_Base58) {
		byte[] publicKey = Base58Utils.decode(publicKey_Base58);
		return encryptByPublicKey(data, publicKey);
	}

	public static byte[] encryptByPublicKey(byte[] data, byte[] publicKey) {
		if (publicKey == null) {
			throw new IllegalArgumentException("Public key is empty!");
		}
		try {
			RSAPublicKey pubKey = loadPublicKey(publicKey);
			Cipher cipher = Cipher.getInstance(ALG_RSA);
			cipher.init(Cipher.ENCRYPT_MODE, pubKey);
			return cipher.doFinal(data);
		} catch (InvalidKeyException e) {
			throw new EncryptionException(e.getMessage(), e);
		} catch (NoSuchAlgorithmException e) {
			throw new EncryptionException(e.getMessage(), e);
		} catch (InvalidKeySpecException e) {
			throw new EncryptionException(e.getMessage(), e);
		} catch (NoSuchPaddingException e) {
			throw new EncryptionException(e.getMessage(), e);
		} catch (IllegalBlockSizeException e) {
			throw new EncryptionException(e.getMessage(), e);
		} catch (BadPaddingException e) {
			throw new EncryptionException(e.getMessage(), e);
		}
	}

	public static byte[] decryptByPrivateKey_Base58(byte[] data, String privateKey_Base58) {
		byte[] privateKey = Base58Utils.decode(privateKey_Base58);
		return decryptByPrivateKey(data, privateKey);
	}

	public static byte[] decryptByPrivateKey(byte[] cipherData, byte[] privateKey) {
		if (privateKey == null) {
			throw new IllegalArgumentException("Private key is empty!");
		}
		try {
			RSAPrivateKey privKey = loadPrivateKey(privateKey);
			Cipher cipher = Cipher.getInstance(ALG_RSA);
			cipher.init(Cipher.DECRYPT_MODE, privKey);
			return cipher.doFinal(cipherData);
		} catch (InvalidKeyException e) {
			throw new EncryptionException(e.getMessage(), e);
		} catch (NoSuchAlgorithmException e) {
			throw new EncryptionException(e.getMessage(), e);
		} catch (InvalidKeySpecException e) {
			throw new EncryptionException(e.getMessage(), e);
		} catch (NoSuchPaddingException e) {
			throw new EncryptionException(e.getMessage(), e);
		} catch (IllegalBlockSizeException e) {
			throw new EncryptionException(e.getMessage(), e);
		} catch (BadPaddingException e) {
			throw new EncryptionException(e.getMessage(), e);
		}
	}

	private static RSAPublicKey loadPublicKey(byte[] pubKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
		KeyFactory keyFactory = KeyFactory.getInstance(ALG_RSA);
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(pubKey);
		return (RSAPublicKey) keyFactory.generatePublic(keySpec);
	}

	private static RSAPrivateKey loadPrivateKey(byte[] priKey)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(priKey);
		KeyFactory keyFactory = KeyFactory.getInstance(ALG_RSA);
		return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
	}
}
