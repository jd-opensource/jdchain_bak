package com.jd.blockchain.consensus.bftsmart;

import com.jd.blockchain.crypto.PubKey;

public class BftsmartClientIncomingConfig implements BftsmartClientIncomingSettings {

    private BftsmartConsensusSettings consensusSettings;

    private byte[] topology;

    private byte[] tomConfig;

    private int clientId;

    private PubKey pubKey;


    @Override
    public BftsmartConsensusSettings getConsensusSettings() {
        return consensusSettings;
    }

    public void setConsensusSettings(BftsmartConsensusSettings consensusSettings) {
        this.consensusSettings = consensusSettings;
    }

    @Override
    public byte[] getTopology() {
        return topology;
    }

    public void setTopology(byte[] topology) {
        this.topology = topology;
    }

    @Override
    public int getClientId() {
        return clientId;
    }

    @Override
    public String getProviderName() {
        return BftsmartConsensusProvider.NAME;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    @Override
    public byte[] getTomConfig() {
        return tomConfig;
    }

    public void setTomConfig(byte[] tomConfig) {
        this.tomConfig = tomConfig;
    }

    @Override
    public PubKey getPubKey() {
        return pubKey;
    }

    public void setPubKey(PubKey pubKey) {
        this.pubKey = pubKey;
    }
}
