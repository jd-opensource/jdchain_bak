package test.com.jd.blockchain.crypto.service.classic;

import static com.jd.blockchain.crypto.CryptoAlgorithm.ENCRYPTION_ALGORITHM;
import static com.jd.blockchain.crypto.CryptoAlgorithm.SYMMETRIC_KEY;
import static com.jd.blockchain.crypto.CryptoKeyType.PUBLIC;
import static com.jd.blockchain.crypto.CryptoKeyType.SYMMETRIC;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;

import com.jd.blockchain.crypto.Ciphertext;
import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.CryptoException;
import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.crypto.SymmetricEncryptionFunction;
import com.jd.blockchain.crypto.SymmetricKey;
import com.jd.blockchain.crypto.service.classic.ClassicAlgorithm;
import com.jd.blockchain.utils.io.BytesUtils;

/**
 * @author zhanglin33
 * @title: AESEncryptionFunctionTest
 * @description: JunitTest for AESAESEncryptionFunction in SPI mode
 * @date 2019-04-01, 13:57
 */
public class AESEncryptionFunctionTest {

	@Test
	public void getAlgorithmTest() {
		CryptoAlgorithm algorithm = Crypto.getAlgorithm("aes");
		assertNotNull(algorithm);

		SymmetricEncryptionFunction symmetricEncryptionFunction = Crypto
				.getSymmetricEncryptionFunction(algorithm);

		assertEquals(symmetricEncryptionFunction.getAlgorithm().name(), algorithm.name());
		assertEquals(symmetricEncryptionFunction.getAlgorithm().code(), algorithm.code());

		algorithm = Crypto.getAlgorithm("AES");
		assertNotNull(algorithm);

		assertEquals(symmetricEncryptionFunction.getAlgorithm().name(), algorithm.name());
		assertEquals(symmetricEncryptionFunction.getAlgorithm().code(), algorithm.code());

		algorithm = Crypto.getAlgorithm("aess");
		assertNull(algorithm);
	}

	@Test
	public void generateSymmetricKeyTest() {
		CryptoAlgorithm algorithm = Crypto.getAlgorithm("aes");
		assertNotNull(algorithm);

		SymmetricEncryptionFunction symmetricEncryptionFunction = Crypto
				.getSymmetricEncryptionFunction(algorithm);

		SymmetricKey symmetricKey = (SymmetricKey) symmetricEncryptionFunction.generateSymmetricKey();

		assertEquals(SYMMETRIC.CODE, symmetricKey.getKeyType().CODE);
		assertEquals(128 / 8, symmetricKey.getRawKeyBytes().length);

		assertEquals(algorithm.code(), symmetricKey.getAlgorithm());

		assertEquals(2 + 1 + 128 / 8, symmetricKey.toBytes().length);
		byte[] algoBytes = CryptoAlgorithm.getCodeBytes(algorithm);
		byte[] keyTypeBytes = new byte[] { SYMMETRIC.CODE };
		byte[] rawKeyBytes = symmetricKey.getRawKeyBytes();
		assertArrayEquals(BytesUtils.concat(algoBytes, keyTypeBytes, rawKeyBytes), symmetricKey.toBytes());
	}

	@Test
	public void encryptTest() {

		byte[] data = new byte[1024];
		Random random = new Random();
		random.nextBytes(data);

		CryptoAlgorithm algorithm = Crypto.getAlgorithm("aes");
		assertNotNull(algorithm);

		SymmetricEncryptionFunction symmetricEncryptionFunction = Crypto
				.getSymmetricEncryptionFunction(algorithm);

		SymmetricKey symmetricKey = (SymmetricKey) symmetricEncryptionFunction.generateSymmetricKey();

		Ciphertext ciphertext = symmetricEncryptionFunction.encrypt(symmetricKey, data);

		byte[] ciphertextBytes = ciphertext.toBytes();
		assertEquals(2 + 16 + 1024, ciphertextBytes.length);
		assertEquals(ClassicAlgorithm.AES.code(), ciphertext.getAlgorithm());
		assertEquals((short) (ENCRYPTION_ALGORITHM | SYMMETRIC_KEY | ((byte) 26 & 0x00FF)), ciphertext.getAlgorithm());

		byte[] algoBytes = BytesUtils.toBytes(ciphertext.getAlgorithm());
		byte[] rawCiphertextBytes = ciphertext.getRawCiphertext();
		assertArrayEquals(BytesUtils.concat(algoBytes, rawCiphertextBytes), ciphertextBytes);
	}

