package test.my.utils.security;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.UUID;

import org.junit.Test;

import com.jd.blockchain.utils.security.RandomUtils;
import com.jd.blockchain.utils.security.ShaUtils;

public class ShaUtilsTest {

//	@Test
//	public void testHash_128() throws UnsupportedEncodingException {
//		String text = "the text with fixed size";
//		byte[] hashBytes = ShaUtils.hash_128(text.getBytes("UTF-8"));
//		assertEquals(16, hashBytes.length);
//		
//		text = UUID.randomUUID().toString();
//		hashBytes = ShaUtils.hash_128(text.getBytes("UTF-8"));
//		assertEquals(16, hashBytes.length);
//		
//		StringBuilder bigText = new StringBuilder();
//		for (int i = 0; i < 256; i++) {
//			bigText.append((char)(97+(i% 20)));
//		}
//		hashBytes = ShaUtils.hash_128(bigText.toString().getBytes("UTF-8"));
//		assertEquals(16, hashBytes.length);
//	}

	@Test
	public void testHash_256ByteArray() throws UnsupportedEncodingException {
		String text = "the text with fixed size";
		byte[] hashBytes = ShaUtils.hash_256(text.getBytes("UTF-8"));
		assertEquals(32, hashBytes.length);
		
		text = UUID.randomUUID().toString();
		hashBytes = ShaUtils.hash_256(text.getBytes("UTF-8"));
		assertEquals(32, hashBytes.length);
		
		StringBuilder bigText = new StringBuilder();
		for (int i = 0; i < 512; i++) {
			bigText.append((char)(97+(i% 20)));
		}
		hashBytes = ShaUtils.hash_256(bigText.toString().getBytes("UTF-8"));
		assertEquals(32, hashBytes.length);
	}
	
	
	/**
	 * 验证采用不同的缓存数组大小进行 hash 计算是否会影响其计算结果；<br>
	 * 
	 * 注：显然，算法本身的效果是不会受算法调用方式的影响的；测试的结果也表明这一点；
	 */
	@Test
	public void testHash_256_withDiffBuffSize() {
		byte[] randBytes = RandomUtils.generateRandomBytes(256);
		
		ByteArrayInputStream in = new ByteArrayInputStream(randBytes);
		byte[] hash1 = hash_256(in, 32);
		
		in = new ByteArrayInputStream(randBytes);
		byte[] hash2 = hash_256(in, 32);
		
		in = new ByteArrayInputStream(randBytes);
		byte[] hash3 = hash_256(in, 64);
		
		in = new ByteArrayInputStream(randBytes);
		byte[] hash4 = hash_256(in, 128);
		
		assertArrayEquals(hash1, hash2);
		assertArrayEquals(hash1, hash3);
		assertArrayEquals(hash1, hash4);
	}

	
	private static byte[] hash_256(InputStream input, int buffSize) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-256");
			byte[] buff =new byte[buffSize];
			int len = 0;
			while((len=input.read(buff)) > 0){
				md.update(buff, 0, len);
			}
			return md.digest();
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e.getMessage(), e);
		} catch (IOException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}
}
