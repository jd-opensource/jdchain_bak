package com.jd.blockchain.crypto.mpc;

import java.math.BigInteger;

import com.jd.blockchain.crypto.paillier.PaillierPublicKeyParameters;
import com.jd.blockchain.crypto.paillier.PaillierUtils;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.agreement.ECDHBasicAgreement;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

import com.jd.blockchain.crypto.utils.sm.SM2Utils;
import com.jd.blockchain.crypto.utils.sm.SM3Utils;
import com.jd.blockchain.utils.io.BytesUtils;

public class MultiSum {

    private static byte[] ePrivKey;
    private static byte[] ePubKey;
    private static ECCurve curve;
    private static ECDomainParameters domainParams;

    public static void generateEphemeralKeyPair(){
        AsymmetricCipherKeyPair eKeyPair = SM2Utils.generateKeyPair();
        ECPrivateKeyParameters ecPrivKey = (ECPrivateKeyParameters) eKeyPair.getPrivate();
        ECPublicKeyParameters ecPubKey= (ECPublicKeyParameters) eKeyPair.getPublic();
        ePrivKey = bigIntegerToBytes(ecPrivKey.getD());
        ePubKey  = ecPubKey.getQ().getEncoded(false);
        curve = SM2Utils.getCurve();
        domainParams = SM2Utils.getDomainParams();
    }

    public static byte[] calculateAgreement(byte[] otherEPubKey, byte[] ePrivKey){
        ECDHBasicAgreement basicAgreement = new ECDHBasicAgreement();
        ECPoint ePubKeyPoint = resolvePubKeyBytes(otherEPubKey);
        ECPublicKeyParameters pubKey = new ECPublicKeyParameters(ePubKeyPoint, domainParams);
        ECPrivateKeyParameters privateKey = new ECPrivateKeyParameters(new BigInteger(1,ePrivKey), domainParams);

        basicAgreement.init(privateKey);
        BigInteger agreement = basicAgreement.calculateAgreement(pubKey);
        return bigIntegerToBytes(agreement);
    }

    public static byte[] deriveShares(byte[] frontID, byte[] rearID, byte[] agreementBytes){
        byte[] inputBytes = BytesUtils.concat(frontID,rearID,agreementBytes);
        return SM3Utils.hash(inputBytes);
    }

    public static byte[] encryptBlindedMsg(byte[] paillierPubKey, int input, byte[] frontShare, byte[] rearShare){
        BigInteger integer = BigInteger.valueOf(input);
        BigInteger frontInteger = new BigInteger(1,frontShare);
        BigInteger rearInteger = new BigInteger(1,rearShare);
        PaillierPublicKeyParameters encKey = PaillierUtils.bytes2PubKey(paillierPubKey);
        BigInteger modulus = encKey.getModulus();
        BigInteger plaintext = integer.add(frontInteger).subtract(rearInteger).mod(modulus);
        return PaillierUtils.encrypt(plaintext.toByteArray(),encKey);
    }

    public static byte[] aggregateCiphertexts(byte[] paillierPubKey, byte[]... ciphertexts){
        return PaillierUtils.add(paillierPubKey,ciphertexts);
    }

    public static byte[] decrypt(byte[] paillierPrivKey, byte[] ciphertext){
        return PaillierUtils.decrypt(ciphertext,paillierPrivKey);
    }

    public static byte[] getEPubKey(){return ePubKey;}

    public static byte[] getEPrivKey(){return ePrivKey;}


//    public byte[] getEPubKeyBytes(){
//        byte[] ePubKeyBytes = new byte[65];
//        byte[] ePubKeyBytesX = ePubKey.getQ().getAffineXCoord().getEncoded();
//        byte[] ePubKeyBytesY = ePubKey.getQ().getAffineYCoord().getEncoded();
//        System.arraycopy(Hex.decode("04"),0,ePubKeyBytes,0,1);
//        System.arraycopy(ePubKeyBytesX,0,ePubKeyBytes,1,32);
//        System.arraycopy(ePubKeyBytesY,0,ePubKeyBytes,1+32,32);
//        return ePubKeyBytes;
//    }
//
//    public byte[] getEPrivKeyBytes(){
//        return bigIntegerToBytes(ePrivKey.getD());
//    }

    public ECPublicKeyParameters resolveEPubKey(byte[] ePubKeyBytes){
        byte[] ePubKeyX = new byte[32];
        byte[] ePubKeyY = new byte[32];
        System.arraycopy(ePubKeyBytes,1,ePubKeyX,0,32);
        System.arraycopy(ePubKeyBytes,1+32,ePubKeyY,0,32);
        ECPoint ePubKeyPoint = curve.createPoint(new BigInteger(1,ePubKeyX), new BigInteger(1,ePubKeyY));
        return new ECPublicKeyParameters(ePubKeyPoint,domainParams);
    }

    public ECPrivateKeyParameters resolveEPrivKey(byte[] ePrivKeyBytes){
        return new ECPrivateKeyParameters(new BigInteger(1,ePrivKeyBytes),domainParams);
    }

    // To convert BigInteger to byte[] whose length is l
    private static byte[] bigIntegerToBytes(BigInteger b){
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
        return curve.decodePoint(publicKey);
    }

}
