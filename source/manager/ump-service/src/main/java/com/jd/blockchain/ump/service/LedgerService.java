package com.jd.blockchain.ump.service;

import com.jd.blockchain.ump.model.state.LedgerBindingConf;
import com.jd.blockchain.ump.model.state.LedgerInited;

import java.util.List;

public interface LedgerService {

    String randomSeed();

    String currentCreateTime();

    String ledgerInitCommand(String peerPath);

    String peerStartCommand(String peerPath);

    LedgerBindingConf allLedgerHashs(String peerPath);

    LedgerBindingConf allLedgerHashs(long lastTime, String peerPath);

    List<LedgerInited> allLedgerIniteds(String peerPath);

    boolean dbExist(String peerPath, String ledgerHash);

    String peerVerifyKey(String peerPath);

    void save(String ledgerAndNodeKey, String ledgerHash);

    String readLedgerHash(String ledgerAndNodeKey);
}
