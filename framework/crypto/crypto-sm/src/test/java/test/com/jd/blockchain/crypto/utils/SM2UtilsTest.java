package test.com.jd.blockchain.crypto.utils;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.jd.blockchain.utils.io.BytesUtils;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.util.test.TestRandomBigInteger;
import org.junit.Test;

import com.jd.blockchain.crypto.utils.sm.SM2Utils;

import java.util.Random;

public class SM2UtilsTest {

    @Test
    public void testGenerateKeyPair() {

        String expectedPrivateKey = "3945208f7b2144b13f36e38ac6d39f95889393692860b51a42fb81ef4df7c5b8";
        String expectedPublicKey = "04"+"09f9df311e5421a150dd7d161e4bc5c672179fad1833fc076bb08ff356f35020"+"ccea490ce26775a52dc6ea718cc1aa600aed05fbf35e084a6632f6072da9ad13";

        AsymmetricCipherKeyPair keyPair = SM2Utils.generateKeyPair(new TestRandomBigInteger(expectedPrivateKey, 16));
        ECPublicKeyParameters ecPub = (ECPublicKeyParameters) keyPair.getPublic();
        ECPrivateKeyParameters ecPriv = (ECPrivateKeyParameters) keyPair.getPrivate();

        byte[] expectedPrivateKeyBytes = Hex.decode(expectedPrivateKey);
        byte[] privKeyBytes = ecPriv.getD().toByteArray();
        assertArrayEquals(expectedPrivateKeyBytes,privKeyBytes);

        byte[] pubKeyBytesX = ecPub.getQ().getAffineXCoord().getEncoded();
        byte[] pubKeyBytesY = ecPub.getQ().getAffineYCoord().getEncoded();
        assertEquals(expectedPublicKey,"04"+Hex.toHexString(pubKeyBytesX)+Hex.toHexString(pubKeyBytesY));

        byte[] pubKeyBytes = ecPub.getQ().getEncoded(false);
        assertArrayEquals(Hex.decode(expectedPublicKey),pubKeyBytes);
    }

    @Test
    public void testPubKeyRetrieve() {

        String expectedPrivateKey = "3945208f7b2144b13f36e38ac6d39f95889393692860b51a42fb81ef4df7c5b8";
        String expectedPublicKey = "04"+"09f9df311e5421a150dd7d161e4bc5c672179fad1833fc076bb08ff356f35020"+"ccea490ce26775a52dc6ea718cc1aa600aed05fbf35e084a6632f6072da9ad13";

        byte[] privateKey = Hex.decode(expectedPrivateKey);
        byte[] publicKey = SM2Utils.retrievePublicKey(privateKey);

        assertEquals(expectedPublicKey,Hex.toHexString(publicKey));
    }

    @Test
    public void testSign() {

        String expectedPrivateKey = "3945208f7b2144b13f36e38ac6d39f95889393692860b51a42fb81ef4df7c5b8";
        String expectedRandomness = "59276E27D506861A16680F3AD9C02DCCEF3CC1FA3CDBE4CE6D54B80DEAC1BC21";
        String expectedMessage    = "message digest";
        String expectedIdentifier = "ALICE123@YAHOO.COM";
        String expectedR          = "b0e3e7d4ac2178f833ad73fa9d1191e41c76c8bfedb5ad89040ba2e5184bde58";
        String expectedS          = "cc8d096578f7dd2669ac1ac42f7e722bcfa42b9e0be0b1b5df7ca0b53fdd5750";

        byte[] privKeyBytes = Hex.decode(expectedPrivateKey);
        byte[] messageBytes = BytesUtils.toBytes(expectedMessage);

        byte[] signature = SM2Utils.sign(messageBytes,privKeyBytes,new TestRandomBigInteger(expectedRandomness, 16),expectedIdentifier);
        assertArrayEquals(Hex.decode(expectedR+expectedS),signature);
    }

    @Test
    public void testVerify() {

        String expectedPublicKey  = "04"+"09f9df311e5421a150dd7d161e4bc5c672179fad1833fc076bb08ff356f35020"+"ccea490ce26775a52dc6ea718cc1aa600aed05fbf35e084a6632f6072da9ad13";
        String expectedMessage    = "message digest";
        String expectedIdentifier = "ALICE123@YAHOO.COM";
        String expectedR          = "b0e3e7d4ac2178f833ad73fa9d1191e41c76c8bfedb5ad89040ba2e5184bde58";
        String expectedS          = "cc8d096578f7dd2669ac1ac42f7e722bcfa42b9e0be0b1b5df7ca0b53fdd5750";

        byte[] pubKeyBytes = Hex.decode(expectedPublicKey);
        byte[] messageBytes = BytesUtils.toBytes(expectedMessage);
        byte[] signatureBytes = Hex.decode(expectedR + expectedS);

        boolean isVerified = SM2Utils.verify(messageBytes,pubKeyBytes,signatureBytes,expectedIdentifier);
        assertTrue(isVerified);
    }

