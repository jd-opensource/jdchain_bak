package test.com.jd.blockchain.crypto.service.classic;

import com.jd.blockchain.crypto.*;
import com.jd.blockchain.crypto.service.classic.ClassicAlgorithm;
import com.jd.blockchain.utils.io.BytesUtils;
import org.junit.Test;

import java.util.Random;

import static com.jd.blockchain.crypto.CryptoAlgorithm.*;
import static com.jd.blockchain.crypto.CryptoKeyType.PRIVATE;
import static com.jd.blockchain.crypto.CryptoKeyType.PUBLIC;
import static org.junit.Assert.*;

/**
 * @author zhanglin33
 * @title: RSACryptoFunctionTest
 * @description: JunitTest for RSACryptoFunction in SPI mode
 * @date 2019-04-23, 15:30
 */
public class RSACryptoFunctionTest {

    @Test
    public void getAlgorithmTest() {

        CryptoAlgorithm algorithm = Crypto.getAlgorithm("RSA");
        assertNotNull(algorithm);

        SignatureFunction signatureFunction = Crypto.getSignatureFunction(algorithm);

        assertEquals(signatureFunction.getAlgorithm().name(), algorithm.name());
        assertEquals(signatureFunction.getAlgorithm().code(), algorithm.code());

        algorithm = Crypto.getAlgorithm("Rsa");
        assertNotNull(algorithm);

        assertEquals(signatureFunction.getAlgorithm().name(), algorithm.name());
        assertEquals(signatureFunction.getAlgorithm().code(), algorithm.code());

        algorithm = Crypto.getAlgorithm("rsa2");
        assertNull(algorithm);
    }

