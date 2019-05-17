package com.jd.blockchain.crypto.service.pki;

import com.jd.blockchain.crypto.*;
import com.jd.blockchain.utils.io.BytesUtils;
import org.junit.Test;

import java.util.Random;

import static com.jd.blockchain.crypto.CryptoAlgorithm.ASYMMETRIC_KEY;
import static com.jd.blockchain.crypto.CryptoAlgorithm.SIGNATURE_ALGORITHM;
import static com.jd.blockchain.crypto.CryptoKeyType.PRIVATE;
import static com.jd.blockchain.crypto.CryptoKeyType.PUBLIC;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.*;

/**
 * @author zhanglin33
 * @title: SM3WITHSM2SignatureFunctionTest
 * @description: TODO
 * @date 2019-05-16, 17:04
 */
public class SM3WITHSM2SignatureFunctionTest {

    @Test
    public void getAlgorithmTest() {

        CryptoAlgorithm algorithm = Crypto.getAlgorithm("SM3WITHSM2");
        assertNotNull(algorithm);

        SignatureFunction signatureFunction = Crypto.getSignatureFunction(algorithm);

        assertEquals(signatureFunction.getAlgorithm().name(), algorithm.name());
        assertEquals(signatureFunction.getAlgorithm().code(), algorithm.code());

        algorithm = Crypto.getAlgorithm("sm3withsm2");
        assertNotNull(algorithm);

        assertEquals(signatureFunction.getAlgorithm().name(), algorithm.name());
        assertEquals(signatureFunction.getAlgorithm().code(), algorithm.code());

        algorithm = Crypto.getAlgorithm("sm3withs");
        assertNull(algorithm);
    }

    @Test
    public void generateKeyPairTest() {

        CryptoAlgorithm algorithm = Crypto.getAlgorithm("SM3WITHSM2");
        assertNotNull(algorithm);

        SignatureFunction signatureFunction = Crypto.getSignatureFunction(algorithm);

        AsymmetricKeypair keyPair = signatureFunction.generateKeypair();

        PubKey pubKey = keyPair.getPubKey();
        PrivKey privKey = keyPair.getPrivKey();

        assertEquals(PUBLIC.CODE, pubKey.getKeyType().CODE);
        assertTrue(pubKey.getRawKeyBytes().length > 32);
        assertEquals(PRIVATE.CODE, privKey.getKeyType().CODE);
        assertTrue(privKey.getRawKeyBytes().length > 65 + 32);

        assertEquals(algorithm.code(), pubKey.getAlgorithm());
        assertEquals(algorithm.code(), privKey.getAlgorithm());

        byte[] algoBytes = CryptoAlgorithm.getCodeBytes(algorithm);
        byte[] pubKeyTypeBytes = new byte[] { PUBLIC.CODE };
        byte[] privKeyTypeBytes = new byte[] { PRIVATE.CODE };
        byte[] rawPubKeyBytes = pubKey.getRawKeyBytes();
        byte[] rawPrivKeyBytes = privKey.getRawKeyBytes();
        assertArrayEquals(BytesUtils.concat(algoBytes, pubKeyTypeBytes, rawPubKeyBytes), pubKey.toBytes());
        assertArrayEquals(BytesUtils.concat(algoBytes, privKeyTypeBytes, rawPrivKeyBytes), privKey.toBytes());
    }

    @Test
    public void retrievePubKeyTest() {

        CryptoAlgorithm algorithm = Crypto.getAlgorithm("SM3WITHSM2");
        assertNotNull(algorithm);

        SignatureFunction signatureFunction = Crypto.getSignatureFunction(algorithm);

        AsymmetricKeypair keyPair = signatureFunction.generateKeypair();

        PubKey pubKey = keyPair.getPubKey();
        PrivKey privKey = keyPair.getPrivKey();

        PubKey retrievedPubKey = signatureFunction.retrievePubKey(privKey);

        assertEquals(pubKey.getKeyType(), retrievedPubKey.getKeyType());
        assertEquals(pubKey.getRawKeyBytes().length, retrievedPubKey.getRawKeyBytes().length);
        assertEquals(pubKey.getAlgorithm(), retrievedPubKey.getAlgorithm());
        assertArrayEquals(pubKey.toBytes(), retrievedPubKey.toBytes());
    }

