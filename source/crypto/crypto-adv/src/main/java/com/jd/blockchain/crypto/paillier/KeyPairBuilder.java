package com.jd.blockchain.crypto.paillier;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;
/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Hendrik Kunert
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

/**
 * A class that is used for generating a pair of associated public and private
 * keys.
 *
 * @see KeyPair
 */
public class KeyPairBuilder {

    private int bits = 1024;

    private int certainty = 0;

    private Random rng;

    private BigInteger upperBound;

    /**
     * Sets the size of the key to be created.
     * <p>
     * The default size is 1024 bits.
     *
     * @param bits The size of the key in bits.
     * @return This instance of KeyPairBuilder for method chaining.
     */
    public KeyPairBuilder bits(int bits) {
        this.bits = bits;
        return this;
    }

    /**
     * See {@link BigInteger#BigInteger(int, int, Random)} for more details.
     * <p>
     * The default value is 0.
     *
     * @return This instance of KeyPairBuilder for method chaining.
     */
    public KeyPairBuilder certainty(int certainty) {
        this.certainty = certainty;
        return this;
    }

    /**
     * Sets the random number generator that is used for the generation of
     * internally needed prime numbers.
     * <p>
     * The default is {@link SecureRandom}.
     * <p>
     * <b>Warning:</b>
     * The change of this value affects the security of the whole cryptographic
     * system.
     *
     * @param rng The random number generator that should be used instead of
     *            {@link SecureRandom}.
     * @return This instance of KeyPairBuilder for method chaining.
     */
    public KeyPairBuilder randomNumberGenerator(Random rng) {
        this.rng = rng;
        return this;
    }

    /**
     * Sets an upper bound that is used for decrypting ciphertexts representing a negative value.
     * <p>
     * In most cases the upper bound should be the same as of the underlying number system -
     * for example {@link Integer#MAX_VALUE}.
     *
     * @param b The upper bound.
     * @return This instance of KeyPairBuilder for method chaining.
     */
    public KeyPairBuilder upperBound(BigInteger b) {
        this.upperBound = b;
        return this;
    }

    /**
     * Creates a pair of associated public and private keys.
     *
     * @return The pair of associated public and private keys.
     */
    public KeyPair generateKeyPair() {
        if (rng == null) {
            rng = new SecureRandom();
        }

        BigInteger p, q;
        int length = bits / 2;
        if (certainty > 0) {
            p = new BigInteger(length, certainty, rng);
            q = new BigInteger(length, certainty, rng);
        } else {
            p = BigInteger.probablePrime(length, rng);
            q = BigInteger.probablePrime(length, rng);
        }

        BigInteger n = p.multiply(q);
        BigInteger nSquared = n.multiply(n);

        BigInteger pMinusOne = p.subtract(BigInteger.ONE);
        BigInteger qMinusOne = q.subtract(BigInteger.ONE);

        BigInteger lambda = this.lcm(pMinusOne, qMinusOne);

        BigInteger g;
        BigInteger helper;

        do {
            g = new BigInteger(bits, rng);
            helper = calculateL(g.modPow(lambda, nSquared), n);

        } while (!helper.gcd(n).equals(BigInteger.ONE));

        PublicKey publicKey = new PublicKey(n, nSquared, g, bits);
        PrivateKey privateKey = new PrivateKey(lambda, helper.modInverse(n));

        return new KeyPair(privateKey, publicKey, upperBound);

    }

    // TODO separate this somewhere
    private BigInteger calculateL(BigInteger u, BigInteger n) {
        BigInteger result = u.subtract(BigInteger.ONE);
        result = result.divide(n);
        return result;
    }

    // TODO add to own BigInteger extended class
    private BigInteger lcm(BigInteger a, BigInteger b) {
        BigInteger result;
        BigInteger gcd = a.gcd(b);

        result = a.abs().divide(gcd);
        result = result.multiply(b.abs());

        return result;
    }
}
