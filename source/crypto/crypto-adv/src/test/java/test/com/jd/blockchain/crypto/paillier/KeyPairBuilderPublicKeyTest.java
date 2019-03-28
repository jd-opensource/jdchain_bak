package test.com.jd.blockchain.crypto.paillier;

import com.jd.blockchain.crypto.paillier.KeyPair;
import com.jd.blockchain.crypto.paillier.KeyPairBuilder;
import com.jd.blockchain.crypto.paillier.PrivateKey;
import com.jd.blockchain.crypto.paillier.PublicKey;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;

public class KeyPairBuilderPublicKeyTest {

    private KeyPair keypair;
    private PublicKey publicKey;

    @Before
    public void init() {
        KeyPairBuilder keygen = new KeyPairBuilder();
        this.keypair = keygen.generateKeyPair();
        this.publicKey = keypair.getPublicKey();
    }

    @Test
    public void testBitsSetup() {
        int BITS = 1024;
        assertEquals(BITS, publicKey.getBits());
    }

    @Test
    public void testCalculationOfNSquared() {

        BigInteger n = publicKey.getN();
        BigInteger nSquared = n.multiply(n);

        assertEquals(nSquared, publicKey.getnSquared());
    }

    @Test
    public void testCalculationOfGOfG() {
        PrivateKey privateKey = keypair.getPrivateKey();

        BigInteger n = publicKey.getN();
        BigInteger nSquared = publicKey.getnSquared();
        BigInteger g = publicKey.getG();
        BigInteger lambda = privateKey.getLambda();

        BigInteger l = g.modPow(lambda, nSquared);
        l = l.subtract(BigInteger.ONE);
        l = l.divide(n);

        assertEquals(BigInteger.ONE, l.gcd(n));
    }

}
