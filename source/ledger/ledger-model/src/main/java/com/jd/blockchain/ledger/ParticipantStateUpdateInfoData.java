package com.jd.blockchain.ledger;

import com.jd.blockchain.crypto.PubKey;

public class ParticipantStateUpdateInfoData implements ParticipantStateUpdateInfo {
    private PubKey pubKey;
    private ParticipantNodeState state;

    public ParticipantStateUpdateInfoData(PubKey pubKey, ParticipantNodeState state) {
        this.pubKey = pubKey;
        this.state = state;
    }

    public void setPubKey(PubKey pubKey) {
        this.pubKey = pubKey;
    }

    @Override
    public PubKey getPubKey() {
        return pubKey;
    }

    public void setState(ParticipantNodeState state) {
        this.state = state;
    }

    @Override
    public ParticipantNodeState getState() {
        return state;
    }
}
