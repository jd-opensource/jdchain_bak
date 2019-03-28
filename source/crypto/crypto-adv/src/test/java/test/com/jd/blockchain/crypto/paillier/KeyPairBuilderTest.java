package test.com.jd.blockchain.crypto.paillier;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.jd.blockchain.crypto.paillier.KeyPair;
import com.jd.blockchain.crypto.paillier.KeyPairBuilder;
import com.jd.blockchain.crypto.paillier.PrivateKey;
import com.jd.blockchain.crypto.paillier.PublicKey;

@RunWith(PowerMockRunner.class)
@PrepareForTest(KeyPairBuilder.class)
public class KeyPairBuilderTest {

    private static final int BITS = 128;

    private KeyPairBuilder keygen;
    private PublicKey publicKey;
    private PrivateKey privateKey;

    private BigInteger p = BigInteger.valueOf(5);
    private BigInteger q = BigInteger.valueOf(7);
    private BigInteger g1 = BigInteger.valueOf(35);
    private BigInteger g2 = BigInteger.valueOf(36);

    private Random rng;

    @Before
    public void beforeEach() {
        rng = PowerMockito.mock(SecureRandom.class);

        keygen = new KeyPairBuilder()
                .bits(BITS)
                .randomNumberGenerator(rng);

        PowerMockito.mockStatic(BigInteger.class);
    }

    private void prepareTest() throws Exception {

        PowerMockito.when(BigInteger.probablePrime(BITS / 2, rng)).thenReturn(p, q);

        PowerMockito.whenNew(BigInteger.class).withArguments(BITS, rng).thenReturn(g1, g2);

        KeyPair keypair = keygen.generateKeyPair();

        publicKey = keypair.getPublicKey();
        privateKey = keypair.getPrivateKey();
    }

    @Test
    public void computationOfN() throws Exception {
        prepareTest();

        BigInteger e = p.multiply(q);
        BigInteger a = publicKey.getN();

        assertEquals(e, a);
    }


    @Test
    public void computationOfLambda() throws Exception {
        BigInteger e = new BigInteger("12");

        prepareTest();

        BigInteger a = privateKey.getLambda();

        assertEquals(e, a);
    }

    @Test
    public void computationOfG() throws Exception {
        prepareTest();

        PowerMockito.verifyNew(BigInteger.class, Mockito.times(2)).withArguments(Mockito.eq(128), Mockito.any(Random.class));
    }

    @Test
    public void withoutCertainty() throws Exception {
        prepareTest();

        PowerMockito.verifyStatic(Mockito.times(2));
        BigInteger.probablePrime(BITS / 2, rng);

    }

    @Test
    public void withCertainty() throws Exception {
        int certainty = 6;

        keygen.certainty(certainty);

        PowerMockito.whenNew(BigInteger.class).withArguments(BITS / 2, certainty, rng).thenReturn(p, q);

        prepareTest();

        PowerMockito.verifyNew(BigInteger.class, Mockito.times(2)).withArguments(BITS / 2, certainty, rng);
    }

}
