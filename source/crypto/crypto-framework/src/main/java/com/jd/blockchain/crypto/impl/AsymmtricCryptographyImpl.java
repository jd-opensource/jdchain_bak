package com.jd.blockchain.crypto.impl;

import com.jd.blockchain.crypto.Ciphertext;
import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.asymmetric.*;
import com.jd.blockchain.crypto.impl.def.asymmetric.ED25519SignatureFunction;
import com.jd.blockchain.crypto.impl.jni.asymmetric.JNIED25519SignatureFunction;
import com.jd.blockchain.crypto.impl.sm.asymmetric.SM2CryptoFunction;

public class AsymmtricCryptographyImpl implements AsymmetricCryptography {

    private static final SignatureFunction ED25519_SIGF = new ED25519SignatureFunction();

    private static final SignatureFunction SM2_SIGF = new SM2CryptoFunction();

    private static final SignatureFunction JNIED25519_SIGF = new JNIED25519SignatureFunction();

    private static final AsymmetricEncryptionFunction SM2_ENCF = new SM2CryptoFunction();

    /**
     * 封装了非对称密码算法对应的密钥生成算法
     */
    @Override
    public CryptoKeyPair generateKeyPair(CryptoAlgorithm algorithm) {

        //判断算法是签名算法还是非对称加密算法，并根据算法生成密钥对，否则抛出异常
        if (algorithm.isSignable() && algorithm.isAsymmetric()){
            return getSignatureFunction(algorithm).generateKeyPair();
        }
        else if (algorithm.isEncryptable() && algorithm.isAsymmetric()){
            return getAsymmetricEncryptionFunction(algorithm).generateKeyPair();
        }
        else throw new IllegalArgumentException("The specified algorithm is not signature or asymmetric encryption algorithm!");
    }

    @Override
    public SignatureFunction getSignatureFunction(CryptoAlgorithm algorithm) {
        //遍历签名算法，如果满足，则返回实例
        switch (algorithm) {
            case ED25519:
                return ED25519_SIGF;
            case SM2:
                return SM2_SIGF;
            case JNIED25519:
                return JNIED25519_SIGF;
            default:
                break;
        }
        throw new IllegalArgumentException("The specified algorithm is not signature algorithm!");
    }

    @Override
    public boolean verify(byte[] digestBytes, byte[] pubKeyBytes, byte[] data) {

        //得到SignatureDigest类型的签名摘要，并得到算法标识
        SignatureDigest signatureDigest = resolveSignatureDigest(digestBytes);
        CryptoAlgorithm algorithm = signatureDigest.getAlgorithm();
        PubKey pubKey = resolvePubKey(pubKeyBytes);

        //验证两个输入中算法标识一致，否则抛出异常
        if (algorithm != signatureDigest.getAlgorithm())
            throw new IllegalArgumentException("Digest's algorithm and key's are not matching!");

        //根据算法标识，调用对应算法实例来验证签名摘要
        return getSignatureFunction(algorithm).verify(signatureDigest,pubKey,data);
    }

    @Override
    public AsymmetricEncryptionFunction getAsymmetricEncryptionFunction(CryptoAlgorithm algorithm) {
        //遍历非对称加密算法，如果满足，则返回实例
        switch (algorithm) {
            case SM2:
                return SM2_ENCF;
            default:
                break;
        }
        throw new IllegalArgumentException("The specified algorithm is not asymmetric encryption algorithm!");
    }

    @Override
    public byte[] decrypt(byte[] privKeyBytes, byte[] ciphertextBytes) {

        //分别得到PrivKey和Ciphertext类型的密钥和密文,以及privKey对应的算法
        PrivKey privKey = resolvePrivKey(privKeyBytes);
        Ciphertext ciphertext = resolveCiphertext(ciphertextBytes);
        CryptoAlgorithm algorithm = privKey.getAlgorithm();

        //验证两个输入中算法标识一致，否则抛出异常
        if (algorithm != ciphertext.getAlgorithm())
            throw new IllegalArgumentException("Ciphertext's algorithm and key's are not matching!");

        //根据算法标识，调用对应算法实例来计算返回明文
        return getAsymmetricEncryptionFunction(algorithm).decrypt(privKey,ciphertext);
    }

