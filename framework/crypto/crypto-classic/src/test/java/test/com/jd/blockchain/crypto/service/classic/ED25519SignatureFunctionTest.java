package test.com.jd.blockchain.crypto.service.classic;

import static com.jd.blockchain.crypto.CryptoAlgorithm.ASYMMETRIC_KEY;
import static com.jd.blockchain.crypto.CryptoAlgorithm.SIGNATURE_ALGORITHM;
import static com.jd.blockchain.crypto.CryptoKeyType.PRIVATE;
import static com.jd.blockchain.crypto.CryptoKeyType.PUBLIC;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;

import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.CryptoException;
import com.jd.blockchain.crypto.AsymmetricKeypair;
import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.crypto.PrivKey;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.crypto.SignatureDigest;
import com.jd.blockchain.crypto.SignatureFunction;
import com.jd.blockchain.crypto.service.classic.ClassicAlgorithm;
import com.jd.blockchain.utils.io.BytesUtils;

/**
 * @author zhanglin33
 * @title: ED25519SignatureFunctionTest
 * @description: JunitTest for ED25519SignatureFunction in SPI mode
 * @date 2019-04-01, 14:01
 */
public class ED25519SignatureFunctionTest {

	@Test
	public void getAlgorithmTest() {

		CryptoAlgorithm algorithm = Crypto.getAlgorithm("ed25519");
		assertNotNull(algorithm);

		SignatureFunction signatureFunction = Crypto.getSignatureFunction(algorithm);

		assertEquals(signatureFunction.getAlgorithm().name(), algorithm.name());
		assertEquals(signatureFunction.getAlgorithm().code(), algorithm.code());

		algorithm = Crypto.getAlgorithm("Ed25519");
		assertNotNull(algorithm);

		assertEquals(signatureFunction.getAlgorithm().name(), algorithm.name());
		assertEquals(signatureFunction.getAlgorithm().code(), algorithm.code());

		algorithm = Crypto.getAlgorithm("eddsa");
		assertNull(algorithm);
	}

	@Test
	public void generateKeyPairTest() {

		CryptoAlgorithm algorithm = Crypto.getAlgorithm("ed25519");
		assertNotNull(algorithm);

		SignatureFunction signatureFunction = Crypto.getSignatureFunction(algorithm);

		AsymmetricKeypair keyPair = signatureFunction.generateKeypair();

		PubKey pubKey = keyPair.getPubKey();
		PrivKey privKey = keyPair.getPrivKey();

		assertEquals(PUBLIC.CODE, pubKey.getKeyType().CODE);
		assertEquals(32, pubKey.getRawKeyBytes().length);
		assertEquals(PRIVATE.CODE, privKey.getKeyType().CODE);
		assertEquals(32, privKey.getRawKeyBytes().length);

		assertEquals(algorithm.code(), pubKey.getAlgorithm());
		assertEquals(algorithm.code(), privKey.getAlgorithm());

		assertEquals(2 + 1 + 32, pubKey.toBytes().length);
		assertEquals(2 + 1 + 32, privKey.toBytes().length);

		byte[] algoBytes = CryptoAlgorithm.getCodeBytes(algorithm);
		byte[] pubKeyTypeBytes = new byte[] { PUBLIC.CODE };
		byte[] privKeyTypeBytes = new byte[] { PRIVATE.CODE };
		byte[] rawPubKeyBytes = pubKey.getRawKeyBytes();
		byte[] rawPrivKeyBytes = privKey.getRawKeyBytes();
		assertArrayEquals(BytesUtils.concat(algoBytes, pubKeyTypeBytes, rawPubKeyBytes), pubKey.toBytes());
		assertArrayEquals(BytesUtils.concat(algoBytes, privKeyTypeBytes, rawPrivKeyBytes), privKey.toBytes());
	}

