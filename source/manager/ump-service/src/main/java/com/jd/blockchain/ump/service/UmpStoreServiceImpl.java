/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.gateway.service.DataRetrievalService
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2019/1/15 下午3:08
 * Description:
 */
package com.jd.blockchain.ump.service;

import com.jd.blockchain.ump.model.UmpConstant;
import com.jd.blockchain.ump.model.penetrate.LeaderDomain;
import com.jd.blockchain.ump.model.penetrate.store.MemStore;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 *  data store;
 */
@Service
public class UmpStoreServiceImpl implements UmpStoreService{
    @Override
    public LeaderDomain findLedgerInfo(String ledgerHash) {
        return (LeaderDomain) MemStore.instance.get(ledgerHash);
    }

    @Override
    public List<LeaderDomain> findAllLedgers() {
        return (List<LeaderDomain>)MemStore.instance.get(UmpConstant.ALL_LEDGER);
    }

    @Override
    public List<String> findStates(String seed) {
        return (List<String>)MemStore.instance.get(seed);
    }

    @Override
    public Object findRecord(String key) {
        return MemStore.instance.get(key);
    }

    @Override
    public void saveLedgerInfo(LeaderDomain leaderDomain) {
        MemStore.instance.put(leaderDomain.getLedgerHash(),leaderDomain);
        //also put it into ALL_LEDGER;
        Object obj = MemStore.instance.get(UmpConstant.ALL_LEDGER);
        List<LeaderDomain> leaderDomains = null;
        if(obj == null){
            leaderDomains = new ArrayList<>();
        }else {
            leaderDomains = (List<LeaderDomain>)obj;
        }
        leaderDomains.add(leaderDomain);
        MemStore.instance.put(UmpConstant.ALL_LEDGER,leaderDomains);
    }

    @Override
    public void saveRecord(String key, Object obj) {
        MemStore.instance.put(key,obj);
    }

    @Override
    public void saveState4Seed(String key, String state) {
        Object obj = MemStore.instance.get(key);
        List<String> states = null;
        if(obj == null){
            states = new ArrayList<>();
        }else {
            states = (List<String>)obj;
        }
        states.add(state);
        MemStore.instance.put(key,states);
    }
}