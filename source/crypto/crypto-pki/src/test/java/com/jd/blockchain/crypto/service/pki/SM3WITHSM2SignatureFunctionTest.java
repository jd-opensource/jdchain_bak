package com.jd.blockchain.crypto.service.pki;

import com.jd.blockchain.crypto.*;
import com.jd.blockchain.crypto.utils.CSRBuilder;
import com.jd.blockchain.crypto.utils.CertParser;
import com.jd.blockchain.utils.io.BytesUtils;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

import java.security.PublicKey;
import java.util.Random;

import static com.jd.blockchain.crypto.CryptoAlgorithm.ASYMMETRIC_KEY;
import static com.jd.blockchain.crypto.CryptoAlgorithm.SIGNATURE_ALGORITHM;
import static com.jd.blockchain.crypto.CryptoKeyType.PRIVATE;
import static com.jd.blockchain.crypto.CryptoKeyType.PUBLIC;
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


    @Test
    public void testWithCSRAndCert() {

        String publicKeyStr =  "3059301306072a8648ce3d020106082a811ccf5501822d03420004aa6586478be879504fdd02892f" +
                "b4cf2bdb3d96a316f41ff7bcadfabef4ea836678984f9ba6931a609391426ca7164592f5fccd062be56fc32b3eec3a5" +
                "0400971";

        String privateKeyStr = "308193020100301306072a8648ce3d020106082a811ccf5501822d0479307702010104207abd2953" +
                "84f193abe4f3715c26bc15225061f007131755d94ca12c7e0ce0b3a0a00a06082a811ccf5501822da14403420004aa6" +
                "586478be879504fdd02892fb4cf2bdb3d96a316f41ff7bcadfabef4ea836678984f9ba6931a609391426ca7164592f5" +
                "fccd062be56fc32b3eec3a50400971";

        String expectedUserCert = "MIICwjCCAmWgAwIBAgIFIChnMlIwDAYIKoEcz1UBg3UFADBdMQswCQYDVQQGEwJDTjEwMC4GA1UEC" +
                "gwnQ2hpbmEgRmluYW5jaWFsIENlcnRpZmljYXRpb24gQXV0aG9yaXR5MRwwGgYDVQQDDBNDRkNBIFRFU1QgU00yIE9DQTEx" +
                "MB4XDTE5MDgyODA4MTUzNloXDTIxMDgyODA4MTUzNloweDELMAkGA1UEBhMCQ04xGDAWBgNVBAoMD0NGQ0EgVEVTVCBPQ0E" +
                "xMTERMA8GA1UECwwITG9jYWwgUkExFTATBgNVBAsMDEluZGl2aWR1YWwtMTElMCMGA1UEAwwcMDUxQHpoYW5nbGluIUBaMT" +
                "g2MTIyMjkyOTVAODBZMBMGByqGSM49AgEGCCqBHM9VAYItA0IABKplhkeL6HlQT90CiS+0zyvbPZajFvQf97yt+r706oNme" +
                "JhPm6aTGmCTkUJspxZFkvX8zQYr5W/DKz7sOlBACXGjgfQwgfEwHwYDVR0jBBgwFoAUvqZ+TT18j6BV5sEvCS4sIEOzQn8w" +
                "SAYDVR0gBEEwPzA9BghggRyG7yoBAjAxMC8GCCsGAQUFBwIBFiNodHRwOi8vd3d3LmNmY2EuY29tLmNuL3VzL3VzLTE1Lmh" +
                "0bTA5BgNVHR8EMjAwMC6gLKAqhihodHRwOi8vMjEwLjc0LjQyLjMvT0NBMTEvU00yL2NybDI0OTkuY3JsMAsGA1UdDwQEAw" +
                "ID6DAdBgNVHQ4EFgQU07tMtbs5PWkwN33OQVH116xd1kowHQYDVR0lBBYwFAYIKwYBBQUHAwIGCCsGAQUFBwMEMAwGCCqBH" +
                "M9VAYN1BQADSQAwRgIhAPuuInppVhugw6EIG4wgkouxX/MX2dTLe478wx7LFXSQAiEAneiz51vrurICNCfeecaBHgzaj7+3" +
                "kmrdIZJBoxYGbso=";

        String issuerCert =
                "-----BEGIN CERTIFICATE-----\n" +
                        "MIICTzCCAfOgAwIBAgIKJFSZ4SRVDndYUTAMBggqgRzPVQGDdQUAMF0xCzAJBgNV\n" +
                        "BAYTAkNOMTAwLgYDVQQKDCdDaGluYSBGaW5hbmNpYWwgQ2VydGlmaWNhdGlvbiBB\n" +
                        "dXRob3JpdHkxHDAaBgNVBAMME0NGQ0EgVEVTVCBDUyBTTTIgQ0EwHhcNMTIwODI5\n" +
                        "MDU0ODQ3WhcNMzIwODI0MDU0ODQ3WjBdMQswCQYDVQQGEwJDTjEwMC4GA1UECgwn\n" +
                        "Q2hpbmEgRmluYW5jaWFsIENlcnRpZmljYXRpb24gQXV0aG9yaXR5MRwwGgYDVQQD\n" +
                        "DBNDRkNBIFRFU1QgU00yIE9DQTExMFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAE\n" +
                        "L1mx4wriQUojGsIkNL14kslv9nwiqsiVpELOZauzghrbccNlPYKNYKZOCvXwIIqU\n" +
                        "9QY02d4weqKqo/JMcNsKEaOBmDCBlTAfBgNVHSMEGDAWgBS12JBvXPDYM9JjvX6y\n" +
                        "w43GTxJ6YTAMBgNVHRMEBTADAQH/MDgGA1UdHwQxMC8wLaAroCmGJ2h0dHA6Ly8y\n" +
                        "MTAuNzQuNDIuMy90ZXN0cmNhL1NNMi9jcmwxLmNybDALBgNVHQ8EBAMCAQYwHQYD\n" +
                        "VR0OBBYEFL6mfk09fI+gVebBLwkuLCBDs0J/MAwGCCqBHM9VAYN1BQADSAAwRQIh\n" +
                        "AKuk7s3eYCZDck5NWU0eNQmLhBN/1zmKs517qFrDrkJWAiAP4cVfLtdza/OkwU9P\n" +
                        "PrIDl+E4aL3FypntFXHG3T+Keg==\n" +
                        "-----END CERTIFICATE-----";

        byte[] rawPublicKeyBytes = Hex.decode(publicKeyStr);
        byte[] rawPrivateKeyBytes = Hex.decode(privateKeyStr);

        CSRBuilder builder = new CSRBuilder();
        builder.init("SM3withSM2", rawPublicKeyBytes, rawPrivateKeyBytes);

        CertParser parser = new CertParser();
        parser.parse(expectedUserCert,issuerCert);

        PublicKey rawPublicKeyInCert = parser.getPubKey();
        // check that the public key in inputs and the public key in certificate are consistent
        assertArrayEquals(rawPublicKeyBytes, rawPublicKeyInCert.getEncoded());

        String algoName = parser.getSigAlgName();
        int keyLength = parser.getKeyLength();
        String length = String.valueOf(keyLength);
        String algo = (algoName.contains("RSA")? (algoName + length).toUpperCase(): algoName.toUpperCase());

        CryptoAlgorithm algorithm = Crypto.getAlgorithm(algo);
        assertNotNull(algorithm);
        SignatureFunction signatureFunction = Crypto.getSignatureFunction(algorithm);

        PubKey  pubKey  = new PubKey(algorithm, rawPublicKeyBytes);
        PrivKey privKey = new PrivKey(algorithm, rawPrivateKeyBytes);

        // signTest
        byte[] data = new byte[1024];
        Random random = new Random();
        random.nextBytes(data);

        SignatureDigest signature = signatureFunction.sign(privKey, data);
        assertTrue(signatureFunction.verify(signature, pubKey, data));
    }
}
