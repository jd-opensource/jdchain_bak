package com.jd.blockchain.ledger.core;

import java.util.HashMap;
import java.util.Map;

import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.CryptoProvider;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.CryptoSetting;
import com.jd.blockchain.ledger.LedgerException;
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
		// 加载账本数据库；
		VersioningKVStorage ledgerVersioningStorage = storageService.getVersioningKVStorage();
		ExPolicyKVStorage ledgerExPolicyStorage = storageService.getExPolicyKVStorage();
		LedgerRepository ledgerRepo = new LedgerRepositoryImpl(ledgerHash, LEDGER_PREFIX, ledgerExPolicyStorage,
				ledgerVersioningStorage);

		// 校验 crypto service provider ；
		CryptoSetting cryptoSetting = ledgerRepo.getAdminInfo().getSettings().getCryptoSetting();
		checkCryptoSetting(cryptoSetting, ledgerHash);

		// 创建账本上下文；
		LedgerRepositoryContext ledgerCtx = new LedgerRepositoryContext(ledgerRepo, storageService);
		ledgers.put(ledgerHash, ledgerCtx);
		return ledgerRepo;
	}

	/**
	 * 检查账本的密码参数设置与本地节点的运行时环境是否匹配；
	 * 
	 * @param cryptoSetting
	 * @param ledgerHash
	 */
	private void checkCryptoSetting(CryptoSetting cryptoSetting, HashDigest ledgerHash) {
		CryptoProvider[] cryptoProviders = cryptoSetting.getSupportedProviders();
		if (cryptoProviders == null || cryptoProviders.length == 0) {
			throw new LedgerException("No supported crypto service providers has been setted in the ledger["
					+ ledgerHash.toBase58() + "]!");
		}
		for (CryptoProvider cp : cryptoProviders) {
			CryptoProvider regCp = Crypto.getProvider(cp.getName());
			checkCryptoProviderConsistency(regCp, cp);
		}
	}

	/**
	 * 检查密码服务提供者的信息是否匹配；
	 * 
	 * @param registeredProvider
	 * @param settingProvider
	 */
	private void checkCryptoProviderConsistency(CryptoProvider registeredProvider, CryptoProvider settingProvider) {
		if (registeredProvider == null) {
			throw new LedgerException("Crypto service provider[" + settingProvider.getName()
					+ "] has not registered in the runtime environment of current peer!");
		}

		CryptoAlgorithm[] runtimeAlgothms = registeredProvider.getAlgorithms();
		CryptoAlgorithm[] settingAlgothms = settingProvider.getAlgorithms();
		if (runtimeAlgothms.length != settingAlgothms.length) {
			throw new LedgerException("Crypto service provider[" + settingProvider.getName()
					+ "] has not registered in runtime of current peer!");
		}
		HashMap<Short, CryptoAlgorithm> runtimeAlgothmMap = new HashMap<Short, CryptoAlgorithm>();
		for (CryptoAlgorithm alg : runtimeAlgothms) {
			runtimeAlgothmMap.put(alg.code(), alg);
		}
		for (CryptoAlgorithm alg : settingAlgothms) {
			CryptoAlgorithm regAlg = runtimeAlgothmMap.get(alg.code());
			if (regAlg == null) {
				throw new LedgerException(
						String.format("Crypto algorithm[%s] is not defined by provider[%s] in runtime of current peer!",
								alg.toString(), registeredProvider.getName()));
			}
			if (!regAlg.name().equals(alg.name())) {
				throw new LedgerException(String.format(
						"Crypto algorithm[%s] do not match the same code algorithm[%s] defined by provider[%s] in runtime of current peer!",
						CryptoAlgorithm.getString(alg), CryptoAlgorithm.getString(regAlg),
						registeredProvider.getName()));
			}
		}
	}

	@Override
	public void unregister(HashDigest ledgerHash) {
		LedgerRepositoryContext ledgerCtx = ledgers.remove(ledgerHash);
		if (ledgerCtx != null) {
			ledgerCtx.ledgerRepo.close();
		}
	}

//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see com.jd.blockchain.ledger.core.LedgerManager#newLedger(com.jd.blockchain.
//	 * ledger.core.ConsensusConfig, com.jd.blockchain.ledger.core.CryptoConfig)
//	 */
//	@Override
//	public LedgerEditor newLedger(LedgerInitSetting initSetting, KVStorageService storageService) {
//		LedgerEditor genesisBlockEditor = LedgerTransactionalEditor.createEditor(initSetting, LEDGER_PREFIX,
//				storageService.getExPolicyKVStorage(), storageService.getVersioningKVStorage());
//		return genesisBlockEditor;
//	}

	static String getLedgerStoragePrefix(HashDigest ledgerHash) {
		String base58LedgerHash = Base58Utils.encode(ledgerHash.toBytes());
		return LEDGER_PREFIX + base58LedgerHash + LedgerConsts.KEY_SEPERATOR;
	}

	
	private static class LedgerRepositoryContext {

		public final LedgerRepository ledgerRepo;

		@SuppressWarnings("unused")
		public final KVStorageService storageService;

		public LedgerRepositoryContext(LedgerRepository ledgerRepo, KVStorageService storageService) {
			this.ledgerRepo = ledgerRepo;
			this.storageService = storageService;
		}
	}
}
