package com.jd.blockchain.ump.service;

import com.jd.blockchain.ump.model.config.PeerLocalConfig;
import com.jd.blockchain.ump.model.config.PeerSharedConfigVv;
import com.jd.blockchain.ump.model.user.UserKeys;
import com.jd.blockchain.ump.model.user.UserKeysVv;

public interface UmpSimulateService {

    UserKeysVv userKeysVv(int nodeId);

    UserKeys userKeys(int nodeId);

    PeerLocalConfig nodePeerLocalConfig(int nodeId, boolean isMaster);

    PeerSharedConfigVv peerSharedConfigVv(int nodeId, boolean isMaster);
}
