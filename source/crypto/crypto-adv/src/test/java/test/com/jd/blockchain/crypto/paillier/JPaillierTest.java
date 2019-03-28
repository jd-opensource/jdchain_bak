package test.com.jd.blockchain.crypto.paillier;

import com.jd.blockchain.crypto.paillier.KeyPair;
import com.jd.blockchain.crypto.paillier.KeyPairBuilder;
import com.jd.blockchain.crypto.paillier.PublicKey;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class JPaillierTest {
    private KeyPair keyPair;
    private PublicKey publicKey;

    @Before
    public void init() {
        KeyPairBuilder keygen = new KeyPairBuilder();
        keyPair = keygen.generateKeyPair();
        publicKey = keyPair.getPublicKey();
    }

    @Test
    public void testEncryption() {
        BigInteger plainData = BigInteger.valueOf(10);

        BigInteger encryptedData = publicKey.encrypt(plainData);

        assertNotEquals(plainData, encryptedData);
    }

    @Test
    public void testDecyption() {
        BigInteger plainData = BigInteger.valueOf(10);

        BigInteger encryptedData = publicKey.encrypt(plainData);
        BigInteger decryptedData = keyPair.decrypt(encryptedData);

        assertEquals(plainData, decryptedData);
    }
}
