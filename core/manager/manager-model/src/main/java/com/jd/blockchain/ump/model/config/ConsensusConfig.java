package com.jd.blockchain.ump.model.config;

public class ConsensusConfig {

    private String confPath;

    private byte[] content;

    public String getConfPath() {
        return confPath;
    }

    public void setConfPath(String confPath) {
        this.confPath = confPath;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
