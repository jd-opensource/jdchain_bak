package test.com.jd.blockchain.crypto.hash;

import static org.junit.Assert.*;

import java.util.Random;

import com.jd.blockchain.crypto.smutils.hash.SM3Utils;
import com.jd.blockchain.utils.io.BytesUtils;
import com.jd.blockchain.utils.security.RipeMD160Utils;
import com.jd.blockchain.utils.security.ShaUtils;

import org.junit.Test;

import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.hash.HashCryptography;
import com.jd.blockchain.crypto.hash.HashDigest;
import com.jd.blockchain.crypto.hash.HashFunction;
import com.jd.blockchain.crypto.impl.HashCryptographyImpl;

public class HashCryptographyImplTest {

	@Test
	public void testGetFunction() {
		HashCryptography hashCrypto = new HashCryptographyImpl();
		Random rand = new Random();
		// test SHA256
		CryptoAlgorithm algorithm = CryptoAlgorithm.SHA256;
		byte[] data = new byte[256];
		rand.nextBytes(data);
		verifyGetFunction(hashCrypto, algorithm, data, 256 / 8,null);

		data = new byte[0];
		verifyGetFunction(hashCrypto, algorithm, data, 256 / 8,null);

		data = new byte[1056];
		rand.nextBytes(data);
		verifyGetFunction(hashCrypto, algorithm, data, 256 / 8,null);

		data = null;
		verifyGetFunction(hashCrypto, algorithm, data, 256 / 8,NullPointerException.class);


		// test RIPEMD160
		algorithm = CryptoAlgorithm.RIPEMD160;
		data=new byte[256];
		rand.nextBytes(data);
		verifyGetFunction(hashCrypto, algorithm, data, 160 / 8,null);

		data = new byte[0];
		verifyGetFunction(hashCrypto, algorithm, data, 160/ 8,null);

		data = new byte[1056];
		rand.nextBytes(data);
		verifyGetFunction(hashCrypto, algorithm, data, 160 / 8,null);

		data = null;
		verifyGetFunction(hashCrypto, algorithm, data, 160 / 8,NullPointerException.class);

		// test SM3
		algorithm = CryptoAlgorithm.SM3;
		data = new byte[256];
		rand.nextBytes(data);
		verifyGetFunction(hashCrypto, algorithm, data, 256 / 8,null);

		data = new byte[0];
		verifyGetFunction(hashCrypto, algorithm, data, 256 / 8,null);

		data = new byte[1056];
		rand.nextBytes(data);
		verifyGetFunction(hashCrypto, algorithm, data, 256 / 8,null);

		data = null;
		verifyGetFunction(hashCrypto, algorithm, data, 256 / 8,NullPointerException.class);

		// test AES
		data = new byte[0];
		algorithm = CryptoAlgorithm.AES;
		verifyGetFunction(hashCrypto, algorithm, data, 256 / 8,IllegalArgumentException.class);

		// test JNISHA256
		algorithm = CryptoAlgorithm.JNISHA256;
		data = new byte[256];
		rand.nextBytes(data);
		verifyGetFunction(hashCrypto, algorithm, data, 256 / 8,null);

		data = new byte[0];
		verifyGetFunction(hashCrypto, algorithm, data, 256 / 8,null);

		data = new byte[1056];
		rand.nextBytes(data);
		verifyGetFunction(hashCrypto, algorithm, data, 256 / 8,null);

		data = null;
		verifyGetFunction(hashCrypto, algorithm, data, 256 / 8,IllegalArgumentException.class);

		// test JNIRIPEMD160
		algorithm = CryptoAlgorithm.JNIRIPEMD160;
		data=new byte[256];
		rand.nextBytes(data);
		verifyGetFunction(hashCrypto, algorithm, data, 160 / 8,null);

		data = new byte[0];
		verifyGetFunction(hashCrypto, algorithm, data, 160/ 8,null);

		data = new byte[1056];
		rand.nextBytes(data);
		verifyGetFunction(hashCrypto, algorithm, data, 160 / 8,null);

		data = null;
		verifyGetFunction(hashCrypto, algorithm, data, 160 / 8,IllegalArgumentException.class);
	}

