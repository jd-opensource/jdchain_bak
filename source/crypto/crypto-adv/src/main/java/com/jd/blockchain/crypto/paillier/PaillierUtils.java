package com.jd.blockchain.crypto.paillier;

import com.jd.blockchain.utils.io.BytesUtils;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;

import java.math.BigInteger;
import java.security.SecureRandom;

import static org.bouncycastle.util.BigIntegers.ONE;

/**
 * @author zhanglin33
 * @title: PaillierUtils
 * @description: encryption, decryption, homomorphic addition and scalar multiplication in Paillier algorithm
 * @date 2019-04-30, 14:49
 */
public class PaillierUtils {

    private static final int MODULUS_LENGTH = 256;
    private static final int MODULUSSQUARED_LENGTH = 512;

    private static final int P_LENGTH = 128;
    private static final int PSQUARED_LENGTH = 256;
    private static final int Q_LENGTH = 128;
    private static final int QSQUARED_LENGTH = 256;
    private static final int PINVERSE_LENGTH = 128;
    private static final int MUP_LENGTH = 128;
    private static final int MUQ_LENGTH = 128;

    private static final int PRIVKEY_LENGTH = P_LENGTH + PSQUARED_LENGTH + Q_LENGTH + QSQUARED_LENGTH
            + PINVERSE_LENGTH + MUP_LENGTH + MUQ_LENGTH;

    public static AsymmetricCipherKeyPair generateKeyPair(){
        PaillierKeyPairGenerator generator = new PaillierKeyPairGenerator();
        return generator.generateKeyPair();
    }

    public static byte[] encrypt(byte[] plainBytes, byte[] publicKey) {
        PaillierPublicKeyParameters pubKeyParams = bytes2PubKey(publicKey);
        return encrypt(plainBytes, pubKeyParams);
    }

    public static byte[] encrypt(byte[] plainBytes, PaillierPublicKeyParameters pubKeyParams) {
        SecureRandom random = new SecureRandom();
        return encrypt(plainBytes, pubKeyParams, random);
    }

    // c = g^m * r^n mod n^2 = (1+n)^m * r^n mod n^2 = (1 + n*m mod n^2) * r^n mod n^2
    public static byte[] encrypt(byte[] plainBytes, PaillierPublicKeyParameters pubKeyParams, SecureRandom random){

        BigInteger n = pubKeyParams.getModulus();
        BigInteger nSquared = pubKeyParams.getModulusSquared();

        BigInteger m = new BigInteger(1,plainBytes);
        BigInteger r = new BigInteger(n.bitLength(), random);

        BigInteger rawCiphertext = n.multiply(m).add(ONE).mod(nSquared);
        BigInteger c = r.modPow(n, nSquared).multiply(rawCiphertext).mod(nSquared);

        return bigIntegerToBytes(c, MODULUSSQUARED_LENGTH);
    }


    public static byte[] decrypt(byte[] cipherBytes, byte[] privateKey) {
        PaillierPrivateKeyParameters privKeyParams = bytes2PrivKey(privateKey);
        return decrypt(cipherBytes,privKeyParams);
    }
    // m mod p = L(c^(p-1) mod p^2) * muP mod p
    // m mod q = L(c^(q-1) mod q^2) * muQ mod q
    // m = (m mod p) * (1/q mod p) * q + (m mod q) * (1/p mod q) * p
    //   = ((m mod q)-(m mod p)) * (1/p mod q) mod q * p + (m mod p)
    public static byte[] decrypt(byte[] cipherBytes, PaillierPrivateKeyParameters privKeyParams){

        BigInteger cihphertext = new BigInteger(1, cipherBytes);

        BigInteger p = privKeyParams.getP();
        BigInteger pSquared = privKeyParams.getPSquared();
        BigInteger q = privKeyParams.getQ();
        BigInteger qSquared = privKeyParams.getQSquared();
        BigInteger pInverse = privKeyParams.getPInverse();
        BigInteger muP = privKeyParams.getMuP();
        BigInteger muQ = privKeyParams.getMuQ();

        BigInteger mModP =
                privKeyParams.lFunction(cihphertext.modPow(p.subtract(ONE),pSquared),p).multiply(muP).mod(p);
        BigInteger mModQ =
                privKeyParams.lFunction(cihphertext.modPow(q.subtract(ONE),qSquared),q).multiply(muQ).mod(q);

        BigInteger midValue = mModQ.subtract(mModP).multiply(pInverse).mod(q);
        BigInteger m = midValue.multiply(p).add(mModP);

        return m.toByteArray();
    }


    public static byte[] pubKey2Bytes(PaillierPublicKeyParameters pubKeyParams) {
        BigInteger n = pubKeyParams.getModulus();
        return bigIntegerToBytes(n, MODULUS_LENGTH);
    }

    public static PaillierPublicKeyParameters bytes2PubKey(byte[] publicKey) {

        if (publicKey.length != MODULUS_LENGTH) {
            throw new IllegalArgumentException("publicKey's length does not meet algorithm's requirement!");
        }

        BigInteger n = new BigInteger(1, publicKey);
        return new PaillierPublicKeyParameters(n);
    }


