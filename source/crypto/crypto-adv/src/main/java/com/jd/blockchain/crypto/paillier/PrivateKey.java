package com.jd.blockchain.crypto.paillier;

import java.math.BigInteger;
import java.util.List;
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

import com.jd.blockchain.utils.io.BytesUtils;

/**
 * A class that represents the private part of the Paillier key pair.
 */
public class PrivateKey {

    private final BigInteger lambda;
    private final BigInteger preCalculatedDenominator;

    public PrivateKey(BigInteger lambda, BigInteger preCalculatedDenominator) {
        this.lambda = lambda;

        this.preCalculatedDenominator = preCalculatedDenominator;
    }

    public PrivateKey(byte[] lambdaBytes, byte[] preCalculatedDenominatorBytes){
        this.lambda = new BigInteger(lambdaBytes);
        this.preCalculatedDenominator = new BigInteger(preCalculatedDenominatorBytes);
    }

    public PrivateKey(byte[] privKeyBytes){
        List<byte[]> list = PaillierUtils.split(privKeyBytes, "##PrivateKey##".getBytes());
        this.lambda = new BigInteger(list.get(0));
        this.preCalculatedDenominator = new BigInteger(list.get(1));
    }

    public BigInteger getLambda() {
        return lambda;
    }

    public BigInteger getPreCalculatedDenominator() {
        return preCalculatedDenominator;
    }

    public byte[] getLambdaBytes(){
        return lambda.toByteArray();
    }

    public byte[] getPreCalculatedDenominatorBytes(){
        return preCalculatedDenominator.toByteArray();
    }

    public byte[] getPrivKeyBytes(){
        return BytesUtils.concat(getLambdaBytes(),"##PrivateKey##".getBytes(),getPreCalculatedDenominatorBytes());
    }
}