	@Test
	public void decryptTest() {

		byte[] data = new byte[1024];
		Random random = new Random();
		random.nextBytes(data);

		CryptoAlgorithm algorithm = Crypto.getAlgorithm("aes");
		assertNotNull(algorithm);

		SymmetricEncryptionFunction symmetricEncryptionFunction = Crypto
				.getSymmetricEncryptionFunction(algorithm);

		SymmetricKey symmetricKey = (SymmetricKey) symmetricEncryptionFunction.generateSymmetricKey();

		Ciphertext ciphertext = symmetricEncryptionFunction.encrypt(symmetricKey, data);

		byte[] decryptedPlaintext = symmetricEncryptionFunction.decrypt(symmetricKey, ciphertext);

		assertArrayEquals(data, decryptedPlaintext);
	}

	// @Test
	// public void streamEncryptTest(){
	//
	// byte[] data = new byte[1024];
	// Random random = new Random();
	// random.nextBytes(data);
	//
	//
	// InputStream inputStream = new ByteArrayInputStream(data);
	// OutputStream outputStream = new ByteArrayOutputStream();
	//
	// CryptoAlgorithm algorithm = CryptoServiceProviders.getAlgorithm("aes");
	// assertNotNull(algorithm);
	//
	// SymmetricEncryptionFunction symmetricEncryptionFunction =
	// CryptoServiceProviders.getSymmetricEncryptionFunction(algorithm);
	//
	// SymmetricKey symmetricKey = (SymmetricKey)
	// symmetricEncryptionFunction.generateSymmetricKey();
	//
	// symmetricEncryptionFunction.encrypt(symmetricKey,inputStream,outputStream);
	//
	// assertNotNull(outputStream);
	//
	//
	// }

	@Test
	public void supportSymmetricKeyTest() {

		CryptoAlgorithm algorithm = Crypto.getAlgorithm("aes");
		assertNotNull(algorithm);

		SymmetricEncryptionFunction symmetricEncryptionFunction = Crypto
				.getSymmetricEncryptionFunction(algorithm);

		SymmetricKey symmetricKey = (SymmetricKey) symmetricEncryptionFunction.generateSymmetricKey();
		byte[] symmetricKeyBytes = symmetricKey.toBytes();

		assertTrue(symmetricEncryptionFunction.supportSymmetricKey(symmetricKeyBytes));

		algorithm = Crypto.getAlgorithm("ripemd160");
		assertNotNull(algorithm);
		byte[] algoBytes = CryptoAlgorithm.getCodeBytes(algorithm);
		byte[] pubKeyTypeBytes = new byte[] { PUBLIC.CODE };
		byte[] rawKeyBytes = symmetricKey.getRawKeyBytes();
		byte[] ripemd160KeyBytes = BytesUtils.concat(algoBytes, pubKeyTypeBytes, rawKeyBytes);

		assertFalse(symmetricEncryptionFunction.supportSymmetricKey(ripemd160KeyBytes));
	}

