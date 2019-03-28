package test.com.jd.blockchain.crypto.mpc;

import com.jd.blockchain.crypto.mpc.MultiSum;
import com.jd.blockchain.crypto.paillier.KeyPair;
import com.jd.blockchain.crypto.paillier.KeyPairBuilder;
import com.jd.blockchain.crypto.paillier.PublicKey;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.*;

public class MultiSumTest {

    private KeyPair keyPair;
    private PublicKey encKey;

    @Before
    public void init() {
        KeyPairBuilder keygen = new KeyPairBuilder();
        keyPair = keygen.generateKeyPair();
        encKey = keyPair.getPublicKey();
    }
    
    @Test
    public void testMultiSum() {

        MultiSum instance1 = new MultiSum();
        MultiSum instance2 = new MultiSum();
        MultiSum instance3 = new MultiSum();

        BigInteger value1 = BigInteger.valueOf(6);
        BigInteger value2 = BigInteger.valueOf(60);
        BigInteger value3 = BigInteger.valueOf(600);
        BigInteger expectedSum = BigInteger.valueOf(666);

        byte[] id1 = "1".getBytes();
        byte[] id2 = "2".getBytes();
        byte[] id3 = "3".getBytes();

        instance1.generateEphemeralKeyPair();
        instance2.generateEphemeralKeyPair();
        instance3.generateEphemeralKeyPair();

        ECPublicKeyParameters ePubKey1 = instance1.getEPubKey();
        ECPublicKeyParameters ePubKey2 = instance2.getEPubKey();
        ECPublicKeyParameters ePubKey3 = instance3.getEPubKey();

        BigInteger sk12 = instance1.calculateAgreement(ePubKey2);
        BigInteger sk23 = instance2.calculateAgreement(ePubKey3);
        BigInteger sk31 = instance1.calculateAgreement(ePubKey3);

        assertEquals(sk12,instance2.calculateAgreement(ePubKey1));
        assertEquals(sk23,instance3.calculateAgreement(ePubKey2));
        assertEquals(sk31,instance3.calculateAgreement(ePubKey1));

        BigInteger s12 = MultiSum.deriveShares(id1,id2,sk12);
        BigInteger s23 = MultiSum.deriveShares(id2,id3,sk23);
        BigInteger s31 = MultiSum.deriveShares(id3,id1,sk31);

        assertEquals(s12, MultiSum.deriveShares(id1,id2,sk12));
        assertEquals(s23, MultiSum.deriveShares(id2,id3,sk23));
        assertEquals(s31, MultiSum.deriveShares(id3,id1,sk31));

        BigInteger c1 = MultiSum.encryptBlindedMsg(encKey,value1,s12,s31);
        BigInteger c2 = MultiSum.encryptBlindedMsg(encKey,value2,s23,s12);
        BigInteger c3 = MultiSum.encryptBlindedMsg(encKey,value3,s31,s23);

        BigInteger aggregatedCiphertext = MultiSum.aggregateCiphertexts(encKey,c1,c2,c3);

        BigInteger decryptedValue = MultiSum.decrypt(keyPair,aggregatedCiphertext);

        assertEquals(expectedSum,decryptedValue);
    }

    @Test
    public void testResolveEPrivKey(){

        MultiSum instance = new MultiSum();
        instance.generateEphemeralKeyPair();

        ECPrivateKeyParameters expectedEPrivKey = instance.getEPrivKey();
        byte[] ePrivKeyBytes = instance.getEPrivKeyBytes();
        ECPrivateKeyParameters ePrivKey = instance.resolveEPrivKey(ePrivKeyBytes);
        assertEquals(expectedEPrivKey.getD(),ePrivKey.getD());
    }

    @Test
    public void testResolveEPubKey(){

        MultiSum instance = new MultiSum();
        instance.generateEphemeralKeyPair();

        ECPublicKeyParameters expectedEPubKey = instance.getEPubKey();
        byte[] ePubKeyBytes = instance.getEPubKeyBytes();
        ECPublicKeyParameters ePubKey = instance.resolveEPubKey(ePubKeyBytes);

        assertEquals(Hex.toHexString(expectedEPubKey.getQ().getAffineXCoord().getEncoded()),Hex.toHexString(ePubKey.getQ().getAffineXCoord().getEncoded()));
        assertEquals(Hex.toHexString(expectedEPubKey.getQ().getAffineYCoord().getEncoded()),Hex.toHexString(ePubKey.getQ().getAffineYCoord().getEncoded()));
    }
}