package com.jd.blockchain.gateway;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.transaction.BlockchainQueryService;
import com.jd.blockchain.transaction.TransactionService;

public interface PeerService {

	/**
	 * 获取账本数量最多的查询器
	 *
	 * @return
	 */
	BlockchainQueryService getQueryService();

	/**
	 * 获取某个账本中区块高度最高的查询器
	 *
	 * @param ledgerHash
	 * @return
	 */
	BlockchainQueryService getQueryService(HashDigest ledgerHash);

	/**
	 * 获取交易处理器
	 *
	 * @return
	 */
	TransactionService getTransactionService();
	
}
