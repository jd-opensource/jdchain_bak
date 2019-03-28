package com.jd.blockchain.crypto.service.classic;

import com.jd.blockchain.crypto.Ciphertext;
import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.PrivKey;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.crypto.asymmetric.*;

/**
 * @author zhanglin33
 * @title: RSACryptoFunction
 * @description: Interfaces for RSA crypto functions, including key generation, encryption, signature, and so on
 * @date 2019-03-25, 17:28
 */
public class RSACryptoFunction implements AsymmetricEncryptionFunction, SignatureFunction {
    @Override
    public Ciphertext encrypt(PubKey pubKey, byte[] data) {
        return null;
    }

    @Override
    public byte[] decrypt(PrivKey privKey, Ciphertext ciphertext) {
        return new byte[0];
    }

    @Override
    public SignatureDigest sign(PrivKey privKey, byte[] data) {
        return null;
    }

    @Override
    public boolean verify(SignatureDigest digest, PubKey pubKey, byte[] data) {
        return false;
    }

    @Override
    public byte[] retrievePubKeyBytes(byte[] privKeyBytes) {
        return new byte[0];
    }

    @Override
    public boolean supportPrivKey(byte[] privKeyBytes) {
        return false;
    }

    @Override
    public PrivKey resolvePrivKey(byte[] privKeyBytes) {
        return null;
    }

    @Override
    public boolean supportPubKey(byte[] pubKeyBytes) {
        return false;
    }

    @Override
    public PubKey resolvePubKey(byte[] pubKeyBytes) {
        return null;
    }

    @Override
    public boolean supportDigest(byte[] digestBytes) {
        return false;
    }

    @Override
    public SignatureDigest resolveDigest(byte[] digestBytes) {
        return null;
    }

    @Override
    public boolean supportCiphertext(byte[] ciphertextBytes) {
        return false;
    }

    @Override
    public AsymmetricCiphertext resolveCiphertext(byte[] ciphertextBytes) {
        return null;
    }

    @Override
    public CryptoAlgorithm getAlgorithm() {
        return null;
    }

    @Override
    public CryptoKeyPair generateKeyPair() {
        return null;
    }
}
