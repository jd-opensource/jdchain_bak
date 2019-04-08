package test.com.jd.blockchain.crypto.service.sm;

import com.jd.blockchain.crypto.*;
import com.jd.blockchain.crypto.symmetric.SymmetricEncryptionFunction;
import com.jd.blockchain.utils.io.BytesUtils;
import org.junit.Test;

import java.util.Random;

import static com.jd.blockchain.crypto.CryptoAlgorithm.ENCRYPTION_ALGORITHM;
import static com.jd.blockchain.crypto.CryptoAlgorithm.SYMMETRIC_KEY;
import static com.jd.blockchain.crypto.CryptoKeyType.PUBLIC;
import static com.jd.blockchain.crypto.CryptoKeyType.SYMMETRIC;
import static org.junit.Assert.*;

/**
 * @author zhanglin33
 * @title: SM4EncryptionFunctionTest
 * @description: JunitTest for SM4EncryptionFunction in SPI mode
 * @date 2019-04-03, 16:35
 */
public class SM4EncryptionFunctionTest {
    @Test
    public void getAlgorithmTest(){
        CryptoAlgorithm algorithm = CryptoServiceProviders.getAlgorithm("sm4");
        assertNotNull(algorithm);

        SymmetricEncryptionFunction symmetricEncryptionFunction =
                CryptoServiceProviders.getSymmetricEncryptionFunction(algorithm);

        assertEquals(symmetricEncryptionFunction.getAlgorithm().name(), algorithm.name());
        assertEquals(symmetricEncryptionFunction.getAlgorithm().code(), algorithm.code());

        algorithm = CryptoServiceProviders.getAlgorithm("sM4");
        assertNotNull(algorithm);

        assertEquals(symmetricEncryptionFunction.getAlgorithm().name(), algorithm.name());
        assertEquals(symmetricEncryptionFunction.getAlgorithm().code(), algorithm.code());

        algorithm = CryptoServiceProviders.getAlgorithm("smm4");
        assertNull(algorithm);
    }

    @Test
    public void generateSymmetricKeyTest(){
        CryptoAlgorithm algorithm = CryptoServiceProviders.getAlgorithm("sm4");
        assertNotNull(algorithm);

        SymmetricEncryptionFunction symmetricEncryptionFunction =
                CryptoServiceProviders.getSymmetricEncryptionFunction(algorithm);

        SymmetricKey symmetricKey = (SymmetricKey) symmetricEncryptionFunction.generateSymmetricKey();

        assertEquals(SYMMETRIC.CODE,symmetricKey.getKeyType().CODE);
        assertEquals(128 / 8, symmetricKey.getRawKeyBytes().length);

        assertEquals(algorithm.name(),symmetricKey.getAlgorithm().name());
        assertEquals(algorithm.code(),symmetricKey.getAlgorithm().code());

        assertEquals(2 + 1 + 128 / 8, symmetricKey.toBytes().length);
        byte[] algoBytes = CryptoAlgorithm.toBytes(algorithm);
        byte[] keyTypeBytes = new byte[] {SYMMETRIC.CODE};
        byte[] rawKeyBytes = symmetricKey.getRawKeyBytes();
        assertArrayEquals(BytesUtils.concat(algoBytes,keyTypeBytes,rawKeyBytes),symmetricKey.toBytes());
    }



    @Test
    public void encryptTest(){

        byte[] data = new byte[1024];
        Random random = new Random();
        random.nextBytes(data);

        CryptoAlgorithm algorithm = CryptoServiceProviders.getAlgorithm("sm4");
        assertNotNull(algorithm);

        SymmetricEncryptionFunction symmetricEncryptionFunction =
                CryptoServiceProviders.getSymmetricEncryptionFunction(algorithm);

        SymmetricKey symmetricKey = (SymmetricKey) symmetricEncryptionFunction.generateSymmetricKey();

        Ciphertext ciphertext = symmetricEncryptionFunction.encrypt(symmetricKey,data);

        byte[] ciphertextBytes = ciphertext.toBytes();
        assertEquals(2 + 16 +16 + 1024 , ciphertextBytes.length);
        CryptoAlgorithm ciphertextAlgo = ciphertext.getAlgorithm();
        assertEquals("SM4",ciphertextAlgo.name());
        assertEquals((short) (ENCRYPTION_ALGORITHM | SYMMETRIC_KEY | ((byte) 4 & 0x00FF)),ciphertextAlgo.code());

        byte[] algoBytes = CryptoAlgorithm.toBytes(ciphertextAlgo);
        byte[] rawCiphertextBytes = ciphertext.getRawCiphertext();
        assertArrayEquals(BytesUtils.concat(algoBytes,rawCiphertextBytes),ciphertextBytes);
    }


