package test.com.jd.blockchain.crypto.service.sm;

import com.jd.blockchain.crypto.*;
import com.jd.blockchain.crypto.asymmetric.AsymmetricEncryptionFunction;
import com.jd.blockchain.crypto.asymmetric.CryptoKeyPair;
import com.jd.blockchain.crypto.asymmetric.SignatureDigest;
import com.jd.blockchain.crypto.asymmetric.SignatureFunction;
import com.jd.blockchain.utils.io.BytesUtils;
import org.junit.Test;

import java.util.Random;

import static com.jd.blockchain.crypto.CryptoAlgorithm.*;
import static com.jd.blockchain.crypto.CryptoKeyType.PRIVATE;
import static com.jd.blockchain.crypto.CryptoKeyType.PUBLIC;
import static org.junit.Assert.*;

/**
 * @author zhanglin33
 * @title: SM2CyptoFunctionTest
 * @description: JunitTest for SM2CyptoFunction in SPI mode
 * @date 2019-04-03, 16:32
 */
public class SM2CyptoFunctionTest {

    @Test
    public void getAlgorithmTest(){

        CryptoAlgorithm algorithm = CryptoServiceProviders.getAlgorithm("sm2");
        assertNotNull(algorithm);

        SignatureFunction signatureFunction = CryptoServiceProviders.getSignatureFunction(algorithm);


        assertEquals(signatureFunction.getAlgorithm().name(), algorithm.name());
        assertEquals(signatureFunction.getAlgorithm().code(), algorithm.code());

        algorithm = CryptoServiceProviders.getAlgorithm("sM2");
        assertNotNull(algorithm);

        assertEquals(signatureFunction.getAlgorithm().name(), algorithm.name());
        assertEquals(signatureFunction.getAlgorithm().code(), algorithm.code());

        algorithm = CryptoServiceProviders.getAlgorithm("sm22");
        assertNull(algorithm);
    }

    @Test
    public void generateKeyPairTest(){

        CryptoAlgorithm algorithm = CryptoServiceProviders.getAlgorithm("sm2");
        assertNotNull(algorithm);

        SignatureFunction signatureFunction = CryptoServiceProviders.getSignatureFunction(algorithm);

        CryptoKeyPair keyPair = signatureFunction.generateKeyPair();

        PubKey pubKey = keyPair.getPubKey();
        PrivKey privKey = keyPair.getPrivKey();

        assertEquals(PUBLIC.CODE,pubKey.getKeyType().CODE);
        assertEquals(65, pubKey.getRawKeyBytes().length);
        assertEquals(PRIVATE.CODE,privKey.getKeyType().CODE);
        assertEquals(32, privKey.getRawKeyBytes().length);

        assertEquals(algorithm.name(),pubKey.getAlgorithm().name());
        assertEquals(algorithm.code(),pubKey.getAlgorithm().code());
        assertEquals(algorithm.name(),privKey.getAlgorithm().name());
        assertEquals(algorithm.code(),privKey.getAlgorithm().code());

        assertEquals(2 + 1 + 65, pubKey.toBytes().length);
        assertEquals(2 + 1 + 32, privKey.toBytes().length);

        byte[] algoBytes = CryptoAlgorithm.toBytes(algorithm);
        byte[] pubKeyTypeBytes = new byte[] {PUBLIC.CODE};
        byte[] privKeyTypeBytes = new byte[] {PRIVATE.CODE};
        byte[] rawPubKeyBytes = pubKey.getRawKeyBytes();
        byte[] rawPrivKeyBytes = privKey.getRawKeyBytes();
        assertArrayEquals(BytesUtils.concat(algoBytes,pubKeyTypeBytes,rawPubKeyBytes),pubKey.toBytes());
        assertArrayEquals(BytesUtils.concat(algoBytes,privKeyTypeBytes,rawPrivKeyBytes),privKey.toBytes());
    }

    @Test
    public void retrievePubKeyTest(){

        CryptoAlgorithm algorithm = CryptoServiceProviders.getAlgorithm("sm2");
        assertNotNull(algorithm);

        SignatureFunction signatureFunction = CryptoServiceProviders.getSignatureFunction(algorithm);

        CryptoKeyPair keyPair = signatureFunction.generateKeyPair();

        PubKey pubKey = keyPair.getPubKey();
        PrivKey privKey = keyPair.getPrivKey();

        PubKey retrievedPubKey = signatureFunction.retrievePubKey(privKey);

        assertEquals(pubKey.getKeyType(),retrievedPubKey.getKeyType());
        assertEquals(pubKey.getRawKeyBytes().length, retrievedPubKey.getRawKeyBytes().length);
        assertEquals(pubKey.getAlgorithm().name(),retrievedPubKey.getAlgorithm().name());
        assertEquals(pubKey.getAlgorithm().code(),retrievedPubKey.getAlgorithm().code());
        assertArrayEquals(pubKey.toBytes(),retrievedPubKey.toBytes());
    }

