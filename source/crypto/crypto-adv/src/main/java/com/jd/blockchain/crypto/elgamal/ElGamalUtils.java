package com.jd.blockchain.crypto.elgamal;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.engines.ElGamalEngine;
import org.bouncycastle.crypto.generators.ElGamalKeyPairGenerator;
import org.bouncycastle.crypto.params.*;

import java.math.BigInteger;
import java.security.SecureRandom;

public class ElGamalUtils {

    private static final BigInteger g512 = new BigInteger("153d5d6172adb43045b68ae8e1de1070b6137005686d29d3d73a7749199681ee5b212c9b96bfdcfa5b20cd5e3fd2044895d609cf9b410b7a0f12ca1cb9a428cc", 16);
    private static final BigInteger p512 = new BigInteger("9494fec095f3b85ee286542b3836fc81a5dd0a0349b4c239dd38744d488cf8e31db8bcb7d33b41abb9e5a33cca9144b1cef332c94bf0573bf047a3aca98cdf3b", 16);

    private static final ElGamalParameters dhParams = new ElGamalParameters(p512, g512, 0);



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

        ElGamalKeyGenerationParameters params = new ElGamalKeyGenerationParameters(random, dhParams);
        ElGamalKeyPairGenerator kpGen = new ElGamalKeyPairGenerator();

        // To generate the key pair
        kpGen.init(params);
        return kpGen.generateKeyPair();
    }

    public static byte[] retrievePublicKey(byte[] privateKey)
    {
        BigInteger g = dhParams.getG();
        BigInteger p = dhParams.getP();


        BigInteger pubKey = g.modPow(new BigInteger(1,privateKey), p);
        byte[] pubKey2Bytes = pubKey.toByteArray();

        int pubKeySize = (p.bitLength() + 7)/8;
        byte[] result = new byte[pubKeySize];

        if (pubKey2Bytes.length > result.length)
        {
            System.arraycopy(pubKey2Bytes, 1, result, result.length  - (pubKey2Bytes.length - 1), pubKey2Bytes.length - 1);
        }
        else
        {
            System.arraycopy(pubKey2Bytes, 0, result, result.length  - pubKey2Bytes.length, pubKey2Bytes.length);
        }

        return result;
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

        BigInteger pubKey = new BigInteger(1,publicKey);

        ElGamalPublicKeyParameters pubKeyParams = new ElGamalPublicKeyParameters(pubKey,dhParams);
        return encrypt(plainBytes, pubKeyParams, random);
    }

    public static byte[] encrypt(byte[] plainBytes, ElGamalPublicKeyParameters pubKeyParams){

        SecureRandom random = new SecureRandom();
        return encrypt(plainBytes, pubKeyParams, random);
    }

    public static byte[] encrypt(byte[] plainBytes, ElGamalPublicKeyParameters pubKeyParams, SecureRandom random){

        ParametersWithRandom params = new ParametersWithRandom(pubKeyParams, random);

        ElGamalEngine encryptor = new ElGamalEngine();
        encryptor.init(true, params);
        return encryptor.processBlock(plainBytes,0,plainBytes.length);
    }


    public static byte[] decrypt(byte[] cipherBytes, byte[] privateKey){
        BigInteger privKey = new BigInteger(1,privateKey);

        ElGamalPrivateKeyParameters privKeyParams = new ElGamalPrivateKeyParameters(privKey,dhParams);

        ElGamalEngine decryptor = new ElGamalEngine();
        decryptor.init(false,privKeyParams);
        return decryptor.processBlock(cipherBytes,0,cipherBytes.length);
    }

    public static ElGamalParameters getElGamalParameters(){return dhParams;}

}
