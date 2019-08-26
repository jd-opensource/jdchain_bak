package com.jd.blockchain.transaction;

import com.jd.blockchain.ledger.ParticipantInfo;
import com.jd.blockchain.ledger.ParticipantRegisterOperation;

public interface ParticipantRegisterOperationBuilder {

    /**
     * 注册；
     *
     * @param
     *
     * @param
     *
     * @return
     */
    ParticipantRegisterOperation register(ParticipantInfo participantInfo);


}
