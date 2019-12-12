package com.jd.blockchain.crypto.mpc;

import com.jd.blockchain.crypto.CryptoException;
import com.jd.blockchain.crypto.elgamal.ElGamalUtils;
import com.jd.blockchain.utils.io.BytesUtils;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.params.ElGamalParameters;
import org.bouncycastle.crypto.params.ElGamalPrivateKeyParameters;
import org.bouncycastle.crypto.params.ElGamalPublicKeyParameters;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

public class IntCompare {

    private static final int INTLENGTH = 32;
    private static final int ELEMENTLENGTH = 64;
    private static final int CIPHERLENGTH = 128;

    private static BigInteger p;

    private static byte[] pubKeyBytes;
    private static byte[] privKeyBytes;

    public static void generateKeyPair(){

        ElGamalParameters dhParams = ElGamalUtils.getElGamalParameters();

        p = dhParams.getP();

        AsymmetricCipherKeyPair keyPair = ElGamalUtils.generateKeyPair();
        ElGamalPublicKeyParameters pubKeyParams = (ElGamalPublicKeyParameters) keyPair.getPublic();
        ElGamalPrivateKeyParameters privKeyParams = (ElGamalPrivateKeyParameters) keyPair.getPrivate();

        pubKeyBytes = bigIntegerTo64Bytes(pubKeyParams.getY());
        privKeyBytes = bigIntegerTo64Bytes(privKeyParams.getX());
    }

    public static byte[][] sponsor(int sponsorInput, byte[] pubKeyBytes){

        String sponsorBinaryStr = to32BinaryString(sponsorInput);


        byte[][] cipherArray  = new byte[INTLENGTH * 2][CIPHERLENGTH];

        byte[] unitMsg = bigIntegerTo64Bytes(BigInteger.ONE);
        byte[] randMsg = new byte[ELEMENTLENGTH - 1];

        int i;
        for (i = 0; i < INTLENGTH; i++){

            SecureRandom random = new SecureRandom();
            random.nextBytes(randMsg);

            if (sponsorBinaryStr.charAt(i) == '1'){
                cipherArray[i]             = ElGamalUtils.encrypt(unitMsg, pubKeyBytes);
                cipherArray[i + INTLENGTH] = ElGamalUtils.encrypt(randMsg, pubKeyBytes);
            }
            else {
                cipherArray[i]             = ElGamalUtils.encrypt(randMsg, pubKeyBytes);
                cipherArray[i + INTLENGTH] = ElGamalUtils.encrypt(unitMsg, pubKeyBytes);
            }
        }

        return cipherArray;
    }

    public static byte[][] responder(int responderInt, byte[][] cipherArray, byte[] pubKeyBytes){

        if (cipherArray.length != 2 * INTLENGTH) {
            throw new CryptoException("The cipherArray has wrong format!");
        }

        int i,j;
        for (i = 0; i < cipherArray.length; i++){
            if(cipherArray[i].length != CIPHERLENGTH) {
                throw new CryptoException("The cipherArray has wrong format!");
            }
        }

        String[] responderStrSet = encoding(responderInt, false);

        BigInteger tmpLeftBigInteger;
        BigInteger tmpRightBigInteger;
        BigInteger leftBigInteger;
        BigInteger rightBigInteger;
        byte[] tmpCipherArray = new byte[CIPHERLENGTH];

        byte[] randMsg = new byte[ELEMENTLENGTH -1 ];

        byte[][] aggregatedCipherArray = new byte[INTLENGTH][CIPHERLENGTH];

        for (i = 0; i < aggregatedCipherArray.length; i++){

            if (responderStrSet[i] != null){

                tmpLeftBigInteger  = BigInteger.ONE;
                tmpRightBigInteger = BigInteger.ONE;

                for (j = 0; j < responderStrSet[i].length(); j++){
                    if (responderStrSet[i].charAt(j) == '1'){
                        System.arraycopy(cipherArray[j], 0, tmpCipherArray, 0, tmpCipherArray.length);
                    }
                    else{
                        System.arraycopy(cipherArray[j + INTLENGTH], 0,tmpCipherArray, 0, tmpCipherArray.length);
                    }

                    leftBigInteger     = getLeftBigIntegerFrom128Bytes(tmpCipherArray);
                    tmpLeftBigInteger  = tmpLeftBigInteger.multiply(leftBigInteger).mod(p);
                    rightBigInteger    = getRightBigIntegerFrom128Bytes(tmpCipherArray);
                    tmpRightBigInteger = tmpRightBigInteger.multiply(rightBigInteger).mod(p);
                }

                aggregatedCipherArray[i] = BytesUtils.concat(bigIntegerTo64Bytes(tmpLeftBigInteger),bigIntegerTo64Bytes(tmpRightBigInteger));
            }
            else {
                SecureRandom random = new SecureRandom();
                random.nextBytes(randMsg);
                aggregatedCipherArray[i] = ElGamalUtils.encrypt(randMsg, pubKeyBytes);
            }
        }

        int[] permutation = randPermute(INTLENGTH);

        for (i = 0; i < INTLENGTH; i++){
            System.arraycopy(aggregatedCipherArray[i], 0, tmpCipherArray, 0, CIPHERLENGTH);
            System.arraycopy(aggregatedCipherArray[permutation[i]-1], 0, aggregatedCipherArray[i], 0, CIPHERLENGTH);
            System.arraycopy(tmpCipherArray, 0, aggregatedCipherArray[permutation[i]-1], 0, CIPHERLENGTH);
        }

        return aggregatedCipherArray;
    }

