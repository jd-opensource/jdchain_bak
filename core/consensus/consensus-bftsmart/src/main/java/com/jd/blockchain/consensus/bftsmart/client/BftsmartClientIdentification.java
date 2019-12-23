package com.jd.blockchain.consensus.bftsmart.client;

import com.jd.blockchain.consensus.ClientIdentification;
import com.jd.blockchain.consensus.bftsmart.BftsmartConsensusProvider;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.crypto.SignatureDigest;

public class BftsmartClientIdentification implements ClientIdentification {

    private byte[] identityInfo;
    private PubKey pubKey;
    private SignatureDigest signatureDigest;

    public BftsmartClientIdentification() {

    }

    public BftsmartClientIdentification(ClientIdentification clientIdentification) {
        identityInfo = clientIdentification.getIdentityInfo();
        pubKey = clientIdentification.getPubKey();
        signatureDigest = clientIdentification.getSignature();
    }

    @Override
    public byte[] getIdentityInfo() {
        return identityInfo;
    }

    public void setIdentityInfo(byte[] identityInfo) {
        this.identityInfo = identityInfo;
    }

    @Override
    public PubKey getPubKey() {
        return pubKey;
    }

    public void setPubKey(PubKey pubKey) {
        this.pubKey = pubKey;
    }

    @Override
    public SignatureDigest getSignature() {
        return signatureDigest;
    }

    @Override
    public String getProviderName() {
        return BftsmartConsensusProvider.NAME;
    }

    public void setSignatureDigest(SignatureDigest signatureDigest) {
        this.signatureDigest = signatureDigest;
    }

}
