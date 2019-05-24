package test.com.jd.blockchain.crypto.utils.classic;

import com.jd.blockchain.crypto.utils.classic.RSAUtils;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.junit.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Random;

import static org.junit.Assert.*;

/**
 * @author zhanglin33
 * @title: RSAUtilsTest
 * @description: Tests for methods in RSAUtils
 * @date 2019-04-11, 17:10
 */
public class RSAUtilsTest {

    @Test
    public void generateKeyPairTest() {
        AsymmetricCipherKeyPair kp = RSAUtils.generateKeyPair();
        keyPairTest(kp);
    }

    @Test
    public void generateKeyPair_ShortExpTest() {
        AsymmetricCipherKeyPair kp = RSAUtils.generateKeyPair_shortExp();
        keyPairTest(kp);
    }

    private void keyPairTest(AsymmetricCipherKeyPair keyPair) {
        RSAKeyParameters pubKey = (RSAKeyParameters) keyPair.getPublic();
        RSAPrivateCrtKeyParameters privKey = (RSAPrivateCrtKeyParameters) keyPair.getPrivate();

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
    public void test(){

        AsymmetricCipherKeyPair kp = RSAUtils.generateKeyPair();
        RSAKeyParameters pubKey = (RSAKeyParameters) kp.getPublic();
        RSAPrivateCrtKeyParameters privKey = (RSAPrivateCrtKeyParameters) kp.getPrivate();

        byte[] privKeyBytes = RSAUtils.privKey2Bytes_RawKey(privKey);
        byte[] pubKeyBytes  = RSAUtils.pubKey2Bytes_RawKey(pubKey);
        byte[] retrievedPubKeyBytes = RSAUtils.retrievePublicKey(privKeyBytes);

        assertArrayEquals(pubKeyBytes,retrievedPubKeyBytes);

        byte[] data = new byte[128];
        Random random = new Random();
        random.nextBytes(data);

        byte[] signatureFromPrivKey = RSAUtils.sign(data, privKey);
        byte[] signatureFromPrivKeyBytes = RSAUtils.sign(data, privKeyBytes);

        assertNotNull(signatureFromPrivKey);
        assertEquals(2048 / 8, signatureFromPrivKey.length);
        assertArrayEquals(signatureFromPrivKeyBytes,signatureFromPrivKey);

        byte[] signature = RSAUtils.sign(data,privKey);

        boolean isValidFromPubKey = RSAUtils.verify(data, pubKey, signature);
        boolean isValidFromPubKeyBytes = RSAUtils.verify(data, pubKeyBytes, signature);

        assertTrue(isValidFromPubKey);
        assertTrue(isValidFromPubKeyBytes);

        byte[] ciphertextFromPubKey = RSAUtils.encrypt(data,pubKey);
        byte[] ciphertextFromPubKeyBytes = RSAUtils.encrypt(data,pubKeyBytes);

        assertEquals(256,ciphertextFromPubKey.length);
        assertEquals(256,ciphertextFromPubKeyBytes.length);


        data = new byte[1024];
        random.nextBytes(data);
        byte[] ciphertext = RSAUtils.encrypt(data, pubKey);

        byte[] plaintextFromPrivKey = RSAUtils.decrypt(ciphertext, privKey);
        byte[] plaintextFromPrivKeyBytes = RSAUtils.decrypt(ciphertext, privKeyBytes);

        assertArrayEquals(data, plaintextFromPrivKey);
        assertArrayEquals(data, plaintextFromPrivKeyBytes);
    }


//    @Test
    public void performanceTest(){

        int count = 10000;
        byte[] data = new byte[128];
        Random random = new Random();
        random.nextBytes(data);

        AsymmetricCipherKeyPair keyPair = RSAUtils.generateKeyPair();
        AsymmetricKeyParameter privKey = keyPair.getPrivate();
        AsymmetricKeyParameter pubKey = keyPair.getPublic();

        byte[] signature = RSAUtils.sign(data,privKey);
        byte[] ciphertext = RSAUtils.encrypt(data,pubKey);

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

        System.out.println("=================== do RSA encrypt test ===================");

        for (int r = 0; r < 5; r++) {
            System.out.println("------------- round[" + r + "] --------------");
            long startTS = System.currentTimeMillis();
            for (int i = 0; i < count; i++) {
                RSAUtils.encrypt(data,pubKey);
            }
            long elapsedTS = System.currentTimeMillis() - startTS;
            System.out.println(String.format("RSA Encrypting Count=%s; Elapsed Times=%s; TPS=%.2f", count, elapsedTS,
                    (count * 1000.00D) / elapsedTS));
        }

        System.out.println("=================== do RSA decrypt test ===================");
        for (int r = 0; r < 5; r++) {
            System.out.println("------------- round[" + r + "] --------------");
            long startTS = System.currentTimeMillis();
            for (int i = 0; i < count; i++) {
                RSAUtils.decrypt(ciphertext,privKey);
            }
            long elapsedTS = System.currentTimeMillis() - startTS;
            System.out.println(String.format("RSA Decrypting Count=%s; Elapsed Times=%s; TPS=%.2f", count, elapsedTS,
                    (count * 1000.00D) / elapsedTS));
        }
    }

//    @Test
    public void encryptionConsistencyTest(){

        int count = 10000;
        byte[] data = new byte[222];
        Random random = new Random();
        random.nextBytes(data);

        KeyPairGenerator keyPairGen = null;
        try {
            keyPairGen = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        assert keyPairGen != null;
        keyPairGen.initialize(2048, new SecureRandom());
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

        byte[] publicKeyBytes  = publicKey.getEncoded();
        byte[] privateKeyBytes = privateKey.getEncoded();

        RSAKeyParameters pubKey            = RSAUtils.bytes2PubKey_PKCS8(publicKeyBytes);
        RSAPrivateCrtKeyParameters privKey = RSAUtils.bytes2PrivKey_PKCS8(privateKeyBytes);

        Cipher cipher;
        byte[] ciphertext = null;
        byte[] plaintext = null;

        System.out.println("=================== do BouncyCastle-based RSA encrypt test ===================");
        for (int r = 0; r < 5; r++) {
            System.out.println("------------- round[" + r + "] --------------");
            long startTS = System.currentTimeMillis();
            for (int i = 0; i < count; i++) {
                ciphertext = RSAUtils.encrypt(data,pubKey);
            }
            long elapsedTS = System.currentTimeMillis() - startTS;
            System.out.println(String.format("BouncyCastle-based RSA Encrypting Count=%s; Elapsed Times=%s; TPS=%.2f", count, elapsedTS,
                    (count * 1000.00D) / elapsedTS));
        }

        assert ciphertext != null;
        System.out.println("=================== do BouncyCastle-based RSA decrypt test ===================");
        for (int r = 0; r < 5; r++) {
            System.out.println("------------- round[" + r + "] --------------");
            long startTS = System.currentTimeMillis();
            for (int i = 0; i < count; i++) {
                plaintext = RSAUtils.decrypt(ciphertext,privKey);
            }
            long elapsedTS = System.currentTimeMillis() - startTS;
            System.out.println(String.format("BouncyCastle-based RSA Decrypting Count=%s; Elapsed Times=%s; TPS=%.2f", count, elapsedTS,
                    (count * 1000.00D) / elapsedTS));
        }

        System.out.println("=================== do JDK-based RSA encrypt test ===================");
        for (int r = 0; r < 5; r++) {
            System.out.println("------------- round[" + r + "] --------------");
            long startTS = System.currentTimeMillis();
            for (int i = 0; i < count; i++) {
                try {
                    cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                    cipher.init(Cipher.ENCRYPT_MODE, publicKey);
                    ciphertext = cipher.doFinal(data);
                } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
                        | IllegalBlockSizeException | BadPaddingException e) {
                    e.printStackTrace();
                }
            }
            long elapsedTS = System.currentTimeMillis() - startTS;
            System.out.println(String.format("JDK-based RSA Encrypting Count=%s; Elapsed Times=%s; TPS=%.2f", count, elapsedTS,
                    (count * 1000.00D) / elapsedTS));
        }

        System.out.println("=================== do JDK-based RSA decrypt test ===================");
        for (int r = 0; r < 5; r++) {
            System.out.println("------------- round[" + r + "] --------------");
            long startTS = System.currentTimeMillis();
            for (int i = 0; i < count; i++) {
                try {
                    cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                    cipher.init(Cipher.DECRYPT_MODE, privateKey);
                    plaintext = cipher.doFinal(ciphertext);
                } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
                        | IllegalBlockSizeException | BadPaddingException e) {
                    e.printStackTrace();
                }
            }
            long elapsedTS = System.currentTimeMillis() - startTS;
            System.out.println(String.format("JDK-based RSA Decrypting Count=%s; Elapsed Times=%s; TPS=%.2f", count, elapsedTS,
                    (count * 1000.00D) / elapsedTS));
        }


        assertArrayEquals(data,plaintext);
        assertArrayEquals(data,plaintext);
    }

//    @Test
    public void signatureConsistencyTest() {

        int count = 10000;
        byte[] data = new byte[222];
        Random random = new Random();
        random.nextBytes(data);

        KeyPairGenerator keyPairGen = null;
        try {
            keyPairGen = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        assert keyPairGen != null;
        keyPairGen.initialize(2048, new SecureRandom());
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

        byte[] publicKeyBytes = publicKey.getEncoded();
        byte[] privateKeyBytes = privateKey.getEncoded();

        byte[] signature = null;
        boolean isValid = false;

        RSAKeyParameters pubKey = RSAUtils.bytes2PubKey_PKCS8(publicKeyBytes);
        RSAPrivateCrtKeyParameters privKey = RSAUtils.bytes2PrivKey_PKCS8(privateKeyBytes);

        System.out.println("=================== do BouncyCastle-based RSA sign test ===================");
        for (int r = 0; r < 5; r++) {
            System.out.println("------------- round[" + r + "] --------------");
            long startTS = System.currentTimeMillis();
            for (int i = 0; i < count; i++) {
                signature = RSAUtils.sign(data,privKey);
            }
            long elapsedTS = System.currentTimeMillis() - startTS;
            System.out.println(String.format("BouncyCastle-based RSA Signing Count=%s; Elapsed Times=%s; TPS=%.2f", count, elapsedTS,
                    (count * 1000.00D) / elapsedTS));
        }

        System.out.println("=================== do BouncyCastle-based RSA verify test ===================");
        for (int r = 0; r < 5; r++) {
            System.out.println("------------- round[" + r + "] --------------");
            long startTS = System.currentTimeMillis();
            for (int i = 0; i < count; i++) {
                isValid = RSAUtils.verify(data,pubKey,signature);
            }
            long elapsedTS = System.currentTimeMillis() - startTS;
            System.out.println(String.format("BouncyCastle-based RSA Verifying Count=%s; Elapsed Times=%s; TPS=%.2f", count, elapsedTS,
                    (count * 1000.00D) / elapsedTS));
        }




        System.out.println("=================== do JDK-based RSA sign test ===================");
        for (int r = 0; r < 5; r++) {
            System.out.println("------------- round[" + r + "] --------------");
            long startTS = System.currentTimeMillis();
            for (int i = 0; i < count; i++) {
                try {
                    Signature signer = Signature.getInstance("SHA256withRSA");
                    signer.initSign(privateKey);
                    signer.update(data);
                    signature = signer.sign();
                } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
                    e.printStackTrace();
                }
            }
            long elapsedTS = System.currentTimeMillis() - startTS;
            System.out.println(String.format("JDK-based RSA Signing Count=%s; Elapsed Times=%s; TPS=%.2f", count, elapsedTS,
                    (count * 1000.00D) / elapsedTS));
        }

        System.out.println("=================== do JDK-based RSA verify test ===================");
        for (int r = 0; r < 5; r++) {
            System.out.println("------------- round[" + r + "] --------------");
            long startTS = System.currentTimeMillis();
            for (int i = 0; i < count; i++) {
                try {
                    Signature verifier = Signature.getInstance("SHA256withRSA");
                    verifier.initVerify(publicKey);
                    verifier.update(data);
                    isValid = verifier.verify(signature);
                } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
                    e.printStackTrace();
                }
            }
            long elapsedTS = System.currentTimeMillis() - startTS;
            System.out.println(String.format("JDK-based RSA Verifying Count=%s; Elapsed Times=%s; TPS=%.2f", count, elapsedTS,
                    (count * 1000.00D) / elapsedTS));
        }

        System.out.println(isValid);

    }
}
