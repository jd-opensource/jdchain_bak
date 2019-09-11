package com.jd.blockchain.ledger.core;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.storage.service.KVStorageService;

/**
 * 账本管理器；
 * 
 * @author huanghaiquan
 *
 */
public interface LedgerManage extends LedgerService {
	
	static final String LEDGER_PREFIX = "LDG://";
	
	LedgerQuery register(HashDigest ledgerHash, KVStorageService storageService);
	
	void unregister(HashDigest ledgerHash);

//	/**
//	 * 创建新账本；
//	 * 
//	 * @param initSetting
//	 *            初始化配置；
//	 * @param initPermissions
//	 *            参与者的初始化授权列表；与参与者列表一致；
//	 * @return
//	 */
//	LedgerEditor newLedger(LedgerInitSetting initSetting, KVStorageService storageService);

}