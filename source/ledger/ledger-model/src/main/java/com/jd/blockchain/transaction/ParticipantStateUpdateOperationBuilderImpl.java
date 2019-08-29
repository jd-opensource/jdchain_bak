package com.jd.blockchain.transaction;

import com.jd.blockchain.ledger.ParticipantStateUpdateInfo;
import com.jd.blockchain.ledger.ParticipantStateUpdateOperation;

public class ParticipantStateUpdateOperationBuilderImpl implements ParticipantStateUpdateOperationBuilder {

    @Override
    public ParticipantStateUpdateOperation update(ParticipantStateUpdateInfo stateUpdateInfo) {
        return new ParticipantStateUpdateOpTemplate(stateUpdateInfo);
    }
}
