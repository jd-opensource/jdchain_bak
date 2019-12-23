package com.jd.blockchain.crypto.paillier;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

import java.math.BigInteger;

import static org.bouncycastle.util.BigIntegers.ONE;

/**
 * @author zhanglin33
 * @title: PaillierPublicKeyParameters
 * @description: parameters about Paillier public key
 * @date 2019-04-30, 14:41
 */
public class PaillierPublicKeyParameters extends AsymmetricKeyParameter {

    private BigInteger modulus;
    private BigInteger modulusSquared;
    private BigInteger generator;

    public PaillierPublicKeyParameters(BigInteger modulus) {
        super(false);
        this.modulus = validate(modulus);
        this.modulusSquared = modulus.multiply(modulus);
        this.generator = modulus.add(ONE);
    }

    public BigInteger getModulus() {
        return modulus;
    }

    public BigInteger getModulusSquared() {
        return modulusSquared;
    }

    public BigInteger getGenerator() {
        return generator;
    }

    private BigInteger validate(BigInteger modulus)
    {
        if ((modulus.intValue() & 1) == 0)
        {
            throw new IllegalArgumentException("The modulus is even!");
        }

        // the value is the product of the 132 smallest primes from 3 to 751
        if (!modulus.gcd(new BigInteger("145188775577763990151158743208307020242261438098488931355057091965" +
                "931517706595657435907891265414916764399268423699130577757433083166" +
                "651158914570105971074227669275788291575622090199821297575654322355" +
                "049043101306108213104080801056529374892690144291505781966373045481" +
                "8359472391642885328171302299245556663073719855")).equals(ONE))
        {
            throw new IllegalArgumentException("The modulus has a small prime factor!");
        }

        return modulus;
    }
}
