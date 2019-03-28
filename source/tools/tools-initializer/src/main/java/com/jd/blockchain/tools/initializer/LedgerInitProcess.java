package com.jd.blockchain.tools.initializer;

import com.jd.blockchain.consensus.ConsensusProvider;
import com.jd.blockchain.consensus.ConsensusSettings;
import com.jd.blockchain.consensus.service.ConsensusServiceProvider;
import com.jd.blockchain.crypto.asymmetric.PrivKey;
import com.jd.blockchain.crypto.hash.HashDigest;
import com.jd.blockchain.ledger.CryptoSetting;

/**
 * 
 * @author huanghaiquan
 *
 */
public interface LedgerInitProcess {

	/**
	 * Init a new ledger;
	 * 
	 * @param currentId
	 * @param privKey
	 * @param ledgerInitProps
	 *            账本初始化配置属性；
	 * @param consensusSettings
	 *            共识配置属性；
	 * @param consensusServiceProvider 
	 * @param prompter
	 * @return 返回新账本的 hash；如果未初始化成功，则返回 null；
	 */
	/**
	 * Init a new ledger;
	 * @param currentId Id of current participant;
	 * @param privKey Private key of current participant; 
	 * @param ledgerInitProps The settings about this initialization;
	 * @param consensusSettings The consensus settings 
	 * @param consensusProvider
	 * @param dbConnConfig
	 * @param prompter
	 * @return
	 */
	HashDigest initialize(int currentId, PrivKey privKey, LedgerInitProperties ledgerInitProps,
			ConsensusSettings consensusSettings, ConsensusProvider consensusProvider,
			DBConnectionConfig dbConnConfig, Prompter prompter);

	/**
	 * @param currentId
	 * @param privKey
	 * @param ledgerInitProps
	 * @param consensusSettings
	 * @param consensusProvider
	 * @param dbConnConfig
	 * @param prompter
	 * @param cryptoSetting
	 * @return
	 */
	HashDigest initialize(int currentId, PrivKey privKey, LedgerInitProperties ledgerInitProps,
			ConsensusSettings consensusSettings, ConsensusProvider consensusProvider,
			DBConnectionConfig dbConnConfig, Prompter prompter, CryptoSetting cryptoSetting);

}
