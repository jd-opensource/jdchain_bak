package com.jd.blockchain.ump.model.config;


import com.jd.blockchain.ump.model.UmpConstant;
import com.jd.blockchain.ump.model.user.UserKeys;

/**
 *
 */
public class PeerSharedConfigVv {

    private String sharedKey;

    private String name;

    private int userId;

    private String pubKey;

    private String initAddr;

    private int initPort;

    private String consensusNode;

    private String peerPath = UmpConstant.PROJECT_PATH;

    private String dbName;

    private int nodeSize;

    private String masterAddr;

    private int masterPort;

    private String ledgerName;

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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getInitAddr() {
        return initAddr;
    }

    public String getPubKey() {
        return pubKey;
    }

    public void setPubKey(String pubKey) {
        this.pubKey = pubKey;
    }

    public void setInitAddr(String initAddr) {
        this.initAddr = initAddr;
    }

    public int getInitPort() {
        return initPort;
    }

    public void setInitPort(int initPort) {
        this.initPort = initPort;
    }

    public String getConsensusNode() {
        return consensusNode;
    }

    public void setConsensusNode(String consensusNode) {
        this.consensusNode = consensusNode;
    }

    public String getPeerPath() {
        return peerPath;
    }

    public void setPeerPath(String peerPath) {
        this.peerPath = peerPath;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public int getNodeSize() {
        return nodeSize;
    }

    public void setNodeSize(Integer nodeSize) {
        this.nodeSize = nodeSize;
    }

    public String getMasterAddr() {
        return masterAddr;
    }

    public void setMasterAddr(String masterAddr) {
        this.masterAddr = masterAddr;
    }

    public int getMasterPort() {
        return masterPort;
    }

    public void setMasterPort(Integer masterPort) {
        this.masterPort = masterPort;
    }

    public String getLedgerName() {
        return ledgerName;
    }

    public void setLedgerName(String ledgerName) {
        this.ledgerName = ledgerName;
    }

    public PeerLocalConfig toPeerLocalConfig(UserKeys userKeys) {

        PeerLocalConfig localConfig = new PeerLocalConfig();

        localConfig.setSharedKey(sharedKey);
        localConfig.setName(name);
        localConfig.setInitAddr(initAddr);
        localConfig.setInitPort(initPort);
        localConfig.setConsensusNode(consensusNode);
        localConfig.setPubKey(userKeys.getPubKey());
        localConfig.setPrivKey(userKeys.getPrivKey());
        localConfig.setEncodePwd(userKeys.getEncodePwd());
        localConfig.setPeerPath(peerPath);
        localConfig.setDbName(dbName);

        MasterConfig masterConfig = new MasterConfig();

        if (master()) {
            masterConfig.buildIsMaster(true)
                    .buildLedgerName(ledgerName)
                    .buildNodeSize(nodeSize);
        } else {
            masterConfig.buildIsMaster(false)
                    .buildMasterAddr(masterAddr)
                    .buildMasterPort(masterPort);
        }

        localConfig.setMasterConfig(masterConfig);

        return localConfig;
    }

    private boolean master() {
        if (masterAddr == null || masterAddr.length() == 0) {
            return true;
        }
        return false;
    }
}
