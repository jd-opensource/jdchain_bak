package test.com.jd.blockchain.crypto.service.classic;

import com.jd.blockchain.crypto.*;
import com.jd.blockchain.utils.io.BytesUtils;
import org.junit.Test;

import java.util.Random;

import static com.jd.blockchain.crypto.CryptoAlgorithm.ASYMMETRIC_KEY;
import static com.jd.blockchain.crypto.CryptoAlgorithm.SIGNATURE_ALGORITHM;
import static com.jd.blockchain.crypto.CryptoKeyType.PRIVATE;
import static com.jd.blockchain.crypto.CryptoKeyType.PUBLIC;
import static org.junit.Assert.*;

/**
 * @author zhanglin33
 * @title: ED25519SignatureFunctionTest
 * @description: JunitTest for ED25519SignatureFunction in SPI mode
 * @date 2019-04-01, 14:01
 */
public class ED25519SignatureFunctionTest {

    @Test
    public void getAlgorithmTest(){

        CryptoAlgorithm algorithm = CryptoServiceProviders.getAlgorithm("ed25519");
        assertNotNull(algorithm);

        SignatureFunction signatureFunction = CryptoServiceProviders.getSignatureFunction(algorithm);


        assertEquals(signatureFunction.getAlgorithm().name(), algorithm.name());
        assertEquals(signatureFunction.getAlgorithm().code(), algorithm.code());

        algorithm = CryptoServiceProviders.getAlgorithm("Ed25519");
        assertNotNull(algorithm);

        assertEquals(signatureFunction.getAlgorithm().name(), algorithm.name());
        assertEquals(signatureFunction.getAlgorithm().code(), algorithm.code());

        algorithm = CryptoServiceProviders.getAlgorithm("eddsa");
        assertNull(algorithm);
    }