    public static int sponsorOutput(byte[][] aggregatedCipherArray, byte[] privKeyBytes){

        if (aggregatedCipherArray.length != INTLENGTH) {
            throw new CryptoException("The aggregatedCipherArray has wrong format!");
        }

        int i;
        byte[] plaintext;

        for (i = 0; i < aggregatedCipherArray.length; i++){

            if(aggregatedCipherArray[i].length != CIPHERLENGTH) {
                throw new CryptoException("The aggregatedCipherArray has wrong format!");
            }

            plaintext = ElGamalUtils.decrypt(aggregatedCipherArray[i], privKeyBytes);

            if ((plaintext.length ==1) && (BigInteger.ONE.equals(BigInteger.valueOf((int) plaintext[0])))){
                return 1;
            }
        }

        return 0;
    }

    public static byte[] getPubKeyBytes(){return pubKeyBytes;}

    public static byte[] getPrivKeyBytes(){return privKeyBytes;}



    private static String[] encoding(int integer, boolean encodingOpts){
        String str = to32BinaryString(integer);
        String[] strArray = new String[INTLENGTH];
        int i;

        // 1-encoding
        if (encodingOpts){
            for (i = 0; i < INTLENGTH; i++){
                if (str.charAt(i) == '1'){
                    strArray[i] = str.substring(0, i + 1);
                }
            }
        }
        // 0-encoding
        else {
            for (i = 0; i < INTLENGTH; i++) {
                if (str.charAt(i) == '0') {
                    strArray[i] = str.substring(0, i) + "1";
                }
            }
        }

        return strArray;
    }


    private static String to32BinaryString(int integer) {

        if (integer < 0) {
            throw new CryptoException("integer must be non-negative!");
        }

        int i;
        String str = Integer.toBinaryString(integer);
        StringBuilder result = new StringBuilder();
        for (i = 0; i < INTLENGTH - str.length(); i++) {
            result.append("0");
        }
        return result.append(str).toString();
    }

    /**
     * @param min the lower bound (inclusive).  Must be non-negative.
     * @param max the upper bound (inclusive).  Must be positive.
     * @return the next pseudorandom, uniformly distributed {@code int}
     * value between min (inclusive) and max (inclusive)
     * from this random number generator's sequence
     * @throws CryptoException if min is not non-negative,
     *                                  max is not positive, or min is bigger than max
     */
    private static int randInt(int min, int max) {
        if (min < 0) {
            throw new CryptoException("min must be non-negative!");
        }
        if (max <= 0) {
            throw new CryptoException("max must be positive!");
        }
        if (min > max) {
            throw new CryptoException("min must not be greater than max");
        }

        Random random = new Random();
        return random.nextInt(max) % (max - min + 1) + min;
    }

    private static int[] randPermute(int num) {

        int[] array = new int[num];
        int i;
        int rand;
        int tmp;

        for (i = 0; i < num; i++) {
            array[i] = i + 1;
        }

        for (i = 0; i < num; i++) {
            rand = randInt(1, num);
            tmp = array[i];
            array[i] = array[rand - 1];
            array[rand - 1] = tmp;
        }

        return array;
    }

    // To convert BigInteger to byte[] whose length is 64
    private static byte[] bigIntegerTo64Bytes(BigInteger b){
        byte[] tmp = b.toByteArray();
        byte[] result = new byte[64];
        if (tmp.length > result.length) {
            System.arraycopy(tmp, tmp.length - result.length, result, 0, result.length);
        }
        else {
            System.arraycopy(tmp,0,result,result.length-tmp.length,tmp.length);
        }
        return result;
    }

    private static BigInteger getLeftBigIntegerFrom128Bytes(byte[] byteArray){
        if (byteArray.length != 128) {
            throw new CryptoException("The byteArray's length must be 128!");
        }
        byte[] tmp = new byte[64];
        System.arraycopy(byteArray, 0, tmp, 0, tmp.length);
        return new BigInteger(1, tmp);
    }

    private static BigInteger getRightBigIntegerFrom128Bytes(byte[] byteArray){
        if (byteArray.length != 128) {
            throw new CryptoException("The byteArray's length must be 128!");
        }
        byte[] tmp = new byte[64];
        System.arraycopy(byteArray, 64, tmp, 0, tmp.length);
        return new BigInteger(1, tmp);
    }
}


