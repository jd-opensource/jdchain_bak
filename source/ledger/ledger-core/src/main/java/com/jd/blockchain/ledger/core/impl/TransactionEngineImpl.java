package com.jd.blockchain.ledger.core.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.jd.blockchain.ledger.LedgerBlock;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.blockchain.crypto.hash.HashDigest;
import com.jd.blockchain.ledger.core.LedgerDataSet;
import com.jd.blockchain.ledger.core.LedgerEditor;
import com.jd.blockchain.ledger.core.LedgerRepository;
import com.jd.blockchain.ledger.core.LedgerService;
import com.jd.blockchain.ledger.service.TransactionBatchProcess;
import com.jd.blockchain.ledger.service.TransactionEngine;

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

		LedgerBlock ledgerBlock = ledgerRepo.getLatestBlock();
		LedgerEditor newBlockEditor = ledgerRepo.createNextBlock();
		LedgerDataSet previousBlockDataset = ledgerRepo.getDataSet(ledgerBlock);
		batch = new InnerTransactionBatchProcessor(ledgerHash, newBlockEditor, previousBlockDataset, opHdlRegs,
				ledgerService, ledgerBlock.getHeight());
		batchs.put(ledgerHash, batch);
		return batch;
	}

	@Override
	public TransactionBatchProcess getBatch(HashDigest ledgerHash) {
		return batchs.get(ledgerHash);
	}

	private void finishBatch(HashDigest ledgerHash) {
		batchs.remove(ledgerHash);
	}

	private class InnerTransactionBatchProcessor extends TransactionBatchProcessor {

		private HashDigest ledgerHash;

		private long blockHeight;

		/**
		 * 创建交易批处理器；
		 * 
		 * @param ledgerHash
		 *            账本哈希；
		 * @param newBlockEditor
		 *            新区块的数据编辑器；
		 * @param previousBlockDataset
		 *            新区块的前一个区块的数据集；即未提交新区块之前的经过共识的账本最新数据集；
		 * @param opHandles
		 *            操作处理对象注册表；
		 */
		public InnerTransactionBatchProcessor(HashDigest ledgerHash, LedgerEditor newBlockEditor,
				LedgerDataSet previousBlockDataset, OperationHandleRegisteration opHandles,
				LedgerService ledgerService, long blockHeight) {
			super(newBlockEditor, previousBlockDataset, opHandles, ledgerService);
			this.ledgerHash = ledgerHash;
			this.blockHeight = blockHeight;
		}

		@Override
		protected void onCommitted() {
			super.onCommitted();
			finishBatch(ledgerHash);
		}

		@Override
		protected void onCanceled() {
			super.onCanceled();
			finishBatch(ledgerHash);
		}

		@Override
		public long blockHeight() {
			return this.blockHeight;
		}
	}
}
