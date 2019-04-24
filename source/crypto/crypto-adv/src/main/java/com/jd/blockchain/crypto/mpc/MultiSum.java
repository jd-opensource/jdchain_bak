package com.jd.blockchain.crypto.mpc;

import java.math.BigInteger;

import com.n1analytics.paillier.PaillierPrivateKey;
import com.n1analytics.paillier.PaillierPublicKey;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.agreement.ECDHBasicAgreement;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Hex;

import com.jd.blockchain.crypto.utils.sm.SM2Utils;
import com.jd.blockchain.crypto.utils.sm.SM3Utils;
import com.jd.blockchain.utils.io.BytesUtils;

public class MultiSum {

    private ECPrivateKeyParameters ePrivKey;
    private ECPublicKeyParameters ePubKey;
    private ECCurve curve;
    private ECDomainParameters domainParams;

    public void generateEphemeralKeyPair(){
        AsymmetricCipherKeyPair eKeyPair = SM2Utils.generateKeyPair();
        this.ePrivKey = (ECPrivateKeyParameters) eKeyPair.getPrivate();
        this.ePubKey  = (ECPublicKeyParameters) eKeyPair.getPublic();
        this.curve = SM2Utils.getCurve();
        this.domainParams = SM2Utils.getDomainParams();
    }

    public BigInteger calculateAgreement(CipherParameters otherEPubKey){
        ECDHBasicAgreement basicAgreement = new ECDHBasicAgreement();
        basicAgreement.init(ePrivKey);
        return basicAgreement.calculateAgreement(otherEPubKey);
    }

    public static BigInteger deriveShares(byte[] frontID, byte[] rearID, BigInteger agreement){
        byte[] agreementBytes = agreement.toByteArray();
        byte[] inputBytes = BytesUtils.concat(frontID,rearID,agreementBytes);
        return new BigInteger(1,SM3Utils.hash(inputBytes));
    }

    public static BigInteger encryptBlindedMsg(PaillierPublicKey encKey, BigInteger msg, BigInteger frontShare, BigInteger rearShare){
        BigInteger modulus = encKey.getModulus();
        BigInteger plaintext = msg.add(frontShare).subtract(rearShare).mod(modulus);
        return encKey.raw_encrypt(plaintext);
    }

    public static BigInteger aggregateCiphertexts(PaillierPublicKey encKey, BigInteger... bigIntegers){
        BigInteger aggregatedCiphertext = BigInteger.ONE;
        BigInteger modulusSquared = encKey.getModulusSquared();
        for (BigInteger entry : bigIntegers) {
            aggregatedCiphertext = aggregatedCiphertext.multiply(entry).mod(modulusSquared);
        }
        return aggregatedCiphertext;
    }

    public static BigInteger decrypt(PaillierPrivateKey decKey, BigInteger ciphertext){
        return decKey.raw_decrypt(ciphertext);
    }

    public ECPublicKeyParameters getEPubKey(){return ePubKey;}

    public ECPrivateKeyParameters getEPrivKey(){return ePrivKey;}


    public byte[] getEPubKeyBytes(){
        byte[] ePubKeyBytes = new byte[65];
        byte[] ePubKeyBytesX = ePubKey.getQ().getAffineXCoord().getEncoded();
        byte[] ePubKeyBytesY = ePubKey.getQ().getAffineYCoord().getEncoded();
        System.arraycopy(Hex.decode("04"),0,ePubKeyBytes,0,1);
        System.arraycopy(ePubKeyBytesX,0,ePubKeyBytes,1,32);
        System.arraycopy(ePubKeyBytesY,0,ePubKeyBytes,1+32,32);
        return ePubKeyBytes;
    }

    public byte[] getEPrivKeyBytes(){
        return BigIntegerToLBytes(ePrivKey.getD(),32);
    }

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
    private static byte[] BigIntegerToLBytes(BigInteger b, int l){
        byte[] tmp = b.toByteArray();
        byte[] result = new byte[l];
        if (tmp.length > result.length) {
            System.arraycopy(tmp, tmp.length - result.length, result, 0, result.length);
        }
        else {
            System.arraycopy(tmp,0,result,result.length-tmp.length,tmp.length);
        }
        return result;
    }
}
