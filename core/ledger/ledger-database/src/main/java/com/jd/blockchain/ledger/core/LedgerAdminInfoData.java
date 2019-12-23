package com.jd.blockchain.ledger.core;

import com.jd.blockchain.ledger.*;

/**
 * @author shaozhuguang
 * @date 2019-09-16
 *
 * LedgerAdminInfo的独立实现类，主要用于页面展示，区分 {@link LedgerAdminDataset}
 */
public class LedgerAdminInfoData implements LedgerAdminInfo {

    /**
     * 元数据
     */
    private LedgerMetadata_V2 metadata;

    /**
     * 账本配置
     *
     */
    private LedgerSettings ledgerSettings;

    /**
     * 参与方数量
     *
     */
    private long participantCount;

    /**
     * 参与方
     *
     */
    private ParticipantNode[] participantNodes;

    /**
     * 包装构造方法
     *
     * @param ledgerAdminInfo
     */
    public LedgerAdminInfoData(LedgerAdminInfo ledgerAdminInfo) {
        this(ledgerAdminInfo.getMetadata(), ledgerAdminInfo.getSettings(),
                ledgerAdminInfo.getParticipantCount(), ledgerAdminInfo.getParticipants());
    }

    public LedgerAdminInfoData(LedgerMetadata_V2 metadata, LedgerSettings ledgerSettings, long participantCount, ParticipantNode[] participantNodes) {
        this.metadata = metadata;
        this.ledgerSettings = ledgerSettings;
        this.participantCount = participantCount;
        this.participantNodes = participantNodes;
    }

    /**
     * 返回元数据配置信息
     *
     * @return
     */
    @Override
    public LedgerMetadata_V2 getMetadata() {
        return this.metadata;
    }

    /**
     * 返回当前设置的账本配置；
     *
     * @return
     */
    @Override
    public LedgerSettings getSettings() {
        return this.ledgerSettings;
    }

    /**
     * 返回当前参与方的数量
     *
     * @return
     */
    @Override
    public long getParticipantCount() {
        return this.participantCount;
    }

    /**
     * 返回当前参与方列表
     *
     * @return
     */
    @Override
    public ParticipantNode[] getParticipants() {
        return this.participantNodes;
    }
}
