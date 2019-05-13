package com.jd.blockchain.crypto;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;

/**
 * @author zhanglin33
 * @title: CSRBuilder
 * @description: A builder for certificate signing request, supporting rsa and sm2
 * @date 2019-05-10, 15:10
 */
public class CSRBuilder {

    private String C;
    private String ST;
    private String L;
    private String O;
    private String OU;
    private String CN;
    private String E;

    public AsymmetricCipherKeyPair init() {
        return null;
    }

    public String buildRequest(String keyName, AsymmetricCipherKeyPair keyPair, String algoName,
                               String[] applicantInfo) {
        return null;
    }
}
