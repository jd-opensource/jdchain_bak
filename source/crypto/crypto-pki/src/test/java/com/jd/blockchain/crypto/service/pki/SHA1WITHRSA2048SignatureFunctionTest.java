package com.jd.blockchain.crypto.service.pki;

import com.jd.blockchain.crypto.*;
import com.jd.blockchain.crypto.utils.CSRBuilder;
import com.jd.blockchain.crypto.utils.CertParser;
import com.jd.blockchain.utils.io.BytesUtils;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

import java.security.*;
import java.util.Random;

import static com.jd.blockchain.crypto.CryptoAlgorithm.*;
import static com.jd.blockchain.crypto.CryptoKeyType.PRIVATE;
import static com.jd.blockchain.crypto.CryptoKeyType.PUBLIC;
import static org.junit.Assert.*;

/**
 * @author zhanglin33
 * @title: SHA1WITHRSA2048SignatureFunctionTest
 * @description: TODO
 * @date 2019-05-16, 10:49
 */
public class SHA1WITHRSA2048SignatureFunctionTest {

    @Test
    public void getAlgorithmTest() {

        CryptoAlgorithm algorithm = Crypto.getAlgorithm("SHA1WITHRSA2048");
        assertNotNull(algorithm);

        SignatureFunction signatureFunction = Crypto.getSignatureFunction(algorithm);

        assertEquals(signatureFunction.getAlgorithm().name(), algorithm.name());
        assertEquals(signatureFunction.getAlgorithm().code(), algorithm.code());

        algorithm = Crypto.getAlgorithm("SHA1withRsa2048");
        assertNotNull(algorithm);

        assertEquals(signatureFunction.getAlgorithm().name(), algorithm.name());
        assertEquals(signatureFunction.getAlgorithm().code(), algorithm.code());

        algorithm = Crypto.getAlgorithm("rsa2048");
        assertNull(algorithm);
    }

