package com.jd.blockchain.ledger.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.service.TransactionBatchProcess;
import com.jd.blockchain.service.TransactionEngine;

public class TransactionEngineImpl implements TransactionEngine {

	@Autowired
	private LedgerService ledgerService;

	@Autowired
	private OperationHandleRegisteration opHdlRegs;

	private Map<HashDigest, TransactionBatchProcessor> batchs = new ConcurrentHashMap<>();

	public TransactionEngineImpl() {
	}

	public TransactionEngineImpl(LedgerService ledgerService, OperationHandleRegisteration opHdlRegs) {
		this.ledgerService = ledgerService;
		this.opHdlRegs = opHdlRegs;
	}

	@Override
	public synchronized TransactionBatchProcess createNextBatch(HashDigest ledgerHash) {
		TransactionBatchProcessor batch = batchs.get(ledgerHash);
		if (batch != null) {
			throw new IllegalStateException(
					"The transaction batch process of ledger already exist! Cann't create another one!");
		}

		LedgerRepository ledgerRepo = ledgerService.getLedger(ledgerHash);

		batch = new InnerTransactionBatchProcessor(ledgerRepo,
				opHdlRegs);
		batchs.put(ledgerHash, batch);
		return batch;
	}

	@Override
	public TransactionBatchProcess getBatch(HashDigest ledgerHash) {
		return batchs.get(ledgerHash);
	}

	public void freeBatch(HashDigest ledgerHash) {
		finishBatch(ledgerHash);
	}

	public void resetNewBlockEditor(HashDigest ledgerHash) {

		LedgerRepository ledgerRepo = ledgerService.getLedger(ledgerHash);
		((LedgerRepositoryImpl)ledgerRepo).resetNextBlockEditor();
	}

	private void finishBatch(HashDigest ledgerHash) {
		batchs.remove(ledgerHash);
	}

	private class InnerTransactionBatchProcessor extends TransactionBatchProcessor {

//		private HashDigest ledgerHash;

		/**
		 * 创建交易批处理器；
		 * 
		 * @param ledgerRepo           账本；
		 * @param handlesRegisteration 操作处理对象注册表；
		 *
		 */
		public InnerTransactionBatchProcessor(LedgerRepository ledgerRepo,
				OperationHandleRegisteration handlesRegisteration) {
			super(ledgerRepo, handlesRegisteration);
//			ledgerHash = ledgerRepo.getHash();
		}

		@Override
		protected void onCommitted() {
			super.onCommitted();
			finishBatch(getLedgerHash());
		}

		@Override
		protected void onCanceled() {
			super.onCanceled();
			finishBatch(getLedgerHash());
		}

	}
}
