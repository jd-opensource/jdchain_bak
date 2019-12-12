package com.jd.blockchain.crypto.paillier;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.math.Primes;
import org.bouncycastle.util.BigIntegers;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * @author zhanglin33
 * @title: PaillierKeyPairGenerator
 * @description: generator of paillier key pair
 * @date 2019-04-30, 14:48
 */
public class PaillierKeyPairGenerator {

    private static final int STRENGTH = 2048;

    public AsymmetricCipherKeyPair generateKeyPair() {

        int pLength = (STRENGTH + 1) / 2;
        int qLength = STRENGTH - pLength;

        BigInteger p;
        BigInteger q;
        BigInteger n;

        do {
            do {
                SecureRandom pRandom = new SecureRandom();
                p = BigIntegers.createRandomPrime(pLength, 1, pRandom);
            } while (!isProbablePrime(p));
            do {
                SecureRandom qRandom = new SecureRandom();
                q = BigIntegers.createRandomPrime(qLength, 1, qRandom);
            } while (q.equals(p) || !isProbablePrime(p));
            n = q.multiply(p);
        } while (n.bitLength() != STRENGTH);

        return new AsymmetricCipherKeyPair(new PaillierPublicKeyParameters(n), new PaillierPrivateKeyParameters(p,q));
    }

    // Primes class for FIPS 186-4 C.3 primality checking
    private boolean isProbablePrime(BigInteger x)
    {
        int iterations = 3;
        SecureRandom random = new SecureRandom();
        return !Primes.hasAnySmallFactors(x) && Primes.isMRProbablePrime(x, random, iterations);
    }
}
