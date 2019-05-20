package com.jd.blockchain.crypto.utils.classic;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.OpenSSHPublicKeyUtil;
import org.bouncycastle.jce.spec.OpenSSHPublicKeySpec;
import org.bouncycastle.util.encoders.Base64;

/**
 * @author zhanglin33
 * @title: SSHKeyParser
 * @description: a parser for parsing asymmetric keys in Base64 format
 * @date 2019-05-17, 17:52
 */
public class SSHKeyParser {

    private String pubKeyFormat;
    private String pubKeyType;

    public AsymmetricKeyParameter pubKeyParse(String pubKeyStr) {

        byte[] pubKeyBytes;

        if (pubKeyStr.startsWith("ssh") || pubKeyStr.startsWith("ecdsa")) {
            String[] algoAndKeyAndLocal = pubKeyStr.split(" ");
            pubKeyBytes = Base64.decode(algoAndKeyAndLocal[1]);
        } else {
            pubKeyBytes = Base64.decode(pubKeyStr);
        }

        OpenSSHPublicKeySpec pubKeySpec = new OpenSSHPublicKeySpec(pubKeyBytes);

        pubKeyFormat = pubKeySpec.getFormat();
        pubKeyType   = pubKeySpec.getType();

        return OpenSSHPublicKeyUtil.parsePublicKey(pubKeyBytes);
    }





    public String getPubKeyFormat() {
        return pubKeyFormat;
    }

    public String getPubKeyType() {
        return pubKeyType;
    }
}