	private void verifyGetFunction(HashCryptography hashCrypto, CryptoAlgorithm algorithm, byte[] data,
								   int expectedRawBytes,Class<?> expectedException) {
		Exception actualEx = null;
		try {
		HashFunction hf = hashCrypto.getFunction(algorithm);
		assertNotNull(hf);

		HashDigest hd = hf.hash(data);

		assertEquals(algorithm, hd.getAlgorithm());

		assertEquals(expectedRawBytes, hd.getRawDigest().length);

		// verify encoding;
		byte[] encodedHash = hd.toBytes();
		assertEquals(expectedRawBytes + 1, encodedHash.length);


		assertEquals(algorithm.CODE, encodedHash[0]);

		//verify equals
		assertEquals(true, hd.equals(hf.hash(data)));

		//verify verify
		assertTrue( hf.verify(hd, data));

		} catch (Exception e) {
			actualEx = e;
		}

		if(expectedException==null){
			assertNull(actualEx);
		}
		else {
			assertNotNull(actualEx);
			assertTrue(expectedException.isAssignableFrom(actualEx.getClass()));
		}
	}

	@Test
	public void testVerifyHashDigestByteArray() {
		HashCryptography hashCrypto = new HashCryptographyImpl();
		//test SHA256
		byte[] data=new byte[256];
		Random rand = new Random();
		rand.nextBytes(data);
		CryptoAlgorithm algorithm=CryptoAlgorithm.SHA256;
		verifyHashDigestByteArray(hashCrypto,algorithm,data,null);
		data=null;
		verifyHashDigestByteArray(hashCrypto,algorithm,data,NullPointerException.class);

		//test RIPEMD160
		algorithm=CryptoAlgorithm.RIPEMD160;
		data=new byte[896];
		rand.nextBytes(data);
		verifyHashDigestByteArray(hashCrypto,algorithm,data,null);
		data=null;
		verifyHashDigestByteArray(hashCrypto,algorithm,data,NullPointerException.class);

		//test SM3
		algorithm=CryptoAlgorithm.SM3;
		data=new byte[896];
		rand.nextBytes(data);
		verifyHashDigestByteArray(hashCrypto,algorithm,data,null);
		data=null;
		verifyHashDigestByteArray(hashCrypto,algorithm,data,NullPointerException.class);


		//test AES
		algorithm=CryptoAlgorithm.AES;
		data=new byte[277];
		rand.nextBytes(data);
		verifyHashDigestByteArray(hashCrypto,algorithm,data,IllegalArgumentException.class);

		//test JNISHA256
		data=new byte[256];
		rand = new Random();
		rand.nextBytes(data);
		algorithm=CryptoAlgorithm.JNISHA256;
		verifyHashDigestByteArray(hashCrypto,algorithm,data,null);
		data=null;
		verifyHashDigestByteArray(hashCrypto,algorithm,data,IllegalArgumentException.class);

		//test JNIRIPEMD160
		algorithm=CryptoAlgorithm.JNIRIPEMD160;
		data=new byte[896];
		rand.nextBytes(data);
		verifyHashDigestByteArray(hashCrypto,algorithm,data,null);
		data=null;
		verifyHashDigestByteArray(hashCrypto,algorithm,data,IllegalArgumentException.class);
	}

	private void verifyHashDigestByteArray(HashCryptography hashCrypto,CryptoAlgorithm algorithm,byte[] data,Class<?> expectedException){
		Exception actualEx=null;
		try {
			HashFunction hf = hashCrypto.getFunction(algorithm);
			assertNotNull(hf);
			HashDigest hd = hf.hash(data);
			hashCrypto.verify(hd,data);
		}catch (Exception e)
		{
			actualEx=e;
		}
		if (expectedException==null)
		{
			assertNull(actualEx);
		}
		else{
			assertNotNull(actualEx);
			assertTrue(expectedException.isAssignableFrom(actualEx.getClass()));
		}
	}

