package com.jd.blockchain.crypto.impl.jni.asymmetric;

import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.asymmetric.*;
import com.jd.blockchain.crypto.jniutils.asymmetric.JNIED25519Utils;

import static com.jd.blockchain.crypto.CryptoAlgorithm.JNIED25519;
import static com.jd.blockchain.crypto.CryptoBytes.ALGORYTHM_BYTES;
import static com.jd.blockchain.crypto.CryptoKeyType.PRIV_KEY;
import static com.jd.blockchain.crypto.CryptoKeyType.PUB_KEY;
import static com.jd.blockchain.crypto.base.BaseCryptoKey.KEY_TYPE_BYTES;

public class JNIED25519SignatureFunction implements SignatureFunction {

    private static final int PUBKEY_SIZE = 32;
    private static final int PRIVKEY_SIZE = 32;
    private static final int DIGEST_SIZE = 64;

    private static final int PUBKEY_LENGTH = ALGORYTHM_BYTES + KEY_TYPE_BYTES + PUBKEY_SIZE;
    private static final int PRIVKEY_LENGTH = ALGORYTHM_BYTES + KEY_TYPE_BYTES + PRIVKEY_SIZE;
    private static final int SIGNATUREDIGEST_LENGTH = ALGORYTHM_BYTES + DIGEST_SIZE;

    public JNIED25519SignatureFunction() {
    }

    @Override
    public SignatureDigest sign(PrivKey privKey, byte[] data) {

        if (data == null)
            throw new IllegalArgumentException("This data is null!");

        JNIED25519Utils ed25519 = new JNIED25519Utils();

        byte[] rawPrivKeyBytes = privKey.getRawKeyBytes();
        byte[] rawPubKeyBytes = ed25519.getPubKey(rawPrivKeyBytes);

        // 验证原始私钥长度为256比特，即32字节
        if (rawPrivKeyBytes.length != PRIVKEY_SIZE)
            throw new IllegalArgumentException("This key has wrong format!");

        // 验证密钥数据的算法标识对应JNIED25519签名算法
        if (privKey.getAlgorithm() != JNIED25519)
            throw new IllegalArgumentException("This key is not ED25519 private key!");

        // 调用JNIED25519签名算法计算签名结果
        return new SignatureDigest(JNIED25519, ed25519.sign(data, rawPrivKeyBytes, rawPubKeyBytes));
    }

    @Override
    public boolean verify(SignatureDigest digest, PubKey pubKey, byte[] data) {

        JNIED25519Utils ed25519 = new JNIED25519Utils();

        byte[] rawPubKeyBytes = pubKey.getRawKeyBytes();
        byte[] rawDigestBytes = digest.getRawDigest();

        // 验证原始公钥长度为256比特，即32字节
        if (rawPubKeyBytes.length != PUBKEY_SIZE)
            throw new IllegalArgumentException("This key has wrong format!");

        // 验证密钥数据的算法标识对应JNIED25519签名算法
        if (pubKey.getAlgorithm() != JNIED25519)
            throw new IllegalArgumentException("This key is not ED25519 public key!");

        // 验证密文数据的算法标识对应JNIED25519签名算法，并且原始摘要长度为64字节
        if (digest.getAlgorithm() != JNIED25519 || rawDigestBytes.length != DIGEST_SIZE)
            throw new IllegalArgumentException("This is not ED25519 signature digest!");

        // 调用JNIED25519验签算法验证签名结果
        return ed25519.verify(data, rawPubKeyBytes, rawDigestBytes);
    }

    @Override
    public boolean supportPrivKey(byte[] privKeyBytes) {
        // 验证输入字节数组长度=算法标识长度+密钥类型长度+密钥长度，密钥数据的算法标识对应JNIED25519签名算法，并且密钥类型是私钥
        return privKeyBytes.length == PRIVKEY_LENGTH
                && privKeyBytes[0] == JNIED25519.CODE && privKeyBytes[1] == PRIV_KEY.CODE;
    }

    @Override
    public PrivKey resolvePrivKey(byte[] privKeyBytes) {
        // 由框架调用 support 方法检查有效性，在此不做重复检查；
        return new PrivKey(privKeyBytes);
    }

    @Override
    public boolean supportPubKey(byte[] pubKeyBytes) {
        // 验证输入字节数组长度=算法标识长度+密钥类型长度+密钥长度，密钥数据的算法标识对应JNIED25519签名算法，并且密钥类型是公钥
        return pubKeyBytes.length == PUBKEY_LENGTH &&
                pubKeyBytes[0] == JNIED25519.CODE && pubKeyBytes[1] == PUB_KEY.CODE;

    }

    @Override
    public PubKey resolvePubKey(byte[] pubKeyBytes) {
        // 由框架调用 support 方法检查有效性，在此不做重复检查；
        return new PubKey(pubKeyBytes);
    }

    @Override
    public boolean supportDigest(byte[] digestBytes) {
        // 验证输入字节数组长度=算法标识长度+摘要长度，字节数组的算法标识对应JNIED25519算法
        return digestBytes.length == SIGNATUREDIGEST_LENGTH && digestBytes[0] == JNIED25519.CODE;
    }

    @Override
    public SignatureDigest resolveDigest(byte[] digestBytes) {
        // 由框架调用 support 方法检查有效性，在此不做重复检查；
        return new SignatureDigest(digestBytes);
    }

    @Override
    public CryptoAlgorithm getAlgorithm() {
        return JNIED25519;
    }

    @Override
    public CryptoKeyPair generateKeyPair() {

        JNIED25519Utils ed25519 = new JNIED25519Utils();
        byte[] rawPrivKeyBytes = new byte[PRIVKEY_SIZE];
        byte[] rawPubKeyBytes = new byte[PUBKEY_SIZE];

        // 调用JNIED25519算法的密钥生成算法生成公私钥对
        ed25519.generateKeyPair(rawPrivKeyBytes, rawPubKeyBytes);

        return new CryptoKeyPair(new PubKey(JNIED25519, rawPubKeyBytes), new PrivKey(JNIED25519, rawPrivKeyBytes));

    }
}
