package com.jd.blockchain.transaction;

import com.jd.blockchain.ledger.ParticipantStateUpdateInfo;
import com.jd.blockchain.ledger.ParticipantStateUpdateOperation;

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
     ParticipantStateUpdateOperation update(ParticipantStateUpdateInfo stateUpdateInfo);
}