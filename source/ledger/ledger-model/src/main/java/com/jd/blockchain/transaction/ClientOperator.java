package com.jd.blockchain.transaction;

/**
 * 面向客户端的操作；
 * 
 * @author huanghaiquan
 *
 */
public interface ClientOperator extends UserOperator, DataAccountOperator, ContractOperator, EventOperator, ParticipantOperator, ParticipantStateOperator{

}