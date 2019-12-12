package com.jd.blockchain.ump.service;

import com.jd.blockchain.ump.model.MasterAddr;
import com.jd.blockchain.ump.model.PeerSharedConfigs;
import com.jd.blockchain.ump.model.config.*;
import com.jd.blockchain.ump.model.state.PeerInstallSchedules;
import com.jd.blockchain.ump.model.state.PeerStartupSchedules;


public interface UmpService {

    PeerSharedConfigs loadPeerSharedConfigs(PeerLocalConfig sharedConfig);

    LedgerConfig response(PeerSharedConfigs peerSharedConfigs, PeerLocalConfig localConfig);

    String save(MasterAddr masterAddr, LedgerConfig ledgerConfig, PeerLocalConfig localConfig);

    String ledgerAndNodeKey(LedgerConfig ledgerConfig, PeerSharedConfig sharedConfig);

    PeerInstallSchedules install(LedgerIdentification identification, PeerLocalConfig localConfig, String ledgerAndNodeKey);

    PeerInstallSchedules install(String ledgerAndNodeKey);

    PeerInstallSchedules init(String ledgerAndNodeKey);

    PeerInstallSchedules init(LedgerIdentification identification, PeerLocalConfig localConfig, String ledgerAndNodeKey);

//    PeerInstallSchedules startup(String ledgerAndNodeKey);

    PeerStartupSchedules startup();

    boolean stop(String ledgerAndNodeKey);

    boolean stop();

}
