package com.jd.blockchain.crypto.service.classic;

import com.jd.blockchain.crypto.*;
import com.jd.blockchain.crypto.utils.classic.RSAUtils;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;

import static com.jd.blockchain.crypto.BaseCryptoKey.KEY_TYPE_BYTES;
import static com.jd.blockchain.crypto.CryptoBytes.ALGORYTHM_CODE_SIZE;
import static com.jd.blockchain.crypto.CryptoKeyType.PRIVATE;
import static com.jd.blockchain.crypto.CryptoKeyType.PUBLIC;

/**
 * @author zhanglin33
 * @title: RSACryptoFunction
 * @description: Interfaces for RSA crypto functions, including key generation, encryption, signature, and so on
 * @date 2019-03-25, 17:28
 */
public class RSACryptoFunction implements AsymmetricEncryptionFunction, SignatureFunction {

    private static final CryptoAlgorithm RSA = ClassicAlgorithm.RSA;

    // modulus.length = 256, publicExponent.length = 3
    private static final int PUBKEY_SIZE = 259;
    // modulus.length = 256, publicExponent.length = 3, privateExponent.length = 256, p.length = 128, q.length =128,
    // dP.length = 128, dQ.length = 128, qInv.length = 128
    private static final int PRIVKEY_SIZE = 1155;

    private static final int SIGNATUREDIGEST_SIZE = 256;
    private static final int CIPHERTEXTBLOCK_SIZE = 256;

    private static final int PUBKEY_LENGTH = ALGORYTHM_CODE_SIZE + KEY_TYPE_BYTES + PUBKEY_SIZE;
    private static final int PRIVKEY_LENGTH = ALGORYTHM_CODE_SIZE + KEY_TYPE_BYTES + PRIVKEY_SIZE;
    private static final int SIGNATUREDIGEST_LENGTH = ALGORYTHM_CODE_SIZE + SIGNATUREDIGEST_SIZE;
    @Override
    public Ciphertext encrypt(PubKey pubKey, byte[] data) {

        byte[] rawPubKeyBytes = pubKey.getRawKeyBytes();

        // 验证原始公钥长度为257字节
        if (rawPubKeyBytes.length != PUBKEY_SIZE) {
            throw new CryptoException("This key has wrong format!");
        }

        // 验证密钥数据的算法标识对应RSA算法
        if (pubKey.getAlgorithm() != RSA.code()) {
            throw new CryptoException("The is not RSA public key!");
        }

        // 调用RSA加密算法计算密文
        return new AsymmetricCiphertext(RSA, RSAUtils.encrypt(data, rawPubKeyBytes));
    }

    @Override
    public byte[] decrypt(PrivKey privKey, Ciphertext ciphertext) {

        byte[] rawPrivKeyBytes = privKey.getRawKeyBytes();
        byte[] rawCiphertextBytes = ciphertext.getRawCiphertext();

        // 验证原始私钥长度为1153字节
        if (rawPrivKeyBytes.length != PRIVKEY_SIZE) {
            throw new CryptoException("This key has wrong format!");
        }

        // 验证密钥数据的算法标识对应RSA算法
        if (privKey.getAlgorithm() != RSA.code()) {
            throw new CryptoException("This key is not RSA private key!");
        }

        // 验证密文数据的算法标识对应RSA算法，并且密文是分组长度的整数倍
        if (ciphertext.getAlgorithm() != RSA.code()
                || rawCiphertextBytes.length % CIPHERTEXTBLOCK_SIZE != 0) {
            throw new CryptoException("This is not RSA ciphertext!");
        }

        // 调用RSA解密算法得到明文结果
        return RSAUtils.decrypt(rawCiphertextBytes, rawPrivKeyBytes);
    }

    @Override
    public PubKey retrievePubKey(PrivKey privKey) {
        byte[] rawPrivKeyBytes = privKey.getRawKeyBytes();
        byte[] rawPubKeyBytes = RSAUtils.retrievePublicKey(rawPrivKeyBytes);
        return new PubKey(RSA, rawPubKeyBytes);
    }

    @Override
    public SignatureDigest sign(PrivKey privKey, byte[] data) {

        byte[] rawPrivKeyBytes = privKey.getRawKeyBytes();

        // 验证原始私钥长度为1153字节
        if (rawPrivKeyBytes.length != PRIVKEY_SIZE) {
            throw new CryptoException("This key has wrong format!");
        }

        // 验证密钥数据的算法标识对应RSA签名算法
        if (privKey.getAlgorithm() != RSA.code()) {
            throw new CryptoException("This key is not RSA private key!");
        }

        // 调用RSA签名算法计算签名结果
        return new SignatureDigest(RSA, RSAUtils.sign(data, rawPrivKeyBytes));
    }

