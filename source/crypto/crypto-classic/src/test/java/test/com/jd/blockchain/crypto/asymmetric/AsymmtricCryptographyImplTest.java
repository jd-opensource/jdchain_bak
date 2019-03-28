//package test.com.jd.blockchain.crypto.asymmetric;
//
//import static com.jd.blockchain.crypto.CryptoKeyType.PRIV_KEY;
//import static com.jd.blockchain.crypto.CryptoKeyType.PUB_KEY;
//import static org.junit.Assert.assertArrayEquals;
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertNull;
//import static org.junit.Assert.assertTrue;
//
//import java.util.Random;
//
//import org.junit.Test;
//
//import com.jd.blockchain.crypto.Ciphertext;
//import com.jd.blockchain.crypto.CryptoAlgorithm;
//import com.jd.blockchain.crypto.CryptoException;
//import com.jd.blockchain.crypto.CryptoKeyType;
//import com.jd.blockchain.crypto.PrivKey;
//import com.jd.blockchain.crypto.PubKey;
//import com.jd.blockchain.crypto.asymmetric.AsymmetricCryptography;
//import com.jd.blockchain.crypto.asymmetric.AsymmetricEncryptionFunction;
//import com.jd.blockchain.crypto.asymmetric.CryptoKeyPair;
//import com.jd.blockchain.crypto.asymmetric.SignatureDigest;
//import com.jd.blockchain.crypto.asymmetric.SignatureFunction;
//import com.jd.blockchain.crypto.impl.AsymmtricCryptographyImpl;
//import com.jd.blockchain.crypto.service.classic.ClassicCryptoService;
//import com.jd.blockchain.utils.io.BytesUtils;
//
//public class AsymmtricCryptographyImplTest {
//
//    @Test
//    public void testGenerateKeyPair() {
//
//        //test ED25519
//    	CryptoAlgorithm algorithm = ClassicCryptoService.ED25519.getAlgorithm();
//        CryptoKeyPair keyPair = ClassicCryptoService.ED25519.generateKeyPair();
//
//        PubKey pubKey = keyPair.getPubKey();
//        PrivKey privKey = keyPair.getPrivKey();
//
//        assertNotNull(pubKey);
//        assertNotNull(privKey);
//
//        assertEquals(ClassicCryptoService.ED25519.getAlgorithm().code(),pubKey.getAlgorithm().code());
//        assertEquals(ClassicCryptoService.ED25519.getAlgorithm().code(),privKey.getAlgorithm().code());
//
//        assertEquals(32,pubKey.getRawKeyBytes().length);
//        assertEquals(32,privKey.getRawKeyBytes().length);
//
//        byte[] pubKeyBytes = pubKey.toBytes();
//        byte[] privKeyBytes = privKey.toBytes();
//
//        assertEquals(32+1+1,pubKeyBytes.length);
//        assertEquals(32+1+1,privKeyBytes.length);
//
//        byte[] algorithmBytes = CryptoAlgorithm.toBytes(ClassicCryptoService.ED25519.getAlgorithm());
//        assertEquals(algorithmBytes[0],pubKeyBytes[0]);
//        assertEquals(algorithmBytes[1],pubKeyBytes[1]);
//        assertEquals(algorithmBytes[0],privKeyBytes[0]);
//        assertEquals(algorithmBytes[1],privKeyBytes[1]);
//
//        assertEquals(pubKey.getKeyType().CODE,pubKeyBytes[CryptoAlgorithm.CODE_SIZE]);
//        assertEquals(privKey.getKeyType().CODE,privKeyBytes[CryptoAlgorithm.CODE_SIZE]);
//
//
//        //test SM2
//        algorithm = CryptoAlgorithm.SM2;
//        keyPair = asymmetricCrypto.generateKeyPair(algorithm);
//
//        assertNotNull(keyPair);
//
//        pubKey = keyPair.getPubKey();
//        privKey = keyPair.getPrivKey();
//
//        assertNotNull(pubKey);
//        assertNotNull(privKey);
//
//        assertEquals(algorithm,pubKey.getAlgorithm());
//        assertEquals(algorithm,privKey.getAlgorithm());
//
//        assertEquals(65,pubKey.getRawKeyBytes().length);
//        assertEquals(32,privKey.getRawKeyBytes().length);
//
//        pubKeyBytes = pubKey.toBytes();
//        privKeyBytes = privKey.toBytes();
//
//        assertEquals(32+1+1,privKeyBytes.length);
//        assertEquals(65+1+1,pubKeyBytes.length);
//
//        assertEquals(CryptoAlgorithm.SM2.CODE,pubKeyBytes[0]);
//        assertEquals(CryptoAlgorithm.SM2.CODE,privKeyBytes[0]);
//        assertEquals(CryptoAlgorithm.SM2, CryptoAlgorithm.valueOf(pubKey.getAlgorithm().CODE));
//        assertEquals(CryptoAlgorithm.SM2, CryptoAlgorithm.valueOf(privKey.getAlgorithm().CODE));
//
//        assertEquals(pubKey.getKeyType().CODE,pubKeyBytes[1]);
//        assertEquals(privKey.getKeyType().CODE,privKeyBytes[1]);
//
//
//        //test JNIED25519
//        algorithm = CryptoAlgorithm.JNIED25519;
//        keyPair = asymmetricCrypto.generateKeyPair(algorithm);
//
//        assertNotNull(keyPair);
//
//        pubKey = keyPair.getPubKey();
//        privKey = keyPair.getPrivKey();
//
//        assertNotNull(pubKey);
//        assertNotNull(privKey);
//
//        assertEquals(algorithm,pubKey.getAlgorithm());
//        assertEquals(algorithm,privKey.getAlgorithm());
//
//        assertEquals(32,pubKey.getRawKeyBytes().length);
//        assertEquals(32,privKey.getRawKeyBytes().length);
//
//        pubKeyBytes = pubKey.toBytes();
//        privKeyBytes = privKey.toBytes();
//
//        assertEquals(32+1+1,pubKeyBytes.length);
//        assertEquals(32+1+1,privKeyBytes.length);
//
//        assertEquals(CryptoAlgorithm.JNIED25519.CODE,pubKeyBytes[0]);
//        assertEquals(CryptoAlgorithm.JNIED25519.CODE,privKeyBytes[0]);
//        assertEquals(CryptoAlgorithm.JNIED25519, CryptoAlgorithm.valueOf(pubKey.getAlgorithm().CODE));
//        assertEquals(CryptoAlgorithm.JNIED25519, CryptoAlgorithm.valueOf(privKey.getAlgorithm().CODE));
//
//        assertEquals(pubKey.getKeyType().CODE,pubKeyBytes[1]);
//        assertEquals(privKey.getKeyType().CODE,privKeyBytes[1]);
//    }
//
//    @Test
//    public void testGetSignatureFunction() {
//
//        AsymmetricCryptography asymmetricCrypto =  new AsymmtricCryptographyImpl();
//        Random random = new Random();
//
//
//        //test ED25519
//        CryptoAlgorithm algorithm = CryptoAlgorithm.ED25519;
//
//        // 测试256字节的消息进行签名
//        byte[] data = new byte[256];
//        random.nextBytes(data);
//        verifyGetSignatureFunction(asymmetricCrypto,algorithm,data,32,32,64,null);
//
//        //错误的算法标识
//        verifyGetSignatureFunction(asymmetricCrypto,CryptoAlgorithm.AES,data,32,32,64,IllegalArgumentException.class);
//
//        data = null;
//        verifyGetSignatureFunction(asymmetricCrypto,algorithm,data,32,32,64,NullPointerException.class);
//
//
//        //test SM2
//        algorithm = CryptoAlgorithm.SM2;
//
//        // 测试256字节的消息进行签名
//        data = new byte[256];
//        random.nextBytes(data);
//        verifyGetSignatureFunction(asymmetricCrypto,algorithm,data,65,32,64,null);
//
//        //错误的算法标识
//        verifyGetSignatureFunction(asymmetricCrypto,CryptoAlgorithm.AES,data,65,32,64,IllegalArgumentException.class);
//
//        data = null;
//        verifyGetSignatureFunction(asymmetricCrypto,algorithm,data,65,32,64,NullPointerException.class);
//
//
//        //test JNNIED25519
//        algorithm = CryptoAlgorithm.JNIED25519;
//
//        // 测试256字节的消息进行签名
//        data = new byte[256];
//        random.nextBytes(data);
//        verifyGetSignatureFunction(asymmetricCrypto,algorithm,data,32,32,64,null);
//
//        //错误的算法标识
//        verifyGetSignatureFunction(asymmetricCrypto,CryptoAlgorithm.AES,data,32,32,64,IllegalArgumentException.class);
//
//        data = null;
//        verifyGetSignatureFunction(asymmetricCrypto,algorithm,data,32,32,64,IllegalArgumentException.class);
//    }
//
//    private void verifyGetSignatureFunction(AsymmetricCryptography asymmetricCrypto, CryptoAlgorithm algorithm, byte[] data,
//                                            int expectedPubKeyLength, int expectedPrivKeyLength,
//                                            int expectedSignatureDigestLength, Class<?> expectedException){
//
//        //初始化一个异常
//        Exception actualEx = null;
//
//        try {
//            SignatureFunction sf = asymmetricCrypto.getSignatureFunction(algorithm);
//
//            assertNotNull(sf);
//
//            CryptoKeyPair keyPair = sf.generateKeyPair();
//            PubKey pubKey = keyPair.getPubKey();
//            PrivKey privKey = keyPair.getPrivKey();
//            byte[] rawPubKeyBytes = pubKey.getRawKeyBytes();
//            byte[] rawPrivKeyBytes = privKey.getRawKeyBytes();
//            byte[] pubKeyBytes = pubKey.toBytes();
//            byte[] privKeyBytes = privKey.toBytes();
//
//            assertEquals(algorithm, pubKey.getAlgorithm());
//            assertEquals(algorithm, privKey.getAlgorithm());
//            assertEquals(expectedPubKeyLength,rawPubKeyBytes.length);
//            assertEquals(expectedPrivKeyLength,rawPrivKeyBytes.length);
//
//            assertArrayEquals(BytesUtils.concat(new byte[]{algorithm.CODE},new byte[]{CryptoKeyType.PUB_KEY.CODE},rawPubKeyBytes), pubKeyBytes);
//            assertArrayEquals(BytesUtils.concat(new byte[]{algorithm.CODE},new byte[]{CryptoKeyType.PRIV_KEY.CODE},rawPrivKeyBytes), privKeyBytes);
//
//            SignatureDigest signatureDigest = sf.sign(privKey,data);
//            byte[] rawDigest = signatureDigest.getRawDigest();
//
//            assertEquals(algorithm,signatureDigest.getAlgorithm());
//            assertEquals(expectedSignatureDigestLength,rawDigest.length);
//            byte[] signatureDigestBytes = signatureDigest.toBytes();
//            assertArrayEquals(BytesUtils.concat(new byte[]{algorithm.CODE},rawDigest),signatureDigestBytes);
//
//            assertTrue(signatureDigest.equals(signatureDigest));
//            assertEquals(signatureDigest.hashCode(),signatureDigest.hashCode());
//
//            assertTrue(sf.verify(signatureDigest,pubKey,data));
//
//            assertTrue(sf.supportPubKey(pubKeyBytes));
//            assertTrue(sf.supportPrivKey(privKeyBytes));
//            assertTrue(sf.supportDigest(signatureDigestBytes));
//
//            assertEquals(pubKey,sf.resolvePubKey(pubKeyBytes));
//            assertEquals(privKey,sf.resolvePrivKey(privKeyBytes));
//            assertEquals(signatureDigest,sf.resolveDigest(signatureDigestBytes));
//
//            assertEquals(algorithm,sf.getAlgorithm());
//
//        } catch (Exception e){
//            actualEx = e;
//        }
//
//        if (expectedException == null) {
//            assertNull(actualEx);
//        }
//        else {
//            assertNotNull(actualEx);
//            assertTrue(expectedException.isAssignableFrom(actualEx.getClass()));
//        }
//    }
//
//    @Test
//    public void testVerify() {
//
//        AsymmetricCryptography asymmetricCrypto =  new AsymmtricCryptographyImpl();
//        Random randomData = new Random();
//
//        //test ED25519
//        CryptoAlgorithm algorithm = CryptoAlgorithm.ED25519;
//
//        // 测试256字节的消息进行签名
//        byte[] data = new byte[256];
//        randomData.nextBytes(data);
//        SignatureFunction sf = asymmetricCrypto.getSignatureFunction(algorithm);
//        CryptoKeyPair keyPair = sf.generateKeyPair();
//        byte[] pubKeyBytes = keyPair.getPubKey().toBytes();
//
//        byte[] signatureDigestBytes = sf.sign(keyPair.getPrivKey(),data).toBytes();
//        verifyVerify(asymmetricCrypto,true,data,pubKeyBytes,signatureDigestBytes,null);
//
//        //签名数据末尾两个字节丢失情况下，抛出异常
//        byte[] truncatedSignatureDigestBytes = new byte[signatureDigestBytes.length-2];
//        System.arraycopy(signatureDigestBytes,0,truncatedSignatureDigestBytes,0,truncatedSignatureDigestBytes.length);
//        verifyVerify(asymmetricCrypto,false,data,pubKeyBytes,truncatedSignatureDigestBytes,IllegalArgumentException.class);
//
//        byte[] signatureDigestBytesWithWrongAlgCode = signatureDigestBytes;
//        signatureDigestBytesWithWrongAlgCode[0] = CryptoAlgorithm.SHA256.CODE;
//        verifyVerify(asymmetricCrypto,false,data,pubKeyBytes,signatureDigestBytesWithWrongAlgCode,IllegalArgumentException.class);
//
//        signatureDigestBytes = null;
//        verifyVerify(asymmetricCrypto,false,data,pubKeyBytes,signatureDigestBytes,NullPointerException.class);
//
//
//        //test SM2
//        algorithm = CryptoAlgorithm.SM2;
//
//        // 测试256字节的消息进行签名
//        data = new byte[256];
//        randomData.nextBytes(data);
//        sf = asymmetricCrypto.getSignatureFunction(algorithm);
//        keyPair = sf.generateKeyPair();
//        pubKeyBytes = keyPair.getPubKey().toBytes();
//
//        signatureDigestBytes = sf.sign(keyPair.getPrivKey(),data).toBytes();
//        verifyVerify(asymmetricCrypto,true,data,pubKeyBytes,signatureDigestBytes,null);
//
//        //签名数据末尾两个字节丢失情况下，抛出异常
//        truncatedSignatureDigestBytes = new byte[signatureDigestBytes.length-2];
//        System.arraycopy(signatureDigestBytes,0,truncatedSignatureDigestBytes,0,truncatedSignatureDigestBytes.length);
//        verifyVerify(asymmetricCrypto,false,data,pubKeyBytes,truncatedSignatureDigestBytes,IllegalArgumentException.class);
//
//        signatureDigestBytesWithWrongAlgCode = signatureDigestBytes;
//        signatureDigestBytesWithWrongAlgCode[0] = CryptoAlgorithm.SHA256.CODE;
//        verifyVerify(asymmetricCrypto,false,data,pubKeyBytes,signatureDigestBytesWithWrongAlgCode,IllegalArgumentException.class);
//
//        signatureDigestBytes = null;
//        verifyVerify(asymmetricCrypto,false,data,pubKeyBytes,signatureDigestBytes,NullPointerException.class);
//
//        //test JNIED25519
//        algorithm = CryptoAlgorithm.JNIED25519;
//
//        // 测试256字节的消息进行签名
//        data = new byte[256];
//        randomData.nextBytes(data);
//        sf = asymmetricCrypto.getSignatureFunction(algorithm);
//        keyPair = sf.generateKeyPair();
//        pubKeyBytes = keyPair.getPubKey().toBytes();
//
//        signatureDigestBytes = sf.sign(keyPair.getPrivKey(),data).toBytes();
//        verifyVerify(asymmetricCrypto,true,data,pubKeyBytes,signatureDigestBytes,null);
//
//        //签名数据末尾两个字节丢失情况下，抛出异常
//        truncatedSignatureDigestBytes = new byte[signatureDigestBytes.length-2];
//        System.arraycopy(signatureDigestBytes,0,truncatedSignatureDigestBytes,0,truncatedSignatureDigestBytes.length);
//        verifyVerify(asymmetricCrypto,false,data,pubKeyBytes,truncatedSignatureDigestBytes,IllegalArgumentException.class);
//
//        signatureDigestBytesWithWrongAlgCode = signatureDigestBytes;
//        signatureDigestBytesWithWrongAlgCode[0] = CryptoAlgorithm.SHA256.CODE;
//        verifyVerify(asymmetricCrypto,false,data,pubKeyBytes,signatureDigestBytesWithWrongAlgCode,IllegalArgumentException.class);
//
//        signatureDigestBytes = null;
//        verifyVerify(asymmetricCrypto,false,data,pubKeyBytes,signatureDigestBytes,NullPointerException.class);
//    }
//
//    private void verifyVerify(AsymmetricCryptography asymmetricCrypto,boolean expectedResult,byte[] data,
//                              byte[] pubKeyBytes, byte[] signatureDigestBytes, Class<?> expectedException){
//
//        //初始化一个异常
//        Exception actualEx = null;
//        boolean pass = false;
//
//        try {
//
//            pass = asymmetricCrypto.verify(signatureDigestBytes,pubKeyBytes,data);
//
//        }
//        catch (Exception e){
//            actualEx = e;
//        }
//
//        assertEquals(expectedResult, pass);
//
//        if (expectedException == null) {
//            assertNull(actualEx);
//        }
//        else {
//            assertNotNull(actualEx);
//            assertTrue(expectedException.isAssignableFrom(actualEx.getClass()));
//        }
//    }
//
//    @Test
//    public void testGetAsymmetricEncryptionFunction() {
//
//        AsymmetricCryptography asymmetricCrypto =  new AsymmtricCryptographyImpl();
//        Random random = new Random();
//
//
//        //test SM2
//        CryptoAlgorithm algorithm = CryptoAlgorithm.SM2;
//
//        //Case 1: SM2Encryption with 16 bytes data
//        byte[] data = new byte[16];
//        random.nextBytes(data);
//        verifyGetAsymmetricEncryptionFunction(asymmetricCrypto, algorithm,65,32,65+16+32,data,null);
//
//        //Case 2: SM2Encryption with 256 bytes data
//        data = new byte[256];
//        random.nextBytes(data);
//        verifyGetAsymmetricEncryptionFunction(asymmetricCrypto, algorithm,65,32,65+256+32,data,null);
//
//        //Case 3: SM2Encryption with 1 bytes data
//        data = new byte[3];
//        random.nextBytes(data);
//        verifyGetAsymmetricEncryptionFunction(asymmetricCrypto, algorithm,65,32,65+3+32,data,null);
//
//        //Case 4: SM2Encryption with wrong algorithm
//        verifyGetAsymmetricEncryptionFunction(asymmetricCrypto,CryptoAlgorithm.AES,65,32,65+3+32,data,IllegalArgumentException.class);
//
//        //Case 5: SM2Encryption with null data
//        data = null;
//        verifyGetAsymmetricEncryptionFunction(asymmetricCrypto,algorithm,65,32,65+32,data,NullPointerException.class);
//    }
//
//    private void verifyGetAsymmetricEncryptionFunction(AsymmetricCryptography asymmetricCrypto, CryptoAlgorithm algorithm,
//                                                       int expectedPubKeyLength, int expectedPrivKeyLength,
//                                                       int expectedCiphertextLength, byte[] data, Class<?> expectedException){
//
//        //初始化一个异常
//        Exception actualEx = null;
//
//        try {
//            AsymmetricEncryptionFunction aef = asymmetricCrypto.getAsymmetricEncryptionFunction(algorithm);
//            //验证获取的算法实例非空
//            assertNotNull(aef);
//
//            CryptoKeyPair keyPair = aef.generateKeyPair();
//            PubKey pubKey = keyPair.getPubKey();
//            PrivKey privKey = keyPair.getPrivKey();
//            byte[] rawPubKeyBytes = pubKey.getRawKeyBytes();
//            byte[] rawPrivKeyBytes = privKey.getRawKeyBytes();
//            byte[] pubKeyBytes = pubKey.toBytes();
//            byte[] privKeyBytes = privKey.toBytes();
//
//            assertEquals(algorithm, pubKey.getAlgorithm());
//            assertEquals(algorithm, privKey.getAlgorithm());
//            assertEquals(expectedPubKeyLength,rawPubKeyBytes.length);
//            assertEquals(expectedPrivKeyLength,rawPrivKeyBytes.length);
//
//            assertArrayEquals(BytesUtils.concat(new byte[]{algorithm.CODE},new byte[]{CryptoKeyType.PUB_KEY.CODE},rawPubKeyBytes), pubKeyBytes);
//            assertArrayEquals(BytesUtils.concat(new byte[]{algorithm.CODE},new byte[]{CryptoKeyType.PRIV_KEY.CODE},rawPrivKeyBytes), privKeyBytes);
//
//            Ciphertext ciphertext = aef.encrypt(pubKey,data);
//            byte[] rawCiphertextBytes = ciphertext.getRawCiphertext();
//
//            assertEquals(algorithm,ciphertext.getAlgorithm());
//            assertEquals(expectedCiphertextLength,rawCiphertextBytes.length);
//            byte[] ciphertextBytes = ciphertext.toBytes();
//            assertArrayEquals(BytesUtils.concat(new byte[]{algorithm.CODE},rawCiphertextBytes),ciphertextBytes);
//
//            assertArrayEquals(data,aef.decrypt(privKey,ciphertext));
//
//            assertTrue(aef.supportPubKey(pubKeyBytes));
//            assertTrue(aef.supportPrivKey(privKeyBytes));
//            assertTrue(aef.supportCiphertext(ciphertextBytes));
//
//            assertEquals(pubKey,aef.resolvePubKey(pubKeyBytes));
//            assertEquals(privKey,aef.resolvePrivKey(privKeyBytes));
//            assertEquals(ciphertext,aef.resolveCiphertext(ciphertextBytes));
//
//            assertEquals(algorithm,aef.getAlgorithm());
//
//
//        }catch (Exception e){
//            actualEx = e;
//        }
//
//        if(expectedException == null){
//            assertNull(actualEx);
//        }
//        else {
//            assertNotNull(actualEx);
//            assertTrue(expectedException.isAssignableFrom(actualEx.getClass()));
//        }
//    }
//
//    @Test
//    public void testDecrypt() {
//
//        AsymmetricCryptography asymmetricCrypto =  new AsymmtricCryptographyImpl();
//        Random random = new Random();
//
//        byte[] data = new byte[16];
//        random.nextBytes(data);
//
//        //test SM2
//        CryptoAlgorithm algorithm = CryptoAlgorithm.SM2;
//        AsymmetricEncryptionFunction aef = asymmetricCrypto.getAsymmetricEncryptionFunction(algorithm);
//        CryptoKeyPair keyPair = aef.generateKeyPair();
//        PubKey pubKey = keyPair.getPubKey();
//        PrivKey privKey = keyPair.getPrivKey();
//        byte[] rawPrivKeyBytes = privKey.getRawKeyBytes();
//        Ciphertext ciphertext = aef.encrypt(pubKey,data);
//        byte[] ciphertextBytes = ciphertext.toBytes();
//
//        verifyDecrypt(asymmetricCrypto, algorithm, rawPrivKeyBytes, data, ciphertextBytes, null);
//
//        //密钥的算法标识与密文的算法标识不一致情况
//        verifyDecrypt(asymmetricCrypto, CryptoAlgorithm.AES, rawPrivKeyBytes, data, ciphertextBytes, IllegalArgumentException.class);
//
//        //密文末尾两个字节丢失情况下，抛出异常
//        byte[] truncatedCiphertextBytes = new byte[ciphertextBytes.length-2];
//        System.arraycopy(ciphertextBytes,0,truncatedCiphertextBytes,0,truncatedCiphertextBytes.length);
//        verifyDecrypt(asymmetricCrypto, algorithm, rawPrivKeyBytes, data, truncatedCiphertextBytes, com.jd.blockchain.crypto.CryptoException.class);
//
//        byte[] ciphertextBytesWithWrongAlgCode = ciphertextBytes;
//        ciphertextBytesWithWrongAlgCode[0] = CryptoAlgorithm.SHA256.CODE;
//        verifyDecrypt(asymmetricCrypto,algorithm,rawPrivKeyBytes,data,ciphertextBytesWithWrongAlgCode,IllegalArgumentException.class);
//
//        ciphertextBytes = null;
//        verifyDecrypt(asymmetricCrypto,algorithm,rawPrivKeyBytes,data,ciphertextBytes,NullPointerException.class);
//    }
//
//    private void verifyDecrypt(AsymmetricCryptography asymmetricCrypto, CryptoAlgorithm algorithm,
//                               byte[] key, byte[] data, byte[] ciphertextBytes, Class<?> expectedException){
//        Exception actualEx = null;
//
//        try {
//            PrivKey privKey = new PrivKey(algorithm,key);
//
//            byte[] plaintext = asymmetricCrypto.decrypt(privKey.toBytes(), ciphertextBytes);
//
//            //解密后的明文与初始的明文一致
//            assertArrayEquals(data,plaintext);
//        }
//        catch (Exception e){
//            actualEx = e;
//        }
//
//        if (expectedException == null) {
//            assertNull(actualEx);
//        }
//        else {
//            assertNotNull(actualEx);
//            assertTrue(expectedException.isAssignableFrom(actualEx.getClass()));
//        }
//    }
//
//    @Test
//    public void testResolveCiphertext() {
//
//
//        AsymmetricCryptography asymmetricCrypto =  new AsymmtricCryptographyImpl();
//        Random random = new Random();
//
//        byte[] data = new byte[16];
//        random.nextBytes(data);
//
//        //test SM2
//        CryptoAlgorithm algorithm = CryptoAlgorithm.SM2;
//        AsymmetricEncryptionFunction aef = asymmetricCrypto.getAsymmetricEncryptionFunction(algorithm);
//        CryptoKeyPair keyPair = aef.generateKeyPair();
//        PubKey pubKey = keyPair.getPubKey();
//        PrivKey privKey = keyPair.getPrivKey();
//        byte[] rawPrivKeyBytes = privKey.getRawKeyBytes();
//        Ciphertext ciphertext = aef.encrypt(pubKey,data);
//        byte[] ciphertextBytes = ciphertext.toBytes();
//
//        verifyResolveCiphertext(asymmetricCrypto, algorithm, ciphertextBytes, null);
//
//
//        //密文末尾两个字节丢失情况下，抛出异常
//        byte[] truncatedCiphertextBytes = new byte[ciphertextBytes.length-2];
//        System.arraycopy(ciphertextBytes,0,truncatedCiphertextBytes,0,truncatedCiphertextBytes.length);
//        verifyDecrypt(asymmetricCrypto, algorithm, rawPrivKeyBytes, data, truncatedCiphertextBytes, CryptoException.class);
//
//        byte[] ciphertextBytesWithWrongAlgCode = ciphertextBytes;
//        ciphertextBytesWithWrongAlgCode[0] = CryptoAlgorithm.SHA256.CODE;
//        verifyResolveCiphertext(asymmetricCrypto,algorithm,ciphertextBytesWithWrongAlgCode,IllegalArgumentException.class);
//
//        ciphertextBytes = null;
//        verifyResolveCiphertext(asymmetricCrypto,algorithm,ciphertextBytes,NullPointerException.class);
//    }
//
//    private void verifyResolveCiphertext(AsymmetricCryptography asymmetricCrypto, CryptoAlgorithm algorithm, byte[] ciphertextBytes,
//                                         Class<?> expectedException){
//        Exception actualEx = null;
//
//        try {
//
//            Ciphertext ciphertext = asymmetricCrypto.resolveCiphertext(ciphertextBytes);
//
//            assertNotNull(ciphertext);
//
//            assertEquals(algorithm, ciphertext.getAlgorithm());
//
//            assertArrayEquals(ciphertextBytes, ciphertext.toBytes());
//        }
//        catch (Exception e){
//            actualEx = e;
//        }
//
//        if (expectedException == null) {
//            assertNull(actualEx);
//        }
//        else {
//            assertNotNull(actualEx);
//            assertTrue(expectedException.isAssignableFrom(actualEx.getClass()));
//        }
//    }
//
//    @Test
//    public void testTryResolveCiphertext() {
//    }
//
//    @Test
//    public void testResolveSignatureDigest() {
//
//        AsymmetricCryptography asymmetricCrypto =  new AsymmtricCryptographyImpl();
//        Random randomData = new Random();
//
//        //test ED25519
//        CryptoAlgorithm algorithm = CryptoAlgorithm.ED25519;
//
//        // 测试256字节的消息进行签名
//        byte[] data = new byte[256];
//        randomData.nextBytes(data);
//        SignatureFunction sf = asymmetricCrypto.getSignatureFunction(algorithm);
//        CryptoKeyPair keyPair = sf.generateKeyPair();
//
//        byte[] signatureDigestBytes = sf.sign(keyPair.getPrivKey(),data).toBytes();
//        verifyResolveSignatureDigest(asymmetricCrypto,algorithm,64,signatureDigestBytes,null);
//
//        //签名数据末尾两个字节丢失情况下，抛出异常
//        byte[] truncatedSignatureDigestBytes = new byte[signatureDigestBytes.length-2];
//        System.arraycopy(signatureDigestBytes,0,truncatedSignatureDigestBytes,0,truncatedSignatureDigestBytes.length);
//        verifyResolveSignatureDigest(asymmetricCrypto,algorithm,64,truncatedSignatureDigestBytes,IllegalArgumentException.class);
//
//        signatureDigestBytes = null;
//        verifyResolveSignatureDigest(asymmetricCrypto,algorithm,64,signatureDigestBytes,NullPointerException.class);
//
//
//        //test SM2
//        algorithm = CryptoAlgorithm.SM2;
//
//        // 测试256字节的消息进行签名
//        data = new byte[256];
//        randomData.nextBytes(data);
//        sf = asymmetricCrypto.getSignatureFunction(algorithm);
//        keyPair = sf.generateKeyPair();
//
//        signatureDigestBytes = sf.sign(keyPair.getPrivKey(),data).toBytes();
//        verifyResolveSignatureDigest(asymmetricCrypto,algorithm,64,signatureDigestBytes,null);
//
//        //签名数据末尾两个字节丢失情况下，抛出异常
//        truncatedSignatureDigestBytes = new byte[signatureDigestBytes.length-2];
//        System.arraycopy(signatureDigestBytes,0,truncatedSignatureDigestBytes,0,truncatedSignatureDigestBytes.length);
//        verifyResolveSignatureDigest(asymmetricCrypto,algorithm,64,truncatedSignatureDigestBytes,IllegalArgumentException.class);
//
//        signatureDigestBytes = null;
//        verifyResolveSignatureDigest(asymmetricCrypto,algorithm,64,signatureDigestBytes,NullPointerException.class);
//
//        //test JNIED25519
//        algorithm = CryptoAlgorithm.JNIED25519;
//
//        // 测试256字节的消息进行签名
//        data = new byte[256];
//        randomData.nextBytes(data);
//        sf = asymmetricCrypto.getSignatureFunction(algorithm);
//        keyPair = sf.generateKeyPair();
//
//        signatureDigestBytes = sf.sign(keyPair.getPrivKey(),data).toBytes();
//        verifyResolveSignatureDigest(asymmetricCrypto,algorithm,64,signatureDigestBytes,null);
//
//        //签名数据末尾两个字节丢失情况下，抛出异常
//        truncatedSignatureDigestBytes = new byte[signatureDigestBytes.length-2];
//        System.arraycopy(signatureDigestBytes,0,truncatedSignatureDigestBytes,0,truncatedSignatureDigestBytes.length);
//        verifyResolveSignatureDigest(asymmetricCrypto,algorithm,64,truncatedSignatureDigestBytes,IllegalArgumentException.class);
//
//        signatureDigestBytes = null;
//        verifyResolveSignatureDigest(asymmetricCrypto,algorithm,64,signatureDigestBytes,NullPointerException.class);
//    }
//
//    private void verifyResolveSignatureDigest(AsymmetricCryptography asymmetricCrypto, CryptoAlgorithm algorithm,
//                                              int expectedSignatureDigestLength,
//                                              byte[] signatureDigestBytes, Class<?> expectedException){
//
//        //初始化一个异常
//        Exception actualEx = null;
//
//        try {
//
//            SignatureDigest signatureDigest = asymmetricCrypto.resolveSignatureDigest(signatureDigestBytes);
//
//            assertNotNull(signatureDigest);
//
//            assertEquals(algorithm,signatureDigest.getAlgorithm());
//
//            assertEquals(expectedSignatureDigestLength,signatureDigest.getRawDigest().length);
//
//            assertArrayEquals(signatureDigestBytes,signatureDigest.toBytes());
//
//        }
//        catch (Exception e){
//            actualEx = e;
//        }
//
//        if (expectedException == null) {
//            assertNull(actualEx);
//        }
//        else {
//            assertNotNull(actualEx);
//            assertTrue(expectedException.isAssignableFrom(actualEx.getClass()));
//        }
//    }
//
//    @Test
//    public void testTryResolveSignatureDigest() {
//    }
//
//    @Test
//    public void testRetrievePubKeyBytes() {
//
//        AsymmetricCryptography asymmetricCrypto =  new AsymmtricCryptographyImpl();
//
//        //test ED25519
//        CryptoAlgorithm algorithm = CryptoAlgorithm.ED25519;
//
//        CryptoKeyPair keyPair = asymmetricCrypto.generateKeyPair(algorithm);
//
//        byte[] expectedPrivKeyBytes = keyPair.getPrivKey().toBytes();
//        byte[] expectedPubKeyBytes = keyPair.getPubKey().toBytes();
//
//        byte[] pubKeyBytes = asymmetricCrypto.retrievePubKeyBytes(expectedPrivKeyBytes);
//
//        assertArrayEquals(expectedPubKeyBytes,pubKeyBytes);
//
//
//        //test SM2
//        algorithm = CryptoAlgorithm.SM2;
//
//        keyPair = asymmetricCrypto.generateKeyPair(algorithm);
//
//        expectedPrivKeyBytes = keyPair.getPrivKey().toBytes();
//        expectedPubKeyBytes = keyPair.getPubKey().toBytes();
//
//        pubKeyBytes = asymmetricCrypto.retrievePubKeyBytes(expectedPrivKeyBytes);
//
//        assertArrayEquals(expectedPubKeyBytes,pubKeyBytes);
//
//
//        //test JNIED25519
//        algorithm = CryptoAlgorithm.JNIED25519;
//
//        keyPair = asymmetricCrypto.generateKeyPair(algorithm);
//
//        expectedPrivKeyBytes = keyPair.getPrivKey().toBytes();
//        expectedPubKeyBytes = keyPair.getPubKey().toBytes();
//
//        pubKeyBytes = asymmetricCrypto.retrievePubKeyBytes(expectedPrivKeyBytes);
//
//        assertArrayEquals(expectedPubKeyBytes,pubKeyBytes);
//
//    }
//
//
//    @Test
//    public void testResolvePubKey() {
//
//        AsymmetricCryptography asymmetricCrypto =  new AsymmtricCryptographyImpl();
//
//        //test ED25519
//        CryptoAlgorithm algorithm = CryptoAlgorithm.ED25519;
//
//        CryptoKeyPair keyPair = asymmetricCrypto.generateKeyPair(algorithm);
//
//        byte[] pubKeyBytes = keyPair.getPubKey().toBytes();
//        verifyResolvePubKey(asymmetricCrypto,algorithm,32,pubKeyBytes,null);
//
//        byte[] truncatedPubKeyBytes = new byte[pubKeyBytes.length-2];
//        System.arraycopy(pubKeyBytes,0,truncatedPubKeyBytes,0,truncatedPubKeyBytes.length);
//        verifyResolvePubKey(asymmetricCrypto,algorithm,32,truncatedPubKeyBytes,IllegalArgumentException.class);
//
//        byte[] pubKeyBytesWithWrongAlgCode = pubKeyBytes;
//        pubKeyBytesWithWrongAlgCode[0] = CryptoAlgorithm.SHA256.CODE;
//        verifyResolvePubKey(asymmetricCrypto,algorithm,32,pubKeyBytesWithWrongAlgCode,IllegalArgumentException.class);
//
//        byte[] pubKeyBytesWithWrongKeyType= pubKeyBytes;
//        pubKeyBytesWithWrongKeyType[1] = PRIV_KEY.CODE;
//        verifyResolvePubKey(asymmetricCrypto,algorithm,32,pubKeyBytesWithWrongKeyType,IllegalArgumentException.class);
//
//        pubKeyBytes = null;
//        verifyResolvePubKey(asymmetricCrypto,algorithm,32,pubKeyBytes,NullPointerException.class);
//
//
//        //test SM2
//        algorithm = CryptoAlgorithm.SM2;
//
//        keyPair = asymmetricCrypto.generateKeyPair(algorithm);
//
//        pubKeyBytes = keyPair.getPubKey().toBytes();
//        verifyResolvePubKey(asymmetricCrypto,algorithm,65,pubKeyBytes,null);
//
//        truncatedPubKeyBytes = new byte[pubKeyBytes.length-2];
//        System.arraycopy(pubKeyBytes,0,truncatedPubKeyBytes,0,truncatedPubKeyBytes.length);
//        verifyResolvePubKey(asymmetricCrypto,algorithm,65,truncatedPubKeyBytes,IllegalArgumentException.class);
//
//        pubKeyBytesWithWrongAlgCode = pubKeyBytes;
//        pubKeyBytesWithWrongAlgCode[0] = CryptoAlgorithm.SHA256.CODE;
//        verifyResolvePubKey(asymmetricCrypto,algorithm,65,pubKeyBytesWithWrongAlgCode,IllegalArgumentException.class);
//
//        pubKeyBytesWithWrongKeyType= pubKeyBytes;
//        pubKeyBytesWithWrongKeyType[1] = PRIV_KEY.CODE;
//        verifyResolvePubKey(asymmetricCrypto,algorithm,65,pubKeyBytesWithWrongKeyType,IllegalArgumentException.class);
//
//        pubKeyBytes = null;
//        verifyResolvePubKey(asymmetricCrypto,algorithm,65,pubKeyBytes,NullPointerException.class);
//
//        //test JNIED25519
//        algorithm = CryptoAlgorithm.JNIED25519;
//
//        keyPair = asymmetricCrypto.generateKeyPair(algorithm);
//
//        pubKeyBytes = keyPair.getPubKey().toBytes();
//        verifyResolvePubKey(asymmetricCrypto,algorithm,32,pubKeyBytes,null);
//
//        truncatedPubKeyBytes = new byte[pubKeyBytes.length-2];
//        System.arraycopy(pubKeyBytes,0,truncatedPubKeyBytes,0,truncatedPubKeyBytes.length);
//        verifyResolvePubKey(asymmetricCrypto,algorithm,32,truncatedPubKeyBytes,IllegalArgumentException.class);
//
//        pubKeyBytesWithWrongAlgCode = pubKeyBytes;
//        pubKeyBytesWithWrongAlgCode[0] = CryptoAlgorithm.SHA256.CODE;
//        verifyResolvePubKey(asymmetricCrypto,algorithm,32,pubKeyBytesWithWrongAlgCode,IllegalArgumentException.class);
//
//        pubKeyBytesWithWrongKeyType= pubKeyBytes;
//        pubKeyBytesWithWrongKeyType[1] = PRIV_KEY.CODE;
//        verifyResolvePubKey(asymmetricCrypto,algorithm,32,pubKeyBytesWithWrongKeyType,IllegalArgumentException.class);
//
//        pubKeyBytes = null;
//        verifyResolvePubKey(asymmetricCrypto,algorithm,32,pubKeyBytes,NullPointerException.class);
//    }
//
//    private void verifyResolvePubKey(AsymmetricCryptography asymmetricCrypto, CryptoAlgorithm algorithm,
//                                     int expectedPubKeyLength, byte[] pubKeyBytes,Class<?> expectedException){
//
//        Exception actualEx = null;
//
//        try {
//            PubKey pubKey = asymmetricCrypto.resolvePubKey(pubKeyBytes);
//
//            assertNotNull(pubKey);
//
//            assertEquals(algorithm, pubKey.getAlgorithm());
//
//            assertEquals(expectedPubKeyLength, pubKey.getRawKeyBytes().length);
//
//            assertArrayEquals(pubKeyBytes, pubKey.toBytes());
//
//        }
//        catch (Exception e){
//            actualEx = e;
//        }
//
//        if (expectedException == null) {
//            assertNull(actualEx);
//        }
//        else {
//            assertNotNull(actualEx);
//            assertTrue(expectedException.isAssignableFrom(actualEx.getClass()));
//        }
//    }
//
//    @Test
//    public void testTryResolvePubKey() {
//    }
//
//    @Test
//    public void testResolvePrivKey() {
//
//        AsymmetricCryptography asymmetricCrypto =  new AsymmtricCryptographyImpl();
//
//        //test ED25519
//        CryptoAlgorithm algorithm = CryptoAlgorithm.ED25519;
//
//        CryptoKeyPair keyPair = asymmetricCrypto.generateKeyPair(algorithm);
//
//        byte[] privKeyBytes = keyPair.getPrivKey().toBytes();
//        verifyResolvePrivKey(asymmetricCrypto,algorithm,32,privKeyBytes,null);
//
//        byte[] truncatedPrivKeyBytes = new byte[privKeyBytes.length-2];
//        System.arraycopy(privKeyBytes,0,truncatedPrivKeyBytes,0,truncatedPrivKeyBytes.length);
//        verifyResolvePrivKey(asymmetricCrypto,algorithm,32,truncatedPrivKeyBytes,IllegalArgumentException.class);
//
//        byte[] privKeyBytesWithWrongAlgCode = privKeyBytes;
//        privKeyBytesWithWrongAlgCode[0] = CryptoAlgorithm.SHA256.CODE;
//        verifyResolvePrivKey(asymmetricCrypto,algorithm,32,privKeyBytesWithWrongAlgCode,IllegalArgumentException.class);
//
//        byte[] privKeyBytesWithWrongKeyType = privKeyBytes;
//        privKeyBytesWithWrongKeyType[1] = PUB_KEY.CODE;
//        verifyResolvePrivKey(asymmetricCrypto,algorithm,32,privKeyBytesWithWrongKeyType,IllegalArgumentException.class);
//
//        privKeyBytes = null;
//        verifyResolvePrivKey(asymmetricCrypto,algorithm,32,privKeyBytes,NullPointerException.class);
//
//
//        //test SM2
//        algorithm = CryptoAlgorithm.SM2;
//
//        keyPair = asymmetricCrypto.generateKeyPair(algorithm);
//
//        privKeyBytes = keyPair.getPrivKey().toBytes();
//        verifyResolvePrivKey(asymmetricCrypto,algorithm,32,privKeyBytes,null);
//
//        truncatedPrivKeyBytes = new byte[privKeyBytes.length-2];
//        System.arraycopy(privKeyBytes,0,truncatedPrivKeyBytes,0,truncatedPrivKeyBytes.length);
//        verifyResolvePrivKey(asymmetricCrypto,algorithm,32,truncatedPrivKeyBytes,IllegalArgumentException.class);
//
//        privKeyBytesWithWrongAlgCode = privKeyBytes;
//        privKeyBytesWithWrongAlgCode[0] = CryptoAlgorithm.SHA256.CODE;
//        verifyResolvePrivKey(asymmetricCrypto,algorithm,32,privKeyBytesWithWrongAlgCode,IllegalArgumentException.class);
//
//        privKeyBytesWithWrongKeyType = privKeyBytes;
//        privKeyBytesWithWrongKeyType[1] = PUB_KEY.CODE;
//        verifyResolvePrivKey(asymmetricCrypto,algorithm,32,privKeyBytesWithWrongKeyType,IllegalArgumentException.class);
//
//        privKeyBytes = null;
//        verifyResolvePrivKey(asymmetricCrypto,algorithm,32,privKeyBytes,NullPointerException.class);
//
//        //test JNIED25519
//        algorithm = CryptoAlgorithm.JNIED25519;
//
//        keyPair = asymmetricCrypto.generateKeyPair(algorithm);
//
//        privKeyBytes = keyPair.getPrivKey().toBytes();
//        verifyResolvePrivKey(asymmetricCrypto,algorithm,32,privKeyBytes,null);
//
//        truncatedPrivKeyBytes = new byte[privKeyBytes.length-2];
//        System.arraycopy(privKeyBytes,0,truncatedPrivKeyBytes,0,truncatedPrivKeyBytes.length);
//        verifyResolvePrivKey(asymmetricCrypto,algorithm,32,truncatedPrivKeyBytes,IllegalArgumentException.class);
//
//        privKeyBytesWithWrongAlgCode = privKeyBytes;
//        privKeyBytesWithWrongAlgCode[0] = CryptoAlgorithm.SHA256.CODE;
//        verifyResolvePrivKey(asymmetricCrypto,algorithm,32,privKeyBytesWithWrongAlgCode,IllegalArgumentException.class);
//
//        privKeyBytesWithWrongKeyType = privKeyBytes;
//        privKeyBytesWithWrongKeyType[1] = PUB_KEY.CODE;
//        verifyResolvePrivKey(asymmetricCrypto,algorithm,32,privKeyBytesWithWrongKeyType,IllegalArgumentException.class);
//
//        privKeyBytes = null;
//        verifyResolvePrivKey(asymmetricCrypto,algorithm,32,privKeyBytes,NullPointerException.class);
//    }
//
//    private void verifyResolvePrivKey(AsymmetricCryptography asymmetricCrypto, CryptoAlgorithm algorithm,
//                                     int expectedPrivKeyLength, byte[] privKeyBytes,Class<?> expectedException){
//
//        Exception actualEx = null;
//
//        try {
//            PrivKey privKey = asymmetricCrypto.resolvePrivKey(privKeyBytes);
//
//            assertNotNull(privKey);
//
//            assertEquals(algorithm, privKey.getAlgorithm());
//
//            assertEquals(expectedPrivKeyLength, privKey.getRawKeyBytes().length);
//
//            assertArrayEquals(privKeyBytes, privKey.toBytes());
//
//        }
//        catch (Exception e){
//            actualEx = e;
//        }
//
//        if (expectedException == null) {
//            assertNull(actualEx);
//        }
//        else {
//            assertNotNull(actualEx);
//            assertTrue(expectedException.isAssignableFrom(actualEx.getClass()));
//        }
//    }
//
//    @Test
//    public void testTryResolvePrivKey() {
//    }
//}