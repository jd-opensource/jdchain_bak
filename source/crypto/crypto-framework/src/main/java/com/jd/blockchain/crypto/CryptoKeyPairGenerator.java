package com.jd.blockchain.crypto;

public interface CryptoKeyPairGenerator {

    /**
     * 返回密钥对；
     */
    CryptoKeyPair generateKeyPair();

}
