package com.jd.blockchain.crypto.impl.sm.asymmetric;

import com.jd.blockchain.crypto.Ciphertext;
import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.asymmetric.*;
import com.jd.blockchain.crypto.smutils.asymmetric.SM2Utils;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;

import static com.jd.blockchain.crypto.CryptoAlgorithm.SM2;
import static com.jd.blockchain.crypto.CryptoBytes.ALGORYTHM_BYTES;
import static com.jd.blockchain.crypto.CryptoKeyType.PRIV_KEY;
import static com.jd.blockchain.crypto.CryptoKeyType.PUB_KEY;
import static com.jd.blockchain.crypto.base.BaseCryptoKey.KEY_TYPE_BYTES;

public class SM2CryptoFunction implements AsymmetricEncryptionFunction, SignatureFunction {

    private static final int ECPOINT_SIZE = 65;
    private static final int PRIVKEY_SIZE = 32;
    private static final int SIGNATUREDIGEST_SIZE = 64;
    private static final int HASHDIGEST_SIZE = 32;

    private static final int PUBKEY_LENGTH = ALGORYTHM_BYTES + KEY_TYPE_BYTES + ECPOINT_SIZE;
    private static final int PRIVKEY_LENGTH = ALGORYTHM_BYTES + KEY_TYPE_BYTES + PRIVKEY_SIZE;
    private static final int SIGNATUREDIGEST_LENGTH = ALGORYTHM_BYTES + SIGNATUREDIGEST_SIZE;

    @Override
    public Ciphertext encrypt(PubKey pubKey, byte[] data) {

        byte[] rawPubKeyBytes = pubKey.getRawKeyBytes();

        // 验证原始公钥长度为65字节
        if (rawPubKeyBytes.length != ECPOINT_SIZE)
            throw new IllegalArgumentException("This key has wrong format!");

        // 验证密钥数据的算法标识对应SM2算法
        if (pubKey.getAlgorithm() != SM2)
            throw new IllegalArgumentException("The is not sm2 public key!");

        // 调用SM2加密算法计算密文
        return new AsymmetricCiphertext(SM2, SM2Utils.encrypt(data, rawPubKeyBytes));
    }

    @Override
    public byte[] decrypt(PrivKey privKey, Ciphertext ciphertext) {

        byte[] rawPrivKeyBytes = privKey.getRawKeyBytes();
        byte[] rawCiphertextBytes = ciphertext.getRawCiphertext();

        // 验证原始私钥长度为32字节
        if (rawPrivKeyBytes.length != PRIVKEY_SIZE)
            throw new IllegalArgumentException("This key has wrong format!");

        // 验证密钥数据的算法标识对应SM2算法
        if (privKey.getAlgorithm() != SM2)
            throw new IllegalArgumentException("This key is not SM2 private key!");

        // 验证密文数据的算法标识对应SM2签名算法，并且原始摘要长度为64字节
        if (ciphertext.getAlgorithm() != SM2 || rawCiphertextBytes.length < ECPOINT_SIZE + HASHDIGEST_SIZE)
            throw new IllegalArgumentException("This is not SM2 ciphertext!");

        // 调用SM2解密算法得到明文结果
        return SM2Utils.decrypt(rawCiphertextBytes,rawPrivKeyBytes);
    }

    @Override
    public byte[] retrievePubKeyBytes(byte[] privKeyBytes) {

        byte[] rawPrivKeyBytes = resolvePrivKey(privKeyBytes).getRawKeyBytes();
        byte[] rawPubKeyBytes = SM2Utils.retrievePublicKey(rawPrivKeyBytes);
        return new PubKey(SM2,rawPubKeyBytes).toBytes();
    }

    @Override
    public boolean supportPrivKey(byte[] privKeyBytes) {
        // 验证输入字节数组长度=算法标识长度+密钥类型长度+密钥长度，密钥数据的算法标识对应SM2算法，并且密钥类型是私钥
        return privKeyBytes.length == PRIVKEY_LENGTH && privKeyBytes[0] == SM2.CODE && privKeyBytes[1] == PRIV_KEY.CODE;
    }

    @Override
    public PrivKey resolvePrivKey(byte[] privKeyBytes) {
        // 由框架调用 support 方法检查有效性，在此不做重复检查；
        return new PrivKey(privKeyBytes);
    }

    @Override
    public boolean supportPubKey(byte[] pubKeyBytes) {
        // 验证输入字节数组长度=算法标识长度+密钥类型长度+椭圆曲线点长度，密钥数据的算法标识对应SM2算法，并且密钥类型是公钥
        return pubKeyBytes.length == PUBKEY_LENGTH && pubKeyBytes[0] == SM2.CODE && pubKeyBytes[1] == PUB_KEY.CODE;
    }

    @Override
    public PubKey resolvePubKey(byte[] pubKeyBytes) {
        // 由框架调用 support 方法检查有效性，在此不做重复检查；
        return new PubKey(pubKeyBytes);
    }

