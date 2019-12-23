package com.jd.blockchain.consensus.bftsmart.service;

import com.jd.blockchain.consensus.NodeSettings;
import com.jd.blockchain.consensus.bftsmart.BftsmartConsensusSettings;

public class BftsmartServerSettingConfig implements BftsmartServerSettings {
    private NodeSettings replicaSettings;
    private String realmName;
    private BftsmartConsensusSettings consensusSettings;


    @Override
    public String getRealmName() {
        return realmName;
    }

    public void setRealmName(String realmName) {
        this.realmName = realmName;
    }


    @Override
    public NodeSettings getReplicaSettings() {
        return replicaSettings;
    }

    public void setReplicaSettings(NodeSettings replicaSettings) {
        this.replicaSettings = replicaSettings;
    }


    @Override
    public BftsmartConsensusSettings getConsensusSettings() {
        return consensusSettings;
    }

    public void setConsensusSettings(BftsmartConsensusSettings consensusSettings) {
        this.consensusSettings = consensusSettings;
    }
}
