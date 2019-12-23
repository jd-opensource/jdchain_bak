package test.my.utils.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import org.junit.Test;

import com.jd.blockchain.utils.security.RandomUtils;

public class RandomUtilsTest {

	@Test
	public void testGenerateRandomBytesWithDifferntSeed() throws UnsupportedEncodingException {
		String seedStr = UUID.randomUUID().toString();
		byte[] seed = seedStr.getBytes("UTF-8");
		String seedStr2 = UUID.randomUUID().toString();
		byte[] seed2 = seedStr2.getBytes("UTF-8");
		System.out.println("seed.lenght=" + seed.length);
		
		byte[] random1 = RandomUtils.generateRandomBytes(16, seed);
		byte[] random2 = RandomUtils.generateRandomBytes(16, seed2);
		
		assertNoEqualBytes(random1, random2);
		
		random1 = RandomUtils.generateRandomBytes(32, seed);
		random2 = RandomUtils.generateRandomBytes(32, seed2);
		
		assertNoEqualBytes(random1, random2);
		
		random1 = RandomUtils.generateRandomBytes(64, seed);
		random2 = RandomUtils.generateRandomBytes(64, seed2);
		
		assertNoEqualBytes(random1, random2);
	}
	
	private void assertNoEqualBytes(byte[] random1, byte[] random2) {
		boolean notEqual = random1.length != random2.length;
		assertEquals(random1.length, random2.length);
		for (int i = 0; i < random1.length; i++) {
			notEqual = notEqual | (random1[i] != random2[i]);
			if (notEqual) {
				break;
			}
		}
		assertTrue(notEqual);
	}

}