	@Test
	public void retrievePubKeyTest() {

		CryptoAlgorithm algorithm = Crypto.getAlgorithm("ed25519");
		assertNotNull(algorithm);

		SignatureFunction signatureFunction = Crypto.getSignatureFunction(algorithm);

		AsymmetricKeypair keyPair = signatureFunction.generateKeypair();

		PubKey pubKey = keyPair.getPubKey();
		PrivKey privKey = keyPair.getPrivKey();

		PubKey retrievedPubKey = signatureFunction.retrievePubKey(privKey);

		assertEquals(pubKey.getKeyType(), retrievedPubKey.getKeyType());
		assertEquals(pubKey.getRawKeyBytes().length, retrievedPubKey.getRawKeyBytes().length);
		assertEquals(pubKey.getAlgorithm(), retrievedPubKey.getAlgorithm());
		assertArrayEquals(pubKey.toBytes(), retrievedPubKey.toBytes());
	}

	@Test
	public void signTest() {

		byte[] data = new byte[1024];
		Random random = new Random();
		random.nextBytes(data);

		CryptoAlgorithm algorithm = Crypto.getAlgorithm("ed25519");
		assertNotNull(algorithm);

		SignatureFunction signatureFunction = Crypto.getSignatureFunction(algorithm);

		AsymmetricKeypair keyPair = signatureFunction.generateKeypair();

		PrivKey privKey = keyPair.getPrivKey();
		SignatureDigest signatureDigest = signatureFunction.sign(privKey, data);

		byte[] signatureBytes = signatureDigest.toBytes();

		assertEquals(2 + 64, signatureBytes.length);
		assertEquals(ClassicAlgorithm.ED25519.code(), signatureDigest.getAlgorithm());
		assertEquals((short) (SIGNATURE_ALGORITHM | ASYMMETRIC_KEY | ((byte) 21 & 0x00FF)),
				signatureDigest.getAlgorithm());

		byte[] algoBytes = BytesUtils.toBytes(signatureDigest.getAlgorithm());
		byte[] rawSinatureBytes = signatureDigest.getRawDigest();
		assertArrayEquals(BytesUtils.concat(algoBytes, rawSinatureBytes), signatureBytes);
	}

	@Test
	public void verifyTest() {
		byte[] data = new byte[1024];
		Random random = new Random();
		random.nextBytes(data);

		CryptoAlgorithm algorithm = Crypto.getAlgorithm("ed25519");
		assertNotNull(algorithm);

		SignatureFunction signatureFunction = Crypto.getSignatureFunction(algorithm);

		AsymmetricKeypair keyPair = signatureFunction.generateKeypair();

		PubKey pubKey = keyPair.getPubKey();
		PrivKey privKey = keyPair.getPrivKey();
		SignatureDigest signatureDigest = signatureFunction.sign(privKey, data);

		assertTrue(signatureFunction.verify(signatureDigest, pubKey, data));
	}

	@Test
	public void supportPrivKeyTest() {

		CryptoAlgorithm algorithm = Crypto.getAlgorithm("ed25519");
		assertNotNull(algorithm);

		SignatureFunction signatureFunction = Crypto.getSignatureFunction(algorithm);

		AsymmetricKeypair keyPair = signatureFunction.generateKeypair();

		PrivKey privKey = keyPair.getPrivKey();
		byte[] privKeyBytes = privKey.toBytes();

		assertTrue(signatureFunction.supportPrivKey(privKeyBytes));

		algorithm = Crypto.getAlgorithm("ripemd160");
		assertNotNull(algorithm);
		byte[] algoBytes = CryptoAlgorithm.getCodeBytes(algorithm);
		byte[] pubKeyTypeBytes = new byte[] { PUBLIC.CODE };
		byte[] rawKeyBytes = privKey.getRawKeyBytes();
		byte[] ripemd160PubKeyBytes = BytesUtils.concat(algoBytes, pubKeyTypeBytes, rawKeyBytes);

		assertFalse(signatureFunction.supportPrivKey(ripemd160PubKeyBytes));
	}

