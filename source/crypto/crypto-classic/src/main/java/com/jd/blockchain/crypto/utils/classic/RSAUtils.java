package com.jd.blockchain.crypto.utils.classic;

import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RSAPrivateKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.encodings.PKCS1Encoding;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.crypto.signers.RSADigestSigner;
import org.bouncycastle.jcajce.provider.asymmetric.util.KeyUtil;

import com.jd.blockchain.utils.io.BytesUtils;

/**
 * @author zhanglin33
 * @title: RSAUtils
 * @description: RSA2048 encryption(RSA/ECB/PKCS1Padding) and signature(SHA256withRSA) algorithms,
 *               and keys are output in raw, PKCS1v2 and PKCS8 formats
 * @date 2019-03-25, 17:20
 */
public class  RSAUtils {

    private static final int KEYSIZEBITS = 2048;
    private static final int CERTAINTY = 100;

    private static final int MODULUS_LENGTH = 2048 / 8;
    private static final int PRIVEXP_LENGTH = 2048 / 8;
    private static final int P_LENGTH       = 1024 / 8;
    private static final int Q_LENGTH       = 1024 / 8;
    private static final int DP_LENGTH      = 1024 / 8;
    private static final int DQ_LENGTH      = 1024 / 8;
    private static final int QINV_LENGTH    = 1024 / 8;

    private static final BigInteger PUBEXP_0X03 = BigInteger.valueOf(0x03);
    private static final BigInteger PUBEXP_0X010001 = BigInteger.valueOf(0x010001);

    private static final BigInteger VERSION_2PRIMES = BigInteger.valueOf(0);

