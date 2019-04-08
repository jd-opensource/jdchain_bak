package test.com.jd.blockchain.crypto.service.classic;

import com.jd.blockchain.crypto.*;
import com.jd.blockchain.crypto.symmetric.SymmetricEncryptionFunction;
import com.jd.blockchain.utils.io.BytesUtils;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

import static com.jd.blockchain.crypto.CryptoKeyType.PUBLIC;
import static com.jd.blockchain.crypto.CryptoKeyType.SYMMETRIC;
import static org.junit.Assert.*;

/**
 * @author zhanglin33
 * @title: AESEncryptionFunctionTest
 * @description: JunitTest for AESAESEncryptionFunction in SPI mode
 * @date 2019-04-01, 13:57
 */
public class AESEncryptionFunctionTest {

    @Test
    public void getAlgorithmTest(){
        CryptoAlgorithm algorithm = CryptoServiceProviders.getAlgorithm("aes");
        assertNotNull(algorithm);

        SymmetricEncryptionFunction symmetricEncryptionFunction =
                CryptoServiceProviders.getSymmetricEncryptionFunction(algorithm);

        assertEquals(symmetricEncryptionFunction.getAlgorithm().name(), algorithm.name());
        assertEquals(symmetricEncryptionFunction.getAlgorithm().code(), algorithm.code());

        algorithm = CryptoServiceProviders.getAlgorithm("AES");
        assertNotNull(algorithm);

        assertEquals(symmetricEncryptionFunction.getAlgorithm().name(), algorithm.name());
        assertEquals(symmetricEncryptionFunction.getAlgorithm().code(), algorithm.code());

        algorithm = CryptoServiceProviders.getAlgorithm("aess");
        assertNull(algorithm);
    }

    @Test
    public void generateSymmetricKeyTest(){
        CryptoAlgorithm algorithm = CryptoServiceProviders.getAlgorithm("aes");
        assertNotNull(algorithm);

        SymmetricEncryptionFunction symmetricEncryptionFunction =
                CryptoServiceProviders.getSymmetricEncryptionFunction(algorithm);

        SymmetricKey symmetricKey = (SymmetricKey) symmetricEncryptionFunction.generateSymmetricKey();

        assertEquals(SYMMETRIC.CODE,symmetricKey.getKeyType().CODE);
        assertEquals(128 / 8, symmetricKey.getRawKeyBytes().length);

        assertEquals(algorithm.name(),symmetricKey.getAlgorithm().name());
        assertEquals(algorithm.toString(),symmetricKey.getAlgorithm().toString());

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

        CryptoAlgorithm algorithm = CryptoServiceProviders.getAlgorithm("aes");
        assertNotNull(algorithm);

        SymmetricEncryptionFunction symmetricEncryptionFunction =
                CryptoServiceProviders.getSymmetricEncryptionFunction(algorithm);

        SymmetricKey symmetricKey = (SymmetricKey) symmetricEncryptionFunction.generateSymmetricKey();

        Ciphertext ciphertext = symmetricEncryptionFunction.encrypt(symmetricKey,data);

        byte[] ciphertextBytes = ciphertext.toBytes();
        assertEquals(2 + 16 + 1024 , ciphertextBytes.length);
        CryptoAlgorithm ciphertextAlgo = ciphertext.getAlgorithm();
        assertEquals(algorithm.name(),ciphertext.getAlgorithm().name());
        assertEquals(algorithm.toString(),ciphertext.getAlgorithm().toString());

        byte[] algoBytes = CryptoAlgorithm.toBytes(ciphertextAlgo);
        byte[] rawCiphertextBytes = ciphertext.getRawCiphertext();
        assertArrayEquals(BytesUtils.concat(algoBytes,rawCiphertextBytes),ciphertextBytes);
    }


    @Test
    public void decryptTest(){

        byte[] data = new byte[1024];
        Random random = new Random();
        random.nextBytes(data);

        CryptoAlgorithm algorithm = CryptoServiceProviders.getAlgorithm("aes");
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
        CryptoAlgorithm algorithm = CryptoServiceProviders.getAlgorithm("aes");
        assertNotNull(algorithm);

        SymmetricEncryptionFunction symmetricEncryptionFunction =
                CryptoServiceProviders.getSymmetricEncryptionFunction(algorithm);

        SymmetricKey symmetricKey = (SymmetricKey) symmetricEncryptionFunction.generateSymmetricKey();
        byte[] symmetricKeyBytes = symmetricKey.toBytes();

        assertTrue(symmetricEncryptionFunction.supportSymmetricKey(symmetricKeyBytes));

        algorithm = CryptoServiceProviders.getAlgorithm("ripemd160");
        assertNotNull(algorithm);
        byte[] algoBytes = CryptoAlgorithm.toBytes(algorithm);
        byte[] pubKeyTypeBytes = new byte[] {PUBLIC.CODE};
        byte[] rawKeyBytes = symmetricKey.getRawKeyBytes();
        byte[] ripemd160KeyBytes = BytesUtils.concat(algoBytes,pubKeyTypeBytes,rawKeyBytes);

        assertFalse(symmetricEncryptionFunction.supportSymmetricKey(ripemd160KeyBytes));
    }

