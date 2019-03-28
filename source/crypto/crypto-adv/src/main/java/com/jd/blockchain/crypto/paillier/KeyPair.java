package com.jd.blockchain.crypto.paillier;

import java.math.BigInteger;
import java.util.List;

import com.jd.blockchain.utils.io.BytesUtils;

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
 * A class that holds a pair of associated public and private keys.
 */
public class KeyPair {
    private final PrivateKey privateKey;
    private final PublicKey publicKey;
    private final BigInteger upperBound;

    public KeyPair(PrivateKey privateKey, PublicKey publicKey, BigInteger upperBound) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.upperBound = upperBound;
    }

    public KeyPair(byte[] privKeyBytes,byte[] pubKeyBytes,byte[] upperBoundBytes){
        this.privateKey = new PrivateKey(privKeyBytes);
        this.publicKey = new PublicKey(pubKeyBytes);
        this.upperBound = new BigInteger(upperBoundBytes);
    }

    public KeyPair(byte[] keyPairBytes){
        List<byte[]> list = PaillierUtils.split(keyPairBytes, "##KeyPair##".getBytes());
        this.privateKey = new PrivateKey(list.get(0));
        this.publicKey = new PublicKey(list.get(1));
        this.upperBound = new BigInteger(list.get(2));
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public BigInteger getUpperBound() { return upperBound; }

    /**
     * Decrypts the given ciphertext.
     *
     * @param c The ciphertext that should be decrypted.
     * @return The corresponding plaintext. If an upper bound was given to {@link KeyPairBuilder},
     * the result can also be negative. See {@link KeyPairBuilder#upperBound(BigInteger)} for details.
     */
    public final BigInteger decrypt(BigInteger c) {

        BigInteger n = publicKey.getN();
        BigInteger nSquare = publicKey.getnSquared();
        BigInteger lambda = privateKey.getLambda();

        BigInteger u = privateKey.getPreCalculatedDenominator();

        BigInteger p = c.modPow(lambda, nSquare).subtract(BigInteger.ONE).divide(n).multiply(u).mod(n);

        if (upperBound != null && p.compareTo(upperBound) > 0) {
            p = p.subtract(n);
        }

        return p;
    }

    public byte[] getUpperBoundBytes(){ return upperBound.toByteArray(); }

    public byte[] getKeyPairBytes(){
        return BytesUtils.concat(privateKey.getPrivKeyBytes(),"##KeyPair##".getBytes(),publicKey.getPubKeyBytes(),"##KeyPair##".getBytes(),upperBound.toByteArray());
    }
}
