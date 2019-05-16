package test.com.jd.blockchain.crypto.service.classic;

import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.CryptoException;
import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.HashFunction;
import com.jd.blockchain.crypto.service.classic.ClassicAlgorithm;
import com.jd.blockchain.utils.io.BytesUtils;
import org.junit.Test;

import java.util.Random;

import static com.jd.blockchain.crypto.CryptoAlgorithm.HASH_ALGORITHM;
import static org.junit.Assert.*;

/**
 * @author zhanglin33
 * @title: SHA256HashFunctionTest
 * @description: JunitTest for SHA256HashFunction in SPI mode
 * @date 2019-04-01, 14:21
 */

public class SHA256HashFunctionTest {

	@Test
	public void getAlgorithmTest() {
		CryptoAlgorithm algorithm = Crypto.getAlgorithm("sha256");
		assertNotNull(algorithm);

		HashFunction hashFunction = Crypto.getHashFunction(algorithm);

		assertEquals(hashFunction.getAlgorithm().name(), algorithm.name());
		assertEquals(hashFunction.getAlgorithm().code(), algorithm.code());

		algorithm = Crypto.getAlgorithm("SHa256");
		assertNotNull(algorithm);

		assertEquals(hashFunction.getAlgorithm().name(), algorithm.name());
		assertEquals(hashFunction.getAlgorithm().code(), algorithm.code());

		algorithm = Crypto.getAlgorithm("sha-256");
		assertNull(algorithm);
	}

	@Test
	public void hashTest() {

		byte[] data = new byte[1024];
		Random random = new Random();
		random.nextBytes(data);

		CryptoAlgorithm algorithm = Crypto.getAlgorithm("sha256");
		assertNotNull(algorithm);

		HashFunction hashFunction = Crypto.getHashFunction(algorithm);

		HashDigest digest = hashFunction.hash(data);
		byte[] rawDigestBytes = digest.getRawDigest();
		byte[] algoBytes = CryptoAlgorithm.getCodeBytes(algorithm);

		byte[] digestBytes = digest.toBytes();
		assertEquals(256 / 8 + 2, digestBytes.length);
		assertArrayEquals(digestBytes, BytesUtils.concat(algoBytes, rawDigestBytes));

		assertEquals(algorithm.code(), digest.getAlgorithm());

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
	public void verifyTest() {
		byte[] data = new byte[1024];
		Random random = new Random();
		random.nextBytes(data);

		CryptoAlgorithm algorithm = Crypto.getAlgorithm("sha256");
		assertNotNull(algorithm);

		HashFunction hashFunction = Crypto.getHashFunction(algorithm);

		HashDigest digest = hashFunction.hash(data);

		assertTrue(hashFunction.verify(digest, data));
	}

	@Test
	public void supportHashDigestTest() {
		byte[] data = new byte[1024];
		Random random = new Random();
		random.nextBytes(data);

		CryptoAlgorithm algorithm = Crypto.getAlgorithm("sha256");
		assertNotNull(algorithm);

		HashFunction hashFunction = Crypto.getHashFunction(algorithm);

		HashDigest digest = hashFunction.hash(data);

		byte[] digestBytes = digest.toBytes();
		assertTrue(hashFunction.supportHashDigest(digestBytes));

		algorithm = Crypto.getAlgorithm("aes");
		assertNotNull(algorithm);
		byte[] algoBytes = CryptoAlgorithm.getCodeBytes(algorithm);
		System.arraycopy(algoBytes, 0, digestBytes, 0, algoBytes.length);
		assertFalse(hashFunction.supportHashDigest(digestBytes));
	}

	@Test
	public void resolveHashDigestTest() {
		byte[] data = new byte[1024];
		Random random = new Random();
		random.nextBytes(data);

		CryptoAlgorithm algorithm = Crypto.getAlgorithm("sha256");
		assertNotNull(algorithm);

		HashFunction hashFunction = Crypto.getHashFunction(algorithm);

		HashDigest digest = hashFunction.hash(data);

		byte[] digestBytes = digest.toBytes();

		HashDigest resolvedDigest = hashFunction.resolveHashDigest(digestBytes);

		assertEquals(256 / 8, resolvedDigest.getRawDigest().length);
		assertEquals(ClassicAlgorithm.SHA256.code(), resolvedDigest.getAlgorithm());
		assertEquals((short) (HASH_ALGORITHM | ((byte) 24 & 0x00FF)), resolvedDigest.getAlgorithm());
		assertArrayEquals(digestBytes, resolvedDigest.toBytes());

		algorithm = Crypto.getAlgorithm("aes");
		assertNotNull(algorithm);
		byte[] algoBytes = CryptoAlgorithm.getCodeBytes(algorithm);
		byte[] rawDigestBytes = digest.getRawDigest();
		byte[] aesDigestBytes = BytesUtils.concat(algoBytes, rawDigestBytes);

		Class<?> expectedException = CryptoException.class;
		Exception actualEx = null;
		try {
			hashFunction.resolveHashDigest(aesDigestBytes);
		} catch (Exception e) {
			actualEx = e;
		}
		assertNotNull(actualEx);
		assertTrue(expectedException.isAssignableFrom(actualEx.getClass()));

		algorithm = Crypto.getAlgorithm("ripemd160");
		assertNotNull(algorithm);
		algoBytes = CryptoAlgorithm.getCodeBytes(algorithm);
		rawDigestBytes = digest.getRawDigest();
		byte[] ripemd160DigestBytes = BytesUtils.concat(algoBytes, rawDigestBytes);

		expectedException = CryptoException.class;
		actualEx = null;
		try {
			hashFunction.resolveHashDigest(ripemd160DigestBytes);
		} catch (Exception e) {
			actualEx = e;
		}
		assertNotNull(actualEx);
		assertTrue(expectedException.isAssignableFrom(actualEx.getClass()));
	}
}
