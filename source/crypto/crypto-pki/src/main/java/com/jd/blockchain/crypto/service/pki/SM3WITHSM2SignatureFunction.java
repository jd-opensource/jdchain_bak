package com.jd.blockchain.crypto.service.pki;

import com.jd.blockchain.crypto.*;
import org.bouncycastle.asn1.gm.GMObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.jcajce.provider.asymmetric.util.KeyUtil;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveGenParameterSpec;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.custom.gm.SM2P256V1Curve;

import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import static com.jd.blockchain.crypto.BaseCryptoKey.KEY_TYPE_BYTES;
import static com.jd.blockchain.crypto.CryptoBytes.ALGORYTHM_CODE_SIZE;
import static com.jd.blockchain.crypto.CryptoKeyType.PRIVATE;
import static com.jd.blockchain.crypto.CryptoKeyType.PUBLIC;
import static com.jd.blockchain.crypto.service.pki.PKIAlgorithm.SM3WITHSM2;

/**
 * @author zhanglin33
 * @title: SM3WITHSM2SignatureFunction
 * @description: TODO
 * @date 2019-05-15, 16:39
 */
public class SM3WITHSM2SignatureFunction implements SignatureFunction {
    private static final int RAW_PUBKEY_SIZE = 65;
    private static final int RAW_PRIVKEY_SIZE = 32 + 65;

    private static final int RAW_SIGNATUREDIGEST_SIZE = 64;

    private static final SM2P256V1Curve CURVE = new SM2P256V1Curve();
    private static final BigInteger GX = new BigInteger("32C4AE2C1F1981195F9904466A39C994" +
            "8FE30BBFF2660BE1715A4589334C74C7", 16);
    private static final BigInteger GY = new BigInteger("BC3736A2F4F6779C59BDCEE36B692153" +
            "D0A9877CC62A474002DF32E52139F0A0", 16);
    private static final ECPoint G = CURVE.createPoint(GX, GY);

    private static final AlgorithmIdentifier SM2_ALGORITHM_IDENTIFIER = new AlgorithmIdentifier(
            X9ObjectIdentifiers.id_ecPublicKey, GMObjectIdentifiers.sm2p256v1);


    @Override
    public SignatureDigest sign(PrivKey privKey, byte[] data) {

        Security.addProvider(new BouncyCastleProvider());

        byte[] rawPrivKeyBytes = privKey.getRawKeyBytes();

        if (rawPrivKeyBytes.length < RAW_PRIVKEY_SIZE) {
            throw new CryptoException("This key has wrong format!");
        }

        if (privKey.getAlgorithm() != SM3WITHSM2.code()) {
            throw new CryptoException("This key is not SM3WITHSM2 private key!");
        }

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(rawPrivKeyBytes);

        KeyFactory keyFactory;
        ECPrivateKey rawPrivKey;
        Signature signer;
        byte[] signature;

        try {
            keyFactory = KeyFactory.getInstance("EC", BouncyCastleProvider.PROVIDER_NAME);
            rawPrivKey = (ECPrivateKey) keyFactory.generatePrivate(keySpec);
            signer = Signature.getInstance("SM3withSM2", BouncyCastleProvider.PROVIDER_NAME);

            signer.initSign(rawPrivKey);
            signer.update(data);
            signature = signer.sign();
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException |
                InvalidKeySpecException | NoSuchProviderException e) {
            throw new CryptoException(e.getMessage(), e);
        }

        return new SignatureDigest(SM3WITHSM2, signature);
    }

