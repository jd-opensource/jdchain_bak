package com.jd.blockchain.crypto.mpc;

import com.jd.blockchain.crypto.CryptoException;
import com.jd.blockchain.crypto.elgamal.ElGamalUtils;
import com.jd.blockchain.utils.io.BytesUtils;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.params.ElGamalPrivateKeyParameters;
import org.bouncycastle.crypto.params.ElGamalPublicKeyParameters;

import java.math.BigInteger;
import java.util.Arrays;

public class EqualVerify {

    private static final int ELEMENTLENGTH = 64;

    private static BigInteger p;

    private static byte[] sponsorEPubKeyBytes;
    private static byte[] sponsorEPrivKeyBytes;

    private static byte[] responderEPubKeyBytes;
    private static byte[] responderEPrivKeyBytes;

    public static void generateParams(){
        p = ElGamalUtils.getElGamalParameters().getP();
    }

    public static void generateSponsorKeyPair(){

        AsymmetricCipherKeyPair keyPair = ElGamalUtils.generateKeyPair();
        ElGamalPublicKeyParameters pubKeyParams = (ElGamalPublicKeyParameters) keyPair.getPublic();
        ElGamalPrivateKeyParameters privKeyParams = (ElGamalPrivateKeyParameters) keyPair.getPrivate();

        sponsorEPubKeyBytes  = bigIntegerTo64Bytes(pubKeyParams.getY());
        sponsorEPrivKeyBytes = bigIntegerTo64Bytes(privKeyParams.getX());
    }

    public static void generateResponderKeyPair(){

        AsymmetricCipherKeyPair keyPair = ElGamalUtils.generateKeyPair();
        ElGamalPublicKeyParameters pubKeyParams = (ElGamalPublicKeyParameters) keyPair.getPublic();
        ElGamalPrivateKeyParameters privKeyParams = (ElGamalPrivateKeyParameters) keyPair.getPrivate();

        responderEPubKeyBytes  = bigIntegerTo64Bytes(pubKeyParams.getY());
        responderEPrivKeyBytes = bigIntegerTo64Bytes(privKeyParams.getX());
    }

    public static byte[] sponsor(int sponsorInput, byte[] sponsorEPubKeyBytes){

        BigInteger sponsorBigInt = BigInteger.valueOf(sponsorInput);
        BigInteger sponsorEPubKey = new BigInteger(1, sponsorEPubKeyBytes);
        BigInteger result = sponsorBigInt.multiply(sponsorEPubKey).mod(p);
        return bigIntegerTo64Bytes(result);
    }

    public static byte[] responder(int responderInput, byte[] sponsorOutput, byte[] responderEPubKeyBytes,
                                   byte[] responderEPrivKeyBytes) {

        if (sponsorOutput.length != ELEMENTLENGTH) {
            throw new CryptoException("The sponsorOutput' length is not 64!");
        }

        BigInteger responderBigInt = BigInteger.valueOf(responderInput);
        BigInteger responderEPubKey = new BigInteger(1,responderEPubKeyBytes);
        BigInteger responderCipher = responderBigInt.multiply(responderEPubKey).mod(p);

        BigInteger responderInputInverse = BigInteger.valueOf(responderInput).modInverse(p);
        BigInteger tmp = new BigInteger(1, sponsorOutput).multiply(responderInputInverse).mod(p);
        BigInteger dhValue = tmp.modPow(new BigInteger(1,responderEPrivKeyBytes), p);

        return BytesUtils.concat(bigIntegerTo64Bytes(responderCipher), bigIntegerTo64Bytes(dhValue));
    }

    public static boolean sponsorCheck(int sponsorInput, byte[] responderOutput, byte[] sponsorEPrivKeyBytes){

        if (responderOutput.length != 2 * ELEMENTLENGTH) {
            throw new CryptoException("The responderOutput's length is not 128!");
        }

        byte[] responderCipherBytes = new byte[ELEMENTLENGTH];
        byte[] dhValueBytes = new byte[ELEMENTLENGTH];
        System.arraycopy(responderOutput, 0, responderCipherBytes, 0, responderCipherBytes.length);
        System.arraycopy(responderOutput, responderCipherBytes.length, dhValueBytes, 0,dhValueBytes.length);

        BigInteger sponsorInputInverse = BigInteger.valueOf(sponsorInput).modInverse(p);
        BigInteger tmp = new BigInteger(1, responderCipherBytes).multiply(sponsorInputInverse);
        BigInteger dhVerifier = tmp.modPow(new BigInteger(1,sponsorEPrivKeyBytes), p);

        return Arrays.equals(dhValueBytes, bigIntegerTo64Bytes(dhVerifier));
    }

    public static byte[] getSponsorEPubKeyBytes(){return sponsorEPubKeyBytes;}

    public static byte[] getSponsorEPrivKeyBytes(){return sponsorEPrivKeyBytes;}

    public static byte[] getResponderEPubKeyBytes(){return responderEPubKeyBytes;}

    public static byte[] getResponderEPrivKeyBytes(){return responderEPrivKeyBytes;}

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
}