    @Test
    public void testEncrypt() {

        String expectedPublicKey  = "04"+"09f9df311e5421a150dd7d161e4bc5c672179fad1833fc076bb08ff356f35020"+"ccea490ce26775a52dc6ea718cc1aa600aed05fbf35e084a6632f6072da9ad13";
        String expectedMessage    = "encryption standard";
        String expectedRandomness = "59276E27D506861A16680F3AD9C02DCCEF3CC1FA3CDBE4CE6D54B80DEAC1BC21";
        String expectedC1         = "0404ebfc718e8d1798620432268e77feb6415e2ede0e073c0f4f640ecd2e149a73e858f9d81e5430a57b36daab8f950a3c64e6ee6a63094d99283aff767e124df0";
        String expectedC2         = "21886ca989ca9c7d58087307ca93092d651efa";
        String expectedC3         = "59983c18f809e262923c53aec295d30383b54e39d609d160afcb1908d0bd8766";

        byte[] pubKeyBytes = Hex.decode(expectedPublicKey);
        byte[] messageBytes = BytesUtils.toBytes(expectedMessage);

        byte[] ciphertext = SM2Utils.encrypt(messageBytes,pubKeyBytes,new TestRandomBigInteger(expectedRandomness, 16));
        assertArrayEquals(Hex.decode(expectedC1 + expectedC3 + expectedC2),ciphertext);


    }

    @Test
    public void testDecrypt() {

        String expectedPrivateKey = "3945208f7b2144b13f36e38ac6d39f95889393692860b51a42fb81ef4df7c5b8";
        String expectedMessage    = "encryption standard";
        String expectedC1         = "0404ebfc718e8d1798620432268e77feb6415e2ede0e073c0f4f640ecd2e149a73e858f9d81e5430a57b36daab8f950a3c64e6ee6a63094d99283aff767e124df0";
        String expectedC2         = "21886ca989ca9c7d58087307ca93092d651efa";
        String expectedC3         = "59983c18f809e262923c53aec295d30383b54e39d609d160afcb1908d0bd8766";

        byte[] privKeyBytes = Hex.decode(expectedPrivateKey);
        byte[] ciphertext = Hex.decode(expectedC1 + expectedC3 + expectedC2);

        byte[] plaintext = SM2Utils.decrypt(ciphertext,privKeyBytes);
        assertArrayEquals(BytesUtils.toBytes(expectedMessage),plaintext);
    }

//    @Test
    public void encryptingPerformace(){

        byte[] data = new byte[1000];
        Random random = new Random();
        random.nextBytes(data);

        int count = 10000;

        byte[] ciphertext = null;

        AsymmetricCipherKeyPair keyPair = SM2Utils.generateKeyPair();
        ECPublicKeyParameters ecPub = (ECPublicKeyParameters) keyPair.getPublic();
        ECPrivateKeyParameters ecPriv = (ECPrivateKeyParameters) keyPair.getPrivate();

        System.out.println("=================== do SM2 encrypt test ===================");

        for (int r = 0; r < 5; r++) {
            System.out.println("------------- round[" + r + "] --------------");
            long startTS = System.currentTimeMillis();
            for (int i = 0; i < count; i++) {
                ciphertext = SM2Utils.encrypt(data,ecPub);
            }
            long elapsedTS = System.currentTimeMillis() - startTS;
            System.out.println(String.format("SM2 Encrypting Count=%s; Elapsed Times=%s; KBPS=%.2f", count, elapsedTS,
                    (count * 1000.00D) / elapsedTS));
        }

        System.out.println("=================== do SM2 decrypt test ===================");
        for (int r = 0; r < 5; r++) {
            System.out.println("------------- round[" + r + "] --------------");
            long startTS = System.currentTimeMillis();
            for (int i = 0; i < count; i++) {
                SM2Utils.decrypt(ciphertext,ecPriv);
            }
            long elapsedTS = System.currentTimeMillis() - startTS;
            System.out.println(String.format("SM2 Decrypting Count=%s; Elapsed Times=%s; KBPS=%.2f", count, elapsedTS,
                    (count * 1000.00D) / elapsedTS));
        }
    }


//    @Test
    public void signingPerformace(){

        byte[] data = new byte[1024];
        Random random = new Random();
        random.nextBytes(data);

        int count = 10000;

        byte[] sm2Digest = null;

        AsymmetricCipherKeyPair keyPair = SM2Utils.generateKeyPair();
        ECPublicKeyParameters ecPub = (ECPublicKeyParameters) keyPair.getPublic();
        ECPrivateKeyParameters ecPriv = (ECPrivateKeyParameters) keyPair.getPrivate();

        System.out.println("=================== do SM2 sign test ===================");

        for (int r = 0; r < 5; r++) {
            System.out.println("------------- round[" + r + "] --------------");
            long startTS = System.currentTimeMillis();
            for (int i = 0; i < count; i++) {
                 sm2Digest = SM2Utils.sign(data,ecPriv);
            }
            long elapsedTS = System.currentTimeMillis() - startTS;
            System.out.println(String.format("SM2 Signing Count=%s; Elapsed Times=%s; TPS=%.2f", count, elapsedTS,
                    (count * 1000.00D) / elapsedTS));
        }

        System.out.println("=================== do SM2 verify test ===================");
        for (int r = 0; r < 5; r++) {
            System.out.println("------------- round[" + r + "] --------------");
            long startTS = System.currentTimeMillis();
            for (int i = 0; i < count; i++) {
                SM2Utils.verify(data,ecPub,sm2Digest);
            }
            long elapsedTS = System.currentTimeMillis() - startTS;
            System.out.println(String.format("SM2 Verifying Count=%s; Elapsed Times=%s; TPS=%.2f", count, elapsedTS,
                    (count * 1000.00D) / elapsedTS));
        }
    }
}