    @Test
    public void signTest() {

        byte[] data = new byte[1024];
        Random random = new Random();
        random.nextBytes(data);

        CryptoAlgorithm algorithm = Crypto.getAlgorithm("SM3WITHSM2");
        assertNotNull(algorithm);

        SignatureFunction signatureFunction = Crypto.getSignatureFunction(algorithm);

        AsymmetricKeypair keyPair = signatureFunction.generateKeypair();

        PrivKey privKey = keyPair.getPrivKey();
        SignatureDigest signatureDigest = signatureFunction.sign(privKey, data);

        byte[] signatureBytes = signatureDigest.toBytes();

        assertTrue(signatureBytes.length > 2 + 64);
        assertEquals(algorithm.code(), signatureDigest.getAlgorithm());

        assertEquals(PKIAlgorithm.SM3WITHSM2.code(), signatureDigest.getAlgorithm());
        assertEquals((short) (SIGNATURE_ALGORITHM | ASYMMETRIC_KEY | ((byte) 33 & 0x00FF)),
                signatureDigest.getAlgorithm());

        byte[] algoBytes = BytesUtils.toBytes(signatureDigest.getAlgorithm());
        byte[] rawSinatureBytes = signatureDigest.getRawDigest();
        assertArrayEquals(BytesUtils.concat(algoBytes, rawSinatureBytes), signatureBytes);
    }

    @Test
    public void verifyTest() {
        byte[] data = new byte[1024];
        Random random = new Random();
        random.nextBytes(data);

        CryptoAlgorithm algorithm = Crypto.getAlgorithm("SM3WITHSM2");
        assertNotNull(algorithm);

        SignatureFunction signatureFunction = Crypto.getSignatureFunction(algorithm);

        AsymmetricKeypair keyPair = signatureFunction.generateKeypair();

        PubKey pubKey = keyPair.getPubKey();
        PrivKey privKey = keyPair.getPrivKey();
        SignatureDigest signatureDigest = signatureFunction.sign(privKey, data);

        System.out.println(signatureDigest.getRawDigest().length);
        assertTrue(signatureFunction.verify(signatureDigest, pubKey, data));
    }

    @Test
    public void supportPrivKeyTest() {

        CryptoAlgorithm algorithm = Crypto.getAlgorithm("SM3WITHSM2");
        assertNotNull(algorithm);

        SignatureFunction signatureFunction = Crypto.getSignatureFunction(algorithm);

        AsymmetricKeypair keyPair = signatureFunction.generateKeypair();

        PrivKey privKey = keyPair.getPrivKey();
        byte[] privKeyBytes = privKey.toBytes();

        assertTrue(signatureFunction.supportPrivKey(privKeyBytes));
    }

    @Test
    public void resolvePrivKeyTest() {

        CryptoAlgorithm algorithm = Crypto.getAlgorithm("SM3WITHSM2");
        assertNotNull(algorithm);

        SignatureFunction signatureFunction = Crypto.getSignatureFunction(algorithm);

        AsymmetricKeypair keyPair = signatureFunction.generateKeypair();

        PrivKey privKey = keyPair.getPrivKey();
        byte[] privKeyBytes = privKey.toBytes();

        PrivKey resolvedPrivKey = signatureFunction.resolvePrivKey(privKeyBytes);

        assertEquals(PRIVATE.CODE, resolvedPrivKey.getKeyType().CODE);
        assertEquals(PKIAlgorithm.SM3WITHSM2.code(), resolvedPrivKey.getAlgorithm());
        assertEquals((short) (SIGNATURE_ALGORITHM | ASYMMETRIC_KEY | ((byte) 33 & 0x00FF)),
                resolvedPrivKey.getAlgorithm());
        assertArrayEquals(privKeyBytes, resolvedPrivKey.toBytes());
    }