    @Test
    public void generateKeyPairTest(){

        CryptoAlgorithm algorithm = CryptoServiceProviders.getAlgorithm("ed25519");
        assertNotNull(algorithm);

        SignatureFunction signatureFunction = CryptoServiceProviders.getSignatureFunction(algorithm);

        CryptoKeyPair keyPair = signatureFunction.generateKeyPair();

        PubKey pubKey = keyPair.getPubKey();
        PrivKey privKey = keyPair.getPrivKey();

        assertEquals(PUBLIC.CODE,pubKey.getKeyType().CODE);
        assertEquals(32, pubKey.getRawKeyBytes().length);
        assertEquals(PRIVATE.CODE,privKey.getKeyType().CODE);
        assertEquals(32, privKey.getRawKeyBytes().length);

        assertEquals(algorithm.name(),pubKey.getAlgorithm().name());
        assertEquals(algorithm.code(),pubKey.getAlgorithm().code());
        assertEquals(algorithm.name(),privKey.getAlgorithm().name());
        assertEquals(algorithm.code(),privKey.getAlgorithm().code());

        assertEquals(2 + 1 + 32, pubKey.toBytes().length);
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

        CryptoAlgorithm algorithm = CryptoServiceProviders.getAlgorithm("ed25519");
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

        CryptoAlgorithm algorithm = CryptoServiceProviders.getAlgorithm("ed25519");
        assertNotNull(algorithm);

        SignatureFunction signatureFunction = CryptoServiceProviders.getSignatureFunction(algorithm);

        CryptoKeyPair keyPair = signatureFunction.generateKeyPair();

        PrivKey privKey = keyPair.getPrivKey();
        SignatureDigest signatureDigest = signatureFunction.sign(privKey,data);

        byte[] signatureBytes = signatureDigest.toBytes();

        assertEquals(2 + 64, signatureBytes.length);
        CryptoAlgorithm signatureAlgo = signatureDigest.getAlgorithm();
        assertEquals("ED25519",signatureAlgo.name());
        assertEquals((short) (SIGNATURE_ALGORITHM | ASYMMETRIC_KEY | ((byte) 21 & 0x00FF)),
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

        CryptoAlgorithm algorithm = CryptoServiceProviders.getAlgorithm("ed25519");
        assertNotNull(algorithm);

        SignatureFunction signatureFunction = CryptoServiceProviders.getSignatureFunction(algorithm);

        CryptoKeyPair keyPair = signatureFunction.generateKeyPair();

        PubKey pubKey = keyPair.getPubKey();
        PrivKey privKey = keyPair.getPrivKey();
        SignatureDigest signatureDigest = signatureFunction.sign(privKey,data);

        assertTrue(signatureFunction.verify(signatureDigest,pubKey,data));
    }


    @Test
    public void supportPrivKeyTest(){

        CryptoAlgorithm algorithm = CryptoServiceProviders.getAlgorithm("ed25519");
        assertNotNull(algorithm);

        SignatureFunction signatureFunction = CryptoServiceProviders.getSignatureFunction(algorithm);

        CryptoKeyPair keyPair = signatureFunction.generateKeyPair();

        PrivKey privKey = keyPair.getPrivKey();
        byte[] privKeyBytes = privKey.toBytes();

        assertTrue(signatureFunction.supportPrivKey(privKeyBytes));

        algorithm = CryptoServiceProviders.getAlgorithm("ripemd160");
        assertNotNull(algorithm);
        byte[] algoBytes = CryptoAlgorithm.toBytes(algorithm);
        byte[] pubKeyTypeBytes = new byte[] {PUBLIC.CODE};
        byte[] rawKeyBytes = privKey.getRawKeyBytes();
        byte[] ripemd160PubKeyBytes = BytesUtils.concat(algoBytes,pubKeyTypeBytes,rawKeyBytes);

        assertFalse(signatureFunction.supportPrivKey(ripemd160PubKeyBytes));
    }

    @Test
    public void resolvePrivKeyTest(){

        CryptoAlgorithm algorithm = CryptoServiceProviders.getAlgorithm("ed25519");
        assertNotNull(algorithm);

        SignatureFunction signatureFunction = CryptoServiceProviders.getSignatureFunction(algorithm);

        CryptoKeyPair keyPair = signatureFunction.generateKeyPair();

        PrivKey privKey = keyPair.getPrivKey();
        byte[] privKeyBytes = privKey.toBytes();

        PrivKey resolvedPrivKey = signatureFunction.resolvePrivKey(privKeyBytes);

        assertEquals(PRIVATE.CODE,resolvedPrivKey.getKeyType().CODE);
        assertEquals(32, resolvedPrivKey.getRawKeyBytes().length);
        assertEquals("ED25519",resolvedPrivKey.getAlgorithm().name());
        assertEquals((short) (SIGNATURE_ALGORITHM | ASYMMETRIC_KEY | ((byte) 21 & 0x00FF)),
                resolvedPrivKey.getAlgorithm().code());
        assertArrayEquals(privKeyBytes,resolvedPrivKey.toBytes());

        algorithm = CryptoServiceProviders.getAlgorithm("ripemd160");
        assertNotNull(algorithm);
        byte[] algoBytes = CryptoAlgorithm.toBytes(algorithm);
        byte[] pubKeyTypeBytes = new byte[] {PUBLIC.CODE};
        byte[] rawKeyBytes = privKey.getRawKeyBytes();
        byte[] ripemd160PubKeyBytes = BytesUtils.concat(algoBytes,pubKeyTypeBytes,rawKeyBytes);

        Class<?> expectedException = CryptoException.class;
        Exception actualEx = null;
        try {
            signatureFunction.resolvePrivKey(ripemd160PubKeyBytes);
        } catch (Exception e) {
            actualEx = e;
        }
        assertNotNull(actualEx);
        assertTrue(expectedException.isAssignableFrom(actualEx.getClass()));
    }

    @Test
    public void supportPubKeyTest(){

        CryptoAlgorithm algorithm = CryptoServiceProviders.getAlgorithm("ed25519");
        assertNotNull(algorithm);

        SignatureFunction signatureFunction = CryptoServiceProviders.getSignatureFunction(algorithm);

        CryptoKeyPair keyPair = signatureFunction.generateKeyPair();

        PubKey pubKey = keyPair.getPubKey();
        byte[] pubKeyBytes = pubKey.toBytes();

        assertTrue(signatureFunction.supportPubKey(pubKeyBytes));

        algorithm = CryptoServiceProviders.getAlgorithm("ripemd160");
        assertNotNull(algorithm);
        byte[] algoBytes = CryptoAlgorithm.toBytes(algorithm);
        byte[] privKeyTypeBytes = new byte[] {PRIVATE.CODE};
        byte[] rawKeyBytes = pubKey.getRawKeyBytes();
        byte[] ripemd160PrivKeyBytes = BytesUtils.concat(algoBytes,privKeyTypeBytes,rawKeyBytes);

        assertFalse(signatureFunction.supportPubKey(ripemd160PrivKeyBytes));
    }

    @Test
    public void resolvePubKeyTest(){

        CryptoAlgorithm algorithm = CryptoServiceProviders.getAlgorithm("ed25519");
        assertNotNull(algorithm);

        SignatureFunction signatureFunction = CryptoServiceProviders.getSignatureFunction(algorithm);

        CryptoKeyPair keyPair = signatureFunction.generateKeyPair();

        PubKey pubKey = keyPair.getPubKey();
        byte[] pubKeyBytes = pubKey.toBytes();

        PubKey resolvedPubKey = signatureFunction.resolvePubKey(pubKeyBytes);

        assertEquals(PUBLIC.CODE,resolvedPubKey.getKeyType().CODE);
        assertEquals(32, resolvedPubKey.getRawKeyBytes().length);
        assertEquals("ED25519",resolvedPubKey.getAlgorithm().name());
        assertEquals((short) (SIGNATURE_ALGORITHM | ASYMMETRIC_KEY | ((byte) 21 & 0x00FF)),
                resolvedPubKey.getAlgorithm().code());
        assertArrayEquals(pubKeyBytes,resolvedPubKey.toBytes());

        algorithm = CryptoServiceProviders.getAlgorithm("ripemd160");
        assertNotNull(algorithm);
        byte[] algoBytes = CryptoAlgorithm.toBytes(algorithm);
        byte[] privKeyTypeBytes = new byte[] {PRIVATE.CODE};
        byte[] rawKeyBytes = pubKey.getRawKeyBytes();
        byte[] ripemd160PrivKeyBytes = BytesUtils.concat(algoBytes,privKeyTypeBytes,rawKeyBytes);


        Class<?> expectedException = CryptoException.class;
        Exception actualEx = null;
        try {
            signatureFunction.resolvePrivKey(ripemd160PrivKeyBytes);
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

        CryptoAlgorithm algorithm = CryptoServiceProviders.getAlgorithm("ed25519");
        assertNotNull(algorithm);

        SignatureFunction signatureFunction =
                CryptoServiceProviders.getSignatureFunction(algorithm);

        CryptoKeyPair keyPair = signatureFunction.generateKeyPair();

        PrivKey privKey = keyPair.getPrivKey();

        SignatureDigest signatureDigest = signatureFunction.sign(privKey,data);

        byte[] signatureDigestBytes = signatureDigest.toBytes();
        assertTrue(signatureFunction.supportDigest(signatureDigestBytes));

        algorithm = CryptoServiceProviders.getAlgorithm("ripemd160");
        assertNotNull(algorithm);
        byte[] algoBytes = CryptoAlgorithm.toBytes(algorithm);
        byte[] rawDigestBytes = signatureDigest.toBytes();
        byte[] ripemd160SignatureBytes = BytesUtils.concat(algoBytes,rawDigestBytes);

        assertFalse(signatureFunction.supportDigest(ripemd160SignatureBytes));
    }

    @Test
    public void resolveDigestTest(){

        byte[] data = new byte[1024];
        Random random = new Random();
        random.nextBytes(data);

        CryptoAlgorithm algorithm = CryptoServiceProviders.getAlgorithm("ed25519");
        assertNotNull(algorithm);

        SignatureFunction signatureFunction =
                CryptoServiceProviders.getSignatureFunction(algorithm);

        CryptoKeyPair keyPair = signatureFunction.generateKeyPair();

        PrivKey privKey = keyPair.getPrivKey();

        SignatureDigest signatureDigest = signatureFunction.sign(privKey,data);

        byte[] signatureDigestBytes = signatureDigest.toBytes();

        SignatureDigest resolvedSignatureDigest = signatureFunction.resolveDigest(signatureDigestBytes);

        assertEquals(64, resolvedSignatureDigest.getRawDigest().length);
        assertEquals("ED25519",resolvedSignatureDigest.getAlgorithm().name());
        assertEquals((short) (SIGNATURE_ALGORITHM | ASYMMETRIC_KEY | ((byte) 21 & 0x00FF)),
                resolvedSignatureDigest.getAlgorithm().code());
        assertArrayEquals(signatureDigestBytes,resolvedSignatureDigest.toBytes());

        algorithm = CryptoServiceProviders.getAlgorithm("ripemd160");
        assertNotNull(algorithm);
        byte[] algoBytes = CryptoAlgorithm.toBytes(algorithm);
        byte[] rawDigestBytes =  signatureDigest.getRawDigest();
        byte[] ripemd160SignatureDigestBytes = BytesUtils.concat(algoBytes,rawDigestBytes);

        Class<?> expectedException = CryptoException.class;
        Exception actualEx = null;
        try {
            signatureFunction.resolveDigest(ripemd160SignatureDigestBytes);
        } catch (Exception e) {
            actualEx = e;
        }
        assertNotNull(actualEx);
        assertTrue(expectedException.isAssignableFrom(actualEx.getClass()));
    }
}
