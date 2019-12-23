package com.jd.blockchain.ump.model.config;

import com.jd.blockchain.ump.model.PartiNode;
import com.jd.blockchain.ump.model.UmpConstant;

import java.util.ArrayList;
import java.util.List;

public class LedgerInitConfig {

    private String seed;

    private String name;

    private String createTime;

    private String consensusProvider;

    private int nodeSize;

    private String cryptoProviders =
            "com.jd.blockchain.crypto.service.classic.ClassicCryptoService, " +
            "com.jd.blockchain.crypto.service.sm.SMCryptoService";

    List<String> securityConfigs = null;

    List<String> partiRolesConfigs = null;


    private List<PartiNode> partiNodes = new ArrayList<>();

    public LedgerInitConfig() {
    }

    public LedgerInitConfig(String seed, String name, String createTime, String consensusProvider, int nodeSize,
                            List<String> securityConfigs, List<String> partiRolesConfigs) {
        this.seed = seed;
        this.name = name;
        this.createTime = createTime;
        this.consensusProvider = consensusProvider;
        this.nodeSize = nodeSize;
        this.securityConfigs = securityConfigs;
        this.partiRolesConfigs = partiRolesConfigs;
    }

    public List<String> toConfigChars(String consensusConf) {

        List<String> configChars = new ArrayList<>();

        configChars.add(toConfigChars(UmpConstant.LEDGER_SEED_PREFIX, seed));

        configChars.add(toConfigChars(UmpConstant.LEDGER_NAME_PREFIX, name));

        configChars.add(toConfigChars(UmpConstant.CREATE_TIME_PREFIX, createTime));

        configChars.add(toConfigChars(UmpConstant.CONSENSUS_PROVIDER_PREFIX, consensusProvider));

        configChars.add(toConfigChars(UmpConstant.CONSENSUS_CONF_PREFIX, consensusConf));

        configChars.add(toConfigChars(UmpConstant.CRYPTO_PROVIDERS_PREFIX, cryptoProviders));

        configChars.add(toConfigChars(UmpConstant.PARTINODE_COUNT, partiNodes.size()));

        if (securityConfigs != null && !securityConfigs.isEmpty()) {
            configChars.addAll(securityConfigs);
        }

        for (PartiNode partiNode : partiNodes) {
            configChars.addAll(partiNode.toConfigChars(this.partiRolesConfigs));
        }

        return configChars;
    }

    public String ledgerKey() {
        return seed + "-" + name;
    }

    public int nodeId(String pubKey) {
        for (int i = 0; i < partiNodes.size(); i++) {
            PartiNode partiNode = partiNodes.get(i);
            if (partiNode.getPubKey().equals(pubKey)) {
                return i;
            }
        }
        throw new IllegalStateException(String.format("Can not find PubKey = %s !", pubKey));
    }

    private String toConfigChars(String prefix, Object value) {
        return prefix + "=" + value;
    }

    public String getSeed() {
        return seed;
    }

    public void setSeed(String seed) {
        this.seed = seed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getConsensusProvider() {
        return consensusProvider;
    }

    public void setConsensusProvider(String consensusProvider) {
        this.consensusProvider = consensusProvider;
    }

    public int getNodeSize() {
        return nodeSize;
    }

    public void setNodeSize(int nodeSize) {
        this.nodeSize = nodeSize;
    }

    public String getCryptoProviders() {
        return cryptoProviders;
    }

    public void setCryptoProviders(String cryptoProviders) {
        this.cryptoProviders = cryptoProviders;
    }

    public List<PartiNode> getPartiNodes() {
        return partiNodes;
    }

    public void setPartiNodes(List<PartiNode> partiNodes) {
        this.partiNodes = partiNodes;
    }

    public void addPartiNode(PartiNode partiNode) {
        this.partiNodes.add(partiNode);
    }
}