    @Override
    public boolean verify(SignatureDigest digest, PubKey pubKey, byte[] data) {

        Security.addProvider(new BouncyCastleProvider());

        byte[] rawPubKeyBytes = pubKey.getRawKeyBytes();
        byte[] rawDigestBytes = digest.getRawDigest();

        if (rawPubKeyBytes.length < RAW_PUBKEY_SIZE) {
            throw new CryptoException("This key has wrong format!");
        }

        if (pubKey.getAlgorithm() != SM3WITHSM2.code()) {
            throw new CryptoException("This key is not SM3WITHSM2 public key!");
        }

        if (digest.getAlgorithm() != SM3WITHSM2.code() || rawDigestBytes.length < RAW_SIGNATUREDIGEST_SIZE) {
            throw new CryptoException("This is not SM3WITHSM2 signature digest!");
        }

        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(rawPubKeyBytes);

        KeyFactory keyFactory;
        ECPublicKey rawPubKey;
        Signature verifier;
        boolean isValid;

        try {
            keyFactory = KeyFactory.getInstance("EC", BouncyCastleProvider.PROVIDER_NAME);
            rawPubKey = (ECPublicKey) keyFactory.generatePublic(keySpec);
            verifier = Signature.getInstance("SM3withSM2", BouncyCastleProvider.PROVIDER_NAME);
            verifier.initVerify(rawPubKey);
            verifier.update(data);
            isValid = verifier.verify(rawDigestBytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException | InvalidKeySpecException
                | NoSuchProviderException e) {
            throw new CryptoException(e.getMessage(), e);
        }

        return isValid;
    }

    @Override
    public boolean supportPubKey(byte[] pubKeyBytes) {
        return pubKeyBytes.length > (ALGORYTHM_CODE_SIZE + KEY_TYPE_BYTES + RAW_PUBKEY_SIZE)
                && CryptoAlgorithm.match(SM3WITHSM2, pubKeyBytes)
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
    public boolean supportPrivKey(byte[] privKeyBytes) {
        return privKeyBytes.length > (ALGORYTHM_CODE_SIZE + KEY_TYPE_BYTES + RAW_PRIVKEY_SIZE)
                && CryptoAlgorithm.match(SM3WITHSM2, privKeyBytes)
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
    public PubKey retrievePubKey(PrivKey privKey) {

        byte[] rawPrivKeyBytes = privKey.getRawKeyBytes();

        if (rawPrivKeyBytes.length < RAW_PRIVKEY_SIZE) {
            throw new CryptoException("This key has wrong format!");
        }

        if (privKey.getAlgorithm() != SM3WITHSM2.code()) {
            throw new CryptoException("This key is not SM3WITHSM2 private key!");
        }

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(rawPrivKeyBytes);

        KeyFactory keyFactory;
        ECPrivateKey rawPrivKey;
        byte[] rawPubKeyBytes;
        try {
            keyFactory = KeyFactory.getInstance("EC", BouncyCastleProvider.PROVIDER_NAME);
            rawPrivKey = (ECPrivateKey) keyFactory.generatePrivate(keySpec);
            BigInteger d = rawPrivKey.getS();
            ECPoint Q = G.multiply(d).normalize();
            rawPubKeyBytes = KeyUtil.getEncodedSubjectPublicKeyInfo(SM2_ALGORITHM_IDENTIFIER,
                    Q.getEncoded(false));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchProviderException e) {
            throw new CryptoException(e.getMessage(), e);
        }

        return new PubKey(SM3WITHSM2, rawPubKeyBytes);
    }

    @Override
    public boolean supportDigest(byte[] digestBytes) {
        return digestBytes.length > (RAW_SIGNATUREDIGEST_SIZE + ALGORYTHM_CODE_SIZE)
                && CryptoAlgorithm.match(SM3WITHSM2, digestBytes);
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
    public AsymmetricKeypair generateKeypair() {

        Security.addProvider(new BouncyCastleProvider());
        KeyPairGenerator generator;
        PublicKey pubKey;
        PrivateKey privKey;
        try {
            generator = KeyPairGenerator.getInstance("EC", BouncyCastleProvider.PROVIDER_NAME);
            generator.initialize(new ECNamedCurveGenParameterSpec("sm2p256v1"));
            KeyPair keyPair = generator.generateKeyPair();
            pubKey  = keyPair.getPublic();
            privKey = keyPair.getPrivate();
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
            throw new CryptoException(e.getMessage(), e);
        }

        byte[] pubKeyBytes  = pubKey.getEncoded();
        byte[] privKeyBytes = privKey.getEncoded();

        return new AsymmetricKeypair(new PubKey(SM3WITHSM2, pubKeyBytes),
                new PrivKey(SM3WITHSM2, privKeyBytes));
    }
    @Override
    public CryptoAlgorithm getAlgorithm() {
        return SM3WITHSM2;
    }
}