    @Override
    public boolean verify(SignatureDigest digest, PubKey pubKey, byte[] data) {

        byte[] rawPubKeyBytes = pubKey.getRawKeyBytes();
        byte[] rawDigestBytes = digest.getRawDigest();

        // 验证原始公钥长度为257字节
        if (rawPubKeyBytes.length != PUBKEY_SIZE) {
            throw new CryptoException("This key has wrong format!");
        }

        // 验证密钥数据的算法标识对应RSA签名算法
        if (pubKey.getAlgorithm() != RSA.code()) {
            throw new CryptoException("This key is not RSA public key!");
        }

        // 验证签名数据的算法标识对应RSA签名算法，并且原始签名长度为256字节
        if (digest.getAlgorithm() != RSA.code() || rawDigestBytes.length != SIGNATUREDIGEST_SIZE) {
            throw new CryptoException("This is not RSA signature digest!");
        }

        // 调用RSA验签算法验证签名结果
        return RSAUtils.verify(data, rawPubKeyBytes, rawDigestBytes);
    }

    @Override
    public boolean supportPrivKey(byte[] privKeyBytes) {
        // 验证输入字节数组长度=算法标识长度+密钥类型长度+密钥长度，密钥数据的算法标识对应RSA算法，并且密钥类型是私钥
        return privKeyBytes.length == PRIVKEY_LENGTH && CryptoAlgorithm.match(RSA, privKeyBytes)
                && privKeyBytes[ALGORYTHM_CODE_SIZE] == PRIVATE.CODE;
    }

    @Override
    public PrivKey resolvePrivKey(byte[] privKeyBytes) {
        if (supportPrivKey(privKeyBytes)) {
            return new PrivKey(privKeyBytes);
        } else {
            throw new CryptoException("privKeyBytes are invalid!");
        }
    }

    @Override
    public boolean supportPubKey(byte[] pubKeyBytes) {
        // 验证输入字节数组长度=算法标识长度+密钥类型长度+椭圆曲线点长度，密钥数据的算法标识对应RSA算法，并且密钥类型是公钥
        return pubKeyBytes.length == PUBKEY_LENGTH && CryptoAlgorithm.match(RSA, pubKeyBytes)
                && pubKeyBytes[ALGORYTHM_CODE_SIZE] == PUBLIC.CODE;
    }

    @Override
    public PubKey resolvePubKey(byte[] pubKeyBytes) {
        if (supportPubKey(pubKeyBytes)) {
            return new PubKey(pubKeyBytes);
        } else {
            throw new CryptoException("pubKeyBytes are invalid!");
        }
    }

    @Override
    public boolean supportDigest(byte[] digestBytes) {
        // 验证输入字节数组长度=算法标识长度+签名长度，字节数组的算法标识对应RSA算法
        return digestBytes.length == SIGNATUREDIGEST_LENGTH && CryptoAlgorithm.match(RSA, digestBytes);
    }

    @Override
    public SignatureDigest resolveDigest(byte[] digestBytes) {
        if (supportDigest(digestBytes)) {
            return new SignatureDigest(digestBytes);
        } else {
            throw new CryptoException("digestBytes are invalid!");
        }
    }

    @Override
    public boolean supportCiphertext(byte[] ciphertextBytes) {
        // 验证输入字节数组长度=密文分组的整数倍，字节数组的算法标识对应RSA算法
        return (ciphertextBytes.length % CIPHERTEXTBLOCK_SIZE == ALGORYTHM_CODE_SIZE)
                && CryptoAlgorithm.match(RSA, ciphertextBytes);
    }

    @Override
    public AsymmetricCiphertext resolveCiphertext(byte[] ciphertextBytes) {
        if (supportCiphertext(ciphertextBytes)) {
            return new AsymmetricCiphertext(ciphertextBytes);
        } else {
            throw new CryptoException("ciphertextBytes are invalid!");
        }
    }

    @Override
    public CryptoAlgorithm getAlgorithm() {
        return RSA;
    }

    @Override
    public AsymmetricKeypair generateKeypair() {

        AsymmetricCipherKeyPair keyPair = RSAUtils.generateKeyPair();
        RSAKeyParameters pubKey = (RSAKeyParameters) keyPair.getPublic();
        RSAPrivateCrtKeyParameters privKey = (RSAPrivateCrtKeyParameters) keyPair.getPrivate();

        byte[] pubKeyBytes = RSAUtils.pubKey2Bytes_RawKey(pubKey);
        byte[] privKeyBytes = RSAUtils.privKey2Bytes_RawKey(privKey);

        return new AsymmetricKeypair(new PubKey(RSA, pubKeyBytes), new PrivKey(RSA, privKeyBytes));
    }
}
