package test.com.jd.blockchain.crypto.mpc;

import com.jd.blockchain.crypto.mpc.MultiSum;
import com.jd.blockchain.crypto.paillier.PaillierPrivateKeyParameters;
import com.jd.blockchain.crypto.paillier.PaillierPublicKeyParameters;
import com.jd.blockchain.crypto.paillier.PaillierUtils;
import com.jd.blockchain.utils.io.BytesUtils;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.junit.Test;

import static org.junit.Assert.*;

public class MultiSumTest {

    @Test
    public void testMultiSum() {

        AsymmetricCipherKeyPair keyPair = PaillierUtils.generateKeyPair();
        PaillierPublicKeyParameters pubKeyParams = (PaillierPublicKeyParameters) keyPair.getPublic();
        PaillierPrivateKeyParameters privKeyParams = (PaillierPrivateKeyParameters) keyPair.getPrivate();

        byte[] encKey = PaillierUtils.pubKey2Bytes(pubKeyParams);
        byte[] decKey = PaillierUtils.privKey2Bytes(privKeyParams);

        int int1 = 6;
        int int2 = 60;
        int int3 = 600;
        int sum  = 666;

        byte[] id1 = BytesUtils.toBytes("1");
        byte[] id2 = BytesUtils.toBytes("2");
        byte[] id3 = BytesUtils.toBytes("3");

        MultiSum.generateEphemeralKeyPair();
        byte[] ePubKey1  = MultiSum.getEPubKey();
        byte[] ePrivKey1 = MultiSum.getEPrivKey();

        MultiSum.generateEphemeralKeyPair();
        byte[] ePubKey2  = MultiSum.getEPubKey();
        byte[] ePrivKey2 = MultiSum.getEPrivKey();

        MultiSum.generateEphemeralKeyPair();
        byte[] ePubKey3  = MultiSum.getEPubKey();
        byte[] ePrivKey3 = MultiSum.getEPrivKey();


        byte[] sk12 = MultiSum.calculateAgreement(ePubKey2,ePrivKey1);
        byte[] sk23 = MultiSum.calculateAgreement(ePubKey3,ePrivKey2);
        byte[] sk31 = MultiSum.calculateAgreement(ePubKey1,ePrivKey3);

        assertArrayEquals(sk12,MultiSum.calculateAgreement(ePubKey1,ePrivKey2));
        assertArrayEquals(sk23,MultiSum.calculateAgreement(ePubKey2,ePrivKey3));
        assertArrayEquals(sk31,MultiSum.calculateAgreement(ePubKey3,ePrivKey1));

        byte[] s12 = MultiSum.deriveShares(id1,id2,sk12);
        byte[] s23 = MultiSum.deriveShares(id2,id3,sk23);
        byte[] s31 = MultiSum.deriveShares(id3,id1,sk31);

        assertArrayEquals(s12, MultiSum.deriveShares(id1,id2,sk12));
        assertArrayEquals(s23, MultiSum.deriveShares(id2,id3,sk23));
        assertArrayEquals(s31, MultiSum.deriveShares(id3,id1,sk31));

        byte[] c1 = MultiSum.encryptBlindedMsg(encKey,int1,s12,s31);
        byte[] c2 = MultiSum.encryptBlindedMsg(encKey,int2,s23,s12);
        byte[] c3 = MultiSum.encryptBlindedMsg(encKey,int3,s31,s23);

        byte[] aggregatedCiphertext = MultiSum.aggregateCiphertexts(encKey,c1,c2,c3);

        byte[] decryptedValue = MultiSum.decrypt(decKey,aggregatedCiphertext);

        assertEquals(sum,byteArrayToInt(decryptedValue));
    }

    private static int byteArrayToInt(byte[] input) {
        int result;
        int length = input.length;
        byte[] buffer = new byte[4];
        if (length <= buffer.length){
            System.arraycopy(input,0,buffer,buffer.length - length,length);
        } else {
            System.arraycopy(input,length - buffer.length,buffer,0, buffer.length);
        }
        result =  buffer[3] & 0xFF |
                (buffer[2] & 0xFF) << 8 |
                (buffer[1] & 0xFF) << 16 |
                (buffer[0] & 0xFF) << 24;
        return result;
    }
}