    @Test
    public void signTest(){

        byte[] data = new byte[1024];
        Random random = new Random();
        random.nextBytes(data);

        CryptoAlgorithm algorithm = CryptoServiceProviders.getAlgorithm("sm2");
        assertNotNull(algorithm);

        SignatureFunction signatureFunction = CryptoServiceProviders.getSignatureFunction(algorithm);

        CryptoKeyPair keyPair = signatureFunction.generateKeyPair();

        PrivKey privKey = keyPair.getPrivKey();
        SignatureDigest signatureDigest = signatureFunction.sign(privKey,data);

        byte[] signatureBytes = signatureDigest.toBytes();

        assertEquals(2 + 64, signatureBytes.length);
        CryptoAlgorithm signatureAlgo = signatureDigest.getAlgorithm();
        assertEquals(algorithm.name(),signatureDigest.getAlgorithm().name());
        assertEquals(algorithm.code(),signatureDigest.getAlgorithm().code());

        assertEquals("SM2",signatureAlgo.name());
        assertEquals((short) (SIGNATURE_ALGORITHM | ENCRYPTION_ALGORITHM | ASYMMETRIC_KEY | ((byte) 2 & 0x00FF)),
                signatureAlgo.code());

        byte[] algoBytes = CryptoAlgorithm.toBytes(signatureAlgo);
        byte[] rawSinatureBytes = signatureDigest.getRawDigest();
        assertArrayEquals(BytesUtils.concat(algoBytes,rawSinatureBytes),signatureBytes);
    }

    @Test
    public void verifyTest(){
        byte[] data = new byte[1024];
        Random random = new Random();
        random.nextBytes(data);

        CryptoAlgorithm algorithm = CryptoServiceProviders.getAlgorithm("sm2");
        assertNotNull(algorithm);

        SignatureFunction signatureFunction = CryptoServiceProviders.getSignatureFunction(algorithm);

        CryptoKeyPair keyPair = signatureFunction.generateKeyPair();

        PubKey pubKey = keyPair.getPubKey();
        PrivKey privKey = keyPair.getPrivKey();
        SignatureDigest signatureDigest = signatureFunction.sign(privKey,data);

        assertTrue(signatureFunction.verify(signatureDigest,pubKey,data));
    }

    @Test
    public void encryptTest(){

        byte[] data = new byte[1024];
        Random random = new Random();
        random.nextBytes(data);

        CryptoAlgorithm algorithm = CryptoServiceProviders.getAlgorithm("sm2");
        assertNotNull(algorithm);

        AsymmetricEncryptionFunction asymmetricEncryptionFunction =
                CryptoServiceProviders.getAsymmetricEncryptionFunction(algorithm);

        CryptoKeyPair keyPair = asymmetricEncryptionFunction.generateKeyPair();

        PubKey pubKey = keyPair.getPubKey();

        Ciphertext ciphertext =asymmetricEncryptionFunction.encrypt(pubKey,data);

        byte[] ciphertextBytes = ciphertext.toBytes();
        assertEquals(2 + 65 + 256 / 8 + 1024, ciphertextBytes.length);
        CryptoAlgorithm ciphertextAlgo = ciphertext.getAlgorithm();
        assertEquals("SM2",ciphertext.getAlgorithm().name());
        assertEquals((short) (SIGNATURE_ALGORITHM | ENCRYPTION_ALGORITHM | ASYMMETRIC_KEY | ((byte) 2 & 0x00FF)),
                ciphertextAlgo.code());

        byte[] algoBytes = CryptoAlgorithm.toBytes(ciphertextAlgo);
        byte[] rawCiphertextBytes = ciphertext.getRawCiphertext();
        assertArrayEquals(BytesUtils.concat(algoBytes,rawCiphertextBytes),ciphertextBytes);
    }


