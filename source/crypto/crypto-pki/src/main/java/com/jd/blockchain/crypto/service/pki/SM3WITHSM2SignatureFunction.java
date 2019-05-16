package com.jd.blockchain.crypto.service.pki;

import com.jd.blockchain.crypto.*;

import static com.jd.blockchain.crypto.service.pki.PKIAlgorithm.SM3WITHSM2;

/**
 * @author zhanglin33
 * @title: SM3WITHSM2SignatureFunction
 * @description: TODO
 * @date 2019-05-15, 16:39
 */
public class SM3WITHSM2SignatureFunction implements SignatureFunction {
    @Override
    public SignatureDigest sign(PrivKey privKey, byte[] data) {
        return null;
    }

    @Override
    public boolean verify(SignatureDigest digest, PubKey pubKey, byte[] data) {
        return false;
    }

    @Override
    public PubKey retrievePubKey(PrivKey privKey) {
        return null;
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
    public AsymmetricKeypair generateKeypair() {
        return null;
    }

    @Override
    public CryptoAlgorithm getAlgorithm() {
        return SM3WITHSM2;
    }
}
