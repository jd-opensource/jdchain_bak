package com.jd.blockchain.ump.model.config;

import com.jd.blockchain.ump.model.MasterAddr;

public class LedgerIdentification {

    private String ledgerKey;

    private String ledgerAndNodeKey;

    private MasterAddr masterAddr;

    private int nodeId;

    private PeerLocalConfig localConfig;

    private LedgerInitConfig initConfig;

    public LedgerIdentification() {
    }

    public LedgerIdentification(int nodeId, PeerLocalConfig localConfig, MasterAddr masterAddr, String ledgerAndNodeKey, LedgerInitConfig initConfig) {
        this.nodeId = nodeId;
        this.localConfig = localConfig;
        this.masterAddr = masterAddr;
        this.ledgerKey = initConfig.ledgerKey();
        this.ledgerAndNodeKey = ledgerAndNodeKey;
        this.initConfig = initConfig;
        init();
    }

    private void init() {
        // 初始化部分配置信息
        MasterConfig masterConfig = localConfig.getMasterConfig();
        // 设置账本名称
        if (masterConfig.getLedgerName() == null || masterConfig.getLedgerName().length() == 0) {
            masterConfig.setLedgerName(initConfig.getName());
        }
        // 设置NodeSize
        if (masterConfig.getNodeSize() == 0) {
            masterConfig.setNodeSize(initConfig.getNodeSize());
        }
    }

    public String getLedgerKey() {
        return ledgerKey;
    }

    public void setLedgerKey(String ledgerKey) {
        this.ledgerKey = ledgerKey;
    }

    public String getLedgerAndNodeKey() {
        return ledgerAndNodeKey;
    }

    public void setLedgerAndNodeKey(String ledgerAndNodeKey) {
        this.ledgerAndNodeKey = ledgerAndNodeKey;
    }

    public MasterAddr getMasterAddr() {
        return masterAddr;
    }

    public void setMasterAddr(MasterAddr masterAddr) {
        this.masterAddr = masterAddr;
    }

    public int getNodeId() {
        return nodeId;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public PeerLocalConfig getLocalConfig() {
        return localConfig;
    }

    public void setLocalConfig(PeerLocalConfig localConfig) {
        this.localConfig = localConfig;
    }

    public LedgerInitConfig getInitConfig() {
        return initConfig;
    }

    public void setInitConfig(LedgerInitConfig initConfig) {
        this.initConfig = initConfig;
    }
}