    @Test
    public void decryptTest(){

        byte[] data = new byte[1024];
        Random random = new Random();
        random.nextBytes(data);

        CryptoAlgorithm algorithm = CryptoServiceProviders.getAlgorithm("sm2");
        assertNotNull(algorithm);

        AsymmetricEncryptionFunction asymmetricEncryptionFunction =
                CryptoServiceProviders.getAsymmetricEncryptionFunction(algorithm);

        CryptoKeyPair keyPair = asymmetricEncryptionFunction.generateKeyPair();

        PubKey pubKey = keyPair.getPubKey();
        PrivKey privKey = keyPair.getPrivKey();

        Ciphertext ciphertext =asymmetricEncryptionFunction.encrypt(pubKey,data);

        byte[] decryptedPlaintext = asymmetricEncryptionFunction.decrypt(privKey,ciphertext);

        assertArrayEquals(data,decryptedPlaintext);
    }


    @Test
    public void supportPrivKeyTest(){

        CryptoAlgorithm algorithm = CryptoServiceProviders.getAlgorithm("sm2");
        assertNotNull(algorithm);

        SignatureFunction signatureFunction = CryptoServiceProviders.getSignatureFunction(algorithm);

        CryptoKeyPair keyPair = signatureFunction.generateKeyPair();

        PrivKey privKey = keyPair.getPrivKey();
        byte[] privKeyBytes = privKey.toBytes();

        assertTrue(signatureFunction.supportPrivKey(privKeyBytes));

        algorithm = CryptoServiceProviders.getAlgorithm("sm3");
        assertNotNull(algorithm);
        byte[] algoBytes = CryptoAlgorithm.toBytes(algorithm);
        byte[] pubKeyTypeBytes = new byte[] {PUBLIC.CODE};
        byte[] rawKeyBytes = privKey.getRawKeyBytes();
        byte[] sm3PubKeyBytes = BytesUtils.concat(algoBytes,pubKeyTypeBytes,rawKeyBytes);

        assertFalse(signatureFunction.supportPrivKey(sm3PubKeyBytes));
    }

    @Test
    public void resolvePrivKeyTest(){

        CryptoAlgorithm algorithm = CryptoServiceProviders.getAlgorithm("sm2");
        assertNotNull(algorithm);

        SignatureFunction signatureFunction = CryptoServiceProviders.getSignatureFunction(algorithm);

        CryptoKeyPair keyPair = signatureFunction.generateKeyPair();

        PrivKey privKey = keyPair.getPrivKey();
        byte[] privKeyBytes = privKey.toBytes();

        PrivKey resolvedPrivKey = signatureFunction.resolvePrivKey(privKeyBytes);

        assertEquals(PRIVATE.CODE,resolvedPrivKey.getKeyType().CODE);
        assertEquals(32, resolvedPrivKey.getRawKeyBytes().length);
        assertEquals("SM2",resolvedPrivKey.getAlgorithm().name());
        assertEquals((short) (SIGNATURE_ALGORITHM | ENCRYPTION_ALGORITHM | ASYMMETRIC_KEY | ((byte) 2 & 0x00FF)),
                resolvedPrivKey.getAlgorithm().code());
        assertArrayEquals(privKeyBytes,resolvedPrivKey.toBytes());

        algorithm = CryptoServiceProviders.getAlgorithm("sm3");
        assertNotNull(algorithm);
        byte[] algoBytes = CryptoAlgorithm.toBytes(algorithm);
        byte[] pubKeyTypeBytes = new byte[] {PUBLIC.CODE};
        byte[] rawKeyBytes = privKey.getRawKeyBytes();
        byte[] sm3PubKeyBytes = BytesUtils.concat(algoBytes,pubKeyTypeBytes,rawKeyBytes);

        Class<?> expectedException = CryptoException.class;
        Exception actualEx = null;
        try {
            signatureFunction.resolvePrivKey(sm3PubKeyBytes);
        } catch (Exception e) {
            actualEx = e;
        }
        assertNotNull(actualEx);
        assertTrue(expectedException.isAssignableFrom(actualEx.getClass()));
    }

    @Test
    public void supportPubKeyTest(){

        CryptoAlgorithm algorithm = CryptoServiceProviders.getAlgorithm("sm2");
        assertNotNull(algorithm);

        SignatureFunction signatureFunction = CryptoServiceProviders.getSignatureFunction(algorithm);

        CryptoKeyPair keyPair = signatureFunction.generateKeyPair();

        PubKey pubKey = keyPair.getPubKey();
        byte[] pubKeyBytes = pubKey.toBytes();

        assertTrue(signatureFunction.supportPubKey(pubKeyBytes));

        algorithm = CryptoServiceProviders.getAlgorithm("sm3");
        assertNotNull(algorithm);
        byte[] algoBytes = CryptoAlgorithm.toBytes(algorithm);
        byte[] privKeyTypeBytes = new byte[] {PRIVATE.CODE};
        byte[] rawKeyBytes = pubKey.getRawKeyBytes();
        byte[] sm3PrivKeyBytes = BytesUtils.concat(algoBytes,privKeyTypeBytes,rawKeyBytes);

        assertFalse(signatureFunction.supportPubKey(sm3PrivKeyBytes));
    }

