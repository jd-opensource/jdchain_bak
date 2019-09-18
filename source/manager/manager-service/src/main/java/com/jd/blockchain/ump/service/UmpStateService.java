package com.jd.blockchain.ump.service;

import com.jd.blockchain.ump.model.MasterAddr;
import com.jd.blockchain.ump.model.config.LedgerIdentification;
import com.jd.blockchain.ump.model.config.PeerLocalConfig;
import com.jd.blockchain.ump.model.state.*;
import com.jd.blockchain.ump.model.user.UserKeys;
import com.jd.blockchain.ump.model.user.UserKeysVv;

import java.util.List;
import java.util.Map;

public interface UmpStateService {

    void save(String ledgerAndNodeKey, PeerLocalConfig localConfig);

    void save(String ledgerKey, List<String> sharedConfigKeys);

    void save(InstallSchedule installSchedule, MasterAddr masterAddr);

    void save(UserKeys userKeys);

    void save(LedgerPeerInstall peerInstall);

    void save(LedgerMasterInstall masterInstall);

    void save(LedgerIdentification identification);

    void saveLedgerHash(String ledgerAndNodeKey, String ledgerHash);

    List<UserKeys> readUserKeysList();

    List<UserKeysVv> readUserKeysVvList();

    UserKeys readUserKeys(int id);

    PeerLocalConfig readConfig(String ledgerAndNodeKey);

    PeerInstallSchedules loadState(String ledgerAndNodeKey);

    PeerInstallSchedules loadInitState(String ledgerAndNodeKey);

    PeerInstallSchedules readState(String ledgerAndNodeKey);

    PeerInstallSchedules readInitState(String ledgerAndNodeKey);

    Map<String, List<InstallSchedule>> readStates(String ledgerKey);

    LedgerIdentification readIdentification(String ledgerAndNodeKey);

    List<LedgerPeerInstall> readLedgerPeerInstalls();

    List<LedgerMasterInstall> readLedgerMasterInstalls();

    List<LedgerPeerInited> readLedgerPeerIniteds();

    List<LedgerPeerInited> readLedgerPeerIniteds(String search);

    List<LedgerInited> readLedgerIniteds(String search);

    String readLedgerHash(String ledgerAndNodeKey);

    int peerPort(String peerPath);

    int peerPort();
}