	@Test
	public void resolvePrivKeyTest() {

		CryptoAlgorithm algorithm = Crypto.getAlgorithm("ed25519");
		assertNotNull(algorithm);

		SignatureFunction signatureFunction = Crypto.getSignatureFunction(algorithm);

		AsymmetricKeypair keyPair = signatureFunction.generateKeypair();

		PrivKey privKey = keyPair.getPrivKey();
		byte[] privKeyBytes = privKey.toBytes();

		PrivKey resolvedPrivKey = signatureFunction.resolvePrivKey(privKeyBytes);

		assertEquals(PRIVATE.CODE, resolvedPrivKey.getKeyType().CODE);
		assertEquals(32, resolvedPrivKey.getRawKeyBytes().length);
		assertEquals(ClassicAlgorithm.ED25519.code(), resolvedPrivKey.getAlgorithm());
		assertEquals((short) (SIGNATURE_ALGORITHM | ASYMMETRIC_KEY | ((byte) 21 & 0x00FF)),
				resolvedPrivKey.getAlgorithm());
		assertArrayEquals(privKeyBytes, resolvedPrivKey.toBytes());

		algorithm = Crypto.getAlgorithm("ripemd160");
		assertNotNull(algorithm);
		byte[] algoBytes = CryptoAlgorithm.getCodeBytes(algorithm);
		byte[] pubKeyTypeBytes = new byte[] { PUBLIC.CODE };
		byte[] rawKeyBytes = privKey.getRawKeyBytes();
		byte[] ripemd160PubKeyBytes = BytesUtils.concat(algoBytes, pubKeyTypeBytes, rawKeyBytes);

		Class<?> expectedException = CryptoException.class;
		Exception actualEx = null;
		try {
			signatureFunction.resolvePrivKey(ripemd160PubKeyBytes);
		} catch (Exception e) {
			actualEx = e;
		}
		assertNotNull(actualEx);
		assertTrue(expectedException.isAssignableFrom(actualEx.getClass()));
	}

	@Test
	public void supportPubKeyTest() {

		CryptoAlgorithm algorithm = Crypto.getAlgorithm("ed25519");
		assertNotNull(algorithm);

		SignatureFunction signatureFunction = Crypto.getSignatureFunction(algorithm);

		AsymmetricKeypair keyPair = signatureFunction.generateKeypair();

		PubKey pubKey = keyPair.getPubKey();
		byte[] pubKeyBytes = pubKey.toBytes();

		assertTrue(signatureFunction.supportPubKey(pubKeyBytes));

		algorithm = Crypto.getAlgorithm("ripemd160");
		assertNotNull(algorithm);
		byte[] algoBytes = CryptoAlgorithm.getCodeBytes(algorithm);
		byte[] privKeyTypeBytes = new byte[] { PRIVATE.CODE };
		byte[] rawKeyBytes = pubKey.getRawKeyBytes();
		byte[] ripemd160PrivKeyBytes = BytesUtils.concat(algoBytes, privKeyTypeBytes, rawKeyBytes);

		assertFalse(signatureFunction.supportPubKey(ripemd160PrivKeyBytes));
	}

	@Test
	public void resolvePubKeyTest() {

		CryptoAlgorithm algorithm = Crypto.getAlgorithm("ed25519");
		assertNotNull(algorithm);

		SignatureFunction signatureFunction = Crypto.getSignatureFunction(algorithm);

		AsymmetricKeypair keyPair = signatureFunction.generateKeypair();

		PubKey pubKey = keyPair.getPubKey();
		byte[] pubKeyBytes = pubKey.toBytes();

		PubKey resolvedPubKey = signatureFunction.resolvePubKey(pubKeyBytes);

		assertEquals(PUBLIC.CODE, resolvedPubKey.getKeyType().CODE);
		assertEquals(32, resolvedPubKey.getRawKeyBytes().length);
		assertEquals(ClassicAlgorithm.ED25519.code(), resolvedPubKey.getAlgorithm());
		assertEquals((short) (SIGNATURE_ALGORITHM | ASYMMETRIC_KEY | ((byte) 21 & 0x00FF)),
				resolvedPubKey.getAlgorithm());
		assertArrayEquals(pubKeyBytes, resolvedPubKey.toBytes());

		algorithm = Crypto.getAlgorithm("ripemd160");
		assertNotNull(algorithm);
		byte[] algoBytes = CryptoAlgorithm.getCodeBytes(algorithm);
		byte[] privKeyTypeBytes = new byte[] { PRIVATE.CODE };
		byte[] rawKeyBytes = pubKey.getRawKeyBytes();
		byte[] ripemd160PrivKeyBytes = BytesUtils.concat(algoBytes, privKeyTypeBytes, rawKeyBytes);

		Class<?> expectedException = CryptoException.class;
		Exception actualEx = null;
		try {
			signatureFunction.resolvePrivKey(ripemd160PrivKeyBytes);
		} catch (Exception e) {
			actualEx = e;
		}
		assertNotNull(actualEx);
		assertTrue(expectedException.isAssignableFrom(actualEx.getClass()));
	}