    @Test
    public void resolvePubKeyTest(){

        CryptoAlgorithm algorithm = CryptoServiceProviders.getAlgorithm("sm2");
        assertNotNull(algorithm);

        SignatureFunction signatureFunction = CryptoServiceProviders.getSignatureFunction(algorithm);

        CryptoKeyPair keyPair = signatureFunction.generateKeyPair();

        PubKey pubKey = keyPair.getPubKey();
        byte[] pubKeyBytes = pubKey.toBytes();

        PubKey resolvedPubKey = signatureFunction.resolvePubKey(pubKeyBytes);

        assertEquals(PUBLIC.CODE,resolvedPubKey.getKeyType().CODE);
        assertEquals(65, resolvedPubKey.getRawKeyBytes().length);
        assertEquals("SM2",resolvedPubKey.getAlgorithm().name());
        assertEquals((short) (SIGNATURE_ALGORITHM | ENCRYPTION_ALGORITHM | ASYMMETRIC_KEY | ((byte) 2 & 0x00FF)),
                resolvedPubKey.getAlgorithm().code());
        assertArrayEquals(pubKeyBytes,resolvedPubKey.toBytes());

        algorithm = CryptoServiceProviders.getAlgorithm("sm3");
        assertNotNull(algorithm);
        byte[] algoBytes = CryptoAlgorithm.toBytes(algorithm);
        byte[] privKeyTypeBytes = new byte[] {PRIVATE.CODE};
        byte[] rawKeyBytes = pubKey.getRawKeyBytes();
        byte[] sm3PrivKeyBytes = BytesUtils.concat(algoBytes,privKeyTypeBytes,rawKeyBytes);

        Class<?> expectedException = CryptoException.class;
        Exception actualEx = null;
        try {
            signatureFunction.resolvePrivKey(sm3PrivKeyBytes);
        } catch (Exception e) {
            actualEx = e;
        }
        assertNotNull(actualEx);
        assertTrue(expectedException.isAssignableFrom(actualEx.getClass()));
    }

    @Test
    public void supportDigestTest(){

        byte[] data = new byte[1024];
        Random random = new Random();
        random.nextBytes(data);

        CryptoAlgorithm algorithm = CryptoServiceProviders.getAlgorithm("sm2");
        assertNotNull(algorithm);

        SignatureFunction signatureFunction =
                CryptoServiceProviders.getSignatureFunction(algorithm);

        CryptoKeyPair keyPair = signatureFunction.generateKeyPair();

        PrivKey privKey = keyPair.getPrivKey();

        SignatureDigest signatureDigest = signatureFunction.sign(privKey,data);

        byte[] signatureDigestBytes = signatureDigest.toBytes();
        assertTrue(signatureFunction.supportDigest(signatureDigestBytes));

        algorithm = CryptoServiceProviders.getAlgorithm("sm3");
        assertNotNull(algorithm);
        byte[] algoBytes = CryptoAlgorithm.toBytes(algorithm);
        byte[] rawDigestBytes = signatureDigest.toBytes();
        byte[] sm3SignatureBytes = BytesUtils.concat(algoBytes,rawDigestBytes);

        assertFalse(signatureFunction.supportDigest(sm3SignatureBytes));
    }