    @Test
    public void test() {

        // generateKeyPairTest
        CryptoAlgorithm algorithm = Crypto.getAlgorithm("SHA1WITHRSA2048");
        assertNotNull(algorithm);
        SignatureFunction signatureFunction = Crypto.getSignatureFunction(algorithm);
        AsymmetricKeypair keyPair = signatureFunction.generateKeypair();
        PubKey pubKey = keyPair.getPubKey();
        PrivKey privKey = keyPair.getPrivKey();
        assertEquals(PUBLIC.CODE, pubKey.getKeyType().CODE);
        assertTrue(pubKey.getRawKeyBytes().length > 259);
        assertEquals(PRIVATE.CODE, privKey.getKeyType().CODE);
        assertTrue(privKey.getRawKeyBytes().length > 1155);
        assertEquals(algorithm.code(), pubKey.getAlgorithm());
        assertEquals(algorithm.code(), privKey.getAlgorithm());
        byte[] algoBytes = CryptoAlgorithm.getCodeBytes(algorithm);
        byte[] pubKeyTypeBytes = new byte[] { PUBLIC.CODE };
        byte[] privKeyTypeBytes = new byte[] { PRIVATE.CODE };
        byte[] rawPubKeyBytes = pubKey.getRawKeyBytes();
        byte[] rawPrivKeyBytes = privKey.getRawKeyBytes();
        assertArrayEquals(BytesUtils.concat(algoBytes, pubKeyTypeBytes, rawPubKeyBytes), pubKey.toBytes());
        assertArrayEquals(BytesUtils.concat(algoBytes, privKeyTypeBytes, rawPrivKeyBytes), privKey.toBytes());

        // retrievePubKeyTest
        PubKey retrievedPubKey = signatureFunction.retrievePubKey(privKey);
        assertEquals(pubKey.getKeyType(), retrievedPubKey.getKeyType());
        assertEquals(pubKey.getRawKeyBytes().length, retrievedPubKey.getRawKeyBytes().length);
        assertEquals(pubKey.getAlgorithm(), retrievedPubKey.getAlgorithm());
        assertArrayEquals(pubKey.toBytes(), retrievedPubKey.toBytes());

        // signTest
        byte[] data = new byte[1024];
        Random random = new Random();
        random.nextBytes(data);
        SignatureDigest signatureDigest = signatureFunction.sign(privKey, data);
        byte[] signatureBytes = signatureDigest.toBytes();
        assertEquals(2 + 256, signatureBytes.length);
        assertEquals(algorithm.code(), signatureDigest.getAlgorithm());
        assertEquals(PKIAlgorithm.SHA1WITHRSA2048.code(), signatureDigest.getAlgorithm());
        assertEquals((short) (SIGNATURE_ALGORITHM | ASYMMETRIC_KEY | ((byte) 31 & 0x00FF)),
                signatureDigest.getAlgorithm());

        byte[] rawSinatureBytes = signatureDigest.getRawDigest();
        assertArrayEquals(BytesUtils.concat(algoBytes, rawSinatureBytes), signatureBytes);

        // verifyTest
        assertTrue(signatureFunction.verify(signatureDigest, pubKey, data));

        // supportPrivKeyTest
        byte[] privKeyBytes = privKey.toBytes();
        assertTrue(signatureFunction.supportPrivKey(privKeyBytes));

        // resolvePrivKeyTest
        PrivKey resolvedPrivKey = signatureFunction.resolvePrivKey(privKeyBytes);
        assertEquals(PRIVATE.CODE, resolvedPrivKey.getKeyType().CODE);
        assertEquals(PKIAlgorithm.SHA1WITHRSA2048.code(), resolvedPrivKey.getAlgorithm());
        assertEquals((short) (SIGNATURE_ALGORITHM | ASYMMETRIC_KEY | ((byte) 31 & 0x00FF)),
                resolvedPrivKey.getAlgorithm());
        assertArrayEquals(privKeyBytes, resolvedPrivKey.toBytes());

        // supportPubKeyTest
        byte[] pubKeyBytes = pubKey.toBytes();
        assertTrue(signatureFunction.supportPubKey(pubKeyBytes));

        // resolvedPubKeyTest
        PubKey resolvedPubKey = signatureFunction.resolvePubKey(pubKeyBytes);
        assertEquals(PUBLIC.CODE, resolvedPubKey.getKeyType().CODE);
        assertEquals(PKIAlgorithm.SHA1WITHRSA2048.code(), resolvedPubKey.getAlgorithm());
        assertEquals((short) (SIGNATURE_ALGORITHM | ASYMMETRIC_KEY | ((byte) 31 & 0x00FF)),
                resolvedPubKey.getAlgorithm());
        assertArrayEquals(pubKeyBytes, resolvedPubKey.toBytes());

        //supportDigestTest
        byte[] signatureDigestBytes = signatureDigest.toBytes();
        assertTrue(signatureFunction.supportDigest(signatureDigestBytes));

        // resolveDigestTest
        SignatureDigest resolvedSignatureDigest = signatureFunction.resolveDigest(signatureDigestBytes);
        assertEquals(256, resolvedSignatureDigest.getRawDigest().length);
        assertEquals(PKIAlgorithm.SHA1WITHRSA2048.code(), resolvedSignatureDigest.getAlgorithm());
        assertEquals((short) (SIGNATURE_ALGORITHM | ASYMMETRIC_KEY | ((byte) 31 & 0x00FF)),
                resolvedSignatureDigest.getAlgorithm());
        assertArrayEquals(signatureDigestBytes, resolvedSignatureDigest.toBytes());
    }

