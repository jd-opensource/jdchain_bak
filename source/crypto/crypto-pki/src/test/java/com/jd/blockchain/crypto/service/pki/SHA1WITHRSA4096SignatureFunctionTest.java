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
 * @title: SHA1WITHRSA4096SignatureFunctionTest
 * @description: TODO
 * @date 2019-05-16, 10:49
 */
public class SHA1WITHRSA4096SignatureFunctionTest {

    private AsymmetricKeypair keyPair = Crypto.getSignatureFunction(Crypto.getAlgorithm("SHA1WITHRSA4096")).
            generateKeypair();

    @Test
    public void getAlgorithmTest() {

        CryptoAlgorithm algorithm = Crypto.getAlgorithm("SHA1WITHRSA4096");
        assertNotNull(algorithm);

        SignatureFunction signatureFunction = Crypto.getSignatureFunction(algorithm);

        assertEquals(signatureFunction.getAlgorithm().name(), algorithm.name());
        assertEquals(signatureFunction.getAlgorithm().code(), algorithm.code());

        algorithm = Crypto.getAlgorithm("SHA1withRsa4096");
        assertNotNull(algorithm);

        assertEquals(signatureFunction.getAlgorithm().name(), algorithm.name());
        assertEquals(signatureFunction.getAlgorithm().code(), algorithm.code());

        algorithm = Crypto.getAlgorithm("rsa2048");
        assertNull(algorithm);
    }

    @Test
    public void generateKeyPairTest() {

        CryptoAlgorithm algorithm = Crypto.getAlgorithm("SHA1WITHRSA4096");
        assertNotNull(algorithm);

        PubKey pubKey = keyPair.getPubKey();
        PrivKey privKey = keyPair.getPrivKey();

        assertEquals(PUBLIC.CODE, pubKey.getKeyType().CODE);
        assertTrue(pubKey.getRawKeyBytes().length > 515);
        assertEquals(PRIVATE.CODE, privKey.getKeyType().CODE);
        assertTrue(privKey.getRawKeyBytes().length > 2307);

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

        CryptoAlgorithm algorithm = Crypto.getAlgorithm("SHA1WITHRSA4096");
        assertNotNull(algorithm);

        SignatureFunction signatureFunction = Crypto.getSignatureFunction(algorithm);

        PubKey pubKey = keyPair.getPubKey();
        PrivKey privKey = keyPair.getPrivKey();

        PubKey retrievedPubKey = signatureFunction.retrievePubKey(privKey);

        assertEquals(pubKey.getKeyType(), retrievedPubKey.getKeyType());
        assertEquals(pubKey.getRawKeyBytes().length, retrievedPubKey.getRawKeyBytes().length);
        assertEquals(pubKey.getAlgorithm(), retrievedPubKey.getAlgorithm());
        assertArrayEquals(pubKey.toBytes(), retrievedPubKey.toBytes());
    }

    @Test
    public void signAndVerifyTest() {

        byte[] data = new byte[1024];
        Random random = new Random();
        random.nextBytes(data);

        CryptoAlgorithm algorithm = Crypto.getAlgorithm("SHA1WITHRSA4096");
        assertNotNull(algorithm);

        SignatureFunction signatureFunction = Crypto.getSignatureFunction(algorithm);

        PrivKey privKey = keyPair.getPrivKey();
        PubKey pubKey = keyPair.getPubKey();

        SignatureDigest signatureDigest = signatureFunction.sign(privKey, data);

        byte[] signatureBytes = signatureDigest.toBytes();

        assertEquals(2 + 512, signatureBytes.length);
        assertEquals(algorithm.code(), signatureDigest.getAlgorithm());

        assertEquals(PKIAlgorithm.SHA1WITHRSA4096.code(), signatureDigest.getAlgorithm());
        assertEquals((short) (SIGNATURE_ALGORITHM | ASYMMETRIC_KEY | ((byte) 32 & 0x00FF)),
                signatureDigest.getAlgorithm());

        byte[] algoBytes = BytesUtils.toBytes(signatureDigest.getAlgorithm());
        byte[] rawSinatureBytes = signatureDigest.getRawDigest();
        assertArrayEquals(BytesUtils.concat(algoBytes, rawSinatureBytes), signatureBytes);

        assertTrue(signatureFunction.verify(signatureDigest, pubKey, data));
    }

