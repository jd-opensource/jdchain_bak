package com.jd.blockchain.crypto.smutils.asymmetric;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.*;
import org.bouncycastle.crypto.signers.SM2Signer;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;

public class SM2Utils {

    private static final int COORDS_SIZE = 32;
    private static final int POINT_SIZE = COORDS_SIZE * 2 + 1;
    private static final int R_SIZE =32;
    private static final int S_SIZE =32;

    // The length of sm3 output is 32 bytes
    private static final int SM3DIGEST_LENGTH = 32;

    private static final BigInteger SM2_ECC_P = new BigInteger("FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF00000000FFFFFFFFFFFFFFFF", 16);
    private static final BigInteger SM2_ECC_A = new BigInteger("FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF00000000FFFFFFFFFFFFFFFC", 16);
    private static final BigInteger SM2_ECC_B = new BigInteger("28E9FA9E9D9F5E344D5A9E4BCF6509A7F39789F515AB8F92DDBCBD414D940E93", 16);
    private static final BigInteger SM2_ECC_N = new BigInteger("FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFF7203DF6B21C6052B53BBF40939D54123", 16);
    private static final BigInteger SM2_ECC_GX = new BigInteger("32C4AE2C1F1981195F9904466A39C9948FE30BBFF2660BE1715A4589334C74C7", 16);
    private static final BigInteger SM2_ECC_GY = new BigInteger("BC3736A2F4F6779C59BDCEE36B692153D0A9877CC62A474002DF32E52139F0A0", 16);

    // To get the curve from the equation y^2=x^3+ax+b according the coefficient a and b,
    // with the big prime p, and obtain the generator g and the domain's parameters
    private static final ECCurve curve = new ECCurve.Fp(SM2_ECC_P, SM2_ECC_A, SM2_ECC_B);
    private static final ECPoint g = curve.createPoint(SM2_ECC_GX, SM2_ECC_GY);
    private static final ECDomainParameters domainParams = new ECDomainParameters(curve, g, SM2_ECC_N);


    //-----------------Key Pair Generation Algorithm-----------------

    /**
     * key generation
     *
     * @return key pair
     */
    public static AsymmetricCipherKeyPair generateKeyPair(){

        SecureRandom random = new SecureRandom();
        return generateKeyPair(random);
    }

    public static AsymmetricCipherKeyPair generateKeyPair(SecureRandom random){

        ECKeyGenerationParameters keyGenerationParams = new ECKeyGenerationParameters(domainParams,random);
        ECKeyPairGenerator keyPairGenerator = new ECKeyPairGenerator();

        // To generate the key pair
        keyPairGenerator.init(keyGenerationParams);
        return keyPairGenerator.generateKeyPair();
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

        SecureRandom random = new SecureRandom();
        ECPrivateKeyParameters privKey = new ECPrivateKeyParameters(new BigInteger(1,privateKey),domainParams);
        CipherParameters param = new ParametersWithRandom(privKey,random);

        return sign(data,param);
    }

    public static byte[] sign(byte[] data, byte[] privateKey, SecureRandom random, String ID){

        ECPrivateKeyParameters privKey = new ECPrivateKeyParameters(new BigInteger(1,privateKey),domainParams);
        CipherParameters param = new ParametersWithID(new ParametersWithRandom(privKey,random),ID.getBytes());

        return sign(data,param);
    }

    private static byte[] sign(byte[] data, CipherParameters param){

        SM2Signer signer = new SM2Signer();

        // To get Z_A and prepare parameters
        signer.init(true,param);
        // To fill the whole message to be signed
        signer.update(data,0,data.length);
        // To get and return the signature result;

        byte[] encodedSignature;
        try {
            encodedSignature = signer.generateSignature();
        } catch (CryptoException e) {
            throw new com.jd.blockchain.crypto.CryptoException(e.getMessage(), e);
        }

        // To decode the signature
        ASN1Sequence sig = ASN1Sequence.getInstance(encodedSignature);
        byte[] rBytes = BigIntegerTo32Bytes(ASN1Integer.getInstance(sig.getObjectAt(0)).getValue());
        byte[] sBytes = BigIntegerTo32Bytes(ASN1Integer.getInstance(sig.getObjectAt(1)).getValue());

        byte[] signature = new byte[R_SIZE + S_SIZE];
        System.arraycopy(rBytes,0,signature,0,R_SIZE);
        System.arraycopy(sBytes,0,signature,R_SIZE,S_SIZE);

        return signature;
    }

    /**
     * verification
     *
     * @param data data to be signed
     * @param publicKey public key
     * @return true or false
     */
    public static boolean verify(byte[] data, byte[] publicKey, byte[] signature){

        ECPoint pubKeyPoint = resolvePubKeyBytes(publicKey);
        ECPublicKeyParameters pubKey = new ECPublicKeyParameters(pubKeyPoint,domainParams);

        return verify(data,pubKey,signature);
    }

    public static boolean verify(byte[] data, byte[] publicKey, byte[] signature, String ID){

        ECPoint pubKeyPoint = resolvePubKeyBytes(publicKey);
        ECPublicKeyParameters pubKey = new ECPublicKeyParameters(pubKeyPoint,domainParams);
        ParametersWithID param = new ParametersWithID(pubKey,ID.getBytes());

        return verify(data,param,signature);
    }

