package test.com.jd.blockchain.crypto.paillier;

import com.jd.blockchain.crypto.paillier.KeyPair;
import com.jd.blockchain.crypto.paillier.KeyPairBuilder;
import com.jd.blockchain.crypto.paillier.PublicKey;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;

public class HomomorphicPropertiesTest {
    private KeyPair keypair;
    private PublicKey publicKey;

    @Before
    public void init() {
        KeyPairBuilder keygen = new KeyPairBuilder();
        this.keypair = keygen.generateKeyPair();
        this.publicKey = keypair.getPublicKey();
    }

    @Test
    public void testHomomorphicAddition() {
        BigInteger plainA = BigInteger.valueOf(102);
        BigInteger plainB = BigInteger.valueOf(203);

        BigInteger encryptedA = publicKey.encrypt(plainA);
        BigInteger encryptedB = publicKey.encrypt(plainB);

        BigInteger decryptedProduct = keypair.decrypt(encryptedA.multiply(
                encryptedB).mod(publicKey.getnSquared()));
        BigInteger plainSum = plainA.add(plainB).mod(publicKey.getN());

        assertEquals(decryptedProduct, plainSum);
    }

    @Test
    public void testHomomorphicConstantMultiplication() {
        BigInteger plainA = BigInteger.valueOf(14);
        BigInteger plainB = BigInteger.valueOf(203);

        BigInteger encryptedA = publicKey.encrypt(plainA);

        BigInteger decryptedPow = keypair.decrypt(encryptedA.modPow(plainB,
                publicKey.getnSquared()));
        BigInteger plainSum = plainA.multiply(plainB).mod(publicKey.getN());

        assertEquals(decryptedPow, plainSum);
    }

    @Test
    public void testHomomorphicMultiplication() {
        BigInteger plainA = BigInteger.valueOf(23);
        BigInteger plainB = BigInteger.valueOf(234);

        BigInteger encryptedA = publicKey.encrypt(plainA);
        BigInteger decryptedPowA = keypair.decrypt(encryptedA.modPow(
                plainB, publicKey.getnSquared()));
        BigInteger plainSumA = plainA.multiply(plainB).mod(publicKey.getN());

        assertEquals(decryptedPowA, plainSumA);

        BigInteger encryptedB = publicKey.encrypt(plainB);
        BigInteger decryptedPowB = keypair.decrypt(encryptedB.modPow(
                plainA, publicKey.getnSquared()));
        BigInteger plainSumB = plainA.multiply(plainB).mod(publicKey.getN());

        assertEquals(decryptedPowB, plainSumB);

        assertEquals(decryptedPowA, decryptedPowB);
    }

    @Test
    public void testHomomorphicMultiplicationPowG() {
        BigInteger plainA = BigInteger.valueOf(230);
        BigInteger plainB = BigInteger.valueOf(100);

        BigInteger g = publicKey.getG();

        BigInteger encryptedA = publicKey.encrypt(plainA);
        BigInteger decryptedPow = keypair.decrypt(encryptedA.multiply(g.modPow(
                plainB, publicKey.getnSquared()).mod(publicKey.getnSquared())));

        BigInteger plainSumA = plainA.add(plainB).mod(publicKey.getN());

        assertEquals(decryptedPow, plainSumA);
    }
}