    @Test
    public void test() {

        CryptoAlgorithm algorithm = Crypto.getAlgorithm("RSA");
        assertNotNull(algorithm);

        SignatureFunction signatureFunction = Crypto.getSignatureFunction(algorithm);

        AsymmetricKeypair keyPair = signatureFunction.generateKeypair();

        PubKey pubKey = keyPair.getPubKey();
        PrivKey privKey = keyPair.getPrivKey();

        assertEquals(PUBLIC.CODE, pubKey.getKeyType().CODE);
        assertEquals(259, pubKey.getRawKeyBytes().length);
        assertEquals(PRIVATE.CODE, privKey.getKeyType().CODE);
        assertEquals(1155, privKey.getRawKeyBytes().length);

        assertEquals(algorithm.code(), pubKey.getAlgorithm());
        assertEquals(algorithm.code(), privKey.getAlgorithm());

        assertEquals(2 + 1 + 259, pubKey.toBytes().length);
        assertEquals(2 + 1 + 1155, privKey.toBytes().length);

        byte[] algoBytes = CryptoAlgorithm.getCodeBytes(algorithm);
        byte[] pubKeyTypeBytes = new byte[] { PUBLIC.CODE };
        byte[] privKeyTypeBytes = new byte[] { PRIVATE.CODE };
        byte[] rawPubKeyBytes = pubKey.getRawKeyBytes();
        byte[] rawPrivKeyBytes = privKey.getRawKeyBytes();
        assertArrayEquals(BytesUtils.concat(algoBytes, pubKeyTypeBytes, rawPubKeyBytes), pubKey.toBytes());
        assertArrayEquals(BytesUtils.concat(algoBytes, privKeyTypeBytes, rawPrivKeyBytes), privKey.toBytes());


        PubKey retrievedPubKey = signatureFunction.retrievePubKey(privKey);

        assertEquals(pubKey.getKeyType(), retrievedPubKey.getKeyType());
        assertEquals(pubKey.getRawKeyBytes().length, retrievedPubKey.getRawKeyBytes().length);
        assertEquals(pubKey.getAlgorithm(), retrievedPubKey.getAlgorithm());
        assertArrayEquals(pubKey.toBytes(), retrievedPubKey.toBytes());


        byte[] data = new byte[128];
        Random random = new Random();
        random.nextBytes(data);

        SignatureDigest signatureDigest = signatureFunction.sign(privKey, data);

        byte[] signatureBytes = signatureDigest.toBytes();

        assertEquals(2 + 256, signatureBytes.length);
        assertEquals(algorithm.code(), signatureDigest.getAlgorithm());

        assertEquals(ClassicAlgorithm.RSA.code(), signatureDigest.getAlgorithm());
        assertEquals((short) (SIGNATURE_ALGORITHM | ENCRYPTION_ALGORITHM | ASYMMETRIC_KEY | ((byte) 23 & 0x00FF)),
                signatureDigest.getAlgorithm());

        byte[] rawSinatureBytes = signatureDigest.getRawDigest();
        assertArrayEquals(BytesUtils.concat(algoBytes, rawSinatureBytes), signatureBytes);


        assertTrue(signatureFunction.verify(signatureDigest, pubKey, data));


        AsymmetricEncryptionFunction asymmetricEncryptionFunction = Crypto
                .getAsymmetricEncryptionFunction(algorithm);

        Ciphertext ciphertext = asymmetricEncryptionFunction.encrypt(pubKey, data);

        byte[] ciphertextBytes = ciphertext.toBytes();
        assertEquals(2 + 256, ciphertextBytes.length);
        assertEquals(ClassicAlgorithm.RSA.code(), ciphertext.getAlgorithm());

        assertEquals((short) (SIGNATURE_ALGORITHM | ENCRYPTION_ALGORITHM | ASYMMETRIC_KEY | ((byte) 23 & 0x00FF)),
                ciphertext.getAlgorithm());

        byte[] rawCiphertextBytes = ciphertext.getRawCiphertext();
        assertArrayEquals(BytesUtils.concat(algoBytes, rawCiphertextBytes), ciphertextBytes);


        byte[] decryptedPlaintext = asymmetricEncryptionFunction.decrypt(privKey, ciphertext);
        assertArrayEquals(data, decryptedPlaintext);


        byte[] privKeyBytes = privKey.toBytes();
        assertTrue(signatureFunction.supportPrivKey(privKeyBytes));
        algorithm = Crypto.getAlgorithm("ripemd160");
        assertNotNull(algorithm);
        byte[] rawKeyBytes = privKey.getRawKeyBytes();
        byte[] ripemd160PubKeyBytes = BytesUtils.concat(algoBytes, pubKeyTypeBytes, rawKeyBytes);
        assertFalse(signatureFunction.supportPrivKey(ripemd160PubKeyBytes));


        PrivKey resolvedPrivKey = signatureFunction.resolvePrivKey(privKeyBytes);
        assertEquals(PRIVATE.CODE, resolvedPrivKey.getKeyType().CODE);
        assertEquals(1155, resolvedPrivKey.getRawKeyBytes().length);
        assertEquals(ClassicAlgorithm.RSA.code(), resolvedPrivKey.getAlgorithm());
        assertEquals((short) (SIGNATURE_ALGORITHM | ENCRYPTION_ALGORITHM | ASYMMETRIC_KEY | ((byte) 23 & 0x00FF)),
                resolvedPrivKey.getAlgorithm());
        assertArrayEquals(privKeyBytes, resolvedPrivKey.toBytes());
        algorithm = Crypto.getAlgorithm("ripemd160");
        assertNotNull(algorithm);
        Class<?> expectedException = CryptoException.class;
        Exception actualEx = null;
        try {
            signatureFunction.resolvePrivKey(ripemd160PubKeyBytes);
        } catch (Exception e) {
            actualEx = e;
        }
        assertNotNull(actualEx);
        assertTrue(expectedException.isAssignableFrom(actualEx.getClass()));



        byte[] pubKeyBytes = pubKey.toBytes();
        assertTrue(signatureFunction.supportPubKey(pubKeyBytes));
        algorithm = Crypto.getAlgorithm("ripemd160");
        assertNotNull(algorithm);
        byte[] ripemd160PrivKeyBytes = BytesUtils.concat(algoBytes, privKeyTypeBytes, rawKeyBytes);
        assertFalse(signatureFunction.supportPubKey(ripemd160PrivKeyBytes));


        PubKey resolvedPubKey = signatureFunction.resolvePubKey(pubKeyBytes);
        assertEquals(PUBLIC.CODE, resolvedPubKey.getKeyType().CODE);
        assertEquals(259, resolvedPubKey.getRawKeyBytes().length);
        assertEquals(ClassicAlgorithm.RSA.code(), resolvedPubKey.getAlgorithm());
        assertEquals((short) (SIGNATURE_ALGORITHM | ENCRYPTION_ALGORITHM | ASYMMETRIC_KEY | ((byte) 23 & 0x00FF)),
                resolvedPubKey.getAlgorithm());
        assertArrayEquals(pubKeyBytes, resolvedPubKey.toBytes());
        algorithm = Crypto.getAlgorithm("ripemd160");
        assertNotNull(algorithm);
        try {
            signatureFunction.resolvePrivKey(ripemd160PrivKeyBytes);
        } catch (Exception e) {
            actualEx = e;
        }
        assertNotNull(actualEx);
        assertTrue(expectedException.isAssignableFrom(actualEx.getClass()));


        byte[] signatureDigestBytes = signatureDigest.toBytes();
        assertTrue(signatureFunction.supportDigest(signatureDigestBytes));

        algorithm = Crypto.getAlgorithm("ripemd160");
        assertNotNull(algorithm);
        byte[] rawDigestBytes = signatureDigest.toBytes();
        byte[] ripemd160SignatureBytes = BytesUtils.concat(algoBytes, rawDigestBytes);

        assertFalse(signatureFunction.supportDigest(ripemd160SignatureBytes));


        SignatureDigest resolvedSignatureDigest = signatureFunction.resolveDigest(signatureDigestBytes);

        assertEquals(256, resolvedSignatureDigest.getRawDigest().length);
        assertEquals(ClassicAlgorithm.RSA.code(), resolvedSignatureDigest.getAlgorithm());
        assertEquals((short) (SIGNATURE_ALGORITHM | ENCRYPTION_ALGORITHM | ASYMMETRIC_KEY | ((byte) 23 & 0x00FF)),
                resolvedSignatureDigest.getAlgorithm());
        assertArrayEquals(signatureDigestBytes, resolvedSignatureDigest.toBytes());

        algorithm = Crypto.getAlgorithm("ripemd160");
        assertNotNull(algorithm);
        byte[] ripemd160SignatureDigestBytes = BytesUtils.concat(algoBytes, rawDigestBytes);

        try {
            signatureFunction.resolveDigest(ripemd160SignatureDigestBytes);
        } catch (Exception e) {
            actualEx = e;
        }
        assertNotNull(actualEx);
        assertTrue(expectedException.isAssignableFrom(actualEx.getClass()));


        assertTrue(asymmetricEncryptionFunction.supportCiphertext(ciphertextBytes));
        algorithm = Crypto.getAlgorithm("ripemd160");
        assertNotNull(algorithm);
        algoBytes = CryptoAlgorithm.getCodeBytes(algorithm);
        byte[] ripemd160CiphertextBytes = BytesUtils.concat(algoBytes, rawCiphertextBytes);
        assertFalse(asymmetricEncryptionFunction.supportCiphertext(ripemd160CiphertextBytes));


        Ciphertext resolvedCiphertext = asymmetricEncryptionFunction.resolveCiphertext(ciphertextBytes);
        assertEquals(256, resolvedCiphertext.getRawCiphertext().length);
        assertEquals(ClassicAlgorithm.RSA.code(), resolvedCiphertext.getAlgorithm());
        assertEquals((short) (SIGNATURE_ALGORITHM | ENCRYPTION_ALGORITHM | ASYMMETRIC_KEY | ((byte) 23 & 0x00FF)),
                resolvedCiphertext.getAlgorithm());
        assertArrayEquals(ciphertextBytes, resolvedCiphertext.toBytes());
        algorithm = Crypto.getAlgorithm("ripemd160");
        assertNotNull(algorithm);
        try {
            asymmetricEncryptionFunction.resolveCiphertext(ripemd160CiphertextBytes);
        } catch (Exception e) {
            actualEx = e;
        }
        assertNotNull(actualEx);
        assertTrue(expectedException.isAssignableFrom(actualEx.getClass()));
    }
}
