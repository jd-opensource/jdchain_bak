package com.jd.blockchain.ump.model.config;

import com.jd.blockchain.ump.model.PartiNode;

public class PeerSharedConfig {

    public static final String DB_ROCKSDB_SUFFIX = "rocksdb_";

    protected String sharedKey;

    protected String name;

    protected String initAddr;

    protected String pubKey;

    protected int initPort;

    protected String consensusNode;

    protected String consensusProvider = "com.jd.blockchain.consensus.bftsmart.BftsmartConsensusProvider";

    protected PartiNode partiNode;

    public String getSharedKey() {
        return sharedKey;
    }

    public void setSharedKey(String sharedKey) {
        this.sharedKey = sharedKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInitAddr() {
        return initAddr;
    }

    public void setInitAddr(String initAddr) {
        this.initAddr = initAddr;
    }

    public String getPubKey() {
        return pubKey;
    }

    public void setPubKey(String pubKey) {
        this.pubKey = pubKey;
    }

    public int getInitPort() {
        return initPort;
    }

    public void setInitPort(int initPort) {
        this.initPort = initPort;
    }

    public String addr() {
        return initAddr + "-" + initPort;
    }

    public String getConsensusNode() {
        return consensusNode;
    }

    public void setConsensusNode(String consensusNode) {
        this.consensusNode = consensusNode;
    }

    public String getConsensusProvider() {
        return consensusProvider;
    }

    public void setConsensusProvider(String consensusProvider) {
        this.consensusProvider = consensusProvider;
    }


}
