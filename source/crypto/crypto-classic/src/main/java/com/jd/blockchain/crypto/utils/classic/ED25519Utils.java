package com.jd.blockchain.crypto.utils.classic;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.generators.Ed25519KeyPairGenerator;
import org.bouncycastle.crypto.params.Ed25519KeyGenerationParameters;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.signers.Ed25519Signer;

import java.security.SecureRandom;

/**
 * @author zhanglin33
 * @title: ED25519Utils
 * @description: ED25519 signature algorithm
 * @date 2019-04-04, 20:01
 */
public class ED25519Utils {

    //-----------------Key Generation Algorithm-----------------

    /**
     * key pair generation
     *
     * @return key pair
     */
    public static AsymmetricCipherKeyPair generateKeyPair(){
        SecureRandom random = new SecureRandom();
        return generateKeyPair(random);
    }

    public static AsymmetricCipherKeyPair generateKeyPair(SecureRandom random){
        Ed25519KeyPairGenerator keyPairGenerator = new Ed25519KeyPairGenerator();
        keyPairGenerator.init(new Ed25519KeyGenerationParameters(random));
        return keyPairGenerator.generateKeyPair();
    }

    /**
     * public key retrieval
     *
     * @param privateKey private key
     * @return publicKey
     */
    public static byte[] retrievePublicKey(byte[] privateKey){
        Ed25519PrivateKeyParameters privKeyParams = new Ed25519PrivateKeyParameters(privateKey,0);
        return privKeyParams.generatePublicKey().getEncoded();
    }


    //-----------------Digital Signature Algorithm-----------------

    /**
     * signature generation
     *
     * @param data data to be signed
     * @param privateKey private key
     * @return signature
     */
    public static byte[] sign(byte[] data, byte[] privateKey){
        Ed25519PrivateKeyParameters privKeyParams = new Ed25519PrivateKeyParameters(privateKey,0);
        return sign(data,privKeyParams);
    }

    public static byte[] sign(byte[] data, CipherParameters params){
        Ed25519Signer signer = new Ed25519Signer();
        signer.init(true, params);
        signer.update(data,0,data.length);
        return signer.generateSignature();
    }

    /**
     * verification
     *
     * @param data data to be signed
     * @param publicKey public key
     * @param signature signature to be verified
     * @return true or false
     */
    public static boolean verify(byte[] data, byte[] publicKey, byte[] signature){
        Ed25519PublicKeyParameters pubKeyParams = new Ed25519PublicKeyParameters(publicKey,0);
        return verify(data,pubKeyParams,signature);
    }

    public static boolean verify(byte[] data, CipherParameters params, byte[] signature){
        Ed25519Signer verifier = new Ed25519Signer();
        verifier.init(false, params);
        verifier.update(data,0,data.length);
        return verifier.verifySignature(signature);
    }
}
