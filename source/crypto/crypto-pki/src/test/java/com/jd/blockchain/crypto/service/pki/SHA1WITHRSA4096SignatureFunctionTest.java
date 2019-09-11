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
 * @title: SHA1WITHRSA4096SignatureFunctionTest
 * @description: TODO
 * @date 2019-05-16, 10:49
 */
public class SHA1WITHRSA4096SignatureFunctionTest {

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

    //@Test
    public void generateKeyPairTest() {

        CryptoAlgorithm algorithm = Crypto.getAlgorithm("SHA1WITHRSA4096");
        assertNotNull(algorithm);

        AsymmetricKeypair keyPair = Crypto.getSignatureFunction(algorithm).generateKeypair();

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

        // retrievePubKeyTest
        SignatureFunction signatureFunction = Crypto.getSignatureFunction(algorithm);
        PubKey retrievedPubKey = signatureFunction.retrievePubKey(privKey);
        assertEquals(pubKey.getKeyType(), retrievedPubKey.getKeyType());
        assertEquals(pubKey.getRawKeyBytes().length, retrievedPubKey.getRawKeyBytes().length);
        assertEquals(pubKey.getAlgorithm(), retrievedPubKey.getAlgorithm());
        assertArrayEquals(pubKey.toBytes(), retrievedPubKey.toBytes());


        // signAndVerifyTest
        byte[] data = new byte[1024];
        Random random = new Random();
        random.nextBytes(data);

        SignatureDigest signatureDigest = signatureFunction.sign(privKey, data);
        byte[] signatureBytes = signatureDigest.toBytes();
        assertEquals(2 + 512, signatureBytes.length);
        assertEquals(algorithm.code(), signatureDigest.getAlgorithm());
        assertEquals(PKIAlgorithm.SHA1WITHRSA4096.code(), signatureDigest.getAlgorithm());
        assertEquals((short) (SIGNATURE_ALGORITHM | ASYMMETRIC_KEY | ((byte) 32 & 0x00FF)),
                signatureDigest.getAlgorithm());
        algoBytes = BytesUtils.toBytes(signatureDigest.getAlgorithm());
        byte[] rawSinatureBytes = signatureDigest.getRawDigest();
        assertArrayEquals(BytesUtils.concat(algoBytes, rawSinatureBytes), signatureBytes);
        assertTrue(signatureFunction.verify(signatureDigest, pubKey, data));

        // supportAndResolvePrivKeyTest
        byte[] privKeyBytes = privKey.toBytes();
        assertTrue(signatureFunction.supportPrivKey(privKeyBytes));
        PrivKey resolvedPrivKey = signatureFunction.resolvePrivKey(privKeyBytes);
        assertEquals(PRIVATE.CODE, resolvedPrivKey.getKeyType().CODE);
        assertEquals(PKIAlgorithm.SHA1WITHRSA4096.code(), resolvedPrivKey.getAlgorithm());
        assertEquals((short) (SIGNATURE_ALGORITHM | ASYMMETRIC_KEY | ((byte) 32 & 0x00FF)),
                resolvedPrivKey.getAlgorithm());
        assertArrayEquals(privKeyBytes, resolvedPrivKey.toBytes());

        // supportAndResolvePubKeyTest
        byte[] pubKeyBytes = pubKey.toBytes();
        assertTrue(signatureFunction.supportPubKey(pubKeyBytes));
        PubKey resolvedPubKey = signatureFunction.resolvePubKey(pubKeyBytes);
        assertEquals(PUBLIC.CODE, resolvedPubKey.getKeyType().CODE);
        assertEquals(PKIAlgorithm.SHA1WITHRSA4096.code(), resolvedPubKey.getAlgorithm());
        assertEquals((short) (SIGNATURE_ALGORITHM | ASYMMETRIC_KEY | ((byte) 32 & 0x00FF)),
                resolvedPubKey.getAlgorithm());
        assertArrayEquals(pubKeyBytes, resolvedPubKey.toBytes());


        // supportAndResolveDigestTest
        byte[] signatureDigestBytes = signatureDigest.toBytes();
        assertTrue(signatureFunction.supportDigest(signatureDigestBytes));
        SignatureDigest resolvedSignatureDigest = signatureFunction.resolveDigest(signatureDigestBytes);
        assertEquals(512, resolvedSignatureDigest.getRawDigest().length);
        assertEquals(PKIAlgorithm.SHA1WITHRSA4096.code(), resolvedSignatureDigest.getAlgorithm());
        assertEquals((short) (SIGNATURE_ALGORITHM | ASYMMETRIC_KEY | ((byte) 32 & 0x00FF)),
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


        String publicKeyStr =  "30820222300d06092a864886f70d01010105000382020f003082020a0282020100b40edee83b609e" +
                "aa001f2496f95d2f3302513306ec9a8e7fce0d2e141fce7ee357a7465314c3f5f4b08cb6c95803c368ddbfedba483cb" +
                "5c45914037ceee5783fc971a12ef9b0e4158dc379b59499eee629324a9beb350c4c10e50837345be128b91f43a03381" +
                "758bbefe41c45712e4b5fdd5bde780167283706b24e37dd753db65b4c6b3e49cd8be825665d9a29a24b77e76473df02" +
                "2327873555aa33ba2ffcc766cbefedc46ec868d10f817822540eaf5754c074dd6d428355ce24a058a4c9ce41e48aad5" +
                "92e7955cf93d779d03d3acf25ae271346a9e4255e4ed902ae016032b04efbee98f43cd767653e089b37540e537aede9" +
                "dbc04f8f1a858b2764b9eedac80b6a8da5ff02aab4be94e071c70718fde7227cdefec31600a1c55bac16f4de9dea8ab" +
                "7824c1ec783b818dfe005f040a3f6872b1c7a6c31a66c1b06eb8d872a23d1b4fdadf9eed58f93b2a2bc145638a79a81" +
                "904d39b22128ced18a2556c21888ed1ec8ad59bd6764f1ea16eb7f3574c53166f0827b5072d23017bd725adeb63eeb2" +
                "29a4e78d4d7426e936753902bb51e3cd90630314f4ab41272a9e36cb668b2ba9c2ebc02e9ef0c377c88482e839f2f4d" +
                "5c8efcfbe1280e52c6bdf80aa487ae03ff9dd9fd981f78172bc1141ce6031b0b8915658d830c696662507d3cf4ddba8" +
                "7daabf97c15cbef58b15e84f16f879328c7c65076d94fc6b4514549831850203010001";

        String privateKeyStr = "30820942020100300d06092a864886f70d01010105000482092c308209280201000282020100b40e" +
                "dee83b609eaa001f2496f95d2f3302513306ec9a8e7fce0d2e141fce7ee357a7465314c3f5f4b08cb6c95803c368ddb" +
                "fedba483cb5c45914037ceee5783fc971a12ef9b0e4158dc379b59499eee629324a9beb350c4c10e50837345be128b9" +
                "1f43a03381758bbefe41c45712e4b5fdd5bde780167283706b24e37dd753db65b4c6b3e49cd8be825665d9a29a24b77" +
                "e76473df022327873555aa33ba2ffcc766cbefedc46ec868d10f817822540eaf5754c074dd6d428355ce24a058a4c9c" +
                "e41e48aad592e7955cf93d779d03d3acf25ae271346a9e4255e4ed902ae016032b04efbee98f43cd767653e089b3754" +
                "0e537aede9dbc04f8f1a858b2764b9eedac80b6a8da5ff02aab4be94e071c70718fde7227cdefec31600a1c55bac16f" +
                "4de9dea8ab7824c1ec783b818dfe005f040a3f6872b1c7a6c31a66c1b06eb8d872a23d1b4fdadf9eed58f93b2a2bc14" +
                "5638a79a81904d39b22128ced18a2556c21888ed1ec8ad59bd6764f1ea16eb7f3574c53166f0827b5072d23017bd725" +
                "adeb63eeb229a4e78d4d7426e936753902bb51e3cd90630314f4ab41272a9e36cb668b2ba9c2ebc02e9ef0c377c8848" +
                "2e839f2f4d5c8efcfbe1280e52c6bdf80aa487ae03ff9dd9fd981f78172bc1141ce6031b0b8915658d830c696662507" +
                "d3cf4ddba87daabf97c15cbef58b15e84f16f879328c7c65076d94fc6b451454983185020301000102820200063fece" +
                "3452a579f817513454d3efb842afcac077dbf689a4d89de13533e4cdfb1bb6be0b6dc0d65b29a13bf1dd7b598e67782" +
                "b6204b4128e149a54c59136c6ed45c661296169a78180d54a46595c939c26ccd33a7c095de6f08b01610726ef885a26" +
                "cebbad5efc14bbe1204d15be5c5de5b64a5cc279b4e6e20bded4a8126973b2ac0e9de11c6a1282f7d060693909a30e0" +
                "c49cc500bedd38ed99c18830a26dd39f772aabf527410d54ed338db022d674f21f1332d3b5d5f67234a58a97300d130" +
                "aed0d46effc2b4e4895665934188d0c75749e26ca5b97645957989530657b332b4ef202b3d70fe2e07d0d526240fbe1" +
                "68e320357be0f54e18008a233a8137e23ca1c54074b31c57eebee49bf2f1c66ea97a2c846a8d26680b97e1240d6763e" +
                "60bcd8d696c806362b18bf0504a39e4d465a9548091dbe97c36f6d8e038d95a72c0a88ff524dc81fe0ed2afd69f4251" +
                "1a90687a4c632c812234a19a7312b2dea3fcd4515800eca700733b0f83509184fe8d3cd21385f0ef0cec37c433d354f" +
                "aed61662a62902c8708c81e2af20898f649c1bafd600baa0409c943cf82bc90ea20a8972da7ab3a252f4f08df2509de" +
                "e1dacfe787eef4d60c0c92ec7a3c6277d7be39deed7704ac721d8efd138a410d632a32535142cf977b09d9fb680bc96" +
                "c538c00440d5e1ed71f8510e6524e564d69a24d03f1a9a0c326b421e32550801c890282010100e8d72f8f1fbeada724" +
                "3e6df25337a2e7fc43e3d4f39877f89abb3b5e453f20f339a1f35e0a2847122f0bd835e6b43fd276f447da04f85cfca" +
                "0fa8bf49b313239e36f9595ef1bfb9b8247bf01cc407dfc444421161a5b2e96d4d1d90cea185945e9447d21a2d8a461" +
                "f43dff9ba58043feeb49552a3bf2472eea59b2aa8631048c76a15898065be0ef957d4802c827e30339d66ad7aed5851" +
                "a5896f12d33f45800dcb10859531c590af7a75e9fa81f1d937a287fb6b066d58720584af2ae161e083681655ae77f48" +
                "34bfe0accf1d12bbdd8c2644f78b207cfcb2dedf9fe7d29e1ae5c2a5623f5a1770db27d2636450c79fd39bce39f009b" +
                "598e0298e1f77bf8d3d0282010100c5f7af9258a42da93204cecb71c544397bc16691cf0d41308fbd3b88eca7d101d1" +
                "377dfe3b7e66707d3e5543b21033ab6c5b0de577740c6e335f512eeb2a839c3f2baeca4ce80e3fbd8fb93fa983e1719" +
                "5c5fee63bc163df334a80f2767871e3434adc0bc7030dea44fce414a08da7e918e6f030cfb20b2d29033e9ef1be3e08" +
                "ef1f50df0c9325a20ff01d0b7a06761403ae3498aea8bad93110d61a6386d470e630990029e2cb098de40fafa330911" +
                "6c3c6de180b7fa41ae14553e891148ab53e970ce372b1777826983baaccac08290e343761d8daa2505f1dc45b8511a3" +
                "6e2d0909237be9ae7ebbaa00b31de1224f32959e4e6f3428140ca8e1e5580789e902820100674075609c8d2be880940" +
                "6a17cf1a1160ab1f8684895862e023fa0f60ef30da38e1d1914cca04bd3ee74ec2e0ade47a70705108fc7c0734bbbff" +
                "1eed1b9cd74f00624d0d2df954bc032bd9b1ec6774f6d736f70d1c26ef2407bffee65130f6f59f99b57ba3013af40d2" +
                "12926565fe8c734835276e61a6c228bddb6f3138acd1f94c3bbcbbe9623cb5a9931c3ba0aa60a9a2d5137cfd9f3aa59" +
                "3aa63c8b5b8162f07ab8df1391f092827bffe400e3bb73d8a9f8e88495357f3482b2c9a7153bc01c9b88dca4e7b6975" +
                "db73e2aa213daa7462cfa4c63afc67d30bcd0a1d2657da323dc0b06e45d09240cab3e0ac1436922a0ede8a79ca0519d" +
                "375a7621d23269690282010100bbc1299716d2bf2b94f0d260494ada65da6596adfb3d8af24fa11d71c36175eccf4c5" +
                "e065cce88c16f474afea546907aa88dc3243aa2a9976ac99fe96bc82a8269b738534d9558ce432ea87724829bb26a66" +
                "1a56a99dc4e6cf727dd17762cc40ca759934e24e9747f49e14832bb2ade97960adb4dd86f2eaa5d719f10d3d6d00742" +
                "9b33d98638671a9c40507f9775f4da41ff86a465c68b9ccbb371458086c3b9755c8064bb378f55ac94dc73a72b96869" +
                "cd969e1f69b36e7af091a024d8e2a4faf3af999811904937f171c58fd028fd272786cf1a286180f074fee1fdd6b8b5a" +
                "9a8c42e0f3b95ef4474fbace54dbc887865467b0524e64dfda3be7b117e34e1028201001a7e846231c34400c1f704e7" +
                "fec6e46c87d61f269b71942bc9cc72005ba30eea3db5d0e5b0b754f7a00b96c883399982b5b3a9916765c5ed9129e44" +
                "a791ce6892f85758c637bc040da132b8f0cd0ac36ba3aae9334414a77f0b50c0aa03643bfd59b9a621342a4807e46fc" +
                "52a5a12fd3ff6762e181c40c2baf3653043c836b14700463af5d68a2a2897897edb5f217d655d5bcd24e7910062f40e" +
                "00f19e2f94b45efbbf60cbf734830756baf72dcfca8d2858ca5df63336999474945f3744a96e4ce23f9067bbca849ef" +
                "1048cba3a4aad73ed73b0fcd8c2e9f6d06aa768548d7107aa58d9d296f853543f6569e4dd33270540d983460773794f" +
                "e9196fc5a54cd";

        String expectedCSR = "MIIE4jCCAsoCAQAwgZwxCzAJBgNVBAYTAkNOMRAwDgYDVQQIDAdCZWlqaW5nMRAwDgYDVQQHDAdCZWlqaW" +
                "5nMQ8wDQYDVQQKDAZKRC5jb20xHjAcBgNVBAsMFUJsb2NrY2hhaW4gRGVwYXJ0bWVudDEWMBQGA1UEAwwNbGVkZ2VyLmpkL" +
                "mNvbTEgMB4GCSqGSIb3DQEJARYRemhhbmdsaW4zM0BqZC5jb20wggIiMA0GCSqGSIb3DQEBAQUAA4ICDwAwggIKAoICAQC0" +
                "Dt7oO2CeqgAfJJb5XS8zAlEzBuyajn/ODS4UH85+41enRlMUw/X0sIy2yVgDw2jdv+26SDy1xFkUA3zu5Xg/yXGhLvmw5BW" +
                "Nw3m1lJnu5ikySpvrNQxMEOUINzRb4Si5H0OgM4F1i77+QcRXEuS1/dW954AWcoNwayTjfddT22W0xrPknNi+glZl2aKaJL" +
                "d+dkc98CIyeHNVWqM7ov/Mdmy+/txG7IaNEPgXgiVA6vV1TAdN1tQoNVziSgWKTJzkHkiq1ZLnlVz5PXedA9Os8lricTRqn" +
                "kJV5O2QKuAWAysE777pj0PNdnZT4ImzdUDlN67enbwE+PGoWLJ2S57trIC2qNpf8CqrS+lOBxxwcY/ecifN7+wxYAocVbrB" +
                "b03p3qireCTB7Hg7gY3+AF8ECj9ocrHHpsMaZsGwbrjYcqI9G0/a357tWPk7KivBRWOKeagZBNObIhKM7RiiVWwhiI7R7Ir" +
                "Vm9Z2Tx6hbrfzV0xTFm8IJ7UHLSMBe9clretj7rIppOeNTXQm6TZ1OQK7UePNkGMDFPSrQScqnjbLZosrqcLrwC6e8MN3yI" +
                "SC6Dny9NXI78++EoDlLGvfgKpIeuA/+d2f2YH3gXK8EUHOYDGwuJFWWNgwxpZmJQfTz03bqH2qv5fBXL71ixXoTxb4eTKMf" +
                "GUHbZT8a0UUVJgxhQIDAQABoAAwDQYJKoZIhvcNAQEFBQADggIBAKowJTG9mZLbDHRndmDUd7PWrq1AiCdk6DStV39B/REF" +
                "OrMYcW9X4Ak69fhXQDtD4gu2lKtumDY0oJ8xleM2FHUSzdooTWb7P/QtCIBy27sH6nvlefRWi7ngSTNJlDmwgr0l07UzZU1" +
                "Yl/ZULn0XlNAFal+qt+4ZNdiulNwkL6IofXV/8vqOeQw5iICDBYHItyY/mqD8IIaClVd8yNpEuE/W9GdJIDNXQjpug+BxL/" +
                "FbjAs6P3ZzJboedJE5urbru2jjb7atl3w/eDo4r6+XNSD8d1PgVmVhzN2WpUWsZNeH2jd9AA6436GjsBssgSRKEc3FTJ+lO" +
                "0Jw2d8GewXXkIv8CT4L3BFwqZhGQt27wlb87+W4dIC05JIaJx52869dvu1ky1CL73GROXeS8rVYJsPwVmK2xy3QTaeHGEQh" +
                "kiVNeV1cc3mll2z7fgbkjPD8zDNBWUdzSXQMzecY1CBD02iz6LaHfvkI7gXXoiIf1cJrnLtYhv3lG45jKr0E45/Wn7oXmYk" +
                "RM4/zHO4KnY7Pp3b3QTgkRJaPnZiG7aiCrdnTIopDSGpTWSPWDLjgwgaCPPz5Pd2Fk+SAE98o7Cu8O0vxMASpd4liaedASE" +
                "n6hnrvcTkFLLG2ecZzJZ0aEqPi4es0FqlqVZrLPH2UYpECgfhkGQQKAx4eRTAZFhz1GSkd";

        String expectedUserCert = "MIIFRjCCBC6gAwIBAgIFIChmYWIwDQYJKoZIhvcNAQEFBQAwWTELMAkGA1UEBhMCQ04xMDAuBgNVB" +
                "AoTJ0NoaW5hIEZpbmFuY2lhbCBDZXJ0aWZpY2F0aW9uIEF1dGhvcml0eTEYMBYGA1UEAxMPQ0ZDQSBURVNUIE9DQTExMB4X" +
                "DTE5MDgyODA4MDAxMVoXDTIxMDgyODA4MDAxMVoweDELMAkGA1UEBhMCQ04xGDAWBgNVBAoTD0NGQ0EgVEVTVCBPQ0ExMTE" +
                "RMA8GA1UECxMITG9jYWwgUkExFTATBgNVBAsTDEluZGl2aWR1YWwtMTElMCMGA1UEAxQcMDUxQHpoYW5nbGluIUBaMTg2MT" +
                "IyMjkyOTVANzCCAiIwDQYJKoZIhvcNAQEBBQADggIPADCCAgoCggIBALQO3ug7YJ6qAB8klvldLzMCUTMG7JqOf84NLhQfz" +
                "n7jV6dGUxTD9fSwjLbJWAPDaN2/7bpIPLXEWRQDfO7leD/JcaEu+bDkFY3DebWUme7mKTJKm+s1DEwQ5Qg3NFvhKLkfQ6Az" +
                "gXWLvv5BxFcS5LX91b3ngBZyg3BrJON911PbZbTGs+Sc2L6CVmXZopokt352Rz3wIjJ4c1Vaozui/8x2bL7+3Ebsho0Q+Be" +
                "CJUDq9XVMB03W1Cg1XOJKBYpMnOQeSKrVkueVXPk9d50D06zyWuJxNGqeQlXk7ZAq4BYDKwTvvumPQ812dlPgibN1QOU3rt" +
                "6dvAT48ahYsnZLnu2sgLao2l/wKqtL6U4HHHBxj95yJ83v7DFgChxVusFvTeneqKt4JMHseDuBjf4AXwQKP2hyscemwxpmw" +
                "bBuuNhyoj0bT9rfnu1Y+TsqK8FFY4p5qBkE05siEoztGKJVbCGIjtHsitWb1nZPHqFut/NXTFMWbwgntQctIwF71yWt62Pu" +
                "simk541NdCbpNnU5ArtR482QYwMU9KtBJyqeNstmiyupwuvALp7ww3fIhILoOfL01cjvz74SgOUsa9+Aqkh64D/53Z/Zgfe" +
                "BcrwRQc5gMbC4kVZY2DDGlmYlB9PPTduofaq/l8FcvvWLFehPFvh5Mox8ZQdtlPxrRRRUmDGFAgMBAAGjgfUwgfIwHwYDVR" +
                "0jBBgwFoAU/Au8RJoOMaGDqYGHJx4FQsa/VvgwSAYDVR0gBEEwPzA9BghggRyG7yoBAjAxMC8GCCsGAQUFBwIBFiNodHRwO" +
                "i8vd3d3LmNmY2EuY29tLmNuL3VzL3VzLTE1Lmh0bTA6BgNVHR8EMzAxMC+gLaArhilodHRwOi8vMjEwLjc0LjQyLjMvT0NB" +
                "MTEvUlNBL2NybDI2NjU1LmNybDALBgNVHQ8EBAMCA+gwHQYDVR0OBBYEFBl1Gmb89bqbEKyFcTU3eOY/5NmKMB0GA1UdJQQ" +
                "WMBQGCCsGAQUFBwMCBggrBgEFBQcDBDANBgkqhkiG9w0BAQUFAAOCAQEADEU//9rnWN1s3/ariMHIUmgzRUdz3fWYiDRzGC" +
                "mcnnETlXDstGmoYmwCM+QwHw6cyKXkwkg9zV7c7CgM471ZuF00gq115d432Ps3RXGCpfQ2fn3gs+91ky/YqJOOyBb8KL0IP" +
                "r/Zh56/y3XX0gORn4GLqaj+oVZrFcmKrPtVhySlXNiD5BRMq39mUbuLBweGsgNVQ9VxiWc8ZBGjlJ6OVsngbvWrtl3zgkKb" +
                "X9lhr8Bxq3G+jOV8jvr1Dkn4a65g2TWcFquxmPvRc5UwN29CimbC7RViCL3Jp+zrGasqbjycuqu5eSXb6gG4/aV0/K9yn5k" +
                "YlZMIBlbsXSEi5J26pg==";

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
