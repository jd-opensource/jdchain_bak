package com.jd.blockchain.ledger.data;

import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.asymmetric.SignatureDigest;
import com.jd.blockchain.ledger.BlockchainKeyPair;
import com.jd.blockchain.ledger.DigitalSignature;
import com.jd.blockchain.utils.io.ByteArray;
import com.jd.blockchain.utils.security.Ed25519Utils;

public class SignatureUtils {

    public static DigitalSignature sign(ByteArray data, BlockchainKeyPair keyPair) {
        return sign(data.bytes(), keyPair);
    }

    public static DigitalSignature sign(byte[] data, BlockchainKeyPair keyPair) {
        // 对交易内容的hash进行签名；
        CryptoAlgorithm algorithm = keyPair.getPrivKey().getAlgorithm();
        switch (algorithm) {
            case ED25519:
                byte[] digest = Ed25519Utils.sign_512(data, keyPair.getPrivKey().getRawKeyBytes());
                DigitalSignatureBlob signature = new DigitalSignatureBlob(keyPair.getPubKey(), new SignatureDigest(digest));
                return signature;
            case SM2:
                throw new IllegalArgumentException("Unsupported KeyType[" + algorithm + "]!");
//            case CA:
//                throw new IllegalArgumentException("Unsupported KeyType[" + keyType + "]!");
            default:
                throw new IllegalArgumentException("Unsupported KeyType[" + algorithm + "]!");
        }
    }

    public static boolean verify(ByteArray data, DigitalSignature signature) {
        return verify(data.bytes(), signature);
    }

    public static boolean verify(byte[] data, DigitalSignature signature) {
        CryptoAlgorithm algorithm = signature.getPubKey().getAlgorithm();
        switch (algorithm) {
            case ED25519:
                return Ed25519Utils.verify(data, signature.getPubKey().getRawKeyBytes(), signature.getDigest().toBytes());
            case SM2:
                throw new IllegalArgumentException("Unsupported KeyType[" + algorithm + "]!");
//            case CA:
//                throw new IllegalArgumentException("Unsupported KeyType[" + keyType + "]!");
            default:
                throw new IllegalArgumentException("Unsupported KeyType[" + algorithm + "]!");
        }
    }
}
