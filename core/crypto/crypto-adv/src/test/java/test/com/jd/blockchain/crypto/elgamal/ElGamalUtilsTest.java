package test.com.jd.blockchain.crypto.elgamal;

import com.jd.blockchain.crypto.elgamal.ElGamalUtils;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.params.ElGamalPrivateKeyParameters;
import org.bouncycastle.crypto.params.ElGamalPublicKeyParameters;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.*;

public class ElGamalUtilsTest {

    @Test
    public void testGenerateKeyPair() {

        AsymmetricCipherKeyPair keyPair = ElGamalUtils.generateKeyPair();

        ElGamalPublicKeyParameters pubKeyParams = (ElGamalPublicKeyParameters) keyPair.getPublic();
        ElGamalPrivateKeyParameters privKeyParams = (ElGamalPrivateKeyParameters) keyPair.getPrivate();

        byte[] privKeyBytes = bigIntegerTo64Bytes(privKeyParams.getX());
        byte[] pubKeyBytes  = bigIntegerTo64Bytes(pubKeyParams.getY());

        byte[] retrievedPubKeyBytes = ElGamalUtils.retrievePublicKey(privKeyBytes);

        assertEquals(64,privKeyBytes.length);
        assertEquals(64,pubKeyBytes.length);

        assertArrayEquals(retrievedPubKeyBytes,pubKeyBytes);
    }

    // To convert BigInteger to byte[] whose length is 64
    private byte[] bigIntegerTo64Bytes(BigInteger b){
        byte[] tmp = b.toByteArray();
        byte[] result = new byte[64];
        if (tmp.length > result.length)
            System.arraycopy(tmp, tmp.length-result.length, result, 0, result.length);
        else System.arraycopy(tmp,0,result,result.length-tmp.length,tmp.length);
        return result;
    }


    @Test
    public void testDecrypt() {

        AsymmetricCipherKeyPair keyPair = ElGamalUtils.generateKeyPair();

        ElGamalPublicKeyParameters pubKeyParams = (ElGamalPublicKeyParameters) keyPair.getPublic();
        ElGamalPrivateKeyParameters privKeyParams = (ElGamalPrivateKeyParameters) keyPair.getPrivate();

        byte[] privKeyBytes = bigIntegerTo64Bytes(privKeyParams.getX());
        byte[] pubKeyBytes  = bigIntegerTo64Bytes(pubKeyParams.getY());

        byte[]  message = Hex.decode("5468697320697320612074657374");

        byte[] ciphertext = ElGamalUtils.encrypt(message,pubKeyBytes);
        byte[] plaintext = ElGamalUtils.decrypt(ciphertext,privKeyBytes);

        assertEquals(128,ciphertext.length);
        assertArrayEquals(plaintext,message);

        BigInteger one = BigInteger.ONE;

        ciphertext = ElGamalUtils.encrypt(bigIntegerTo64Bytes(one),pubKeyBytes);
        plaintext  = ElGamalUtils.decrypt(ciphertext,privKeyBytes);

        assertEquals(one,BigInteger.valueOf((int) plaintext[0]));
        assertTrue(BigInteger.ONE.equals(BigInteger.valueOf((int) plaintext[0])));
    }
}