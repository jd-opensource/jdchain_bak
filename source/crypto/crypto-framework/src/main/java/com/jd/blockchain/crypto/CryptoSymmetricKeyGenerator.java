package com.jd.blockchain.crypto;

public interface CryptoSymmetricKeyGenerator {

    /**
     * 返回对称密钥；
     */
    CryptoKey generateSymmetricKey();
}
