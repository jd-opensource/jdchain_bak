package com.jd.blockchain.transaction;

public interface ParticipantOperator {

    /**
     * 注册参与方操作；
     *
     * @return
     */
    ParticipantRegisterOperationBuilder participants();

    /**
     * 参与方状态更新操作;
     *
     * @return
     */
    ParticipantStateUpdateOperationBuilder states();
}
