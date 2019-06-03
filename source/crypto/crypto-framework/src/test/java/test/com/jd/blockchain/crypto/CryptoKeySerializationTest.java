package test.com.jd.blockchain.crypto;

import static org.junit.Assert.*;

import java.util.UUID;

import org.junit.Test;

import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.CryptoAlgorithmDefinition;
import com.jd.blockchain.crypto.CryptoKeyType;
import com.jd.blockchain.crypto.PrivKey;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.utils.io.BytesUtils;

public class CryptoKeySerializationTest {

	/**
	 * Test the equivalence of serialization and deserialization of PubKey;
	 */
	@Test
	public void testPubKey() {
		CryptoAlgorithm algorithm = CryptoAlgorithmDefinition.defineSignature("TEST", false, (byte) 123);

		// Simulate a public key with a random number;
		byte[] rawBytes = BytesUtils.toBytes(UUID.randomUUID().toString());

		PubKey pubKey = new PubKey(algorithm, rawBytes);

		assertEquals(algorithm.code(), pubKey.getAlgorithm());
		assertEquals(CryptoKeyType.PUBLIC, pubKey.getKeyType());

		// serialize;
		byte[] keyBytes = pubKey.toBytes();

		// deserialize;
		PubKey desPubKey = new PubKey(keyBytes);

		assertEquals(algorithm.code(), desPubKey.getAlgorithm());
		assertEquals(CryptoKeyType.PUBLIC, desPubKey.getKeyType());
		byte[] desRawBytes = desPubKey.getRawKeyBytes();
		assertTrue(BytesUtils.equals(rawBytes, desRawBytes));

	}
	
	/**
	 * Test the equivalence of serialization and deserialization of PrivKey;
	 */
	@Test
	public void testPrivKey() {
		CryptoAlgorithm algorithm = CryptoAlgorithmDefinition.defineSignature("TEST", false, (byte) 123);
		
		// Simulate a public key with a random number;
		byte[] rawBytes = BytesUtils.toBytes(UUID.randomUUID().toString());
		
		PrivKey privKey = new PrivKey(algorithm, rawBytes);
		
		assertEquals(algorithm.code(), privKey.getAlgorithm());
		assertEquals(CryptoKeyType.PRIVATE, privKey.getKeyType());
		
		// serialize;
		byte[] keyBytes = privKey.toBytes();
		
		// deserialize;
		PrivKey desPrivKey = new PrivKey(keyBytes);
		
		assertEquals(algorithm.code(), desPrivKey.getAlgorithm());
		assertEquals(CryptoKeyType.PRIVATE, desPrivKey.getKeyType());
		byte[] desRawBytes = desPrivKey.getRawKeyBytes();
		assertTrue(BytesUtils.equals(rawBytes, desRawBytes));
		
	}

}
