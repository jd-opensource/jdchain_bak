package com.jd.blockchain.crypto.paillier;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

import java.math.BigInteger;

import static org.bouncycastle.util.BigIntegers.ONE;

/**
 * @author zhanglin33
 * @title: PaillierPrivateKeyParameters
 * @description: parameters about Paillier private key
 * @date 2019-04-30, 14:39
 */
public class PaillierPrivateKeyParameters extends AsymmetricKeyParameter {

    private BigInteger p;

    private BigInteger q;

    private BigInteger pSquared;

    private BigInteger qSquared;

    private BigInteger pInverse;

    private BigInteger muP;

    private BigInteger muQ;

    public PaillierPrivateKeyParameters(BigInteger p, BigInteger q) {
        super(true);
        BigInteger generator = p.multiply(q).add(ONE);
        this.p = p;
        this.pSquared = p.multiply(p);
        this.q = q;
        this.qSquared = q.multiply(q);
        this.pInverse = p.modInverse(q);
        this.muP = hFunction(generator, p, pSquared);
        this.muQ = hFunction(generator, q, qSquared);
    }

    public PaillierPrivateKeyParameters(BigInteger p, BigInteger pSquared, BigInteger q, BigInteger qSquared,
                                        BigInteger pInverse, BigInteger muP, BigInteger muQ){
        super(true);
        this.p = p;
        this.pSquared = pSquared;
        this.q = q;
        this.qSquared = qSquared;
        this.pInverse = pInverse;
        this.muP = muP;
        this.muQ = muQ;
    }


    // mu = h(x) = (L(g^(x-1) mod x^2))^(-1) mod n
    private BigInteger hFunction(BigInteger generator, BigInteger x, BigInteger xSquared) {
        BigInteger phiX = lFunction(generator.modPow(x.subtract(ONE),xSquared),x);
        return phiX.modInverse(x);
    }

    // L(x) = (x-1) / n
    public BigInteger lFunction(BigInteger x, BigInteger n) {
        return x.subtract(ONE).divide(n);
    }

    public BigInteger getP()
    {
        return p;
    }

    public BigInteger getPSquared()
    {
        return pSquared;
    }

    public BigInteger getQ()
    {
        return q;
    }

    public BigInteger getQSquared()
    {
        return qSquared;
    }

    public BigInteger getPInverse()
    {
        return pInverse;
    }

    public BigInteger getMuP()
    {
        return muP;
    }

    public BigInteger getMuQ()
    {
        return muQ;
    }
}