    private static boolean verify(byte[] data, CipherParameters param, byte[] signature){


        SM2Signer verifier = new SM2Signer();

        // To get Z_A and prepare parameters
        verifier.init(false,param);
        // To fill the whole message
        verifier.update(data,0,data.length);
        // To verify the signature

        byte[] rBytes = new byte[R_SIZE];
        byte[] sBytes = new byte[S_SIZE];
        System.arraycopy(signature,0,rBytes,0,R_SIZE);
        System.arraycopy(signature,R_SIZE,sBytes,0,S_SIZE);

        BigInteger r = new BigInteger(1,rBytes);
        BigInteger s = new BigInteger(1,sBytes);
        byte[] encodedSignature = new byte[0];
        try {
            encodedSignature = new DERSequence(new ASN1Encodable[] { new ASN1Integer(r), new ASN1Integer(s)}).getEncoded();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return verifier.verifySignature(encodedSignature);
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

        SecureRandom random = new SecureRandom();
        return encrypt(plainBytes,publicKey,random);
    }

    public static byte[] encrypt(byte[] plainBytes, byte[] publicKey, SecureRandom random){

        ECPoint pubKeyPoint = resolvePubKeyBytes(publicKey);
        ECPublicKeyParameters pubKey = new ECPublicKeyParameters(pubKeyPoint,domainParams);
        ParametersWithRandom param = new ParametersWithRandom(pubKey,random);

        SM2Engine encryptor = new SM2Engine();

        // To prepare parameters
        encryptor.init(true,param);

        // To generate the twisted ciphertext c1c2c3. 
        // The latest standard specification indicates that the correct ordering is c1c3c2
        byte[] c1c2c3 = new byte[0];
        try {
            c1c2c3 = encryptor.processBlock(plainBytes,0,plainBytes.length);
        } catch (InvalidCipherTextException e) {
            e.printStackTrace();
        }

        // get correct output c1c3c2 from c1c2c3
        byte[] c1c3c2 = new byte[c1c2c3.length];
        System.arraycopy(c1c2c3,0,c1c3c2,0,POINT_SIZE);
        System.arraycopy(c1c2c3,POINT_SIZE,c1c3c2,POINT_SIZE + SM3DIGEST_LENGTH, plainBytes.length);
        System.arraycopy(c1c2c3,POINT_SIZE + plainBytes.length, c1c3c2,POINT_SIZE,SM3DIGEST_LENGTH);

        return c1c3c2;
    }

    /**
     * decryption
     *
     * @param cipherBytes ciphertext
     * @param privateKey private key
     * @return plaintext
     */
    public static byte[] decrypt(byte[] cipherBytes, byte[] privateKey){


        // To get c1c2c3 from ciphertext whose ordering is c1c3c2
        byte[] c1c2c3 = new byte[cipherBytes.length];
        System.arraycopy(cipherBytes,0,c1c2c3,0,POINT_SIZE);
        System.arraycopy(cipherBytes,POINT_SIZE,c1c2c3,c1c2c3.length-SM3DIGEST_LENGTH, SM3DIGEST_LENGTH);
        System.arraycopy(cipherBytes,SM3DIGEST_LENGTH + POINT_SIZE,c1c2c3,POINT_SIZE,c1c2c3.length-SM3DIGEST_LENGTH-POINT_SIZE);

        ECPrivateKeyParameters privKey = new ECPrivateKeyParameters(new BigInteger(1,privateKey),domainParams);

        SM2Engine decryptor = new SM2Engine();

        // To prepare parameters
        decryptor.init(false,privKey);

        // To output the plaintext
        try {
            return decryptor.processBlock(c1c2c3,0,c1c2c3.length);
        } catch (InvalidCipherTextException e) {
            throw new com.jd.blockchain.crypto.CryptoException(e.getMessage(), e);
        }
    }

    // To convert BigInteger to byte[] whose length is 32
    private static byte[] BigIntegerTo32Bytes(BigInteger b){
        byte[] tmp = b.toByteArray();
        byte[] result = new byte[32];
        if (tmp.length > result.length)
            System.arraycopy(tmp, tmp.length-result.length, result, 0, result.length);
        else System.arraycopy(tmp,0,result,result.length-tmp.length,tmp.length);
        return result;
    }

    // To retrieve the public key point from publicKey in byte array mode
    private static ECPoint resolvePubKeyBytes(byte[] publicKey){

        byte[] pubKeyX = new byte[COORDS_SIZE];
        byte[] pubKeyY = new byte[COORDS_SIZE];
        System.arraycopy(publicKey,1,pubKeyX,0,COORDS_SIZE);
        System.arraycopy(publicKey,1+COORDS_SIZE,pubKeyY,0,COORDS_SIZE);

        return curve.createPoint(new BigInteger(1,pubKeyX), new BigInteger(1,pubKeyY));
    }
    public static ECCurve getCurve(){return curve;}
    public static ECDomainParameters getDomainParams(){return domainParams;}
}



