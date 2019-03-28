//package com.jd.blockchain.ledger.core.impl;
//
//import com.jd.blockchain.crypto.hash.HashDigest;
//import com.jd.blockchain.ledger.TransactionState;
//import com.jd.blockchain.ledger.LedgerBlock;
//import com.jd.blockchain.ledger.LedgerTransaction;
//import com.jd.blockchain.ledger.TransactionRequest;
//import com.jd.blockchain.ledger.core.LedgerDataSet;
//import com.jd.blockchain.ledger.core.LedgerEditor;
//import com.jd.blockchain.ledger.core.LedgerManage;
//import com.jd.blockchain.ledger.core.LedgerTransactionContext;
//import com.jd.blockchain.ledger.core.PrefixAppender;
//import com.jd.blockchain.storage.service.ExPolicyKVStorage;
//import com.jd.blockchain.storage.service.KVStorageService;
//import com.jd.blockchain.storage.service.VersioningKVStorage;
//import com.jd.blockchain.storage.service.utils.BufferedKVStorage;
//
///**
// * 账本初始化；<br>
// * 
// * 初始生成账本时，所有的KV数据先缓冲写入到内存中，待计算得到账本 hash 之后，再重写入到与账本hash相关的持久化存储；
// * 
// * @author huanghaiquan
// *
// */
//class LedgerInitializer implements LedgerEditor {
//
//	private KVStorageService baseStorage;
//
//	private GenesisLedgerStorageProxy ledgerStorageProxy;
//
//	private BufferedKVStorage genesisBufferedStorage;
//
//	private LedgerEditor genesisBlockEditor;
//	
//	private LedgerBlock genesisBlock;
//	
//	private LedgerManage ledgerManager;
//
//	LedgerInitializer(LedgerEditor genesisBlockEditor, BufferedKVStorage bufferedStorage,
//			GenesisLedgerStorageProxy ledgerStorageProxy, KVStorageService kvStorage, LedgerManage ledgerManager) {
//		this.genesisBlockEditor = genesisBlockEditor;
//		this.genesisBufferedStorage = bufferedStorage;
//		this.ledgerStorageProxy = ledgerStorageProxy;
//		this.baseStorage = kvStorage;
//		
//		this.ledgerManager = ledgerManager;
//	}
//
//	@Override
//	public LedgerTransactionContext newTransaction(TransactionRequest txRequest) {
//		return genesisBlockEditor.newTransaction(txRequest);
//	}
//
//	@Override
//	public LedgerBlock prepare() {
//		// create genesis block；
//		genesisBlock = genesisBlockEditor.prepare();
//		
//		return genesisBlock;
//	}
//
//	@Override
//	public void commit() {
//		// commit data of editor; it will flush data to genesisBufferedStorage;
//		genesisBlockEditor.commit();
//		
//		// redirect persistence to storage which created for this new ledger with ledger hash;
//		HashDigest ledgerHash = genesisBlock.getHash();
//		String ledgerPrefix =LedgerManager.getLedgerStoragePrefix(ledgerHash);
//		ExPolicyKVStorage ledgerExStorage = PrefixAppender.prefix(ledgerPrefix, baseStorage.getExPolicyKVStorage());
//		VersioningKVStorage ledgerVerStorage = PrefixAppender.prefix(ledgerPrefix, baseStorage.getVersioningKVStorage());
//
//		// ready to persistent;
//		ledgerStorageProxy.setPersistentStorage(ledgerExStorage, ledgerVerStorage);
//	
//		// flush output;
//		genesisBufferedStorage.flush();
//	}
//
//	@Override
//	public void cancel() {
//		genesisBlockEditor.cancel();
//	}
//}