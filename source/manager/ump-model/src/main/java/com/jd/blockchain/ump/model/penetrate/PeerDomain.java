package com.jd.blockchain.ump.model.penetrate;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author zhaogw
 * date 2019/7/2 10:10
 */
public class PeerDomain {
    @JSONField(name="peerId")
    private int peerId;

    private String basePath;

    @JSONField(name="peerName")
    private String peerName;

    @JSONField(name="host")
    private String host;

    private String serverPort;

    @JSONField(name="initPort")
    private String initPort;

    @JSONField(name="consensusPort")
    private String consensusPort;

    @JSONField(name="visitPort")
    private String visitPort;

    @JSONField(name="mediatorUrl")
    private String mediatorUrl;

    private String dbUri;

    private boolean gatewayBindPeer;

    //the random String from front input by mouse device;
    private String randomSeed;

    @JSONField(name="peerPubKey")
    private String peerPubKey;

    private String peerPrivKey;

    private String peerRawPasswd;
    private String peerPasswd;

    public int getPeerId() {
        return peerId;
    }

    public void setPeerId(int peerId) {
        this.peerId = peerId;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getPeerName() {
        return peerName;
    }

    public void setPeerName(String peerName) {
        this.peerName = peerName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getServerPort() {
        return serverPort;
    }

    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }

    public String getInitPort() {
        return initPort;
    }

    public void setInitPort(String initPort) {
        this.initPort = initPort;
    }

    public String getConsensusPort() {
        return consensusPort;
    }

    public void setConsensusPort(String consensusPort) {
        this.consensusPort = consensusPort;
    }

    public String getVisitPort() {
        return visitPort;
    }

    public void setVisitPort(String visitPort) {
        this.visitPort = visitPort;
    }

    public String getMediatorUrl() {
        return mediatorUrl;
    }

    public void setMediatorUrl(String mediatorUrl) {
        this.mediatorUrl = mediatorUrl;
    }

    public String getDbUri() {
        return dbUri;
    }

    public void setDbUri(String dbUri) {
        this.dbUri = dbUri;
    }

    public boolean isGatewayBindPeer() {
        return gatewayBindPeer;
    }

    public void setGatewayBindPeer(boolean gatewayBindPeer) {
        this.gatewayBindPeer = gatewayBindPeer;
    }

    public String getRandomSeed() {
        return randomSeed;
    }

    public void setRandomSeed(String randomSeed) {
        this.randomSeed = randomSeed;
    }

    public String getPeerPubKey() {
        return peerPubKey;
    }

    public void setPeerPubKey(String peerPubKey) {
        this.peerPubKey = peerPubKey;
    }

    public String getPeerPrivKey() {
        return peerPrivKey;
    }

    public void setPeerPrivKey(String peerPrivKey) {
        this.peerPrivKey = peerPrivKey;
    }

    public String getPeerRawPasswd() {
        return peerRawPasswd;
    }

    public void setPeerRawPasswd(String peerRawPasswd) {
        this.peerRawPasswd = peerRawPasswd;
    }

    public String getPeerPasswd() {
        return peerPasswd;
    }

    public void setPeerPasswd(String peerPasswd) {
        this.peerPasswd = peerPasswd;
    }
}
