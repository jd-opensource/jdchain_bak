package com.jd.blockchain.ledger;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.transaction.ClientOperator;
import com.jd.blockchain.transaction.LedgerInitOperator;

/**
 * 区块链交易模板；
 * 
 * @author huanghaiquan
 *
 */
public interface TransactionBuilder extends ClientOperator, LedgerInitOperator {

	HashDigest getLedgerHash();

	/**
	 * 基于当前的系统时间完成交易定义，并生成就绪的交易数据； <br>
	 * 
	 * 注：调用此方法后，不能再向当前对象加入更多的操作；<br>
	 * 
	 * @return
	 */
	TransactionRequestBuilder prepareRequest();

	/**
	 * 生成交易内容；
	 * 
	 * @return
	 */
	TransactionContent prepareContent();

	/**
	 * 基于当前的系统时间完成交易定义，并生成就绪的交易数据； <br>
	 * 
	 * 注：调用此方法后，不能再向当前对象加入更多的操作；
	 * 
	 * @param time 交易时间戳；
	 * @return
	 */
	TransactionRequestBuilder prepareRequest(long time);

	/**
	 * 生成交易内容；
	 * 
	 * @param time 交易时间戳；
	 * @return
	 */
	TransactionContent prepareContent(long time);

}
