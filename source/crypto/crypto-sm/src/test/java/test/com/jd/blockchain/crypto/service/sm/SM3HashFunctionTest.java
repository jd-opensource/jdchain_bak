package test.com.jd.blockchain.crypto.service.sm;

import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.CryptoException;
import com.jd.blockchain.crypto.CryptoServiceProviders;
import com.jd.blockchain.crypto.hash.HashDigest;
import com.jd.blockchain.crypto.hash.HashFunction;
import com.jd.blockchain.utils.io.BytesUtils;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

/**
 * @author zhanglin33
 * @title: SM3HashFunctionTest
 * @description: JunitTest for SM3HashFunction in SPI mode
 * @date 2019-04-03, 16:33
 */
public class SM3HashFunctionTest {
    @Test
    public void getAlgorithmTest(){
        CryptoAlgorithm algorithm = CryptoServiceProviders.getAlgorithm("sm3");
        assertNotNull(algorithm);

        HashFunction hashFunction = CryptoServiceProviders.getHashFunction(algorithm);

        assertEquals(hashFunction.getAlgorithm().name(), algorithm.name());
        assertEquals(hashFunction.getAlgorithm().code(), algorithm.code());

        algorithm = CryptoServiceProviders.getAlgorithm("Sm3");
        assertNotNull(algorithm);

        assertEquals(hashFunction.getAlgorithm().name(), algorithm.name());
        assertEquals(hashFunction.getAlgorithm().code(), algorithm.code());

        algorithm = CryptoServiceProviders.getAlgorithm("sm3333");
        assertNull(algorithm);
    }

    @Test
    public void hashTest(){

        byte[] data = new byte[1024];
        Random random = new Random();
        random.nextBytes(data);

        CryptoAlgorithm algorithm = CryptoServiceProviders.getAlgorithm("sm3");
        assertNotNull(algorithm);

        HashFunction hashFunction = CryptoServiceProviders.getHashFunction(algorithm);

        HashDigest digest = hashFunction.hash(data);
        byte[] rawDigestBytes = digest.getRawDigest();
        byte[] algoBytes = CryptoAlgorithm.toBytes(algorithm);

        byte[] digestBytes = digest.toBytes();
        assertEquals(256 / 8 + 2,digestBytes.length);
        assertArrayEquals(digestBytes, BytesUtils.concat(algoBytes, rawDigestBytes));

        assertEquals(algorithm.code(),digest.getAlgorithm().code());
        assertEquals(algorithm.name(),digest.getAlgorithm().name());

        Class<?> expectedException = CryptoException.class;
        Exception actualEx = null;
        try {
            data = null;
            hashFunction.hash(data);
        } catch (Exception e) {
            actualEx = e;
        }
        assertNotNull(actualEx);
        assertTrue(expectedException.isAssignableFrom(actualEx.getClass()));
    }

    @Test
    public void verifyTest(){
        byte[] data = new byte[1024];
        Random random = new Random();
        random.nextBytes(data);

        CryptoAlgorithm algorithm = CryptoServiceProviders.getAlgorithm("sm3");
        assertNotNull(algorithm);

        HashFunction hashFunction = CryptoServiceProviders.getHashFunction(algorithm);

        HashDigest digest = hashFunction.hash(data);

        assertTrue(hashFunction.verify(digest,data));
    }

    @Test
    public void supportHashDigestTest(){
        byte[] data = new byte[1024];
        Random random = new Random();
        random.nextBytes(data);

        CryptoAlgorithm algorithm = CryptoServiceProviders.getAlgorithm("sm3");
        assertNotNull(algorithm);

        HashFunction hashFunction = CryptoServiceProviders.getHashFunction(algorithm);

        HashDigest digest = hashFunction.hash(data);

        byte[] digestBytes = digest.toBytes();
        assertTrue(hashFunction.supportHashDigest(digestBytes));

        algorithm = CryptoServiceProviders.getAlgorithm("sm4");
        assertNotNull(algorithm);
        byte[] algoBytes = CryptoAlgorithm.toBytes(algorithm);
        System.arraycopy(algoBytes,0,digestBytes,0,algoBytes.length);
        assertFalse(hashFunction.supportHashDigest(digestBytes));
    }

    @Test
    public void resolveHashDigestTest(){
        byte[] data = new byte[1024];
        Random random = new Random();
        random.nextBytes(data);

        CryptoAlgorithm algorithm = CryptoServiceProviders.getAlgorithm("sm3");
        assertNotNull(algorithm);

        HashFunction hashFunction = CryptoServiceProviders.getHashFunction(algorithm);

        HashDigest digest = hashFunction.hash(data);

        byte[] digestBytes = digest.toBytes();

        HashDigest resolvedDigest = hashFunction.resolveHashDigest(digestBytes);

        assertArrayEquals(digest.toBytes(),resolvedDigest.toBytes());
        assertArrayEquals(digest.getRawDigest(),resolvedDigest.getRawDigest());
        assertEquals(digest.getAlgorithm().name(),resolvedDigest.getAlgorithm().name());
        assertEquals(digest.getAlgorithm().code(),resolvedDigest.getAlgorithm().code());


        algorithm = CryptoServiceProviders.getAlgorithm("sm4");
        assertNotNull(algorithm);
        byte[] algoBytes = CryptoAlgorithm.toBytes(algorithm);
        byte[] rawDigestBytes = digest.getRawDigest();
        byte[] aesDigestBytes = BytesUtils.concat(algoBytes,rawDigestBytes);

        Class<?> expectedException = CryptoException.class;
        Exception actualEx = null;
        try {
            hashFunction.resolveHashDigest(aesDigestBytes);
        } catch (Exception e) {
            actualEx = e;
        }
        assertNotNull(actualEx);
        assertTrue(expectedException.isAssignableFrom(actualEx.getClass()));
//
//        algorithm = CryptoServiceProviders.getAlgorithm("ripemd160");
//        assertNotNull(algorithm);
//        algoBytes = CryptoAlgorithm.toBytes(algorithm);
//        rawDigestBytes = digest.getRawDigest();
//        byte[] ripemd160DigestBytes = BytesUtils.concat(algoBytes,rawDigestBytes);
//
//        expectedException = CryptoException.class;
//        actualEx = null;
//        try {
//            hashFunction.resolveHashDigest(ripemd160DigestBytes);
//        } catch (Exception e) {
//            actualEx = e;
//        }
//        assertNotNull(actualEx);
//        assertTrue(expectedException.isAssignableFrom(actualEx.getClass()));
    }
}
