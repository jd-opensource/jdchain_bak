package com.jd.blockchain.ledger;

import java.io.Closeable;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.transaction.ClientOperator;

/**
 * 区块链交易模板；
 * 
 * @author huanghaiquan
 *
 */
public interface TransactionTemplate extends ClientOperator, Closeable {
	
	HashDigest getLedgerHash();

	/**
	 * 完成交易定义，并生成就绪的交易数据； <br>
	 * 
	 * 注：调用此方法后，不能再向当前对象加入更多的操作；
	 * 
	 * @return
	 */
	PreparedTransaction prepare();

}
