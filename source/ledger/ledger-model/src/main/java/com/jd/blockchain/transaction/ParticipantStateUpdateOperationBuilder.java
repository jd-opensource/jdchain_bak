package com.jd.blockchain.transaction;

import com.jd.blockchain.ledger.BlockchainIdentity;
import com.jd.blockchain.ledger.ParticipantNodeState;
import com.jd.blockchain.ledger.ParticipantStateUpdateInfo;
import com.jd.blockchain.ledger.ParticipantStateUpdateOperation;
import com.jd.blockchain.utils.net.NetworkAddress;

public interface ParticipantStateUpdateOperationBuilder {

    /**
     * 更新参与方状态，已注册->参与共识；
     *
     * @param
     *
     * @param
     *
     * @return
     */
     ParticipantStateUpdateOperation update(BlockchainIdentity blockchainIdentity, NetworkAddress networkAddress, ParticipantNodeState participantNodeState);
}