	@Test
	public void supportDigestTest() {

		byte[] data = new byte[1024];
		Random random = new Random();
		random.nextBytes(data);

		CryptoAlgorithm algorithm = Crypto.getAlgorithm("ed25519");
		assertNotNull(algorithm);

		SignatureFunction signatureFunction = Crypto.getSignatureFunction(algorithm);

		AsymmetricKeypair keyPair = signatureFunction.generateKeypair();

		PrivKey privKey = keyPair.getPrivKey();

		SignatureDigest signatureDigest = signatureFunction.sign(privKey, data);

		byte[] signatureDigestBytes = signatureDigest.toBytes();
		assertTrue(signatureFunction.supportDigest(signatureDigestBytes));

		algorithm = Crypto.getAlgorithm("ripemd160");
		assertNotNull(algorithm);
		byte[] algoBytes = CryptoAlgorithm.getCodeBytes(algorithm);
		byte[] rawDigestBytes = signatureDigest.toBytes();
		byte[] ripemd160SignatureBytes = BytesUtils.concat(algoBytes, rawDigestBytes);

		assertFalse(signatureFunction.supportDigest(ripemd160SignatureBytes));
	}

	@Test
	public void resolveDigestTest() {

		byte[] data = new byte[1024];
		Random random = new Random();
		random.nextBytes(data);

		CryptoAlgorithm algorithm = Crypto.getAlgorithm("ed25519");
		assertNotNull(algorithm);

		SignatureFunction signatureFunction = Crypto.getSignatureFunction(algorithm);

		AsymmetricKeypair keyPair = signatureFunction.generateKeypair();

		PrivKey privKey = keyPair.getPrivKey();

		SignatureDigest signatureDigest = signatureFunction.sign(privKey, data);

		byte[] signatureDigestBytes = signatureDigest.toBytes();

		SignatureDigest resolvedSignatureDigest = signatureFunction.resolveDigest(signatureDigestBytes);

		assertEquals(64, resolvedSignatureDigest.getRawDigest().length);
		assertEquals(ClassicAlgorithm.ED25519.code(), resolvedSignatureDigest.getAlgorithm());
		assertEquals((short) (SIGNATURE_ALGORITHM | ASYMMETRIC_KEY | ((byte) 21 & 0x00FF)),
				resolvedSignatureDigest.getAlgorithm());
		assertArrayEquals(signatureDigestBytes, resolvedSignatureDigest.toBytes());

		algorithm = Crypto.getAlgorithm("ripemd160");
		assertNotNull(algorithm);
		byte[] algoBytes = CryptoAlgorithm.getCodeBytes(algorithm);
		byte[] rawDigestBytes = signatureDigest.getRawDigest();
		byte[] ripemd160SignatureDigestBytes = BytesUtils.concat(algoBytes, rawDigestBytes);

		Class<?> expectedException = CryptoException.class;
		Exception actualEx = null;
		try {
			signatureFunction.resolveDigest(ripemd160SignatureDigestBytes);
		} catch (Exception e) {
			actualEx = e;
		}
		assertNotNull(actualEx);
		assertTrue(expectedException.isAssignableFrom(actualEx.getClass()));
	}
}