    @Override
    public Ciphertext resolveCiphertext(byte[] ciphertextBytes) {
        Ciphertext ciphertext = tryResolveCiphertext(ciphertextBytes);
        if (ciphertext == null)
            throw new IllegalArgumentException("This ciphertextBytes cannot be resolved!");
        else return ciphertext;
    }

    @Override
    public Ciphertext tryResolveCiphertext(byte[] ciphertextBytes) {
        //遍历非对称加密算法，如果满足，则返回解析结果
        if (SM2_ENCF.supportCiphertext(ciphertextBytes)){
            return SM2_ENCF.resolveCiphertext(ciphertextBytes);
        }
        //否则返回null
        return null;
    }

    @Override
    public SignatureDigest resolveSignatureDigest(byte[] digestBytes) {
        SignatureDigest signatureDigest = tryResolveSignatureDigest(digestBytes);
        if (signatureDigest == null)
            throw new IllegalArgumentException("This digestBytes cannot be resolved!");
        else return signatureDigest;
    }

    @Override
    public SignatureDigest tryResolveSignatureDigest(byte[] digestBytes) {
        //遍历签名算法，如果满足，则返回解析结果
        if (ED25519_SIGF.supportDigest(digestBytes)){
            return ED25519_SIGF.resolveDigest(digestBytes);
        }
        if (SM2_SIGF.supportDigest(digestBytes)){
            return SM2_SIGF.resolveDigest(digestBytes);
        }
        if (JNIED25519_SIGF.supportDigest(digestBytes)){
            return JNIED25519_SIGF.resolveDigest(digestBytes);
        }
        //否则返回null
        return null;
    }

    @Override
    public PubKey resolvePubKey(byte[] pubKeyBytes) {
        PubKey pubKey = tryResolvePubKey(pubKeyBytes);
        if (pubKey == null)
            throw new IllegalArgumentException("This pubKeyBytes cannot be resolved!");
        else return pubKey;

    }

    @Override
    public PubKey tryResolvePubKey(byte[] pubKeyBytes) {
        //遍历签名算法，如果满足，则返回解析结果
        if (ED25519_SIGF.supportPubKey(pubKeyBytes)){
            return ED25519_SIGF.resolvePubKey(pubKeyBytes);
        }
        if (SM2_SIGF.supportPubKey(pubKeyBytes)){
            return SM2_SIGF.resolvePubKey(pubKeyBytes);
        }
        if (JNIED25519_SIGF.supportPubKey(pubKeyBytes)){
            return JNIED25519_SIGF.resolvePubKey(pubKeyBytes);
        }
        //遍历非对称加密算法，如果满足，则返回解析结果
        if (SM2_ENCF.supportPubKey(pubKeyBytes)){
            return SM2_ENCF.resolvePubKey(pubKeyBytes);
        }
        //否则返回null
        return null;
    }

    @Override
    public PrivKey resolvePrivKey(byte[] privKeyBytes) {
        PrivKey privKey = tryResolvePrivKey(privKeyBytes);
        if (privKey == null)
            throw new IllegalArgumentException("This privKeyBytes cannot be resolved!");
        else return privKey;
    }

    @Override
    public PrivKey tryResolvePrivKey(byte[] privKeyBytes) {
        //遍历签名算法，如果满足，则返回解析结果
        if (ED25519_SIGF.supportPrivKey(privKeyBytes)){
            return ED25519_SIGF.resolvePrivKey(privKeyBytes);
        }
        if (SM2_SIGF.supportPrivKey(privKeyBytes)){
            return SM2_SIGF.resolvePrivKey(privKeyBytes);
        }
        if (JNIED25519_SIGF.supportPrivKey(privKeyBytes)){
            return JNIED25519_SIGF.resolvePrivKey(privKeyBytes);
        }
        //遍历非对称加密算法，如果满足，则返回解析结果
        if (SM2_ENCF.supportPrivKey(privKeyBytes)){
            return SM2_ENCF.resolvePrivKey(privKeyBytes);
        }
        //否则返回null
        return null;
    }
}
