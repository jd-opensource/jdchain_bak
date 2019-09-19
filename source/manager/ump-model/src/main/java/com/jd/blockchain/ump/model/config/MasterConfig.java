package com.jd.blockchain.ump.model.config;

import com.jd.blockchain.ump.model.MasterAddr;

public class MasterConfig {

    private String masterAddr;

    private int masterPort;

    private String ledgerName;

    private int nodeSize;

    private boolean isMaster = false;

    public MasterConfig() {
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

    public void setMasterPort(int masterPort) {
        this.masterPort = masterPort;
    }

    public String getLedgerName() {
        return ledgerName;
    }

    public void setLedgerName(String ledgerName) {
        this.ledgerName = ledgerName;
    }

    public int getNodeSize() {
        return nodeSize;
    }

    public void setNodeSize(int nodeSize) {
        this.nodeSize = nodeSize;
    }

    public boolean isMaster() {
        return isMaster;
    }

    public void setMaster(boolean master) {
        isMaster = master;
    }

    public MasterConfig buildIsMaster(boolean isMaster) {
        setMaster(isMaster);
        return this;
    }

    public MasterConfig buildNodeSize(int nodeSize) {
        setNodeSize(nodeSize);
        return this;
    }

    public MasterConfig buildLedgerName(String ledgerName) {
        setLedgerName(ledgerName);
        return this;
    }

    public MasterConfig buildMasterAddr(String masterAddr) {
        setMasterAddr(masterAddr);
        return this;
    }

    public MasterConfig buildMasterPort(int masterPort) {
        setMasterPort(masterPort);
        return this;
    }


    public MasterAddr toMasterAddr() {
        return MasterAddr.newInstance(masterAddr, masterPort);
    }
}