    @Test
    public void resolveSymmetricKeyTest(){
        CryptoAlgorithm algorithm = CryptoServiceProviders.getAlgorithm("aes");
        assertNotNull(algorithm);

        SymmetricEncryptionFunction symmetricEncryptionFunction =
                CryptoServiceProviders.getSymmetricEncryptionFunction(algorithm);

        SymmetricKey symmetricKey = (SymmetricKey) symmetricEncryptionFunction.generateSymmetricKey();
        byte[] symmetricKeyBytes = symmetricKey.toBytes();

        SymmetricKey resolvedKey = symmetricEncryptionFunction.resolveSymmetricKey(symmetricKeyBytes);

        assertEquals(SYMMETRIC.CODE,resolvedKey.getKeyType().CODE);
        assertEquals(128 / 8, resolvedKey.getRawKeyBytes().length);

        assertEquals(algorithm.name(),resolvedKey.getAlgorithm().name());
        assertEquals(algorithm.toString(),resolvedKey.getAlgorithm().toString());


        algorithm = CryptoServiceProviders.getAlgorithm("ripemd160");
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

        CryptoAlgorithm algorithm = CryptoServiceProviders.getAlgorithm("aes");
        assertNotNull(algorithm);

        SymmetricEncryptionFunction symmetricEncryptionFunction =
                CryptoServiceProviders.getSymmetricEncryptionFunction(algorithm);

        SymmetricKey symmetricKey = (SymmetricKey) symmetricEncryptionFunction.generateSymmetricKey();

        Ciphertext ciphertext = symmetricEncryptionFunction.encrypt(symmetricKey,data);

        byte[] ciphertextBytes = ciphertext.toBytes();
        assertTrue(symmetricEncryptionFunction.supportCiphertext(ciphertextBytes));

        algorithm = CryptoServiceProviders.getAlgorithm("ripemd160");
        assertNotNull(algorithm);
        byte[] algoBytes = CryptoAlgorithm.toBytes(algorithm);
        byte[] rawCiphertextBytes = ciphertext.toBytes();
        byte[] ripemd160CiphertextBytes = BytesUtils.concat(algoBytes,rawCiphertextBytes);

        assertFalse(symmetricEncryptionFunction.supportCiphertext(ripemd160CiphertextBytes));
    }

    @Test
    public void resolveCiphertextTest(){

        byte[] data = new byte[1024];
        Random random = new Random();
        random.nextBytes(data);

        CryptoAlgorithm algorithm = CryptoServiceProviders.getAlgorithm("aes");
        assertNotNull(algorithm);

        SymmetricEncryptionFunction symmetricEncryptionFunction =
                CryptoServiceProviders.getSymmetricEncryptionFunction(algorithm);

        SymmetricKey symmetricKey = (SymmetricKey) symmetricEncryptionFunction.generateSymmetricKey();

        Ciphertext ciphertext = symmetricEncryptionFunction.encrypt(symmetricKey,data);

        byte[] ciphertextBytes = ciphertext.toBytes();
        assertTrue(symmetricEncryptionFunction.supportCiphertext(ciphertextBytes));

        algorithm = CryptoServiceProviders.getAlgorithm("ripemd160");
        assertNotNull(algorithm);
        byte[] algoBytes = CryptoAlgorithm.toBytes(algorithm);
        byte[] rawCiphertextBytes =  ciphertext.toBytes();
        byte[] ripemd160CiphertextBytes = BytesUtils.concat(algoBytes,rawCiphertextBytes);

        Class<?> expectedException = CryptoException.class;
        Exception actualEx = null;
        try {
            symmetricEncryptionFunction.resolveSymmetricKey(ripemd160CiphertextBytes);
        } catch (Exception e) {
            actualEx = e;
        }
        assertNotNull(actualEx);
        assertTrue(expectedException.isAssignableFrom(actualEx.getClass()));
    }
}
