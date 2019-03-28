package com.jd.blockchain.crypto.paillier;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
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

import com.jd.blockchain.utils.io.ByteArray;
import com.jd.blockchain.utils.io.BytesUtils;

/**
 * A class that represents the public part of the Paillier key pair.
 * <p>
 * As in all asymmetric cryptographic systems it is responsible for the
 * encryption.
 * <p>
 * Additional instructions for the decryption can be found on {@link KeyPair}.
 *
 * @see KeyPair
 */
public class PublicKey {
    private final int bits;
    private final BigInteger n;
    private final BigInteger nSquared;
    private final BigInteger g;

    public PublicKey(BigInteger n, BigInteger nSquared, BigInteger g, int bits) {
        this.n = n;
        this.nSquared = nSquared;
        this.bits = bits;
        this.g = g;
    }

    public PublicKey(byte[] nBytes, byte[] nSquaredBytes, byte[] gBytes, byte[] bitsBytes) {
        this.n = new BigInteger(nBytes);
        this.nSquared = new BigInteger(nSquaredBytes);
        this.g = new BigInteger(gBytes);
        this.bits = PaillierUtils.bytesToInt(bitsBytes);
    }

    public PublicKey(byte[] pubKeyBytes){
        List<byte[]> list = PaillierUtils.split(pubKeyBytes, "##PublicKey##".getBytes());
        this.n = new BigInteger(list.get(0));
        this.nSquared = new BigInteger(list.get(1));
        this.g = new BigInteger(list.get(2));
        this.bits = PaillierUtils.bytesToInt(list.get(3));
    }


    public int getBits() {
        return bits;
    }

    public BigInteger getN() {
        return n;
    }

    public BigInteger getnSquared() {
        return nSquared;
    }

    public BigInteger getG() {
        return g;
    }

    /**
     * Encrypts the given plaintext.
     *
     * @param m The plaintext that should be encrypted.
     * @return The corresponding ciphertext.
     */
    public final BigInteger encrypt(BigInteger m) {

        BigInteger r;
        do {
            r = new BigInteger(bits, new Random());
        } while (r.compareTo(n) >= 0);

        BigInteger result = g.modPow(m, nSquared);
        BigInteger x = r.modPow(n, nSquared);

        result = result.multiply(x);
        result = result.mod(nSquared);

        return result;
    }

    public byte[] getBitsBytes(){
        return PaillierUtils.intToBytes(bits);
    }

    public byte[] getNBytes(){ return n.toByteArray(); }

    public byte[] getNSquaredBytes(){
        return nSquared.toByteArray();
    }

    public byte[] getGBytes(){
        return g.toByteArray();
    }

    public byte[] getPubKeyBytes(){
        return BytesUtils.concat(getNBytes(),"##PublicKey##".getBytes(),getNSquaredBytes(),"##PublicKey##".getBytes(),getGBytes(),"##PublicKey##".getBytes(),getBitsBytes());
    }
}