    @Test
    public void supportPubKeyTest() {

        CryptoAlgorithm algorithm = Crypto.getAlgorithm("SM3WITHSM2");
        assertNotNull(algorithm);

        SignatureFunction signatureFunction = Crypto.getSignatureFunction(algorithm);

        AsymmetricKeypair keyPair = signatureFunction.generateKeypair();

        PubKey pubKey = keyPair.getPubKey();
        byte[] pubKeyBytes = pubKey.toBytes();

        assertTrue(signatureFunction.supportPubKey(pubKeyBytes));
    }

    @Test
    public void resolvePubKeyTest() {

        CryptoAlgorithm algorithm = Crypto.getAlgorithm("SM3WITHSM2");
        assertNotNull(algorithm);

        SignatureFunction signatureFunction = Crypto.getSignatureFunction(algorithm);

        AsymmetricKeypair keyPair = signatureFunction.generateKeypair();

        PubKey pubKey = keyPair.getPubKey();
        byte[] pubKeyBytes = pubKey.toBytes();

        PubKey resolvedPubKey = signatureFunction.resolvePubKey(pubKeyBytes);

        assertEquals(PUBLIC.CODE, resolvedPubKey.getKeyType().CODE);
        assertEquals(PKIAlgorithm.SM3WITHSM2.code(), resolvedPubKey.getAlgorithm());
        assertEquals((short) (SIGNATURE_ALGORITHM | ASYMMETRIC_KEY | ((byte) 33 & 0x00FF)),
                resolvedPubKey.getAlgorithm());
        assertArrayEquals(pubKeyBytes, resolvedPubKey.toBytes());
    }

    @Test
    public void supportDigestTest() {

        byte[] data = new byte[1024];
        Random random = new Random();
        random.nextBytes(data);

        CryptoAlgorithm algorithm = Crypto.getAlgorithm("SM3WITHSM2");
        assertNotNull(algorithm);

        SignatureFunction signatureFunction = Crypto.getSignatureFunction(algorithm);

        AsymmetricKeypair keyPair = signatureFunction.generateKeypair();

        PrivKey privKey = keyPair.getPrivKey();

        SignatureDigest signatureDigest = signatureFunction.sign(privKey, data);

        byte[] signatureDigestBytes = signatureDigest.toBytes();
        assertTrue(signatureFunction.supportDigest(signatureDigestBytes));
    }

    @Test
    public void resolveDigestTest() {

        byte[] data = new byte[1024];
        Random random = new Random();
        random.nextBytes(data);

        CryptoAlgorithm algorithm = Crypto.getAlgorithm("SM3WITHSM2");
        assertNotNull(algorithm);

        SignatureFunction signatureFunction = Crypto.getSignatureFunction(algorithm);

        AsymmetricKeypair keyPair = signatureFunction.generateKeypair();

        PrivKey privKey = keyPair.getPrivKey();

        SignatureDigest signatureDigest = signatureFunction.sign(privKey, data);

        byte[] signatureDigestBytes = signatureDigest.toBytes();

        SignatureDigest resolvedSignatureDigest = signatureFunction.resolveDigest(signatureDigestBytes);

        assertTrue(resolvedSignatureDigest.getRawDigest().length > 64);
        assertEquals(PKIAlgorithm.SM3WITHSM2.code(), resolvedSignatureDigest.getAlgorithm());
        assertEquals((short) (SIGNATURE_ALGORITHM | ASYMMETRIC_KEY | ((byte) 33 & 0x00FF)),
                resolvedSignatureDigest.getAlgorithm());
        assertArrayEquals(signatureDigestBytes, resolvedSignatureDigest.toBytes());
    }
}