    @Test
    public void decryptTest(){

        byte[] data = new byte[1024];
        Random random = new Random();
        random.nextBytes(data);

        CryptoAlgorithm algorithm = CryptoServiceProviders.getAlgorithm("sm4");
        assertNotNull(algorithm);

        SymmetricEncryptionFunction symmetricEncryptionFunction =
                CryptoServiceProviders.getSymmetricEncryptionFunction(algorithm);

        SymmetricKey symmetricKey = (SymmetricKey) symmetricEncryptionFunction.generateSymmetricKey();

        Ciphertext ciphertext = symmetricEncryptionFunction.encrypt(symmetricKey,data);

        byte[] decryptedPlaintext = symmetricEncryptionFunction.decrypt(symmetricKey,ciphertext);

        assertArrayEquals(data,decryptedPlaintext);
    }

//    @Test
//    public void streamEncryptTest(){
//
//        byte[] data = new byte[1024];
//        Random random = new Random();
//        random.nextBytes(data);
//
//
//        InputStream inputStream = new ByteArrayInputStream(data);
//        OutputStream outputStream = new ByteArrayOutputStream();
//
//        CryptoAlgorithm algorithm = CryptoServiceProviders.getAlgorithm("aes");
//        assertNotNull(algorithm);
//
//        SymmetricEncryptionFunction symmetricEncryptionFunction =
//                CryptoServiceProviders.getSymmetricEncryptionFunction(algorithm);
//
//        SymmetricKey symmetricKey = (SymmetricKey) symmetricEncryptionFunction.generateSymmetricKey();
//
//        symmetricEncryptionFunction.encrypt(symmetricKey,inputStream,outputStream);
//
//        assertNotNull(outputStream);
//
//
//    }



    @Test
    public void supportSymmetricKeyTest(){
        CryptoAlgorithm algorithm = CryptoServiceProviders.getAlgorithm("sm4");
        assertNotNull(algorithm);

        SymmetricEncryptionFunction symmetricEncryptionFunction =
                CryptoServiceProviders.getSymmetricEncryptionFunction(algorithm);

        SymmetricKey symmetricKey = (SymmetricKey) symmetricEncryptionFunction.generateSymmetricKey();
        byte[] symmetricKeyBytes = symmetricKey.toBytes();

        assertTrue(symmetricEncryptionFunction.supportSymmetricKey(symmetricKeyBytes));

        algorithm = CryptoServiceProviders.getAlgorithm("sm3");
        assertNotNull(algorithm);
        byte[] algoBytes = CryptoAlgorithm.toBytes(algorithm);
        byte[] pubKeyTypeBytes = new byte[] {PUBLIC.CODE};
        byte[] rawKeyBytes = symmetricKey.getRawKeyBytes();
        byte[] ripemd160KeyBytes = BytesUtils.concat(algoBytes,pubKeyTypeBytes,rawKeyBytes);

        assertFalse(symmetricEncryptionFunction.supportSymmetricKey(ripemd160KeyBytes));
    }

