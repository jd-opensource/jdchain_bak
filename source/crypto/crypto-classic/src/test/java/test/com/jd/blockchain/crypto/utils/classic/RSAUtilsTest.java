package test.com.jd.blockchain.crypto.utils.classic;

import com.jd.blockchain.crypto.utils.classic.RSAUtils;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

/**
 * @author zhanglin33
 * @title: RSAUtilsTest
 * @description: TODO
 * @date 2019-04-11, 17:10
 */
public class RSAUtilsTest {

    @Test
    public void generateKeyPairTest(){
        AsymmetricCipherKeyPair kp = RSAUtils.generateKeyPair();
        RSAKeyParameters pubKey = (RSAKeyParameters) kp.getPublic();
        RSAPrivateCrtKeyParameters privKey = (RSAPrivateCrtKeyParameters) kp.getPrivate();

        byte[] pubKeyBytes_RawKey = RSAUtils.pubKey2Bytes_RawKey(pubKey);
        byte[] pubKeyBytesConverted_RawKey =
                RSAUtils.pubKey2Bytes_RawKey(RSAUtils.bytes2PubKey_RawKey(pubKeyBytes_RawKey));
        assertArrayEquals(pubKeyBytes_RawKey,pubKeyBytesConverted_RawKey);

        byte[] privKeyBytes_RawKey = RSAUtils.privKey2Bytes_RawKey(privKey);
        byte[] privKeyBytesConverted_RawKey =
                RSAUtils.privKey2Bytes_RawKey(RSAUtils.bytes2PrivKey_RawKey(privKeyBytes_RawKey));
        assertArrayEquals(privKeyBytes_RawKey,privKeyBytesConverted_RawKey);

        byte[] pubKeyBytes_PKCS1 = RSAUtils.pubKey2Bytes_PKCS1(pubKey);
        byte[] pubKeyBytesConverted_PKCS1 =
                RSAUtils.pubKey2Bytes_PKCS1(RSAUtils.bytes2PubKey_PKCS1(pubKeyBytes_PKCS1));
        assertArrayEquals(pubKeyBytes_PKCS1,pubKeyBytesConverted_PKCS1);

        byte[] privKeyBytes_PKCS1 = RSAUtils.privKey2Bytes_PKCS1(privKey);
        byte[] privKeyBytesConverted_PKCS1 =
                RSAUtils.privKey2Bytes_PKCS1(RSAUtils.bytes2PrivKey_PKCS1(privKeyBytes_PKCS1));
        assertArrayEquals(privKeyBytes_PKCS1,privKeyBytesConverted_PKCS1);

        byte[] pubKeyBytes_PKCS8 = RSAUtils.pubKey2Bytes_PKCS8(pubKey);
        byte[] pubKeyBytesConverted_PKCS8 =
                RSAUtils.pubKey2Bytes_PKCS8(RSAUtils.bytes2PubKey_PKCS8(pubKeyBytes_PKCS8));
        assertArrayEquals(pubKeyBytes_PKCS8,pubKeyBytesConverted_PKCS8);

        byte[] privKeyBytes_PKCS8 = RSAUtils.privKey2Bytes_PKCS8(privKey);
        byte[] privKeyBytesConverted_PKCS8 =
                RSAUtils.privKey2Bytes_PKCS8(RSAUtils.bytes2PrivKey_PKCS8(privKeyBytes_PKCS8));
        assertArrayEquals(privKeyBytes_PKCS8,privKeyBytesConverted_PKCS8);
    }

    @Test
    public void retrievePublicKeyTest(){

        AsymmetricCipherKeyPair kp = RSAUtils.generateKeyPair();
        RSAKeyParameters pubKey = (RSAKeyParameters) kp.getPublic();
        RSAPrivateCrtKeyParameters privKey = (RSAPrivateCrtKeyParameters) kp.getPrivate();

        byte[] privKeyBytes = RSAUtils.privKey2Bytes_RawKey(privKey);
        byte[] pubKeyBytes  = RSAUtils.pubKey2Bytes_RawKey(pubKey);
        byte[] retrievedPubKeyBytes = RSAUtils.retrievePublicKey(privKeyBytes);

        assertArrayEquals(pubKeyBytes,retrievedPubKeyBytes);
    }

    @Test
    public void signTest(){

        byte[] data = new byte[1024];
        Random random = new Random();
        random.nextBytes(data);

        AsymmetricCipherKeyPair keyPair = RSAUtils.generateKeyPair();
        AsymmetricKeyParameter privKey = keyPair.getPrivate();
        byte[] privKeyBytes = RSAUtils.privKey2Bytes_RawKey((RSAPrivateCrtKeyParameters) privKey);

        byte[] signatureFromPrivKey = RSAUtils.sign(data, privKey);
        byte[] signatureFromPrivKeyBytes = RSAUtils.sign(data, privKeyBytes);

        assertNotNull(signatureFromPrivKey);
        assertEquals(2048 / 8, signatureFromPrivKey.length);
        assertArrayEquals(signatureFromPrivKeyBytes,signatureFromPrivKey);
    }

    @Test
    public void verifyTest(){

        byte[] data = new byte[1024];
        Random random = new Random();
        random.nextBytes(data);

        AsymmetricCipherKeyPair keyPair = RSAUtils.generateKeyPair();
        AsymmetricKeyParameter privKey = keyPair.getPrivate();
        AsymmetricKeyParameter pubKey = keyPair.getPublic();
        byte[] pubKeyBytes = RSAUtils.pubKey2Bytes_RawKey((RSAKeyParameters) pubKey);

        byte[] signature = RSAUtils.sign(data,privKey);

        boolean isValidFromPubKey = RSAUtils.verify(data, pubKey, signature);
        boolean isValidFromPubKeyBytes = RSAUtils.verify(data, pubKeyBytes, signature);

        assertTrue(isValidFromPubKey);
        assertTrue(isValidFromPubKeyBytes);
    }


    @Test
    public void performanceTest(){

        int count = 10000;
        byte[] data = new byte[1024];
        Random random = new Random();
        random.nextBytes(data);

        AsymmetricCipherKeyPair keyPair = RSAUtils.generateKeyPair();
        AsymmetricKeyParameter privKey = keyPair.getPrivate();
        AsymmetricKeyParameter pubKey = keyPair.getPublic();

        byte[] signature = RSAUtils.sign(data,privKey);

        System.out.println("=================== do RSA sign test ===================");

        for (int r = 0; r < 5; r++) {
            System.out.println("------------- round[" + r + "] --------------");
            long startTS = System.currentTimeMillis();
            for (int i = 0; i < count; i++) {
                 RSAUtils.sign(data,privKey);
            }
            long elapsedTS = System.currentTimeMillis() - startTS;
            System.out.println(String.format("RSA Signing Count=%s; Elapsed Times=%s; TPS=%.2f", count, elapsedTS,
                    (count * 1000.00D) / elapsedTS));
        }

        System.out.println("=================== do RSA verify test ===================");
        for (int r = 0; r < 5; r++) {
            System.out.println("------------- round[" + r + "] --------------");
            long startTS = System.currentTimeMillis();
            for (int i = 0; i < count; i++) {
                RSAUtils.verify(data,pubKey,signature);
            }
            long elapsedTS = System.currentTimeMillis() - startTS;
            System.out.println(String.format("RSA Verifying Count=%s; Elapsed Times=%s; TPS=%.2f", count, elapsedTS,
                    (count * 1000.00D) / elapsedTS));
        }
    }
}
