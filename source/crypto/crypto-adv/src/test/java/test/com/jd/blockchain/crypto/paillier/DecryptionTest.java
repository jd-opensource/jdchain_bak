package test.com.jd.blockchain.crypto.paillier;

import com.jd.blockchain.crypto.paillier.KeyPair;
import com.jd.blockchain.crypto.paillier.KeyPairBuilder;
import com.jd.blockchain.crypto.paillier.PublicKey;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

/**
 * Created by kunerd on 22.09.15.
 */
@RunWith(value = Parameterized.class)
public class DecryptionTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(createTestParameter(Long.MIN_VALUE),
                createTestParameter(Integer.MIN_VALUE),
                createTestParameter(Short.MIN_VALUE),
                createTestParameter(0),
                createTestParameter(Short.MAX_VALUE),
                createTestParameter(Integer.MAX_VALUE),
                createTestParameter(Long.MAX_VALUE));
    }

    private BigInteger input;
    private BigInteger expected;

    public DecryptionTest(BigInteger input, BigInteger expected) {
        this.input = input;
        this.expected = expected;
    }

    @Test
    public void test() {
        KeyPair keyPair = new KeyPairBuilder().upperBound(BigInteger.valueOf(Long.MAX_VALUE))
                .generateKeyPair();
        PublicKey publicKey = keyPair.getPublicKey();

        BigInteger encryptedData = publicKey.encrypt(input);

        assertEquals(expected, keyPair.decrypt(encryptedData));
    }

    private static Object[] createTestParameter(long plaintext) {
        BigInteger p = BigInteger.valueOf(plaintext);
        return new Object[]{p, p};
    }

}