    @Test
    public void resolveSymmetricKeyTest(){
        CryptoAlgorithm algorithm = CryptoServiceProviders.getAlgorithm("sm4");
        assertNotNull(algorithm);

        SymmetricEncryptionFunction symmetricEncryptionFunction =
                CryptoServiceProviders.getSymmetricEncryptionFunction(algorithm);

        SymmetricKey symmetricKey = (SymmetricKey) symmetricEncryptionFunction.generateSymmetricKey();
        byte[] symmetricKeyBytes = symmetricKey.toBytes();

        SymmetricKey resolvedKey = symmetricEncryptionFunction.resolveSymmetricKey(symmetricKeyBytes);

        assertEquals(SYMMETRIC.CODE,resolvedKey.getKeyType().CODE);
        assertEquals(128 / 8, resolvedKey.getRawKeyBytes().length);
        assertEquals("SM4",resolvedKey.getAlgorithm().name());
        assertEquals((short) (ENCRYPTION_ALGORITHM | SYMMETRIC_KEY | ((byte) 4 & 0x00FF)),resolvedKey.getAlgorithm().code());
        assertArrayEquals(symmetricKeyBytes,resolvedKey.toBytes());


        algorithm = CryptoServiceProviders.getAlgorithm("sm3");
        assertNotNull(algorithm);
        byte[] algoBytes = CryptoAlgorithm.toBytes(algorithm);
        byte[] pubKeyTypeBytes = new byte[] {PUBLIC.CODE};
        byte[] rawKeyBytes = symmetricKey.getRawKeyBytes();
        byte[] ripemd160KeyBytes = BytesUtils.concat(algoBytes,pubKeyTypeBytes,rawKeyBytes);


        Class<?> expectedException = CryptoException.class;
        Exception actualEx = null;
        try {
            symmetricEncryptionFunction.resolveSymmetricKey(ripemd160KeyBytes);
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

        CryptoAlgorithm algorithm = CryptoServiceProviders.getAlgorithm("sm4");
        assertNotNull(algorithm);

        SymmetricEncryptionFunction symmetricEncryptionFunction =
                CryptoServiceProviders.getSymmetricEncryptionFunction(algorithm);

        SymmetricKey symmetricKey = (SymmetricKey) symmetricEncryptionFunction.generateSymmetricKey();

        Ciphertext ciphertext = symmetricEncryptionFunction.encrypt(symmetricKey,data);

        byte[] ciphertextBytes = ciphertext.toBytes();
        assertTrue(symmetricEncryptionFunction.supportCiphertext(ciphertextBytes));

        algorithm = CryptoServiceProviders.getAlgorithm("sm3");
        assertNotNull(algorithm);
        byte[] algoBytes = CryptoAlgorithm.toBytes(algorithm);
        byte[] rawCiphertextBytes = ciphertext.toBytes();
        byte[] sm3CiphertextBytes = BytesUtils.concat(algoBytes,rawCiphertextBytes);

        assertFalse(symmetricEncryptionFunction.supportCiphertext(sm3CiphertextBytes));
    }

    @Test
    public void resolveCiphertextTest(){

        byte[] data = new byte[1024];
        Random random = new Random();
        random.nextBytes(data);

        CryptoAlgorithm algorithm = CryptoServiceProviders.getAlgorithm("sm4");
        assertNotNull(algorithm);

        SymmetricEncryptionFunction symmetricEncryptionFunction =
                CryptoServiceProviders.getSymmetricEncryptionFunction(algorithm);

        SymmetricKey symmetricKey = (SymmetricKey) symmetricEncryptionFunction.generateSymmetricKey();

        Ciphertext ciphertext = symmetricEncryptionFunction.encrypt(symmetricKey,data);

        byte[] ciphertextBytes = ciphertext.toBytes();

        Ciphertext resolvedCiphertext = symmetricEncryptionFunction.resolveCiphertext(ciphertextBytes);

        assertEquals(1024 + 16 + 16, resolvedCiphertext.getRawCiphertext().length);
        assertEquals("SM4",resolvedCiphertext.getAlgorithm().name());
        assertEquals((short) (ENCRYPTION_ALGORITHM | SYMMETRIC_KEY | ((byte) 4 & 0x00FF)),resolvedCiphertext.getAlgorithm().code());
        assertArrayEquals(ciphertextBytes,resolvedCiphertext.toBytes());

        algorithm = CryptoServiceProviders.getAlgorithm("sm3");
        assertNotNull(algorithm);
        byte[] algoBytes = CryptoAlgorithm.toBytes(algorithm);
        byte[] rawCiphertextBytes =  ciphertext.getRawCiphertext();
        byte[] sm3CiphertextBytes = BytesUtils.concat(algoBytes,rawCiphertextBytes);

        Class<?> expectedException = CryptoException.class;
        Exception actualEx = null;
        try {
            symmetricEncryptionFunction.resolveCiphertext(sm3CiphertextBytes);
        } catch (Exception e) {
            actualEx = e;
        }
        assertNotNull(actualEx);
        assertTrue(expectedException.isAssignableFrom(actualEx.getClass()));
    }
}
