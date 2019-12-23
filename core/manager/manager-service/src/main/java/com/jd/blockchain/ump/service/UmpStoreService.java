/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.gateway.service.DataRetrievalService
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2019/1/15 下午3:08
 * Description:
 */
package com.jd.blockchain.ump.service;

import com.jd.blockchain.ump.model.penetrate.LeaderDomain;

import java.util.List;

/**
 *  data store;
 */
public interface UmpStoreService {
    /**
     * get ledger info by ledgerHash;
     * @param ledgerHash
     * @return
     */
    LeaderDomain findLedgerInfo(String ledgerHash);

    /**
     * get all ledgers;
     * @return
     */
    List <LeaderDomain> findAllLedgers();

    /**
     * get all states by seed;
     * @param seed
     * @return
     */
    List<String> findStates(String seed);

    /**
     * get value by key;
     * @param key
     * @return
     */
    Object findRecord(String key);

    void saveLedgerInfo(LeaderDomain leaderDomain);

    void saveRecord(String key, Object obj);

    void saveState4Seed(String key, String state);
}