    private static final AlgorithmIdentifier RSA_ALGORITHM_IDENTIFIER =
            new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, DERNull.INSTANCE);

    private static final int PLAINTEXT_BLOCKSIZE = 256 - 11;
    private static final int CIPHERTEXT_BLOCKSIZE = 256;


    //-----------------Key Generation Algorithm-----------------

    /**
     * key pair generation
     *
     * @return key pair
     */
    public static AsymmetricCipherKeyPair generateKeyPair(){
        return generateKeyPair(new SecureRandom());
    }

    public static AsymmetricCipherKeyPair generateKeyPair(SecureRandom random){
        AsymmetricCipherKeyPairGenerator kpGen = new RSAKeyPairGenerator();
        kpGen.init(new RSAKeyGenerationParameters(PUBEXP_0X010001, random, KEYSIZEBITS, CERTAINTY));
        return kpGen.generateKeyPair();
    }

    /**
     * key pair generation with short public exponent， resulting in verifying and encrypting more efficiently
     *
     * @return key pair
     */
    public static AsymmetricCipherKeyPair generateKeyPair_shortExp(){
        return generateKeyPair_shortExp(new SecureRandom());
    }

    public static AsymmetricCipherKeyPair generateKeyPair_shortExp(SecureRandom random){
        AsymmetricCipherKeyPairGenerator kpGen = new RSAKeyPairGenerator();
        kpGen.init(new RSAKeyGenerationParameters(PUBEXP_0X03, random, KEYSIZEBITS, CERTAINTY));
        return kpGen.generateKeyPair();
    }

    // Retrieve public key in raw keys form
    public static byte[] retrievePublicKey(byte[] privateKey) {

        RSAPrivateCrtKeyParameters privKey = bytes2PrivKey_RawKey(privateKey);

        BigInteger modulus  = privKey.getModulus();
        BigInteger exponent = privKey.getPublicExponent();

        RSAKeyParameters pubKey = new RSAKeyParameters(false, modulus, exponent);

        return pubKey2Bytes_RawKey(pubKey);
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
        RSAPrivateCrtKeyParameters privKey = bytes2PrivKey_RawKey(privateKey);
        return sign(data,privKey);
    }

    public static byte[] sign(byte[] data, CipherParameters params){

        SHA256Digest digest = new SHA256Digest();
        RSADigestSigner signer = new RSADigestSigner(digest);
        signer.init(true, params);
        signer.update(data, 0, data.length);
        try {
            return signer.generateSignature();
        } catch (CryptoException e) {
            throw new com.jd.blockchain.crypto.CryptoException(e.getMessage(), e);
        }
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
        RSAKeyParameters pubKey = bytes2PubKey_RawKey(publicKey);
        return verify(data,pubKey,signature);
    }

    public static boolean verify(byte[] data, CipherParameters params, byte[] signature){

        SHA256Digest digest = new SHA256Digest();
        RSADigestSigner signer = new RSADigestSigner(digest);

        signer.init(false, params);
        signer.update(data, 0, data.length);
        return signer.verifySignature(signature);
    }


    //-----------------Public Key Encryption Algorithm-----------------

    /**
     * encryption
     *
     * @param plainBytes plaintext
     * @param publicKey public key
     * @return ciphertext
     */
    public static byte[] encrypt(byte[] plainBytes, byte[] publicKey){
        RSAKeyParameters pubKey = bytes2PubKey_RawKey(publicKey);
        return encrypt(plainBytes,pubKey);
    }

    public static byte[] encrypt(byte[] plainBytes, byte[] publicKey, SecureRandom random){

        RSAKeyParameters pubKey = bytes2PubKey_RawKey(publicKey);
        ParametersWithRandom params = new ParametersWithRandom(pubKey,random);

        return encrypt(plainBytes,params);
    }

    public static byte[] encrypt(byte[] plainBytes, CipherParameters params){

        int blockNum = (plainBytes.length % PLAINTEXT_BLOCKSIZE == 0) ? (plainBytes.length / PLAINTEXT_BLOCKSIZE)
                : (plainBytes.length / PLAINTEXT_BLOCKSIZE + 1);
        int inputLength;
        byte[] result = new byte[blockNum * CIPHERTEXT_BLOCKSIZE];
        byte[] buffer;

        AsymmetricBlockCipher encryptor = new PKCS1Encoding(new RSAEngine());
        encryptor.init(true, params);
        try {
            for (int i= 0; i < blockNum; i++) {
                inputLength = ((plainBytes.length - i * PLAINTEXT_BLOCKSIZE) > PLAINTEXT_BLOCKSIZE)?
                        PLAINTEXT_BLOCKSIZE : (plainBytes.length - i * PLAINTEXT_BLOCKSIZE);
                buffer = encryptor.processBlock(plainBytes, i * PLAINTEXT_BLOCKSIZE, inputLength);
                System.arraycopy(buffer,0,
                        result, i * CIPHERTEXT_BLOCKSIZE, CIPHERTEXT_BLOCKSIZE);
            }
        } catch (InvalidCipherTextException e) {
            throw new com.jd.blockchain.crypto.CryptoException(e.getMessage(), e);
        }
        return result;
    }

    /**
     * decryption
     *
     * @param cipherBytes ciphertext
     * @param privateKey private key
     * @return plaintext
     */
    public static byte[] decrypt(byte[] cipherBytes, byte[] privateKey){
        RSAPrivateCrtKeyParameters privKey = bytes2PrivKey_RawKey(privateKey);
        return decrypt(cipherBytes,privKey);
    }

    public static byte[] decrypt(byte[] cipherBytes, CipherParameters params){

        if (cipherBytes.length % CIPHERTEXT_BLOCKSIZE != 0)
        {
            throw new com.jd.blockchain.crypto.CryptoException("ciphertext's length is wrong!");
        }

        int blockNum = cipherBytes.length / CIPHERTEXT_BLOCKSIZE;
        int count = 0;
        byte[] buffer;
        byte[] plaintextWithZeros = new byte[blockNum * PLAINTEXT_BLOCKSIZE];
        byte[] result;

        AsymmetricBlockCipher decryptor = new PKCS1Encoding(new RSAEngine());
        decryptor.init(false,params);
        try {
            for (int i = 0; i < blockNum; i++){
                buffer = decryptor.processBlock(cipherBytes,i * CIPHERTEXT_BLOCKSIZE, CIPHERTEXT_BLOCKSIZE);
                count  += buffer.length;
                System.arraycopy(buffer,0,plaintextWithZeros, i * PLAINTEXT_BLOCKSIZE, buffer.length);
            }
        } catch (InvalidCipherTextException e) {
            throw new com.jd.blockchain.crypto.CryptoException(e.getMessage(), e);
        }

        result = new byte[count];
        System.arraycopy(plaintextWithZeros,0,result,0,result.length);

        return result;
    }


    /**
     * This outputs the key in PKCS1v2 format.
     *      RSAPublicKey ::= SEQUENCE {
     *                          modulus INTEGER, -- n
     *                          publicExponent INTEGER, -- e
     *                      }
     */
    public static byte[] pubKey2Bytes_PKCS1(RSAKeyParameters pubKey)
    {
        ASN1EncodableVector v = new ASN1EncodableVector();

        v.add(new ASN1Integer(pubKey.getModulus()));
        v.add(new ASN1Integer(pubKey.getExponent()));

        DERSequence pubKeySequence = new DERSequence(v);

        byte[] result;

        try {
            result = pubKeySequence.getEncoded(ASN1Encoding.DER);
        } catch (IOException e) {
            throw new com.jd.blockchain.crypto.CryptoException(e.getMessage(), e);
        }

        return result;
    }

    public static byte[] pubKey2Bytes_PKCS8(RSAKeyParameters pubKey){

        BigInteger modulus = pubKey.getModulus();
        BigInteger exponent = pubKey.getExponent();

        return KeyUtil.getEncodedSubjectPublicKeyInfo(RSA_ALGORITHM_IDENTIFIER,
                new org.bouncycastle.asn1.pkcs.RSAPublicKey(modulus, exponent));
    }

    public static byte[] pubKey2Bytes_RawKey(RSAKeyParameters pubKey){

        BigInteger modulus  = pubKey.getModulus();
        BigInteger exponent = pubKey.getExponent();

        byte[] exponentBytes = exponent.toByteArray();
        byte[] modulusBytes = bigInteger2Bytes(modulus,MODULUS_LENGTH);

        return BytesUtils.concat(modulusBytes,exponentBytes);
    }

    public static RSAKeyParameters bytes2PubKey_PKCS1(byte[] pubKeyBytes) {

        ASN1Sequence pubKeySequence = ASN1Sequence.getInstance(pubKeyBytes);

        BigInteger modulus  = ASN1Integer.getInstance(pubKeySequence.getObjectAt(0)).getValue();
        BigInteger exponent = ASN1Integer.getInstance(pubKeySequence.getObjectAt(1)).getValue();

        return new RSAKeyParameters(false, modulus, exponent);
    }

    public static RSAKeyParameters bytes2PubKey_PKCS8(byte[] pubKeyBytes) {

        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(pubKeyBytes);

        KeyFactory keyFactory;
        RSAPublicKey publicKey;

        try {
            keyFactory = KeyFactory.getInstance("RSA");
            publicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new com.jd.blockchain.crypto.CryptoException(e.getMessage(), e);
        }

        BigInteger exponent = publicKey.getPublicExponent();
        BigInteger modulus = publicKey.getModulus();

        return new RSAKeyParameters(false,modulus,exponent);
    }

    public static RSAKeyParameters bytes2PubKey_RawKey(byte[] pubKeyBytes) {

        byte[] modulusBytes  = new byte[MODULUS_LENGTH];
        byte[] exponentBytes = new byte[pubKeyBytes.length - MODULUS_LENGTH];

        System.arraycopy(pubKeyBytes,0, modulusBytes,0, MODULUS_LENGTH);

        System.arraycopy(pubKeyBytes,MODULUS_LENGTH, exponentBytes,0,exponentBytes.length);

        BigInteger modulus = new BigInteger(1, modulusBytes);
        BigInteger exponent = new BigInteger(1, exponentBytes);

        return new RSAKeyParameters(false,modulus,exponent);
    }

    /**
     * This outputs the key in PKCS1v2 format.
     *      RSAPrivateKey ::= SEQUENCE {
     *                          VERSION_2PRIMES Version,
     *                          modulus INTEGER, -- n
     *                          publicExponent INTEGER, -- e
     *                          privateExponent INTEGER, -- d
     *                          prime1 INTEGER, -- p
     *                          prime2 INTEGER, -- q
     *                          exponent1 INTEGER, -- d mod (p-1)
     *                          exponent2 INTEGER, -- d mod (q-1)
     *                          coefficient INTEGER, -- (inverse of q) mod p
     *                          otherPrimeInfos OtherPrimeInfos OPTIONAL
     *                      }
     *
     *      Version ::= INTEGER { two-prime(0), multi(1) }
     *        (CONSTRAINED BY {-- version must be multi if otherPrimeInfos present --})
     *
     * This routine is written to output PKCS1 version 2.1, private keys.
     */
    public static byte[] privKey2Bytes_PKCS1(RSAPrivateCrtKeyParameters privKey)
    {
        ASN1EncodableVector v = new ASN1EncodableVector();

        v.add(new ASN1Integer(VERSION_2PRIMES));                       // version
        v.add(new ASN1Integer(privKey.getModulus()));
        v.add(new ASN1Integer(privKey.getPublicExponent()));
        v.add(new ASN1Integer(privKey.getExponent()));
        v.add(new ASN1Integer(privKey.getP()));
        v.add(new ASN1Integer(privKey.getQ()));
        v.add(new ASN1Integer(privKey.getDP()));
        v.add(new ASN1Integer(privKey.getDQ()));
        v.add(new ASN1Integer(privKey.getQInv()));

        DERSequence privKeySequence = new DERSequence(v);

        byte[] result;

        try {
            result = privKeySequence.getEncoded(ASN1Encoding.DER);
        } catch (IOException e) {
            throw new com.jd.blockchain.crypto.CryptoException(e.getMessage(), e);
        }

        return result;
    }

    public static byte[] privKey2Bytes_PKCS8(RSAPrivateCrtKeyParameters privKey){

        BigInteger modulus = privKey.getModulus();
        BigInteger pubExp  = privKey.getPublicExponent();
        BigInteger privExp = privKey.getExponent();
        BigInteger p       = privKey.getP();
        BigInteger q       = privKey.getQ();
        BigInteger dP      = privKey.getDP();
        BigInteger dQ      = privKey.getDQ();
        BigInteger qInv    = privKey.getQInv();

        return KeyUtil.getEncodedPrivateKeyInfo(RSA_ALGORITHM_IDENTIFIER,
                new RSAPrivateKey(modulus, pubExp, privExp, p, q, dP, dQ, qInv));
    }

    public static byte[] privKey2Bytes_RawKey(RSAPrivateCrtKeyParameters privKey){

        BigInteger modulus = privKey.getModulus();
        BigInteger pubExp  = privKey.getPublicExponent();
        BigInteger privExp = privKey.getExponent();
        BigInteger p       = privKey.getP();
        BigInteger q       = privKey.getQ();
        BigInteger dP      = privKey.getDP();
        BigInteger dQ      = privKey.getDQ();
        BigInteger qInv    = privKey.getQInv();

        byte[] modulusBytes = bigInteger2Bytes(modulus,MODULUS_LENGTH);
        byte[] pubExpBytes  = pubExp.toByteArray();
        byte[] privExpBytes = bigInteger2Bytes(privExp,PRIVEXP_LENGTH);
        byte[] pBytes       = bigInteger2Bytes(p,P_LENGTH);
        byte[] qBytes       = bigInteger2Bytes(q,Q_LENGTH);
        byte[] dPBytes      = bigInteger2Bytes(dP,DP_LENGTH);
        byte[] dQBytes      = bigInteger2Bytes(dQ,DQ_LENGTH);
        byte[] qInvBytes    = bigInteger2Bytes(qInv,QINV_LENGTH);


        return BytesUtils.concat(modulusBytes,pubExpBytes,privExpBytes,pBytes,qBytes,dPBytes,dQBytes,qInvBytes);
    }

    public static RSAPrivateCrtKeyParameters bytes2PrivKey_PKCS1(byte[] privKeyBytes){

        ASN1Sequence priKeySequence = ASN1Sequence.getInstance(privKeyBytes);

        BigInteger modulus = ASN1Integer.getInstance(priKeySequence.getObjectAt(1)).getValue();
        BigInteger pubExp  = ASN1Integer.getInstance(priKeySequence.getObjectAt(2)).getValue();
        BigInteger privExp = ASN1Integer.getInstance(priKeySequence.getObjectAt(3)).getValue();
        BigInteger p       = ASN1Integer.getInstance(priKeySequence.getObjectAt(4)).getValue();
        BigInteger q       = ASN1Integer.getInstance(priKeySequence.getObjectAt(5)).getValue();
        BigInteger dP      = ASN1Integer.getInstance(priKeySequence.getObjectAt(6)).getValue();
        BigInteger dQ      = ASN1Integer.getInstance(priKeySequence.getObjectAt(7)).getValue();
        BigInteger qInv    = ASN1Integer.getInstance(priKeySequence.getObjectAt(8)).getValue();

        return new RSAPrivateCrtKeyParameters(modulus, pubExp, privExp, p, q, dP, dQ, qInv);
    }

    public static RSAPrivateCrtKeyParameters bytes2PrivKey_PKCS8(byte[] privKeyBytes){

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privKeyBytes);

        KeyFactory keyFactory;
        RSAPrivateCrtKey privateKey;

        try {
            keyFactory = KeyFactory.getInstance("RSA");
            privateKey = (RSAPrivateCrtKey) keyFactory.generatePrivate(keySpec);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new com.jd.blockchain.crypto.CryptoException(e.getMessage(), e);
        }

        BigInteger modulus = privateKey.getModulus();
        BigInteger pubExp  = privateKey.getPublicExponent();
        BigInteger privExp = privateKey.getPrivateExponent();
        BigInteger p       = privateKey.getPrimeP();
        BigInteger q       = privateKey.getPrimeQ();
        BigInteger dP      = privateKey.getPrimeExponentP();
        BigInteger dQ      = privateKey.getPrimeExponentQ();
        BigInteger qInv    = privateKey.getCrtCoefficient();

        return new RSAPrivateCrtKeyParameters(modulus, pubExp, privExp, p, q, dP, dQ, qInv);
    }

    public static RSAPrivateCrtKeyParameters bytes2PrivKey_RawKey(byte[] privKeyBytes){

        byte[] modulusBytes  = new byte[MODULUS_LENGTH];
        byte[] pubExpBytes   = new byte[privKeyBytes.length - MODULUS_LENGTH - PRIVEXP_LENGTH - P_LENGTH - Q_LENGTH
                                        - DP_LENGTH - DQ_LENGTH - QINV_LENGTH];
        byte[] privExpBytes  = new byte[PRIVEXP_LENGTH];
        byte[] pBytes        = new byte[P_LENGTH];
        byte[] qBytes        = new byte[Q_LENGTH];
        byte[] dPBytes       = new byte[DP_LENGTH];
        byte[] dQBytes       = new byte[DQ_LENGTH];
        byte[] qInvBytes     = new byte[QINV_LENGTH];

        System.arraycopy(privKeyBytes,0, modulusBytes,0, MODULUS_LENGTH);
        System.arraycopy(privKeyBytes, MODULUS_LENGTH, pubExpBytes,0,pubExpBytes.length);
        System.arraycopy(privKeyBytes,MODULUS_LENGTH + pubExpBytes.length,
                privExpBytes,0,PRIVEXP_LENGTH);
        System.arraycopy(privKeyBytes,MODULUS_LENGTH + pubExpBytes.length + PRIVEXP_LENGTH,
                pBytes,0,P_LENGTH);
        System.arraycopy(privKeyBytes,MODULUS_LENGTH + pubExpBytes.length + PRIVEXP_LENGTH + P_LENGTH,
                qBytes,0,Q_LENGTH);
        System.arraycopy(privKeyBytes,MODULUS_LENGTH + pubExpBytes.length + PRIVEXP_LENGTH + P_LENGTH +
                Q_LENGTH, dPBytes,0,DP_LENGTH);
        System.arraycopy(privKeyBytes,MODULUS_LENGTH + pubExpBytes.length + PRIVEXP_LENGTH + P_LENGTH +
                Q_LENGTH + DP_LENGTH, dQBytes,0,DQ_LENGTH);
        System.arraycopy(privKeyBytes,MODULUS_LENGTH + pubExpBytes.length + PRIVEXP_LENGTH + P_LENGTH +
                Q_LENGTH + DP_LENGTH + DQ_LENGTH, qInvBytes,0,QINV_LENGTH);

        BigInteger modulus = new BigInteger(1, modulusBytes);
        BigInteger pubExp  = new BigInteger(1, pubExpBytes);
        BigInteger privExp = new BigInteger(1, privExpBytes);
        BigInteger p       = new BigInteger(1, pBytes);
        BigInteger q       = new BigInteger(1, qBytes);
        BigInteger dP      = new BigInteger(1, dPBytes);
        BigInteger dQ      = new BigInteger(1, dQBytes);
        BigInteger qInv    = new BigInteger(1, qInvBytes);

        return new RSAPrivateCrtKeyParameters(modulus, pubExp, privExp, p, q, dP, dQ, qInv);
    }

    private static byte[] bigInteger2Bytes(BigInteger src, int length){

        byte[] result = new byte[length];
        byte[] srcBytes = src.toByteArray();
        int srcLength = srcBytes.length;

        if (srcLength > length) {
            System.arraycopy(srcBytes,srcLength - length,
                    result,0, length);
        } else {
            System.arraycopy(srcBytes,0,
                    result,length - srcLength, srcLength);
        }

        return result;
    }
}
