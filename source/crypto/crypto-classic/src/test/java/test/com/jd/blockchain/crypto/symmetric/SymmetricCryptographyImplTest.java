//package test.com.jd.blockchain.crypto.symmetric;
//
//import com.jd.blockchain.crypto.Ciphertext;
//import com.jd.blockchain.crypto.CryptoAlgorithm;
//import com.jd.blockchain.crypto.SymmetricKey;
//import com.jd.blockchain.crypto.impl.SymmetricCryptographyImpl;
//import com.jd.blockchain.crypto.symmetric.SymmetricCryptography;
//import com.jd.blockchain.crypto.symmetric.SymmetricEncryptionFunction;
//import com.jd.blockchain.utils.io.BytesUtils;
//
//import org.junit.Test;
//
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.util.Random;
//
//import static com.jd.blockchain.crypto.CryptoKeyType.PRIVATE;
//import static com.jd.blockchain.crypto.CryptoKeyType.SYMMETRIC;
//import static org.junit.Assert.*;
//
//public class SymmetricCryptographyImplTest {
//
//    @Test
//    public void testGenerateKey() {
//
//        SymmetricCryptography symmetricCrypto = new SymmetricCryptographyImpl();
//
//        //test AES
//        CryptoAlgorithm algorithm = CryptoAlgorithm.AES;
//        verifyGenerateKey(symmetricCrypto,algorithm);
//
//        //test SM4
//        algorithm = CryptoAlgorithm.SM4;
//        verifyGenerateKey(symmetricCrypto,algorithm);
//    }
//
//    private void verifyGenerateKey(SymmetricCryptography symmetricCrypto, CryptoAlgorithm algorithm){
//
//        SymmetricKey symmetricKey= symmetricCrypto.generateKey(algorithm);
//
//        assertNotNull(symmetricKey);
//        assertEquals(algorithm, symmetricKey.getAlgorithm());
//        assertEquals(128/8,symmetricKey.getRawKeyBytes().length);
//
//        byte[] symmetricKeyBytes = symmetricKey.toBytes();
//        //判断密钥数据长度=算法标识长度+密钥掩码长度+原始密钥长度
//        assertEquals(1 + 1 + 128 / 8, symmetricKeyBytes.length);
//
//        assertEquals(algorithm.CODE,symmetricKeyBytes[0]);
//        assertEquals(algorithm,CryptoAlgorithm.valueOf(symmetricKeyBytes[0]));
//    }
//
//    @Test
//    public void testGetSymmetricEncryptionFunction() {
//
//        SymmetricCryptography symmetricCrypto = new SymmetricCryptographyImpl();
//        Random random = new Random();
//
//
//        //test AES
//        CryptoAlgorithm algorithm = CryptoAlgorithm.AES;
//
//        //Case 1: AES with 16 bytes data
//        //刚好一个分组长度，随机生成明文数据
//        byte[] data = new byte[16];
//        random.nextBytes(data);
//        verifyGetSymmetricEncryptionFunction(symmetricCrypto, algorithm, data, 2*16, null);
//
//        //Case 2: AES with 33 bytes data
//        //明文长度大于两倍分组长度，生成的密文是三倍分组长度
//        data = new byte[33];
//        random.nextBytes(data);
//        verifyGetSymmetricEncryptionFunction(symmetricCrypto, algorithm, data, 3*16,null);
//
//        //Case 3: AES with 3 bytes data
//        //明文长度小于分组长度，生成的密文是一倍分组长度
//        data = new byte[3];
//        random.nextBytes(data);
//        verifyGetSymmetricEncryptionFunction(symmetricCrypto, algorithm, data, 16,null);
//
//        //Case 4: AES with 0 bytes data
//        //明文长度小于分组长度，生成的密文是一倍分组长度
//        data = new byte[0];
//        random.nextBytes(data);
//        verifyGetSymmetricEncryptionFunction(symmetricCrypto, algorithm, data, 16,null);
//
//        //Case 5 AES with null
//        //明文为空，可以捕获到异常异常
//        data = null;
//        verifyGetSymmetricEncryptionFunction(symmetricCrypto, algorithm, data, 16,IllegalArgumentException.class);
//
//
//        //test ED25519
//        algorithm = CryptoAlgorithm.ED25519;
//        data = new byte[16];
//        random.nextBytes(data);
//        verifyGetSymmetricEncryptionFunction(symmetricCrypto, algorithm, data, 16,IllegalArgumentException.class);
//
//
//        //test SM4
//        algorithm = CryptoAlgorithm.SM4;
//
//        //Case 1: SM4 with 16 bytes data
//        data = new byte[16];
//        random.nextBytes(data);
//        //密文长度 = IV长度 + 真实密文长度
//        verifyGetSymmetricEncryptionFunction(symmetricCrypto, algorithm, data, 3*16, null);
//
//        //Case 2: SM4 with 33 bytes data
//        data = new byte[33];
//        random.nextBytes(data);
//        verifyGetSymmetricEncryptionFunction(symmetricCrypto, algorithm, data, 4*16,null);
//
//        //Case 3: SM4 with 3 bytes data
//        data = new byte[3];
//        random.nextBytes(data);
//        verifyGetSymmetricEncryptionFunction(symmetricCrypto, algorithm, data, 2*16,null);
//
//        //Case 4: SM4 with 0 bytes data
//        data = new byte[0];
//        random.nextBytes(data);
//        verifyGetSymmetricEncryptionFunction(symmetricCrypto, algorithm, data, 2*16,null);
//
//        //Case 5 SM4 with null
//        data = null;
//        verifyGetSymmetricEncryptionFunction(symmetricCrypto, algorithm, data, 16,IllegalArgumentException.class);
//    }
//
//    //不同明文输入下，用来简化加解密过程的method
//    private void verifyGetSymmetricEncryptionFunction(SymmetricCryptography symmetricCrypto, CryptoAlgorithm algorithm,
//                                                      byte[] data, int expectedCiphertextLength, Class<?> expectedException){
//
//        //初始化一个异常
//        Exception actualEx = null;
//
//        try {
//            SymmetricEncryptionFunction sef = symmetricCrypto.getSymmetricEncryptionFunction(algorithm);
//            //验证获取的算法实例非空
//            assertNotNull(sef);
//
//            SymmetricKey symmetricKey = (SymmetricKey) sef.generateSymmetricKey();
//
//            //验证SymmetricKey的getAlgorithm方法
//            assertEquals(algorithm, symmetricKey.getAlgorithm());
//            //验证SymmetricKey的getRawKeyBytes方法
//            assertEquals(16, symmetricKey.getRawKeyBytes().length);
//            //验证SymmetricKey的toBytes方法
//            assertArrayEquals(BytesUtils.concat(new byte[]{algorithm.CODE},new byte[]{SYMMETRIC.CODE},symmetricKey.getRawKeyBytes()), symmetricKey.toBytes());
//
//
//            Ciphertext ciphertext = sef.encrypt(symmetricKey,data);
//
//            //Ciphertext中算法标识与入参算法一致
//            assertEquals(algorithm, ciphertext.getAlgorithm());
//            //验证原始密文长度与预期长度一致
//            assertEquals(expectedCiphertextLength, ciphertext.getRawCiphertext().length);
//            //验证密文数据长度=算法标识长度+预期长度
//            byte[] ciphertextBytes = ciphertext.toBytes();
//            assertArrayEquals(BytesUtils.concat(new byte[]{algorithm.CODE},ciphertext.getRawCiphertext()), ciphertextBytes);
//
//
//            //验证equal
//            assertTrue(ciphertext.equals(ciphertext));
//            assertEquals(ciphertext.hashCode(),ciphertext.hashCode());
//
//            //验证SymmetricEncryptionFunction的decrypt
//            assertArrayEquals(data, sef.decrypt(symmetricKey,ciphertext));
//
//            //测试SymmetricEncryptionFunction的输入输出流的加解密方法
//            InputStream inPlaintext = new ByteArrayInputStream(data);
//            //16字节的明文输入，将会产生32字节的密文
//            OutputStream outCiphertext = new ByteArrayOutputStream(ciphertext.toBytes().length);
//            InputStream inCiphertext =  new ByteArrayInputStream(ciphertext.toBytes());
//            OutputStream outPlaintext =  new ByteArrayOutputStream(data.length);
//            sef.encrypt(symmetricKey, inPlaintext, outCiphertext);
//            sef.decrypt(symmetricKey, inCiphertext, outPlaintext);
//
//            //验证SymmetricEncryptionFunction的supportCiphertext方法
//            assertTrue(sef.supportCiphertext(ciphertextBytes));
//
//            //验证SymmetricEncryptionFunction的resolveCiphertext方法
//            assertEquals(ciphertext, sef.resolveCiphertext(ciphertextBytes));
//
//            //验证SymmetricEncryptionFunction的supportSymmetricKey方法
//            assertTrue(sef.supportSymmetricKey(symmetricKey.toBytes()));
//
//            //验证SymmetricEncryptionFunction的resolveSymmetricKey方法
//            assertEquals(symmetricKey, sef.resolveSymmetricKey(symmetricKey.toBytes()));
//
//            //验证SymmetricEncryptionFunction的getAlgorithm
//            assertEquals(algorithm, sef.getAlgorithm());
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
//    public void testDecrypt() {
//
//        SymmetricCryptography symmetricCrypto = new SymmetricCryptographyImpl();
//        Random randomData = new Random();
//        Random randomKey = new Random();
//
//
//        //test AES
//        CryptoAlgorithm algorithm = CryptoAlgorithm.AES;
//        SymmetricEncryptionFunction sef = symmetricCrypto.getSymmetricEncryptionFunction(algorithm);
//
//        byte[] data = new byte[16];
//        randomData.nextBytes(data);
//        byte[] key = new byte[16];
//        randomKey.nextBytes(key);
//
//        SymmetricKey symmetricKey = new SymmetricKey(algorithm, key);
//        byte[] ciphertextBytes = sef.encrypt(symmetricKey,data).toBytes();
//
//        verifyDecrypt(symmetricCrypto, algorithm, key, data, ciphertextBytes, null);
//
//        //密钥的算法标识与密文的算法标识不一致情况
//        verifyDecrypt(symmetricCrypto, CryptoAlgorithm.SM4, key, data, ciphertextBytes, IllegalArgumentException.class);
//
//        //密文末尾两个字节丢失情况下，抛出异常
//        byte[] truncatedCiphertextBytes = new byte[ciphertextBytes.length-2];
//        System.arraycopy(ciphertextBytes,0,truncatedCiphertextBytes,0,truncatedCiphertextBytes.length);
//        verifyDecrypt(symmetricCrypto, algorithm, key, data, truncatedCiphertextBytes, IllegalArgumentException.class);
//
//        byte[] ciphertextBytesWithWrongAlgCode = ciphertextBytes;
//        ciphertextBytesWithWrongAlgCode[0] = CryptoAlgorithm.SHA256.CODE;
//        verifyDecrypt(symmetricCrypto,algorithm,key,data,ciphertextBytesWithWrongAlgCode,IllegalArgumentException.class);
//
//        ciphertextBytes = null;
//        verifyDecrypt(symmetricCrypto,algorithm,key,data,ciphertextBytes,NullPointerException.class);
//
//
//        //test SM4
//        algorithm = CryptoAlgorithm.SM4;
//        sef = symmetricCrypto.getSymmetricEncryptionFunction(algorithm);
//        symmetricKey = new SymmetricKey(algorithm, key);
//        ciphertextBytes = sef.encrypt(symmetricKey,data).toBytes();
//
//        verifyDecrypt(symmetricCrypto, algorithm, key, data, ciphertextBytes, null);
//
//        //密钥的算法标识与密文的算法标识不一致情况
//        verifyDecrypt(symmetricCrypto, CryptoAlgorithm.AES, key, data, ciphertextBytes, IllegalArgumentException.class);
//
//        //密文末尾两个字节丢失情况下，抛出异常
//        truncatedCiphertextBytes = new byte[ciphertextBytes.length-2];
//        System.arraycopy(ciphertextBytes,0,truncatedCiphertextBytes,0,truncatedCiphertextBytes.length);
//        verifyDecrypt(symmetricCrypto, algorithm, key, data, truncatedCiphertextBytes, IllegalArgumentException.class);
//
//        ciphertextBytesWithWrongAlgCode = ciphertextBytes;
//        ciphertextBytesWithWrongAlgCode[0] = CryptoAlgorithm.SHA256.CODE;
//        verifyDecrypt(symmetricCrypto,algorithm,key,data,ciphertextBytesWithWrongAlgCode,IllegalArgumentException.class);
//
//        ciphertextBytes = null;
//        verifyDecrypt(symmetricCrypto,algorithm,key,data,ciphertextBytes,NullPointerException.class);
//    }
//
//    private void verifyDecrypt(SymmetricCryptography symmetricCrypto, CryptoAlgorithm algorithm,
//                               byte[] key, byte[] data, byte[] ciphertextBytes, Class<?> expectedException) {
//
//        Exception actualEx = null;
//
//        try {
//            SymmetricKey symmetricKey = new SymmetricKey(algorithm,key);
//
//            byte[] plaintext = symmetricCrypto.decrypt(symmetricKey.toBytes(), ciphertextBytes);
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
//        SymmetricCryptography symmetricCrypto = new SymmetricCryptographyImpl();
//        Random randomData = new Random();
//        Random randomKey = new Random();
//
//        //test AES
//        CryptoAlgorithm algorithm = CryptoAlgorithm.AES;
//        SymmetricEncryptionFunction sef = symmetricCrypto.getSymmetricEncryptionFunction(algorithm);
//
//        byte[] data = new byte[16];
//        randomData.nextBytes(data);
//        byte[] key = new byte[16];
//        randomKey.nextBytes(key);
//
//        SymmetricKey symmetricKey = new SymmetricKey(algorithm, key);
//        byte[] ciphertextBytes = sef.encrypt(symmetricKey,data).toBytes();
//        verifyResolveCiphertext(symmetricCrypto, algorithm, ciphertextBytes, null);
//
//        byte[] truncatedCiphertextBytes = new byte[ciphertextBytes.length-2];
//        System.arraycopy(ciphertextBytes,0,truncatedCiphertextBytes,0,truncatedCiphertextBytes.length);
//        verifyResolveCiphertext(symmetricCrypto,algorithm,truncatedCiphertextBytes,IllegalArgumentException.class);
//
//        byte[] ciphertextBytesWithWrongAlgCode = ciphertextBytes;
//        ciphertextBytesWithWrongAlgCode[0] = CryptoAlgorithm.SHA256.CODE;
//        verifyResolveCiphertext(symmetricCrypto,algorithm,ciphertextBytesWithWrongAlgCode,IllegalArgumentException.class);
//
//        ciphertextBytes = null;
//        verifyResolveCiphertext(symmetricCrypto,algorithm,ciphertextBytes,NullPointerException.class);
//
//
//        //test SM4
//        algorithm = CryptoAlgorithm.SM4;
//        sef = symmetricCrypto.getSymmetricEncryptionFunction(algorithm);
//
//        symmetricKey = new SymmetricKey(algorithm, key);
//        ciphertextBytes = sef.encrypt(symmetricKey,data).toBytes();
//
//        verifyResolveCiphertext(symmetricCrypto, algorithm, ciphertextBytes, null);
//
//        truncatedCiphertextBytes = new byte[ciphertextBytes.length-2];
//        System.arraycopy(ciphertextBytes,0,truncatedCiphertextBytes,0,truncatedCiphertextBytes.length);
//        verifyResolveCiphertext(symmetricCrypto,algorithm,truncatedCiphertextBytes,IllegalArgumentException.class);
//
//        ciphertextBytesWithWrongAlgCode = ciphertextBytes;
//        ciphertextBytesWithWrongAlgCode[0] = CryptoAlgorithm.SHA256.CODE;
//        verifyResolveCiphertext(symmetricCrypto,algorithm,ciphertextBytesWithWrongAlgCode,IllegalArgumentException.class);
//
//        ciphertextBytes = null;
//        verifyResolveCiphertext(symmetricCrypto,algorithm,ciphertextBytes,NullPointerException.class);
//    }
//
//    private void verifyResolveCiphertext(SymmetricCryptography symmetricCrypto, CryptoAlgorithm algorithm, byte[] ciphertextBytes,
//                                         Class<?> expectedException) {
//
//        Exception actualEx = null;
//
//        try {
//            Ciphertext ciphertext = symmetricCrypto.resolveCiphertext(ciphertextBytes);
//
//            assertNotNull(ciphertext);
//
//            assertEquals(algorithm, ciphertext.getAlgorithm());
//
//            assertEquals(0, ciphertext.getRawCiphertext().length % 16);
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
//
//
//    @Test
//    public void testResolveSymmetricKey() {
//
//        SymmetricCryptography symmetricCrypto = new SymmetricCryptographyImpl();
//
//        //test AES
//        CryptoAlgorithm algorithm = CryptoAlgorithm.AES;
//
//        Random randomKey = new Random();
//        byte[] key = new byte[16];
//        randomKey.nextBytes(key);
//
//        byte[] symmetricKeyBytes = BytesUtils.concat(new byte[]{algorithm.CODE},new byte[]{SYMMETRIC.CODE},key);
//        verifyResolveSymmetricKey(symmetricCrypto,algorithm,symmetricKeyBytes,null);
//
//        byte[] truncatedSymmetricKeyBytes = new byte[symmetricKeyBytes.length-2];
//        System.arraycopy(symmetricKeyBytes,0,truncatedSymmetricKeyBytes,0,truncatedSymmetricKeyBytes.length);
//        verifyResolveSymmetricKey(symmetricCrypto,algorithm,truncatedSymmetricKeyBytes,IllegalArgumentException.class);
//
//        byte[] symmetricKeyBytesWithWrongAlgCode = symmetricKeyBytes;
//        symmetricKeyBytesWithWrongAlgCode[0] = CryptoAlgorithm.SHA256.CODE;
//        verifyResolveSymmetricKey(symmetricCrypto,algorithm,symmetricKeyBytesWithWrongAlgCode,IllegalArgumentException.class);
//
//        byte[] symmetricKeyBytesWithWrongKeyType= symmetricKeyBytes;
//        System.arraycopy(symmetricKeyBytes,0,symmetricKeyBytesWithWrongKeyType,0,symmetricKeyBytesWithWrongKeyType.length);
//        symmetricKeyBytesWithWrongKeyType[1] = PRIVATE.CODE;
//        verifyResolveSymmetricKey(symmetricCrypto,algorithm,symmetricKeyBytesWithWrongKeyType,IllegalArgumentException.class);
//
//        symmetricKeyBytes = null;
//        verifyResolveSymmetricKey(symmetricCrypto,algorithm,symmetricKeyBytes,NullPointerException.class);
//
//
//        //test SM4
//        algorithm = CryptoAlgorithm.SM4;
//        symmetricKeyBytes = BytesUtils.concat(new byte[]{algorithm.CODE},new byte[]{SYMMETRIC.CODE},key);
//
//        verifyResolveSymmetricKey(symmetricCrypto,algorithm,symmetricKeyBytes,null);
//
//        truncatedSymmetricKeyBytes = new byte[symmetricKeyBytes.length-2];
//        System.arraycopy(symmetricKeyBytes,0,truncatedSymmetricKeyBytes,0,truncatedSymmetricKeyBytes.length);
//        verifyResolveSymmetricKey(symmetricCrypto,algorithm,truncatedSymmetricKeyBytes,IllegalArgumentException.class);
//
//        symmetricKeyBytesWithWrongAlgCode = symmetricKeyBytes;
//        symmetricKeyBytesWithWrongAlgCode[0] = CryptoAlgorithm.SHA256.CODE;
//        verifyResolveSymmetricKey(symmetricCrypto,algorithm,symmetricKeyBytesWithWrongAlgCode,IllegalArgumentException.class);
//
//        symmetricKeyBytesWithWrongKeyType= symmetricKeyBytes;
//        System.arraycopy(symmetricKeyBytes,0,symmetricKeyBytesWithWrongKeyType,0,symmetricKeyBytesWithWrongKeyType.length);
//        symmetricKeyBytesWithWrongKeyType[1] = PRIVATE.CODE;
//        verifyResolveSymmetricKey(symmetricCrypto,algorithm,symmetricKeyBytesWithWrongKeyType,IllegalArgumentException.class);
//
//        symmetricKeyBytes = null;
//        verifyResolveSymmetricKey(symmetricCrypto,algorithm,symmetricKeyBytes,NullPointerException.class);
//    }
//
//    private void verifyResolveSymmetricKey(SymmetricCryptography symmetricCrypto, CryptoAlgorithm algorithm, byte[] symmetricKeyBytes,
//                                         Class<?> expectedException) {
//
//        Exception actualEx = null;
//
//        try {
//            SymmetricKey symmetricKey = symmetricCrypto.resolveSymmetricKey(symmetricKeyBytes);
//
//            assertNotNull(symmetricKey);
//
//            assertEquals(algorithm, symmetricKey.getAlgorithm());
//
//            assertEquals(16, symmetricKey.getRawKeyBytes().length);
//
//            assertArrayEquals(symmetricKeyBytes, symmetricKey.toBytes());
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
//    public void testTryResolveSymmetricKey() {
//    }
//}