	@Test
	public void resolveSymmetricKeyTest() {

		CryptoAlgorithm algorithm = Crypto.getAlgorithm("aes");
		assertNotNull(algorithm);

		SymmetricEncryptionFunction symmetricEncryptionFunction = Crypto
				.getSymmetricEncryptionFunction(algorithm);

		SymmetricKey symmetricKey = (SymmetricKey) symmetricEncryptionFunction.generateSymmetricKey();
		byte[] symmetricKeyBytes = symmetricKey.toBytes();

		SymmetricKey resolvedKey = symmetricEncryptionFunction.resolveSymmetricKey(symmetricKeyBytes);

		assertEquals(SYMMETRIC.CODE, resolvedKey.getKeyType().CODE);
		assertEquals(128 / 8, resolvedKey.getRawKeyBytes().length);
		assertEquals(ClassicAlgorithm.AES.code(), resolvedKey.getAlgorithm());
		assertEquals((short) (ENCRYPTION_ALGORITHM | SYMMETRIC_KEY | ((byte) 26 & 0x00FF)), resolvedKey.getAlgorithm());
		assertArrayEquals(symmetricKeyBytes, resolvedKey.toBytes());

		algorithm = Crypto.getAlgorithm("ripemd160");
		assertNotNull(algorithm);
		byte[] algoBytes = CryptoAlgorithm.getCodeBytes(algorithm);
		byte[] pubKeyTypeBytes = new byte[] { PUBLIC.CODE };
		byte[] rawKeyBytes = symmetricKey.getRawKeyBytes();
		byte[] ripemd160KeyBytes = BytesUtils.concat(algoBytes, pubKeyTypeBytes, rawKeyBytes);

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
	public void supportCiphertextTest() {

		byte[] data = new byte[1024];
		Random random = new Random();
		random.nextBytes(data);

		CryptoAlgorithm algorithm = Crypto.getAlgorithm("aes");
		assertNotNull(algorithm);

		SymmetricEncryptionFunction symmetricEncryptionFunction = Crypto
				.getSymmetricEncryptionFunction(algorithm);

		SymmetricKey symmetricKey = (SymmetricKey) symmetricEncryptionFunction.generateSymmetricKey();

		Ciphertext ciphertext = symmetricEncryptionFunction.encrypt(symmetricKey, data);

		byte[] ciphertextBytes = ciphertext.toBytes();
		assertTrue(symmetricEncryptionFunction.supportCiphertext(ciphertextBytes));

		algorithm = Crypto.getAlgorithm("ripemd160");
		assertNotNull(algorithm);
		byte[] algoBytes = CryptoAlgorithm.getCodeBytes(algorithm);
		byte[] rawCiphertextBytes = ciphertext.toBytes();
		byte[] ripemd160CiphertextBytes = BytesUtils.concat(algoBytes, rawCiphertextBytes);

		assertFalse(symmetricEncryptionFunction.supportCiphertext(ripemd160CiphertextBytes));
	}

	@Test
	public void resolveCiphertextTest() {

		byte[] data = new byte[1024];
		Random random = new Random();
		random.nextBytes(data);

		CryptoAlgorithm algorithm = Crypto.getAlgorithm("aes");
		assertNotNull(algorithm);

		SymmetricEncryptionFunction symmetricEncryptionFunction = Crypto
				.getSymmetricEncryptionFunction(algorithm);

		SymmetricKey symmetricKey = (SymmetricKey) symmetricEncryptionFunction.generateSymmetricKey();

		Ciphertext ciphertext = symmetricEncryptionFunction.encrypt(symmetricKey, data);

		byte[] ciphertextBytes = ciphertext.toBytes();

		Ciphertext resolvedCiphertext = symmetricEncryptionFunction.resolveCiphertext(ciphertextBytes);

		assertEquals(1024 + 16, resolvedCiphertext.getRawCiphertext().length);
		assertEquals(ClassicAlgorithm.AES.code(), resolvedCiphertext.getAlgorithm());
		assertEquals((short) (ENCRYPTION_ALGORITHM | SYMMETRIC_KEY | ((byte) 26 & 0x00FF)),
				resolvedCiphertext.getAlgorithm());
		assertArrayEquals(ciphertextBytes, resolvedCiphertext.toBytes());

		assertArrayEquals(ciphertext.toBytes(), resolvedCiphertext.toBytes());
		assertArrayEquals(ciphertext.getRawCiphertext(), resolvedCiphertext.getRawCiphertext());
		assertEquals(ciphertext.getAlgorithm(), resolvedCiphertext.getAlgorithm());

		algorithm = Crypto.getAlgorithm("ripemd160");
		assertNotNull(algorithm);
		byte[] algoBytes = CryptoAlgorithm.getCodeBytes(algorithm);
		byte[] rawCiphertextBytes = ciphertext.getRawCiphertext();
		byte[] ripemd160CiphertextBytes = BytesUtils.concat(algoBytes, rawCiphertextBytes);

		Class<?> expectedException = CryptoException.class;
		Exception actualEx = null;
		try {
			symmetricEncryptionFunction.resolveCiphertext(ripemd160CiphertextBytes);
		} catch (Exception e) {
			actualEx = e;
		}
		assertNotNull(actualEx);
		assertTrue(expectedException.isAssignableFrom(actualEx.getClass()));
	}
}
