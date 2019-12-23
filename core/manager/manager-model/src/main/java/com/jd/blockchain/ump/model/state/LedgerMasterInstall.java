package com.jd.blockchain.ump.model.state;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LedgerMasterInstall {

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private String ledgerKey;

    private int totalNodeSize;

    private String createTime;

    private List<PeerInstall> peerInstalls = new ArrayList<>();

    public LedgerMasterInstall() {
    }

    public LedgerMasterInstall(String ledgerKey, int totalNodeSize) {
        this.ledgerKey = ledgerKey;
        this.totalNodeSize = totalNodeSize;
    }

    public LedgerMasterInstall initCreateTime(String createTime) {
        this.createTime = createTime;
        return this;
    }

    public LedgerMasterInstall initCreateTime(Date date) {
        this.createTime = SDF.format(date);
        return this;
    }

    public LedgerMasterInstall add(PeerInstall peerInstall) {
        peerInstalls.add(peerInstall);
        return this;
    }

    public LedgerMasterInstall add(String name, String pubKey, String ipAddr, int initPort,
                    String consensusNode, String consensusProvider) {
        PeerInstall peerInstall = new PeerInstall(
                name, pubKey, ipAddr, initPort, consensusNode, consensusProvider);
        return add(peerInstall);
    }

    public String getLedgerKey() {
        return ledgerKey;
    }

    public void setLedgerKey(String ledgerKey) {
        this.ledgerKey = ledgerKey;
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

    public List<PeerInstall> getPeerInstalls() {
        return peerInstalls;
    }

    public void setPeerInstalls(List<PeerInstall> peerInstalls) {
        this.peerInstalls = peerInstalls;
    }

    public static class PeerInstall {

        private String name;

        private String pubKey;

        private String ipAddr;

        private int initPort;

        private String consensusNode;

        private String consensusProvider;

        public PeerInstall() {
        }

        public PeerInstall(String name, String pubKey, String ipAddr, int initPort, String consensusNode, String consensusProvider) {
            this.name = name;
            this.pubKey = pubKey;
            this.ipAddr = ipAddr;
            this.initPort = initPort;
            this.consensusNode = consensusNode;
            this.consensusProvider = consensusProvider;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPubKey() {
            return pubKey;
        }

        public void setPubKey(String pubKey) {
            this.pubKey = pubKey;
        }

        public String getIpAddr() {
            return ipAddr;
        }

        public void setIpAddr(String ipAddr) {
            this.ipAddr = ipAddr;
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

        public String getConsensusProvider() {
            return consensusProvider;
        }

        public void setConsensusProvider(String consensusProvider) {
            this.consensusProvider = consensusProvider;
        }
    }
}
