package com.jd.blockchain.sdk;

/**
 * 区块链服务；
 * 
 * <br>
 * 这是一个门面服务(facade)；
 * 
 * @author huanghaiquan
 *
 */
public interface BlockchainService extends BlockchainQueryService, BlockchainTransactionService, BlockchainEventService {

}
