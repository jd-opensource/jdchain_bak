package test.my.utils.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.util.Random;
import java.util.UUID;

import org.junit.Test;

import com.jd.blockchain.utils.codec.HexUtils;
import com.jd.blockchain.utils.io.BytesUtils;
import com.jd.blockchain.utils.security.AESUtils;
import com.jd.blockchain.utils.security.ShaUtils;

public class AESUtilsTest {

	@Test
	public void testAESKeyGeneration() throws UnsupportedEncodingException {
		String seedStr = UUID.randomUUID().toString();
		byte[] seed = seedStr.getBytes("UTF-8");
		System.out.println("seed.lenght=" + seed.length);

		// 验证：同一个种子，生成的秘钥总是相同的；
		String key1 = AESUtils.generateKey128_Hex(seed);
		String key2 = AESUtils.generateKey128_Hex(seed);
		String key3 = AESUtils.generateKey128_Hex(seed);

		assertEquals(key1, key2);
		assertEquals(key1, key3);

		// 验证：不同的种子，生成的秘钥总是不同的；
		byte[] seed2 = UUID.randomUUID().toString().getBytes("UTF-8");
		String newKey = AESUtils.generateKey128_Hex(seed2);
		assertNotEquals(key1, newKey);

		// 验证：随机秘钥总是不同的；
		String randomKey1 = AESUtils.generateKey128_Hex();
		String randomKey2 = AESUtils.generateKey128_Hex();
		String randomKey3 = AESUtils.generateKey128_Hex();
		assertNotEquals(randomKey1, randomKey2);
		assertNotEquals(randomKey1, randomKey3);
		assertNotEquals(randomKey2, randomKey3);

		// 验证：更长的种子也可以支持；
		byte[] longSeed = (seedStr + seedStr + seedStr + seedStr).getBytes("UTF-8");
		System.out.println("longSeed.length=" + longSeed.length);
		String keyFromLongSeed = AESUtils.generateKey128_Hex(longSeed);
		String keyFromLongSeed2 = AESUtils.generateKey128_Hex(longSeed);
		assertNotEquals(key1, keyFromLongSeed2);
		assertEquals(keyFromLongSeed, keyFromLongSeed2);

		// 验证：对更长的种子进行 hash 后再生成 key；
		byte[] hashSeed = ShaUtils.hash_256(longSeed);
		System.out.println("hashSeed.length=" + hashSeed.length);
		String keyFromHashSeed = AESUtils.generateKey128_Hex(hashSeed);
		String keyFromHashSeed2 = AESUtils.generateKey128_Hex(hashSeed);

		assertNotEquals(key1, keyFromHashSeed);
		assertNotEquals(keyFromLongSeed, keyFromHashSeed);
		assertEquals(keyFromHashSeed, keyFromHashSeed2);
	}

	@Test
	public void testAESEncryptionWithRandomKey() throws UnsupportedEncodingException {
		String keyString = AESUtils.generateKey128_Hex();

		byte[] origBytes = UUID.randomUUID().toString().getBytes("UTF-8");

		byte[] enBytes = AESUtils.encrypt(origBytes, keyString);
		byte[] deBytes = AESUtils.decrypt(enBytes, keyString);

		assertEquals(origBytes.length, deBytes.length);
		for (int i = 0; i < origBytes.length; i++) {
			assertEquals(origBytes[i], deBytes[i]);
		}
	}

	/**
	 * 根据同一个 seed 生成的秘钥，能够加密，并解密恢复原始内容；
	 * 
	 * @throws UnsupportedEncodingException
	 */
	@Test
	public void testAESEncryptionWithSeed() throws UnsupportedEncodingException {
		byte[] seed = UUID.randomUUID().toString().getBytes("UTF-8");
		byte[] encryptedKey = AESUtils.generateKey128_Bytes(seed);

		byte[] plainBytes = UUID.randomUUID().toString().getBytes("UTF-8");
		String plainContent = HexUtils.encode(plainBytes);
		byte[] encryptedBytes = AESUtils.encrypt(plainBytes, encryptedKey);

		byte[] decryptedKey = AESUtils.generateKey128_Bytes(seed);
		byte[] decryptedBytes = AESUtils.decrypt(encryptedBytes, decryptedKey);
		String decryptedContent = HexUtils.encode(decryptedBytes);

		assertEquals(plainContent, decryptedContent);
	}
	
	@Test
	public void testAESEnryptionConsistance() throws UnsupportedEncodingException {
		byte[] seed = UUID.randomUUID().toString().getBytes("UTF-8");
		byte[] encryptedKey = AESUtils.generateKey128_Bytes(seed);
		
		byte[] plainBytes = UUID.randomUUID().toString().getBytes("UTF-8");
		
		byte[] encryptedBytes = AESUtils.encrypt(plainBytes, encryptedKey);
		byte[] encryptedBytes1 = AESUtils.encrypt(plainBytes, encryptedKey);
		byte[] encryptedBytes2 = AESUtils.encrypt(plainBytes, encryptedKey);
		byte[] encryptedBytes3 = AESUtils.encrypt(plainBytes, encryptedKey);
		
		assertTrue(BytesUtils.equals(encryptedBytes, encryptedBytes1));
		assertTrue(BytesUtils.equals(encryptedBytes1, encryptedBytes2));
		assertTrue(BytesUtils.equals(encryptedBytes2, encryptedBytes3));
	}

	/**
	 * 测试对任意长度的明文数据进行加解密的正确性；
	 */
	@Test
	public void testLongBytes() {
		testLongBytes_128(101);
		testLongBytes_128(171);
		testLongBytes_128(1023);
		testLongBytes_128(1024);
		testLongBytes_128(1025);
	}

	/**
	 * 测试在 128 长度密码下对任意长度的输入进行加密解密的正确性；
	 * @param size
	 */
	private void testLongBytes_128(int size) {
		byte[] data = new byte[size];
		Random rand = new Random();
		rand.nextBytes(data);

		System.out.println("(" + size + ")Bytes data=[" + HexUtils.encode(data) + "]");

		byte[] key = AESUtils.generateKey128_Bytes();

		byte[] encData = AESUtils.encrypt(data, key);

		byte[] decData = AESUtils.decrypt(encData, key);

		assertTrue(BytesUtils.equals(data, decData));
	}
	
	

}
