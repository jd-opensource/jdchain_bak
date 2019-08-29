package com.jd.blockchain.transaction;

import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.ledger.ParticipantStateUpdateInfo;
import com.jd.blockchain.ledger.ParticipantStateUpdateOperation;

public class ParticipantStateUpdateOpTemplate implements ParticipantStateUpdateOperation {

    static {
        DataContractRegistry.register(ParticipantStateUpdateOperation.class);
    }

    private ParticipantStateUpdateInfo stateUpdateInfo;

    public ParticipantStateUpdateOpTemplate(ParticipantStateUpdateInfo stateUpdateInfo) {
        this.stateUpdateInfo = stateUpdateInfo;
    }

    @Override
    public ParticipantStateUpdateInfo getStateUpdateInfo() {
        return stateUpdateInfo;
    }
}
