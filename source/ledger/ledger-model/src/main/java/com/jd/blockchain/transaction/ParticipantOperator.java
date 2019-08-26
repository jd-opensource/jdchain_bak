package com.jd.blockchain.transaction;

public interface ParticipantOperator {

    /**
     * 注册参与方操作；
     *
     * @return
     */
    ParticipantRegisterOperationBuilder participants();
}
