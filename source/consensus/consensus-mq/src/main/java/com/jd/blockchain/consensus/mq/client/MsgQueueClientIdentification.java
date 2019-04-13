/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.mq.client.MsgQueueClientIdentification
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/12/12 下午2:04
 * Description:
 */
package com.jd.blockchain.consensus.mq.client;

import com.jd.blockchain.consensus.ClientIdentification;
import com.jd.blockchain.consensus.mq.MsgQueueConsensusProvider;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.crypto.SignatureDigest;

/**
 *
 * @author shaozhuguang
 * @create 2018/12/12
 * @since 1.0.0
 */

public class MsgQueueClientIdentification implements ClientIdentification {

    private byte[] identityInfo;

    private PubKey pubKey;

    private SignatureDigest signature;

    public MsgQueueClientIdentification() {
    }

    public MsgQueueClientIdentification(ClientIdentification clientIdentification) {
        identityInfo = clientIdentification.getIdentityInfo();
        pubKey = clientIdentification.getPubKey();
        signature = clientIdentification.getSignature();
    }

    public MsgQueueClientIdentification setIdentityInfo(byte[] identityInfo) {
        this.identityInfo = identityInfo;
        return this;
    }

    public MsgQueueClientIdentification setPubKey(PubKey pubKey) {
        this.pubKey = pubKey;
        return this;
    }

    public MsgQueueClientIdentification setSignature(SignatureDigest signature) {
        this.signature = signature;
        return this;
    }

    @Override
    public byte[] getIdentityInfo() {
        return this.identityInfo;
    }

    @Override
    public PubKey getPubKey() {
        return this.pubKey;
    }

    @Override
    public SignatureDigest getSignature() {
        return this.signature;
    }

    @Override
    public String getProviderName() {
        return MsgQueueConsensusProvider.NAME;
    }
}