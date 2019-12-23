package com.jd.blockchain.sdk;


import com.jd.blockchain.consensus.ConsensusSettings;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.CryptoSetting;
import com.jd.blockchain.ledger.ParticipantNode;

/**
 * 账本初始化配置
 *
 * @author shaozhuguang
 * @date 2019-04-23
 * @since 1.0.0
 *
 */
public class LedgerBaseSettings {

    /**
     * 账本初始化种子
     */
    private String seed;

    /**
     * 共识参与方的默克尔树的根；
     */
    private HashDigest participantsHash;

    /**
     * 算法配置
     */
    private CryptoSetting cryptoSetting;

    /**
     * 共识协议
     */
    private String consensusProtocol;

    /**
     * 共识配置
     */
    private ConsensusSettings consensusSettings;

    /**
     * 共识参与方
     */
    private ParticipantNode[] participantNodes;

    public void setSeed(String seed) {
        this.seed = seed;
    }

    public String getSeed() {
        return seed;
    }

    public HashDigest getParticipantsHash() {
        return participantsHash;
    }

    public void setParticipantsHash(HashDigest participantsHash) {
        this.participantsHash = participantsHash;
    }

    public CryptoSetting getCryptoSetting() {
        return cryptoSetting;
    }

    public void setCryptoSetting(CryptoSetting cryptoSetting) {
        this.cryptoSetting = cryptoSetting;
    }

    public String getConsensusProtocol() {
        return consensusProtocol;
    }

    public void setConsensusProtocol(String consensusProtocol) {
        this.consensusProtocol = consensusProtocol;
    }

    public ConsensusSettings getConsensusSettings() {
        return consensusSettings;
    }

    public void setConsensusSettings(ConsensusSettings consensusSettings) {
        this.consensusSettings = consensusSettings;
    }

    public ParticipantNode[] getParticipantNodes() {
        return participantNodes;
    }

    public void setParticipantNodes(ParticipantNode[] participantNodes) {
        this.participantNodes = participantNodes;
    }
}