    public static byte[] privKey2Bytes(PaillierPrivateKeyParameters privKeyParams) {

        BigInteger p        = privKeyParams.getP();
        BigInteger pSquared = privKeyParams.getPSquared();
        BigInteger q        = privKeyParams.getQ();
        BigInteger qSquared = privKeyParams.getQSquared();
        BigInteger pInverse = privKeyParams.getPInverse();
        BigInteger muP      = privKeyParams.getMuP();
        BigInteger muQ      = privKeyParams.getMuQ();

        byte[] pBytes        = bigIntegerToBytes(p, P_LENGTH);
        byte[] pSquaredBytes = bigIntegerToBytes(pSquared, PSQUARED_LENGTH);
        byte[] qBytes        = bigIntegerToBytes(q, Q_LENGTH);
        byte[] qSquaredBytes = bigIntegerToBytes(qSquared, QSQUARED_LENGTH);
        byte[] pInverseBytes = bigIntegerToBytes(pInverse, PINVERSE_LENGTH);
        byte[] muPBytes      = bigIntegerToBytes(muP, MUP_LENGTH);
        byte[] muQBytes      = bigIntegerToBytes(muQ, MUQ_LENGTH);

        return BytesUtils.concat(pBytes,pSquaredBytes,qBytes,qSquaredBytes,pInverseBytes,muPBytes,muQBytes);
    }

    public static PaillierPrivateKeyParameters bytes2PrivKey(byte[] privateKey) {

        if (privateKey.length != PRIVKEY_LENGTH) {
            throw new IllegalArgumentException("privateKey's length does not meet algorithm's requirement!");
        }

        byte[] pBytes        = new byte[P_LENGTH];
        byte[] pSquaredBytes = new byte[PSQUARED_LENGTH];
        byte[] qBytes        = new byte[Q_LENGTH];
        byte[] qSquaredBytes = new byte[QSQUARED_LENGTH];
        byte[] pInverseBytes = new byte[PINVERSE_LENGTH];
        byte[] muPBytes      = new byte[MUP_LENGTH];
        byte[] muQBytes      = new byte[MUQ_LENGTH];

        split(privateKey,pBytes,pSquaredBytes,qBytes,qSquaredBytes,pInverseBytes,muPBytes,muQBytes);

        BigInteger p        = new BigInteger(1, pBytes);
        BigInteger pSquared = new BigInteger(1, pSquaredBytes);
        BigInteger q        = new BigInteger(1, qBytes);
        BigInteger qSquared = new BigInteger(1, qSquaredBytes);
        BigInteger pInverse = new BigInteger(1, pInverseBytes);
        BigInteger muP      = new BigInteger(1, muPBytes);
        BigInteger muQ      = new BigInteger(1, muQBytes);

        return new PaillierPrivateKeyParameters(p,pSquared,q,qSquared,pInverse,muP,muQ);
    }

    public static byte[] add(byte[] publicKey, byte[]... ciphertexts) {
        PaillierPublicKeyParameters pubKeyParams = bytes2PubKey(publicKey);
        return add(pubKeyParams,ciphertexts);
    }

    public static byte[] add(PaillierPublicKeyParameters pubKeyParams, byte[]... ciphertexts) {

        BigInteger result = ONE;
        BigInteger multiplier;
        BigInteger nSquared = pubKeyParams.getModulusSquared();
        for (byte[] each: ciphertexts) {
            multiplier = new BigInteger(1, each);
            result = result.multiply(multiplier).mod(nSquared);
        }

        return bigIntegerToBytes(result, MODULUSSQUARED_LENGTH);
    }

    public static byte[] scalarMultiply(byte[] publicKey, byte[] cipherBytes, long scalar) {
        PaillierPublicKeyParameters pubKeyParams = bytes2PubKey(publicKey);
        return scalarMultiply(pubKeyParams,cipherBytes,scalar);
    }

    public static byte[] scalarMultiply(PaillierPublicKeyParameters pubKeyParams, byte[] cipherBytes, long scalar) {

        BigInteger nSquared  = pubKeyParams.getModulusSquared();
        BigInteger cihertext = new BigInteger(1, cipherBytes);
        BigInteger exponent  = BigInteger.valueOf(scalar);

        BigInteger result = cihertext.modPow(exponent,nSquared);

        return bigIntegerToBytes(result, MODULUSSQUARED_LENGTH);
    }

    // To convert BigInteger to byte array in specified size
    private static byte[] bigIntegerToBytes(BigInteger b, int bytesSize){
        byte[] tmp = b.toByteArray();
        byte[] result = new byte[bytesSize];
        if (tmp.length > result.length) {
            System.arraycopy(tmp, tmp.length - result.length, result, 0, result.length);
        }
        else {
            System.arraycopy(tmp,0,result,result.length-tmp.length,tmp.length);
        }
        return result;
    }

    private static void split(byte[] src, byte[]... bytesList) {

        int srcPos = 0;
        for (byte[] each: bytesList){
            System.arraycopy(src,srcPos,each,0,each.length);
            srcPos += each.length;
            if (srcPos >= src.length){
                break;
            }
        }
    }
}