    @Test
    public void supportAndResolvePrivKeyTest() {

        CryptoAlgorithm algorithm = Crypto.getAlgorithm("SHA1WITHRSA4096");
        assertNotNull(algorithm);

        SignatureFunction signatureFunction = Crypto.getSignatureFunction(algorithm);

        PrivKey privKey = keyPair.getPrivKey();
        byte[] privKeyBytes = privKey.toBytes();

        assertTrue(signatureFunction.supportPrivKey(privKeyBytes));

        PrivKey resolvedPrivKey = signatureFunction.resolvePrivKey(privKeyBytes);

        assertEquals(PRIVATE.CODE, resolvedPrivKey.getKeyType().CODE);
        assertEquals(PKIAlgorithm.SHA1WITHRSA4096.code(), resolvedPrivKey.getAlgorithm());
        assertEquals((short) (SIGNATURE_ALGORITHM | ASYMMETRIC_KEY | ((byte) 32 & 0x00FF)),
                resolvedPrivKey.getAlgorithm());
        assertArrayEquals(privKeyBytes, resolvedPrivKey.toBytes());
    }

    @Test
    public void supportAndResolvePubKeyTest() {

        CryptoAlgorithm algorithm = Crypto.getAlgorithm("SHA1WITHRSA4096");
        assertNotNull(algorithm);

        SignatureFunction signatureFunction = Crypto.getSignatureFunction(algorithm);

        PubKey pubKey = keyPair.getPubKey();
        byte[] pubKeyBytes = pubKey.toBytes();

        assertTrue(signatureFunction.supportPubKey(pubKeyBytes));

        PubKey resolvedPubKey = signatureFunction.resolvePubKey(pubKeyBytes);

        assertEquals(PUBLIC.CODE, resolvedPubKey.getKeyType().CODE);
        assertEquals(PKIAlgorithm.SHA1WITHRSA4096.code(), resolvedPubKey.getAlgorithm());
        assertEquals((short) (SIGNATURE_ALGORITHM | ASYMMETRIC_KEY | ((byte) 32 & 0x00FF)),
                resolvedPubKey.getAlgorithm());
        assertArrayEquals(pubKeyBytes, resolvedPubKey.toBytes());
    }

    @Test
    public void supportAndResolveDigestTest() {

        byte[] data = new byte[1024];
        Random random = new Random();
        random.nextBytes(data);

        CryptoAlgorithm algorithm = Crypto.getAlgorithm("SHA1WITHRSA4096");
        assertNotNull(algorithm);

        SignatureFunction signatureFunction = Crypto.getSignatureFunction(algorithm);

        PrivKey privKey = keyPair.getPrivKey();

        SignatureDigest signatureDigest = signatureFunction.sign(privKey, data);

        byte[] signatureDigestBytes = signatureDigest.toBytes();
        assertTrue(signatureFunction.supportDigest(signatureDigestBytes));

        SignatureDigest resolvedSignatureDigest = signatureFunction.resolveDigest(signatureDigestBytes);

        assertEquals(512, resolvedSignatureDigest.getRawDigest().length);
        assertEquals(PKIAlgorithm.SHA1WITHRSA4096.code(), resolvedSignatureDigest.getAlgorithm());
        assertEquals((short) (SIGNATURE_ALGORITHM | ASYMMETRIC_KEY | ((byte) 32 & 0x00FF)),
                resolvedSignatureDigest.getAlgorithm());
        assertArrayEquals(signatureDigestBytes, resolvedSignatureDigest.toBytes());
    }
}
