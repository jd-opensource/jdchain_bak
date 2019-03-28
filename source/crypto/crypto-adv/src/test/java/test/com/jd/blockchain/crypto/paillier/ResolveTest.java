package test.com.jd.blockchain.crypto.paillier;

import com.jd.blockchain.crypto.paillier.*;
import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;

public class ResolveTest {

    @Test
    public void testResolvePrivateKey() {
        KeyPairBuilder keygen = new KeyPairBuilder();
        KeyPair keyPair = keygen.generateKeyPair();

        PrivateKey privKey = keyPair.getPrivateKey();
        BigInteger lambda = privKey.getLambda();
        BigInteger preCalculatedDenominator = privKey.getPreCalculatedDenominator();

        byte[] privKeyBytes = privKey.getPrivKeyBytes();
        byte[] lambdaBytes = privKey.getLambdaBytes();
        byte[] preCalculatedDenominatorBytes = privKey.getPreCalculatedDenominatorBytes();

        assertEquals(lambda,new BigInteger(lambdaBytes));
        assertEquals(preCalculatedDenominator,new BigInteger(preCalculatedDenominatorBytes));

        assertEquals(lambda,new PrivateKey(lambda,preCalculatedDenominator).getLambda());
        assertEquals(preCalculatedDenominator,(new PrivateKey(lambda,preCalculatedDenominator)).getPreCalculatedDenominator());

        assertEquals(lambda,(new PrivateKey(lambdaBytes,preCalculatedDenominatorBytes)).getLambda());
        assertEquals(preCalculatedDenominator,(new PrivateKey(lambdaBytes,preCalculatedDenominatorBytes)).getPreCalculatedDenominator());

        assertEquals(lambda,(new PrivateKey(privKeyBytes)).getLambda());
        assertEquals(preCalculatedDenominator,(new PrivateKey(privKeyBytes)).getPreCalculatedDenominator());
    }

    @Test
    public void testResolvePublicKey() {
        KeyPairBuilder keygen = new KeyPairBuilder();
        KeyPair keyPair = keygen.generateKeyPair();

        PublicKey pubKey = keyPair.getPublicKey();
        int bits = pubKey.getBits();
        BigInteger n = pubKey.getN();
        BigInteger nSquared = pubKey.getnSquared();
        BigInteger g = pubKey.getG();

        byte[] pubKeyBytes = pubKey.getPubKeyBytes();
        byte[] bitsBytes = pubKey.getBitsBytes();
        byte[] nBytes = pubKey.getNBytes();
        byte[] nSquaredBytes = pubKey.getNSquaredBytes();
        byte[] gBytes = pubKey.getGBytes();

        assertEquals(bits,PaillierUtils.bytesToInt(bitsBytes));
        assertEquals(n,new BigInteger(nBytes));
        assertEquals(nSquared,new BigInteger(nSquaredBytes));
        assertEquals(g,new BigInteger(gBytes));

        assertEquals(bits,(new PublicKey(n,nSquared,g,bits)).getBits());
        assertEquals(n,(new PublicKey(n,nSquared,g,bits)).getN());
        assertEquals(nSquared,(new PublicKey(n,nSquared,g,bits)).getnSquared());
        assertEquals(g,(new PublicKey(n,nSquared,g,bits)).getG());

        assertEquals(bits,(new PublicKey(nBytes,nSquaredBytes,gBytes,bitsBytes)).getBits());
        assertEquals(n,(new PublicKey(nBytes,nSquaredBytes,gBytes,bitsBytes)).getN());
        assertEquals(nSquared,(new PublicKey(nBytes,nSquaredBytes,gBytes,bitsBytes)).getnSquared());
        assertEquals(g,(new PublicKey(nBytes,nSquaredBytes,gBytes,bitsBytes)).getG());

        assertEquals(bits,(new PublicKey(pubKeyBytes)).getBits());
        assertEquals(n,(new PublicKey(pubKeyBytes)).getN());
        assertEquals(nSquared,(new PublicKey(pubKeyBytes)).getnSquared());
        assertEquals(g,(new PublicKey(pubKeyBytes)).getG());
    }

