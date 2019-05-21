package com.jd.blockchain.tools.initializer;

import com.jd.blockchain.consensus.ConsensusProvider;
import com.jd.blockchain.consensus.ConsensusSettings;
import com.jd.blockchain.consensus.service.ConsensusServiceProvider;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.PrivKey;
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
	 * @param currentId       Id of current participant.
	 * @param privKey         Private key of current participant.
	 * @param ledgerInitProps The settings about this initialization.
	 * @param dbConnConfig    The configuration of DB Connection.
	 * @param prompter        Prompter for interaction.
	 * @return
	 */
	HashDigest initialize(int currentId, PrivKey privKey, LedgerInitProperties ledgerInitProps,
			DBConnectionConfig dbConnConfig, Prompter prompter);

	/**
	 * @param currentId
	 * @param privKey
	 * @param ledgerInitProps
	 * @param dbConnConfig
	 * @param prompter
	 * @param cryptoSetting
	 * @return
	 */
	HashDigest initialize(int currentId, PrivKey privKey, LedgerInitProperties ledgerInitProps,
			DBConnectionConfig dbConnConfig, Prompter prompter, CryptoSetting cryptoSetting);

}
