package com.jd.blockchain.ump.model.penetrate;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * mediator's domain;
 * @author zhaogw
 * date 2019/7/2 18:02
 */
public class LeaderDomain {

    @JSONField(name="host")
    private String host;

    @JSONField(name="port")
    private String port;

    @JSONField(name="createTime")
    private String createTime;

    @JSONField(name="ledgerSeed")
    private String ledgerSeed;

    @JSONField(name="peerDomainList")
    private List<PeerDomain> peerDomainList;

    @JSONField(name="ledgerHash")
    private String ledgerHash;

    public List<PeerDomain> getPeerDomainList() {
        return peerDomainList;
    }

    public void setPeerDomainList(List<PeerDomain> peerDomainList) {
        this.peerDomainList = peerDomainList;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getLedgerSeed() {
        return ledgerSeed;
    }

    public void setLedgerSeed(String ledgerSeed) {
        this.ledgerSeed = ledgerSeed;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getLedgerHash() {
        return ledgerHash;
    }

    public void setLedgerHash(String ledgerHash) {
        this.ledgerHash = ledgerHash;
    }
}
