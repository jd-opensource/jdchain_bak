package com.jd.blockchain.ledger;

import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.utils.net.NetworkAddress;

public class ParticipantStateUpdateInfoData implements ParticipantStateUpdateInfo {
    private PubKey pubKey;
    private ParticipantNodeState state;
    private NetworkAddress networkAddress;

    public ParticipantStateUpdateInfoData(PubKey pubKey, ParticipantNodeState state, NetworkAddress networkAddress) {
        this.pubKey = pubKey;
        this.state = state;
        this.networkAddress = networkAddress;
    }

    public void setPubKey(PubKey pubKey) {
        this.pubKey = pubKey;
    }

    @Override
    public PubKey getPubKey() {
        return pubKey;
    }

    public void setNetworkAddress(NetworkAddress networkAddress) {
        this.networkAddress = networkAddress;
    }

    @Override
    public NetworkAddress getNetworkAddress() {
        return networkAddress;
    }

    public void setState(ParticipantNodeState state) {
        this.state = state;
    }

    @Override
    public ParticipantNodeState getState() {
        return state;
    }
}
