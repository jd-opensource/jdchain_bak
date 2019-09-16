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
     * 包装类型
     *     将LedgerAdminInfo重新封装，用于页面显示
     *
     */
    private LedgerAdminInfo ledgerAdminInfo;

    public LedgerAdminInfoData(LedgerAdminInfo ledgerAdminInfo) {
        this.ledgerAdminInfo = ledgerAdminInfo;
    }

    /**
     * 返回元数据配置信息
     *
     * @return
     */
    @Override
    public LedgerMetadata_V2 getMetadata() {
        return ledgerAdminInfo.getMetadata();
    }

    /**
     * 返回当前设置的账本配置；
     *
     * @return
     */
    @Override
    public LedgerSettings getSettings() {
        return ledgerAdminInfo.getSettings();
    }

    /**
     * 返回当前参与方的数量
     *
     * @return
     */
    @Override
    public long getParticipantCount() {
        return ledgerAdminInfo.getParticipantCount();
    }

    /**
     * 返回当前参与方列表
     *
     * @return
     */
    @Override
    public ParticipantNode[] getParticipants() {
        return ledgerAdminInfo.getParticipants();
    }
}
