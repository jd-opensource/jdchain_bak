package com.jd.blockchain.ump.model.config;

public class LedgerConfig {

    private LedgerInitConfig initConfig;

    /**
     * 共识文件配置信息，Base58格式
     */
    private String consensusConfig;


    public LedgerConfig() {
    }

    /**
     * 包装一下，使用JSON处理过程
     *
     * @param ledgerConfig
     */
    public LedgerConfig(LedgerConfig ledgerConfig) {
        this.consensusConfig = ledgerConfig.getConsensusConfig();

    }

    public LedgerConfig(LedgerInitConfig initConfig, String consensusConfig) {
        this.initConfig = initConfig;
        this.consensusConfig = consensusConfig;
    }

    public LedgerInitConfig getInitConfig() {
        return initConfig;
    }

    public void setInitConfig(LedgerInitConfig initConfig) {
        this.initConfig = initConfig;
    }

    public String getConsensusConfig() {
        return consensusConfig;
    }

    public void setConsensusConfig(String consensusConfig) {
        this.consensusConfig = consensusConfig;
    }
}
