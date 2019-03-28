package com.jd.blockchain.crypto.mpc;

import com.jd.blockchain.crypto.paillier.KeyPair;
import com.jd.blockchain.crypto.paillier.PaillierUtils;
import com.jd.blockchain.crypto.paillier.PublicKey;
import com.jd.blockchain.crypto.smutils.asymmetric.SM2Utils;
import com.jd.blockchain.crypto.smutils.hash.SM3Utils;
import com.jd.blockchain.utils.io.BytesUtils;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.agreement.ECDHBasicAgreement;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Hex;

import java.math.BigInteger;

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

    public static BigInteger encryptBlindedMsg(PublicKey encKey, BigInteger msg, BigInteger frontShare, BigInteger rearShare){
        return encKey.encrypt(msg.add(frontShare).subtract(rearShare).mod(encKey.getN()));
    }

    public static BigInteger aggregateCiphertexts(PublicKey encKey, BigInteger... bigIntegersList){
        BigInteger aggregatedCiphertext = BigInteger.ONE;
        for (BigInteger entry : bigIntegersList) {
            aggregatedCiphertext = aggregatedCiphertext.multiply(entry).mod(encKey.getnSquared());
        }
        return aggregatedCiphertext;
    }

    public static BigInteger decrypt(KeyPair keyPair, BigInteger ciphertext){
        return keyPair.decrypt(ciphertext);
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
        return PaillierUtils.BigIntegerToLBytes(ePrivKey.getD(),32);
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

}
