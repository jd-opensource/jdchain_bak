package com.jd.blockchain.crypto.utils.classic;

import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.*;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECMultiplier;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * @author zhanglin33
 * @title: ECDSAUtils
 * @description: ECDSA signature algorithm based on Curve secp256k1 with SHA256
 * @date 2019-03-25, 17:21
 */
public class ECDSAUtils {

    private static final int R_SIZE =32;
    private static final int S_SIZE =32;

    // p = 2^256 - 2^32 - 2^9 - 2^8 - 2^7 - 2^6 - 2^4 - 1
    // the curve equation is y^2 = x^3 + 7.
    private static final X9ECParameters X9_PARAMS = SECNamedCurves.getByName("secp256k1");
    private static final ECCurve CURVE = X9_PARAMS.getCurve();
    private static final ECPoint ECDSA_G = X9_PARAMS.getG();
    private static final BigInteger ECDSA_H = X9_PARAMS.getH();
    private static final BigInteger ECDSA_N = X9_PARAMS.getN();
    private static final ECDomainParameters DOMAIN_PARAMS = new ECDomainParameters(CURVE, ECDSA_G,ECDSA_N,ECDSA_H);


    //-----------------Key Generation Algorithm-----------------

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

        ECKeyGenerationParameters keyGenerationParams = new ECKeyGenerationParameters(DOMAIN_PARAMS,random);
        ECKeyPairGenerator keyPairGenerator = new ECKeyPairGenerator();

        // To generate the key pair
        keyPairGenerator.init(keyGenerationParams);
        return keyPairGenerator.generateKeyPair();
    }

    /**
     * public key retrieval
     *
     * @param privateKey private key
     * @return publicKey
     */
    public static byte[] retrievePublicKey(byte[] privateKey) {
        ECMultiplier createBasePointMultiplier = new FixedPointCombMultiplier();
        ECPoint publicKeyPoint = createBasePointMultiplier.multiply(DOMAIN_PARAMS.getG(), new BigInteger(1,privateKey)).normalize();
        return publicKeyPoint.getEncoded(false);
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
        ECPrivateKeyParameters privKey = new ECPrivateKeyParameters(new BigInteger(1,privateKey), DOMAIN_PARAMS);
        CipherParameters params = new ParametersWithRandom(privKey,random);

        return sign(data,params);
    }

    public static byte[] sign(byte[] data, byte[] privateKey, SecureRandom random, String ID){

        ECPrivateKeyParameters privKey = new ECPrivateKeyParameters(new BigInteger(1,privateKey), DOMAIN_PARAMS);
        CipherParameters params = new ParametersWithID(new ParametersWithRandom(privKey,random),ID.getBytes());

        return sign(data,params);
    }

    public static byte[] sign(byte[] data, CipherParameters params){

        byte[] hashedMsg = SHA256Utils.hash(data);

        ECDSASigner signer = new ECDSASigner();
        signer.init(true, params);
        BigInteger[] signature = signer.generateSignature(hashedMsg);

//        // To decode the signature
//        ASN1Sequence sig = ASN1Sequence.getInstance(encodedSignature);
//        byte[] rBytes = BigIntegerTo32Bytes(ASN1Integer.getInstance(sig.getObjectAt(0)).getValue());
//        byte[] sBytes = BigIntegerTo32Bytes(ASN1Integer.getInstance(sig.getObjectAt(1)).getValue());

        byte[] rBytes = BigIntegerTo32Bytes(signature[0]);
        byte[] sBytes = BigIntegerTo32Bytes(signature[1]);

        byte[] result = new byte[R_SIZE + S_SIZE];
        System.arraycopy(rBytes,0,result,0,R_SIZE);
        System.arraycopy(sBytes,0,result,R_SIZE,S_SIZE);

        return result;
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

        ECPoint pubKeyPoint = resolvePubKeyBytes(publicKey);
        ECPublicKeyParameters pubKey = new ECPublicKeyParameters(pubKeyPoint, DOMAIN_PARAMS);

        return verify(data,pubKey,signature);
    }

    public static boolean verify(byte[] data, CipherParameters params, byte[] signature){

        byte[] hashedMsg = SHA256Utils.hash(data);

        byte[] rBytes = new byte[R_SIZE];
        byte[] sBytes = new byte[S_SIZE];
        System.arraycopy(signature,0,rBytes,0,R_SIZE);
        System.arraycopy(signature,R_SIZE,sBytes,0,S_SIZE);

        BigInteger r = new BigInteger(1,rBytes);
        BigInteger s = new BigInteger(1,sBytes);

        ECDSASigner verifier = new ECDSASigner();
        verifier.init(false,params);
        return verifier.verifySignature(hashedMsg,r,s);
    }


    // To convert BigInteger to byte[] whose length is 32
    private static byte[] BigIntegerTo32Bytes(BigInteger b){
        byte[] tmp = b.toByteArray();
        byte[] result = new byte[32];
        if (tmp.length > result.length) {
            System.arraycopy(tmp, tmp.length - result.length, result, 0, result.length);
        }
        else {
            System.arraycopy(tmp,0,result,result.length-tmp.length,tmp.length);
        }
        return result;
    }

    // To retrieve the public key point from publicKey in byte array mode
    private static ECPoint resolvePubKeyBytes(byte[] publicKey){
        return CURVE.decodePoint(publicKey);
    }

    public static ECCurve getCurve(){return CURVE;}

    public static ECDomainParameters getDomainParams(){return DOMAIN_PARAMS;}
}
