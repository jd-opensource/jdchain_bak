package com.jd.blockchain.ump.model.state;

import com.jd.blockchain.ump.model.MasterAddr;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LedgerPeerInstall {

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private String ledgerKey;

    private String ledgerAndNodeKey;

    private String nodeName;

    private String sharedKey;

    private String peerPath;

    private int totalNodeSize;

    private MasterAddr masterAddr;

    private String createTime;

    public LedgerPeerInstall() {
    }

    public LedgerPeerInstall(String nodeName, String sharedKey, String peerPath, int totalNodeSize) {
        this.nodeName = nodeName;
        this.sharedKey = sharedKey;
        this.peerPath = peerPath;
        this.totalNodeSize = totalNodeSize;
    }

    public LedgerPeerInstall initKey(String ledgerKey, String ledgerAndNodeKey) {
        this.ledgerKey = ledgerKey;
        this.ledgerAndNodeKey = ledgerAndNodeKey;
        return this;
    }

    public LedgerPeerInstall initMasterAddr(MasterAddr masterAddr) {
        this.masterAddr = masterAddr;
        return this;
    }

    public LedgerPeerInstall initCreateTime(Date date) {
        createTime = SDF.format(date);
        return this;
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

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getSharedKey() {
        return sharedKey;
    }

    public void setSharedKey(String sharedKey) {
        this.sharedKey = sharedKey;
    }

    public String getPeerPath() {
        return peerPath;
    }

    public void setPeerPath(String peerPath) {
        this.peerPath = peerPath;
    }

    public int getTotalNodeSize() {
        return totalNodeSize;
    }

    public void setTotalNodeSize(int totalNodeSize) {
        this.totalNodeSize = totalNodeSize;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public MasterAddr getMasterAddr() {
        return masterAddr;
    }

    public void setMasterAddr(MasterAddr masterAddr) {
        this.masterAddr = masterAddr;
    }
}
