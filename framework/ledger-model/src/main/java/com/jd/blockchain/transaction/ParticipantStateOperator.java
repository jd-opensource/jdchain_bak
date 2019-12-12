package com.jd.blockchain.transaction;

public interface ParticipantStateOperator {
    /**
     * 参与方状态更新操作;
     *
     * @return
     */
    ParticipantStateUpdateOperationBuilder states();
}
