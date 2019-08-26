package com.jd.blockchain.transaction;

import com.jd.blockchain.ledger.ParticipantInfo;
import com.jd.blockchain.ledger.ParticipantRegisterOperation;

public class ParticipantRegisterOperationBuilderImpl implements ParticipantRegisterOperationBuilder {
    @Override
    public ParticipantRegisterOperation register(ParticipantInfo participantNode) {
        return new ParticipantRegisterOpTemplate(participantNode);
    }
}