	@Test
	public void testResolveHashDigest() {
		Random rand = new Random();
		HashCryptography hashCrypto = new HashCryptographyImpl();

		//test SHA256
		CryptoAlgorithm algorithm = CryptoAlgorithm.SHA256;
		byte[] data = new byte[256];
		rand.nextBytes(data);
		byte[] hashDigestBytes = hashCrypto.getFunction(algorithm).hash(data).toBytes();
		verifyResolveHashDigest(algorithm, hashCrypto,hashDigestBytes,32+1,null);

		byte[] truncatedHashDigestBytes = new byte[hashDigestBytes.length-2];
		System.arraycopy(hashDigestBytes,0,truncatedHashDigestBytes,0,truncatedHashDigestBytes.length);
		verifyResolveHashDigest(algorithm, hashCrypto,truncatedHashDigestBytes,32+1,IllegalArgumentException.class);

		hashDigestBytes = null;
		verifyResolveHashDigest(algorithm, hashCrypto,hashDigestBytes,32+1,NullPointerException.class);


		//test RIPEMD160
		algorithm = CryptoAlgorithm.RIPEMD160;
		data = new byte[256];
		rand.nextBytes(data);
		hashDigestBytes = hashCrypto.getFunction(algorithm).hash(data).toBytes();
		verifyResolveHashDigest(algorithm, hashCrypto,hashDigestBytes,20+1,null);

		truncatedHashDigestBytes = new byte[hashDigestBytes.length-2];
		System.arraycopy(hashDigestBytes,0,truncatedHashDigestBytes,0,truncatedHashDigestBytes.length);
		verifyResolveHashDigest(algorithm, hashCrypto,truncatedHashDigestBytes,20+1,IllegalArgumentException.class);

		hashDigestBytes = null;
		verifyResolveHashDigest(algorithm, hashCrypto,hashDigestBytes,20+1,NullPointerException.class);


		//test SM3
		algorithm = CryptoAlgorithm.SM3;
		data = new byte[256];
		rand.nextBytes(data);
		hashDigestBytes = hashCrypto.getFunction(algorithm).hash(data).toBytes();
		verifyResolveHashDigest(algorithm, hashCrypto,hashDigestBytes,32+1,null);

		truncatedHashDigestBytes = new byte[hashDigestBytes.length-2];
		System.arraycopy(hashDigestBytes,0,truncatedHashDigestBytes,0,truncatedHashDigestBytes.length);
		verifyResolveHashDigest(algorithm, hashCrypto,truncatedHashDigestBytes,32+1,IllegalArgumentException.class);

		hashDigestBytes = null;
		verifyResolveHashDigest(algorithm, hashCrypto,hashDigestBytes,32+1,NullPointerException.class);


		//test JNISHA256
		algorithm = CryptoAlgorithm.JNISHA256;
		data = new byte[256];
		rand.nextBytes(data);
		hashDigestBytes = hashCrypto.getFunction(algorithm).hash(data).toBytes();
		verifyResolveHashDigest(algorithm, hashCrypto,hashDigestBytes,32+1,null);

		truncatedHashDigestBytes = new byte[hashDigestBytes.length-2];
		System.arraycopy(hashDigestBytes,0,truncatedHashDigestBytes,0,truncatedHashDigestBytes.length);
		verifyResolveHashDigest(algorithm, hashCrypto,truncatedHashDigestBytes,32+1,IllegalArgumentException.class);

		hashDigestBytes = null;
		verifyResolveHashDigest(algorithm, hashCrypto,hashDigestBytes,32+1,NullPointerException.class);

		//test JNIRIPEMD160
		algorithm = CryptoAlgorithm.JNIRIPEMD160;
		data = new byte[256];
		rand.nextBytes(data);
		hashDigestBytes = hashCrypto.getFunction(algorithm).hash(data).toBytes();
		verifyResolveHashDigest(algorithm, hashCrypto,hashDigestBytes,20+1,null);

		truncatedHashDigestBytes = new byte[hashDigestBytes.length-2];
		System.arraycopy(hashDigestBytes,0,truncatedHashDigestBytes,0,truncatedHashDigestBytes.length);
		verifyResolveHashDigest(algorithm, hashCrypto,truncatedHashDigestBytes,20+1,IllegalArgumentException.class);

		hashDigestBytes = null;
		verifyResolveHashDigest(algorithm, hashCrypto,hashDigestBytes,20+1,NullPointerException.class);
	}

	private void verifyResolveHashDigest(CryptoAlgorithm algorithm,HashCryptography
		hashCrypto,byte[] hashDigestBytes,int expectedLength,Class<?>expectedException){

        Exception actualEx=null;

        try {

		HashDigest hashDigest=hashCrypto.resolveHashDigest(hashDigestBytes);
		assertNotNull(hashDigest);
		assertEquals(algorithm,hashDigest.getAlgorithm());
		byte[] algBytes = new byte[1];
		algBytes[0] = algorithm.CODE;
		assertArrayEquals(hashDigestBytes,BytesUtils.concat(algBytes,hashDigest.getRawDigest()));
		assertEquals(expectedLength,hashDigestBytes.length);

	    }catch (Exception e)
		{
			actualEx = e;
		}
		if (expectedException==null)
		{
			assertNull(actualEx);
		}
		else {
        	assertNotNull(actualEx);
        	assertTrue(expectedException.isAssignableFrom(actualEx.getClass()));
		}
	}
	}