    @Override
    public boolean supportCiphertext(byte[] ciphertextBytes) {
        // 验证输入字节数组长度>=算法标识长度+椭圆曲线点长度+哈希长度，字节数组的算法标识对应SM2算法
        return ciphertextBytes.length >= ALGORYTHM_BYTES + ECPOINT_SIZE + HASHDIGEST_SIZE && ciphertextBytes[0] == SM2.CODE;
    }

    @Override
    public AsymmetricCiphertext resolveCiphertext(byte[] ciphertextBytes) {
        // 由框架调用 support 方法检查有效性，在此不做重复检查；
        return new AsymmetricCiphertext(ciphertextBytes);
    }

    @Override
    public SignatureDigest sign(PrivKey privKey, byte[] data) {

        byte[] rawPrivKeyBytes = privKey.getRawKeyBytes();

        // 验证原始私钥长度为256比特，即32字节
        if (rawPrivKeyBytes.length != PRIVKEY_SIZE)
            throw new IllegalArgumentException("This key has wrong format!");

        // 验证密钥数据的算法标识对应SM2签名算法
        if (privKey.getAlgorithm() != SM2)
            throw new IllegalArgumentException("This key is not SM2 private key!");

        // 调用SM2签名算法计算签名结果
        return new SignatureDigest(SM2, SM2Utils.sign(data,rawPrivKeyBytes));
    }

    @Override
    public boolean verify(SignatureDigest digest, PubKey pubKey, byte[] data) {

        byte[] rawPubKeyBytes = pubKey.getRawKeyBytes();
        byte[] rawDigestBytes = digest.getRawDigest();

        // 验证原始公钥长度为520比特，即65字节
        if (rawPubKeyBytes.length != ECPOINT_SIZE)
            throw new IllegalArgumentException("This key has wrong format!");

        // 验证密钥数据的算法标识对应SM2签名算法
        if (pubKey.getAlgorithm() != SM2)
            throw new IllegalArgumentException("This key is not SM2 public key!");

        // 验证签名数据的算法标识对应SM2签名算法，并且原始签名长度为64字节
        if (digest.getAlgorithm() != SM2 || rawDigestBytes.length != SIGNATUREDIGEST_SIZE)
            throw new IllegalArgumentException("This is not SM2 signature digest!");

        // 调用SM2验签算法验证签名结果
        return SM2Utils.verify(data, rawPubKeyBytes, rawDigestBytes);
    }

    @Override
    public boolean supportDigest(byte[] digestBytes) {
        // 验证输入字节数组长度=算法标识长度+签名长度，字节数组的算法标识对应SM2算法
        return digestBytes.length == SIGNATUREDIGEST_LENGTH && digestBytes[0] == SM2.CODE;
    }

    @Override
    public SignatureDigest resolveDigest(byte[] digestBytes) {
        // 由框架调用 support 方法检查有效性，在此不做重复检查；
        return new SignatureDigest(digestBytes);
    }

    @Override
    public CryptoAlgorithm getAlgorithm() {
        return SM2;
    }

    @Override
    public CryptoKeyPair generateKeyPair() {

        // 调用SM2算法的密钥生成算法生成公私钥对priKey和pubKey，返回密钥对
        AsymmetricCipherKeyPair keyPair = SM2Utils.generateKeyPair();
        ECPrivateKeyParameters ecPriv = (ECPrivateKeyParameters) keyPair.getPrivate();
        ECPublicKeyParameters ecPub = (ECPublicKeyParameters) keyPair.getPublic();

        byte[] privKeyBytesD = ecPriv.getD().toByteArray();
        byte[] privKeyBytes = new byte[PRIVKEY_SIZE];
        if (privKeyBytesD.length > PRIVKEY_SIZE)
            System.arraycopy(privKeyBytesD,privKeyBytesD.length-PRIVKEY_SIZE,privKeyBytes,0,PRIVKEY_SIZE);
        else System.arraycopy(privKeyBytesD,0,privKeyBytes,PRIVKEY_SIZE-privKeyBytesD.length,privKeyBytesD.length);

//        byte[] pubKeyBytesX = ecPub.getQ().getAffineXCoord().getEncoded();
//        byte[] pubKeyBytesY = ecPub.getQ().getAffineYCoord().getEncoded();
//        byte[] pubKeyBytes = new byte[ECPOINT_SIZE];
//        System.arraycopy(Hex.decode("04"),0,pubKeyBytes,0,1);
//        System.arraycopy(pubKeyBytesX,0,pubKeyBytes,1,32);
//        System.arraycopy(pubKeyBytesY,0,pubKeyBytes,1+32,32);
        byte[] pubKeyBytes = ecPub.getQ().getEncoded(false);

        return new CryptoKeyPair(new PubKey(SM2,pubKeyBytes),new PrivKey(SM2,privKeyBytes));
    }
}

