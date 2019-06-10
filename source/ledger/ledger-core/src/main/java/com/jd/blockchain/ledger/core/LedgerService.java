package com.jd.blockchain.ledger.core;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.LedgerException;

/**
 * 账本管理器；
 * 
 * @author huanghaiquan
 *
 */
public interface LedgerService {
	
	/**
	 * 返回已注册的账本哈希；
	 * @return
	 */
	HashDigest[] getLedgerHashs();

	/**
	 * 获取指定的账本数据库；<br>
	 * 
	 * 如果指定的账本不存在，则抛出 {@link LedgerException};
	 * 
	 * @param ledgerHash
	 * @return
	 */
	LedgerRepository getLedger(HashDigest ledgerHash);

}