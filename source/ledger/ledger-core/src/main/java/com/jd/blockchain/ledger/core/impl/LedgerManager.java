package com.jd.blockchain.ledger.core.impl;

import java.util.HashMap;
import java.util.Map;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.LedgerInitSetting;
import com.jd.blockchain.ledger.core.LedgerConsts;
import com.jd.blockchain.ledger.core.LedgerEditor;
import com.jd.blockchain.ledger.core.LedgerManage;
import com.jd.blockchain.ledger.core.LedgerRepository;
import com.jd.blockchain.storage.service.ExPolicyKVStorage;
import com.jd.blockchain.storage.service.KVStorageService;
import com.jd.blockchain.storage.service.VersioningKVStorage;
import com.jd.blockchain.utils.codec.Base58Utils;

/**
 * 账本管理器；
 * 
 * @author huanghaiquan
 *
 */
public class LedgerManager implements LedgerManage {

	private static final String LEDGER_PREFIX = "LDG://";

	// @Autowired
	// private ExistentialKVStorage exPolicyStorage;
	//
	// @Autowired
	// private VersioningKVStorage versioningStorage;

	// private PrivilegeModelSetting privilegeModel = new PrivilegeModelConfig();

	private Map<HashDigest, LedgerRepositoryContext> ledgers = new HashMap<>();

	@Override
	public HashDigest[] getLedgerHashs() {
		return ledgers.keySet().toArray(new HashDigest[ledgers.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jd.blockchain.ledger.core.LedgerManager#getLedger(com.jd.blockchain.
	 * crypto.hash.HashDigest)
	 */
	@Override
	public LedgerRepository getLedger(HashDigest ledgerHash) {
		LedgerRepositoryContext ledgerCtx = ledgers.get(ledgerHash);
		if (ledgerCtx == null) {
			return null;
		}

		return ledgerCtx.ledgerRepo;
	}

	@Override
	public LedgerRepository register(HashDigest ledgerHash, KVStorageService storageService) {
		VersioningKVStorage ledgerVersioningStorage = storageService.getVersioningKVStorage();
		ExPolicyKVStorage ledgerExPolicyStorage = storageService.getExPolicyKVStorage();
		LedgerRepository ledgerRepo = new LedgerRepositoryImpl(ledgerHash, LEDGER_PREFIX, ledgerExPolicyStorage,
				ledgerVersioningStorage);

		LedgerRepositoryContext ledgerCtx = new LedgerRepositoryContext();
		ledgerCtx.ledgerRepo = ledgerRepo;
		ledgerCtx.storageService = storageService;
		ledgers.put(ledgerHash, ledgerCtx);
		return ledgerRepo;
	}

	@Override
	public void unregister(HashDigest ledgerHash) {
		LedgerRepositoryContext ledgerCtx = ledgers.get(ledgerHash);
		if (ledgerCtx != null) {
			ledgerCtx.ledgerRepo.close();
			ledgers.remove(ledgerHash);
			ledgerCtx.ledgerRepo = null;
			ledgerCtx.storageService = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jd.blockchain.ledger.core.LedgerManager#newLedger(com.jd.blockchain.
	 * ledger.core.ConsensusConfig, com.jd.blockchain.ledger.core.CryptoConfig)
	 */
	@Override
	public LedgerEditor newLedger(LedgerInitSetting initSetting, KVStorageService storageService) {
		// GenesisLedgerStorageProxy genesisStorageProxy = new
		// GenesisLedgerStorageProxy();
		// BufferedKVStorage bufferedStorage = new
		// BufferedKVStorage(genesisStorageProxy, genesisStorageProxy, false);

		// LedgerEditor genesisBlockEditor =
		// LedgerTransactionalEditor.createEditor(initSetting,
		// bufferedStorage, bufferedStorage);

		// return new LedgerInitializer(genesisBlockEditor, bufferedStorage,
		// genesisStorageProxy, storageService, this);

		LedgerEditor genesisBlockEditor = LedgerTransactionalEditor.createEditor(initSetting, LEDGER_PREFIX,
				storageService.getExPolicyKVStorage(), storageService.getVersioningKVStorage());
		return genesisBlockEditor;
	}

	static String getLedgerStoragePrefix(HashDigest ledgerHash) {
		String base58LedgerHash = Base58Utils.encode(ledgerHash.toBytes());
		return LEDGER_PREFIX + base58LedgerHash + LedgerConsts.KEY_SEPERATOR;
	}

	private static class LedgerRepositoryContext {

		private LedgerRepository ledgerRepo;

		private KVStorageService storageService;
	}
}
