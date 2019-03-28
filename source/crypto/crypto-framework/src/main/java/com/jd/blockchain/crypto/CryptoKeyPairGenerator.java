package com.jd.blockchain.crypto;

import com.jd.blockchain.crypto.asymmetric.CryptoKeyPair;

public interface CryptoKeyPairGenerator {

    /**
     * 返回密钥对；
     */
    CryptoKeyPair generateKeyPair();



}
