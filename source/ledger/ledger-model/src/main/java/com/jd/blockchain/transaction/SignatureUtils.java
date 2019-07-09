package com.jd.blockchain.transaction;

import com.jd.blockchain.crypto.*;
import com.jd.blockchain.ledger.DigitalSignature;
import com.jd.blockchain.ledger.TransactionContent;

public class SignatureUtils {

    public static DigitalSignature sign(TransactionContent txContent, AsymmetricKeypair keyPair) {
        SignatureDigest signatureDigest = sign(txContent, keyPair.getPrivKey());
        return new DigitalSignatureBlob(keyPair.getPubKey(), signatureDigest);
    }

    public static SignatureDigest sign(TransactionContent txContent, PrivKey privKey) {
        return Crypto.getSignatureFunction(privKey.getAlgorithm()).sign(privKey, txContent.getHash().toBytes());
    }

    public static boolean verifySignature(TransactionContent txContent, SignatureDigest signDigest, PubKey pubKey) {
        if (!TxBuilder.verifyTxContentHash(txContent, txContent.getHash())) {
            return false;
        }
        return verifyHashSignature(txContent.getHash(), signDigest, pubKey);
    }

    public static boolean verifyHashSignature(HashDigest hash, SignatureDigest signDigest, PubKey pubKey) {
        return Crypto.getSignatureFunction(pubKey.getAlgorithm()).verify(signDigest, pubKey, hash.toBytes());
    }
}