    @Test
    public void resolveDigestTest(){

        byte[] data = new byte[1024];
        Random random = new Random();
        random.nextBytes(data);

        CryptoAlgorithm algorithm = CryptoServiceProviders.getAlgorithm("sm2");
        assertNotNull(algorithm);

        SignatureFunction signatureFunction =
                CryptoServiceProviders.getSignatureFunction(algorithm);

        CryptoKeyPair keyPair = signatureFunction.generateKeyPair();

        PrivKey privKey = keyPair.getPrivKey();

        SignatureDigest signatureDigest = signatureFunction.sign(privKey,data);

        byte[] signatureDigestBytes = signatureDigest.toBytes();

        SignatureDigest resolvedSignatureDigest = signatureFunction.resolveDigest(signatureDigestBytes);

        assertEquals(64, resolvedSignatureDigest.getRawDigest().length);
        assertEquals("SM2",resolvedSignatureDigest.getAlgorithm().name());
        assertEquals((short) (SIGNATURE_ALGORITHM | ENCRYPTION_ALGORITHM | ASYMMETRIC_KEY | ((byte) 2 & 0x00FF)),
                resolvedSignatureDigest.getAlgorithm().code());
        assertArrayEquals(signatureDigestBytes,resolvedSignatureDigest.toBytes());

        algorithm = CryptoServiceProviders.getAlgorithm("sm3");
        assertNotNull(algorithm);
        byte[] algoBytes = CryptoAlgorithm.toBytes(algorithm);
        byte[] rawDigestBytes =  signatureDigest.getRawDigest();
        byte[] sm3SignatureDigestBytes = BytesUtils.concat(algoBytes,rawDigestBytes);

        Class<?> expectedException = CryptoException.class;
        Exception actualEx = null;
        try {
            signatureFunction.resolveDigest(sm3SignatureDigestBytes);
        } catch (Exception e) {
            actualEx = e;
        }
        assertNotNull(actualEx);
        assertTrue(expectedException.isAssignableFrom(actualEx.getClass()));
    }

    @Test
    public void supportCiphertextTest(){

        byte[] data = new byte[1024];
        Random random = new Random();
        random.nextBytes(data);

        CryptoAlgorithm algorithm = CryptoServiceProviders.getAlgorithm("sm2");
        assertNotNull(algorithm);

        AsymmetricEncryptionFunction asymmetricEncryptionFunction =
                CryptoServiceProviders.getAsymmetricEncryptionFunction(algorithm);

        CryptoKeyPair keyPair = asymmetricEncryptionFunction.generateKeyPair();

        PubKey pubKey = keyPair.getPubKey();

        Ciphertext ciphertext =asymmetricEncryptionFunction.encrypt(pubKey,data);

        byte[] ciphertextBytes = ciphertext.toBytes();

        assertTrue(asymmetricEncryptionFunction.supportCiphertext(ciphertextBytes));

        algorithm = CryptoServiceProviders.getAlgorithm("sm3");
        assertNotNull(algorithm);
        byte[] algoBytes = CryptoAlgorithm.toBytes(algorithm);
        byte[] rawCiphertextBytes = ciphertext.toBytes();
        byte[] sm3CiphertextBytes = BytesUtils.concat(algoBytes,rawCiphertextBytes);

        assertFalse(asymmetricEncryptionFunction.supportCiphertext(sm3CiphertextBytes));
    }

    @Test
    public void resolveCiphertextTest(){

        byte[] data = new byte[1024];
        Random random = new Random();
        random.nextBytes(data);

        CryptoAlgorithm algorithm = CryptoServiceProviders.getAlgorithm("sm2");
        assertNotNull(algorithm);

        AsymmetricEncryptionFunction asymmetricEncryptionFunction =
                CryptoServiceProviders.getAsymmetricEncryptionFunction(algorithm);

        CryptoKeyPair keyPair = asymmetricEncryptionFunction.generateKeyPair();

        PubKey pubKey = keyPair.getPubKey();

        Ciphertext ciphertext =asymmetricEncryptionFunction.encrypt(pubKey,data);

        byte[] ciphertextBytes = ciphertext.toBytes();

        Ciphertext resolvedCiphertext = asymmetricEncryptionFunction.resolveCiphertext(ciphertextBytes);

        assertEquals(65 + 256 / 8 + 1024, resolvedCiphertext.getRawCiphertext().length);
        assertEquals("SM2",resolvedCiphertext.getAlgorithm().name());
        assertEquals((short) (SIGNATURE_ALGORITHM | ENCRYPTION_ALGORITHM | ASYMMETRIC_KEY | ((byte) 2 & 0x00FF)),
                resolvedCiphertext.getAlgorithm().code());
        assertArrayEquals(ciphertextBytes,resolvedCiphertext.toBytes());

        algorithm = CryptoServiceProviders.getAlgorithm("sm3");
        assertNotNull(algorithm);
        byte[] algoBytes = CryptoAlgorithm.toBytes(algorithm);
        byte[] rawCiphertextBytes =  ciphertext.getRawCiphertext();
        byte[] sm3CiphertextBytes = BytesUtils.concat(algoBytes,rawCiphertextBytes);

        Class<?> expectedException = CryptoException.class;
        Exception actualEx = null;
        try {
            asymmetricEncryptionFunction.resolveCiphertext(sm3CiphertextBytes);
        } catch (Exception e) {
            actualEx = e;
        }
        assertNotNull(actualEx);
        assertTrue(expectedException.isAssignableFrom(actualEx.getClass()));
    }

}