    @Test
    public void testResolveKeyPair() {
        KeyPairBuilder keygen = new KeyPairBuilder();
        keygen.upperBound(new BigInteger(PaillierUtils.intToBytes(Integer.MAX_VALUE)));
        KeyPair keyPair = keygen.generateKeyPair();

        PrivateKey privKey = keyPair.getPrivateKey();
        PublicKey pubKey = keyPair.getPublicKey();
        BigInteger upperBound = keyPair.getUpperBound();

        byte[] keyPairBytes = keyPair.getKeyPairBytes();
        byte[] privKeyBytes = privKey.getPrivKeyBytes();
        byte[] pubKeyBytes = pubKey.getPubKeyBytes();
        byte[] upperBoundBytes = keyPair.getUpperBoundBytes();

        assertEquals(upperBound,keyPair.getUpperBound());
        assertEquals(privKey.getLambda(),keyPair.getPrivateKey().getLambda());
        assertEquals(privKey.getPreCalculatedDenominator(),keyPair.getPrivateKey().getPreCalculatedDenominator());
        assertEquals(pubKey.getBits(),keyPair.getPublicKey().getBits());
        assertEquals(pubKey.getN(),keyPair.getPublicKey().getN());
        assertEquals(pubKey.getnSquared(),keyPair.getPublicKey().getnSquared());
        assertEquals(pubKey.getG(),keyPair.getPublicKey().getG());

        assertEquals(upperBound,(new KeyPair(privKey,pubKey,upperBound).getUpperBound()));
        assertEquals(privKey.getLambda(),(new KeyPair(privKey,pubKey,upperBound).getPrivateKey().getLambda()));
        assertEquals(privKey.getPreCalculatedDenominator(),(new KeyPair(privKey,pubKey,upperBound).getPrivateKey().getPreCalculatedDenominator()));
        assertEquals(pubKey.getBits(),(new KeyPair(privKey,pubKey,upperBound).getPublicKey().getBits()));
        assertEquals(pubKey.getN(),(new KeyPair(privKey,pubKey,upperBound).getPublicKey().getN()));
        assertEquals(pubKey.getnSquared(),(new KeyPair(privKey,pubKey,upperBound).getPublicKey().getnSquared()));
        assertEquals(pubKey.getG(),(new KeyPair(privKey,pubKey,upperBound).getPublicKey().getG()));

        assertEquals(upperBound,(new KeyPair(privKeyBytes,pubKeyBytes,upperBoundBytes).getUpperBound()));
        assertEquals(privKey.getLambda(),(new KeyPair(privKeyBytes,pubKeyBytes,upperBoundBytes).getPrivateKey().getLambda()));
        assertEquals(privKey.getPreCalculatedDenominator(),(new KeyPair(privKeyBytes,pubKeyBytes,upperBoundBytes).getPrivateKey().getPreCalculatedDenominator()));
        assertEquals(pubKey.getBits(),(new KeyPair(privKeyBytes,pubKeyBytes,upperBoundBytes).getPublicKey().getBits()));
        assertEquals(pubKey.getN(),(new KeyPair(privKeyBytes,pubKeyBytes,upperBoundBytes).getPublicKey().getN()));
        assertEquals(pubKey.getnSquared(),(new KeyPair(privKeyBytes,pubKeyBytes,upperBoundBytes).getPublicKey().getnSquared()));
        assertEquals(pubKey.getG(),(new KeyPair(privKeyBytes,pubKeyBytes,upperBoundBytes).getPublicKey().getG()));

        assertEquals(upperBound,(new KeyPair(keyPairBytes).getUpperBound()));
        assertEquals(privKey.getLambda(),(new KeyPair(keyPairBytes).getPrivateKey().getLambda()));
        assertEquals(privKey.getPreCalculatedDenominator(),(new KeyPair(keyPairBytes).getPrivateKey().getPreCalculatedDenominator()));
        assertEquals(pubKey.getBits(),(new KeyPair(keyPairBytes).getPublicKey().getBits()));
        assertEquals(pubKey.getN(),(new KeyPair(keyPairBytes).getPublicKey().getN()));
        assertEquals(pubKey.getnSquared(),(new KeyPair(keyPairBytes).getPublicKey().getnSquared()));
        assertEquals(pubKey.getG(),(new KeyPair(keyPairBytes).getPublicKey().getG()));

    }
}
