package com.jd.blockchain.crypto;

public interface AsymmetricKeypairGenerator {

    /**
     * 返回密钥对；
     */
    AsymmetricKeypair generateKeypair();

}
