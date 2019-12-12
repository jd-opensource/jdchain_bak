package com.jd.blockchain.crypto.service.pki;

import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.CryptoAlgorithmDefinition;

/**
 * @author zhanglin33
 * @title: PKIAlgorithm
 * @description: TODO
 * @date 2019-05-15, 16:34
 */
public class PKIAlgorithm {

    public static final CryptoAlgorithm SHA1WITHRSA2048 = CryptoAlgorithmDefinition.defineSignature("SHA1WITHRSA2048",
            false, (byte) 31);

    public static final CryptoAlgorithm SHA1WITHRSA4096 = CryptoAlgorithmDefinition.defineSignature("SHA1WITHRSA4096",
            false, (byte) 32);

    public static final CryptoAlgorithm SM3WITHSM2 = CryptoAlgorithmDefinition.defineSignature("SM3WITHSM2",
            false, (byte) 33);

    private PKIAlgorithm() {
    }

}