    @Test
    public void testWithCSRAndCert() {

        String countryName = "CN";
        String stateName = "Beijing";
        String cityName = "Beijing";
        String organizationName = "JD.com";
        String departmentName = "Blockchain Department";
        String domainName = "ledger.jd.com";
        String emailName = "zhanglin33@jd.com";


        String publicKeyStr =  "30820122300d06092a864886f70d01010105000382010f003082010a0282010100c91e978897a36b" +
                "30a57d265807441b0eff40c5572ecf029cd0cb2999b715edd2d5345c7b60c003075b1352629d03b943d08b25bfc5245" +
                "a400f9ecbc1757394eac452d6316bf90cfcff5edfc427e277aa5266e89b1b2daa2e69dad5575515f49417c9ff332c83" +
                "0dcd5537381f08e00b11123ad947bb11b18666d264ab5d8cdc92d0967fd7e0e6677746e2f01270d0594f74cda4e9d77" +
                "4c6f3824499896bfb6424684af260d71b57a1366b741104fc647d9e38de055568daad60c116686e4afa1e9c83e9e30c" +
                "7216e61353da2f75b2dde2c0ae870cf0ccc90490d1e22ecccbf3985d30933689847e6faf946993f930f83ac5b816075" +
                "793a9a6df20727552a21be30203010001";

        String privateKeyStr = "308204bd020100300d06092a864886f70d0101010500048204a7308204a30201000282010100c91e" +
                "978897a36b30a57d265807441b0eff40c5572ecf029cd0cb2999b715edd2d5345c7b60c003075b1352629d03b943d08" +
                "b25bfc5245a400f9ecbc1757394eac452d6316bf90cfcff5edfc427e277aa5266e89b1b2daa2e69dad5575515f49417" +
                "c9ff332c830dcd5537381f08e00b11123ad947bb11b18666d264ab5d8cdc92d0967fd7e0e6677746e2f01270d0594f7" +
                "4cda4e9d774c6f3824499896bfb6424684af260d71b57a1366b741104fc647d9e38de055568daad60c116686e4afa1e" +
                "9c83e9e30c7216e61353da2f75b2dde2c0ae870cf0ccc90490d1e22ecccbf3985d30933689847e6faf946993f930f83" +
                "ac5b816075793a9a6df20727552a21be30203010001028201003975637e93300d8a2ee573e47762f6461117cca96d46" +
                "982cfc1be6ed3318f142a845d6dc2ad680a703d69fd56b9d6a3b1d23fbeb6f63c4e303736f2bfca5c2585631825f494" +
                "534783d6f3a07bd0b5efbcaa1faf7814ac9118c8d8820f4be9a8b0ac6db819fc86b538bf284369d9f009a667668a82d" +
                "224f7122041eddb492ef5b02d462aa11bc550bf3785a5d7538043586cfb0cd5c313a4746f22a5d4a89a412b279a7517" +
                "176b1858a9677a1a60605aa0b2a7d260cf2e9e23a0e67c4a23ac046c62973239e84874c3695cc3f34073a62bd50b597" +
                "ee06092a93fa6e41303ab4d2293ffad4c8db06e6516ae0a26a4d681305c3b7d535b540a638ca6cff1ce483750281810" +
                "0e39967c5b287bd319b448255a1033591c74f4d688eb71f89a674cdb1be999e9abcf40e6b7f0bb3054262d7625002da" +
                "7d34e56055ee130b66d682a0b9f8ea240a7e2dd1ff3f4b32f3bd352ba0a5dffc0315da34922493c267597787222213b" +
                "f87d99fbd2a809c2647a1937cd1e303d746373db7c409a2ef33f8c06bb838bc612702818100e2374c71db5f0b4a199f" +
                "f9d646a1785a84383a14aceb506f515b380c69ff45b51d0e941f55f0a234d8a3ee30bed656142bb5b985e462c44d234" +
                "cfd424ecd6ca5fc70503862978ffe42b45bd698a99091d6fc65e2d85652e0ef56c52e8e05a6c5e879922c1d794e22f3" +
                "51998217b5c6637a6abb716bf90cd1f23a2eedcdfface50281805d28a872224e2f2183e539d7e4ccd47b73f240c4004" +
                "e72493c69e8dbcd2141eb22565f249edee20ad00e770c95a5655b0470b2cad964d030eab293292bfa62802cff824a10" +
                "d52de8d8545024346106dd186fb53ef05bcea1d0dbfce2fac1cc8ec583fdc0ccdd9d498a983cea081ac55dc734aae84" +
                "1ed802d6caf0e285c88b6d702818068b2a752daf1364c6967bd3e0b1a98956c3489cd1feb19232c4847bc97226aa4d4" +
                "79f6dc39ee51649c0fe321f471470db6dd38ac5b73caded8c3bd437f2d5c67c65a450693bb0a0de7d989d7dc783e4d0" +
                "16f77c871d02233b1123bd8bc2aa97157934cafd6445a819a93ddb4743cd141215b5cbdb5f7629398c48d0bcb17d671" +
                "028181009282554329b89bcb2236a7e2a5bff53627e3ca7cc792d65236085741bc62a3f5767654722af561eff175664" +
                "1af60e3d3959f63f0fec00cb29443ffca4ff751a76ecc6fa192ebe08ec9f643b9bb0d11bc90c1e850f0528ef223ea5f" +
                "e4b4c107cc3a9eb26e6b84d74d87acbf7cc760cd788cbc30f95f8399077b25cdd924604c01";

        String expectedCSR = "MIIC4jCCAcoCAQAwgZwxCzAJBgNVBAYTAkNOMRAwDgYDVQQIDAdCZWlqaW5nMRAwDgYDVQQHDAdCZWlqaW" +
                "5nMQ8wDQYDVQQKDAZKRC5jb20xHjAcBgNVBAsMFUJsb2NrY2hhaW4gRGVwYXJ0bWVudDEWMBQGA1UEAwwNbGVkZ2VyLmpkL" +
                "mNvbTEgMB4GCSqGSIb3DQEJARYRemhhbmdsaW4zM0BqZC5jb20wggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDJ" +
                "HpeIl6NrMKV9JlgHRBsO/0DFVy7PApzQyymZtxXt0tU0XHtgwAMHWxNSYp0DuUPQiyW/xSRaQA+ey8F1c5TqxFLWMWv5DPz" +
                "/Xt/EJ+J3qlJm6JsbLaouadrVV1UV9JQXyf8zLIMNzVU3OB8I4AsREjrZR7sRsYZm0mSrXYzcktCWf9fg5md3RuLwEnDQWU" +
                "90zaTp13TG84JEmYlr+2QkaEryYNcbV6E2a3QRBPxkfZ443gVVaNqtYMEWaG5K+h6cg+njDHIW5hNT2i91st3iwK6HDPDMy" +
                "QSQ0eIuzMvzmF0wkzaJhH5vr5Rpk/kw+DrFuBYHV5Oppt8gcnVSohvjAgMBAAGgADANBgkqhkiG9w0BAQUFAAOCAQEALKHw" +
                "BrlppbN+CSJwHxO99rv2XVD7L8lwwiMqdMzoNHxZUoob4AcGS3Iyxy6obX8fZh7DCA4nN0AmvrJCmWD3hCLclWKlwXTwvFe" +
                "WIeB4xEB9MVPV6/Hs/QaI9Rjhd+O/Alfzov8y+KyYHEWmwxHj2RtQm1ZeulELzMvRjsqP+m5tOM1hLnPMU+AF1tZ9Fc7Jbm" +
                "7Dwh6TAPpmaH1aVbHsnlZyCp8K4nMozVogcXJqg3qsbeJ6c/TofL0fURqiTJxLKMC0aD0TwVcNwDWEmc8cpqGheG1g6UF5l" +
                "QBpeR2Br4f3LXJgr1Op1RxRy6I+X7h1IL38Q3jAOoU4y04O/+an7g==";

        String expectedUserCert = "MIIERjCCAy6gAwIBAgIFIChmYJgwDQYJKoZIhvcNAQEFBQAwWTELMAkGA1UEBhMCQ04xMDAuBgNVB" +
                "AoTJ0NoaW5hIEZpbmFuY2lhbCBDZXJ0aWZpY2F0aW9uIEF1dGhvcml0eTEYMBYGA1UEAxMPQ0ZDQSBURVNUIE9DQTExMB4X" +
                "DTE5MDgyODA2MzEyNVoXDTIxMDgyODA2MzEyNVoweDELMAkGA1UEBhMCQ04xGDAWBgNVBAoTD0NGQ0EgVEVTVCBPQ0ExMTE" +
                "RMA8GA1UECxMITG9jYWwgUkExFTATBgNVBAsTDEluZGl2aWR1YWwtMTElMCMGA1UEAxQcMDUxQHpoYW5nbGluIUBaMTg2MT" +
                "IyMjkyOTVANTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAMkel4iXo2swpX0mWAdEGw7/QMVXLs8CnNDLKZm3F" +
                "e3S1TRce2DAAwdbE1JinQO5Q9CLJb/FJFpAD57LwXVzlOrEUtYxa/kM/P9e38Qn4neqUmbomxstqi5p2tVXVRX0lBfJ/zMs" +
                "gw3NVTc4HwjgCxESOtlHuxGxhmbSZKtdjNyS0JZ/1+DmZ3dG4vAScNBZT3TNpOnXdMbzgkSZiWv7ZCRoSvJg1xtXoTZrdBE" +
                "E/GR9njjeBVVo2q1gwRZobkr6HpyD6eMMchbmE1PaL3Wy3eLArocM8MzJBJDR4i7My/OYXTCTNomEfm+vlGmT+TD4OsW4Fg" +
                "dXk6mm3yBydVKiG+MCAwEAAaOB9TCB8jAfBgNVHSMEGDAWgBT8C7xEmg4xoYOpgYcnHgVCxr9W+DBIBgNVHSAEQTA/MD0GC" +
                "GCBHIbvKgECMDEwLwYIKwYBBQUHAgEWI2h0dHA6Ly93d3cuY2ZjYS5jb20uY24vdXMvdXMtMTUuaHRtMDoGA1UdHwQzMDEw" +
                "L6AtoCuGKWh0dHA6Ly8yMTAuNzQuNDIuMy9PQ0ExMS9SU0EvY3JsMjY2NTAuY3JsMAsGA1UdDwQEAwID6DAdBgNVHQ4EFgQ" +
                "UOTy45HymVDivPwA83lRoMpShasQwHQYDVR0lBBYwFAYIKwYBBQUHAwIGCCsGAQUFBwMEMA0GCSqGSIb3DQEBBQUAA4IBAQ" +
                "A7NFkDMV1mRV/07DJUdZJzvkm+QihhpLp7D2dUblCCEt0jjY5RdLiWG7HvJnlPTThiSkiejEO0Fy1cQzA5jYRhwp+70X8X9" +
                "bt5jEBP/V4PyRXKKEvKZMdppLIeVI6rZk/gJzPh2FQYv3qWaINilxLOBP8Qa0kdMBwo6D6/MYwnSGv5zP4NLFUysLUJiKoM" +
                "lAzEQSPNnkYRX6nogpkdN91/xgH3GA7fiihrjm5oxMAupCli9LQqvlUvRtv5EKIN9c+ixCAYFagG9IIjMDXbDne77n15i01" +
                "420N8sjfTlr9v3W0v1gBSzjzFOT+TTTUtrjfO/Vm8iqq+z22QKIXgYjSF";

        String issuerCert =
                "-----BEGIN CERTIFICATE-----\n" +
                        "MIIDzzCCAregAwIBAgIKUalCR1Mt5ZSK8jANBgkqhkiG9w0BAQUFADBZMQswCQYD\n" +
                        "VQQGEwJDTjEwMC4GA1UEChMnQ2hpbmEgRmluYW5jaWFsIENlcnRpZmljYXRpb24g\n" +
                        "QXV0aG9yaXR5MRgwFgYDVQQDEw9DRkNBIFRFU1QgQ1MgQ0EwHhcNMTIwODI5MDU1\n" +
                        "NDM2WhcNMzIwODI0MDU1NDM2WjBZMQswCQYDVQQGEwJDTjEwMC4GA1UEChMnQ2hp\n" +
                        "bmEgRmluYW5jaWFsIENlcnRpZmljYXRpb24gQXV0aG9yaXR5MRgwFgYDVQQDEw9D\n" +
                        "RkNBIFRFU1QgT0NBMTEwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQC8\n" +
                        "jn0n8Fp6hcRdACdn1+Y6GAkC6KGgNdKyHPrmsdmhCjnd/i4qUFwnG8cp3D4lFw1G\n" +
                        "jmjSO5yVYbik/NbS6lbNpRgTK3fDfMFvLJpRIC+IFhG9SdAC2hwjsH9qTpL9cK2M\n" +
                        "bSdrC6pBdDgnbzaM9AGBF4Y6vXhj5nah4ZMsBvDp19LzXjlGuTPLuAgv9ZlWknsd\n" +
                        "RN70PIAmvomd10uqX4GIJ4Jq/FdKXOLZ2DNK/NhRyN6Yq71L3ham6tutXeZow5t5\n" +
                        "0254AnUlo1u6SeH9F8itER653o/oMLFwp+63qXAcqrHUlOQPX+JI8fkumSqZ4F2F\n" +
                        "t/HfVMnqdtFNCnh5+eIBAgMBAAGjgZgwgZUwHwYDVR0jBBgwFoAUdN7FjQp9EBqq\n" +
                        "aYNbTSHOhpvMcTgwDAYDVR0TBAUwAwEB/zA4BgNVHR8EMTAvMC2gK6AphidodHRw\n" +
                        "Oi8vMjEwLjc0LjQyLjMvdGVzdHJjYS9SU0EvY3JsMS5jcmwwCwYDVR0PBAQDAgEG\n" +
                        "MB0GA1UdDgQWBBT8C7xEmg4xoYOpgYcnHgVCxr9W+DANBgkqhkiG9w0BAQUFAAOC\n" +
                        "AQEAb7W0K9fZPA+JPw6lRiMDaUJ0oh052yEXreMBfoPulxkBj439qombDiFggRLc\n" +
                        "3g8wIEKzMOzOKXTWtnzYwN3y/JQSuJb/M1QqOEEM2PZwCxI4AkBuH6jg03RjlkHg\n" +
                        "/kTtuIFp9ItBCC2/KkKlp0ENfn4XgVg2KtAjZ7lpyVU0LPnhEqqUVY/xthjlCSa7\n" +
                        "/XHNStRxsfCTIBUWJ8n2FZyQhfV/UkMNHDBIiJR0v6C4Ai0/290WvbPEIAq+03Si\n" +
                        "fsHzBeA0C8lP5VzfAr6wWePaZMCpStpLaoXNcAqReKxQllElOqAhRxC5VKH+rnIQ\n" +
                        "OMRZvB7FRyE9IfwKApngcZbA5g==\n" +
                        "-----END CERTIFICATE-----";

        byte[] rawPublicKeyBytes = Hex.decode(publicKeyStr);
        byte[] rawPrivateKeyBytes = Hex.decode(privateKeyStr);

        CSRBuilder builder = new CSRBuilder();
        builder.init("SHA1withRSA", rawPublicKeyBytes, rawPrivateKeyBytes);

        String csr = builder.buildRequest(countryName,stateName,cityName,
                organizationName,departmentName,domainName,
                emailName);

        assertEquals(expectedCSR,csr);

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
