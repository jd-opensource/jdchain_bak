package com.jd.blockchain.sdk;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.utils.Bytes;

public class ContractSettings {

    private Bytes address;

    private PubKey pubKey;

    private HashDigest rootHash;

    private String chainCode;

    public ContractSettings() {
    }

    public ContractSettings(Bytes address, PubKey pubKey, HashDigest rootHash) {
        this.address = address;
        this.pubKey = pubKey;
        this.rootHash = rootHash;
    }

    public ContractSettings(Bytes address, PubKey pubKey, HashDigest rootHash, String chainCode) {
        this.address = address;
        this.pubKey = pubKey;
        this.rootHash = rootHash;
        this.chainCode = chainCode;
    }

    public Bytes getAddress() {
        return address;
    }

    public void setAddress(Bytes address) {
        this.address = address;
    }

    public PubKey getPubKey() {
        return pubKey;
    }

    public void setPubKey(PubKey pubKey) {
        this.pubKey = pubKey;
    }

    public HashDigest getRootHash() {
        return rootHash;
    }

    public void setRootHash(HashDigest rootHash) {
        this.rootHash = rootHash;
    }

    public String getChainCode() {
        return chainCode;
    }

    public void setChainCode(String chainCode) {
        this.chainCode = chainCode;
    }
}
