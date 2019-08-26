package com.jd.blockchain.transaction;

import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.ledger.ParticipantInfo;
import com.jd.blockchain.ledger.ParticipantRegisterOperation;

public class ParticipantRegisterOpTemplate implements ParticipantRegisterOperation {

    static {
        DataContractRegistry.register(ParticipantRegisterOperation.class);
    }

    private ParticipantInfo participantInfo;

    public ParticipantRegisterOpTemplate(ParticipantInfo participantInfo) {

        this.participantInfo = participantInfo;
    }

    @Override
    public ParticipantInfo getParticipantInfo() {
        return participantInfo;
    }
}
