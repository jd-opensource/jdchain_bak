package com.jd.blockchain.transaction;

import com.jd.blockchain.ledger.BlockchainIdentity;
import com.jd.blockchain.ledger.ParticipantNodeState;
import com.jd.blockchain.ledger.ParticipantStateUpdateInfo;
import com.jd.blockchain.ledger.ParticipantStateUpdateOperation;
import com.jd.blockchain.utils.net.NetworkAddress;

public class ParticipantStateUpdateOperationBuilderImpl implements ParticipantStateUpdateOperationBuilder {

    @Override
    public ParticipantStateUpdateOperation update(BlockchainIdentity blockchainIdentity, NetworkAddress networkAddress, ParticipantNodeState participantNodeState) {
        return new ParticipantStateUpdateOpTemplate(blockchainIdentity, networkAddress, participantNodeState);
    }
}
