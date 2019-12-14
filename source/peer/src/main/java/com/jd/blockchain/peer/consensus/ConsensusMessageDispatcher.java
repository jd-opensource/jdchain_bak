package com.jd.blockchain.peer.consensus;

import com.jd.blockchain.binaryproto.BinaryProtocol;
import com.jd.blockchain.ledger.LedgerBlock;
import com.jd.blockchain.ledger.OperationResult;
import com.jd.blockchain.ledger.TransactionRequest;
import com.jd.blockchain.ledger.TransactionResponse;
import com.jd.blockchain.ledger.TransactionState;
import com.jd.blockchain.ledger.core.TransactionBatchProcessor;
import com.jd.blockchain.ledger.core.TransactionEngineImpl;
import com.jd.blockchain.service.TransactionBatchProcess;
import com.jd.blockchain.service.TransactionBatchResultHandle;
import com.jd.blockchain.service.TransactionEngine;
import com.jd.blockchain.utils.codec.Base58Utils;
import com.jd.blockchain.utils.concurrent.AsyncFuture;
import com.jd.blockchain.utils.concurrent.CompletableAsyncFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jd.blockchain.consensus.service.MessageHandle;
import com.jd.blockchain.consensus.service.StateSnapshot;
import com.jd.blockchain.crypto.HashDigest;

import javax.swing.plaf.nimbus.State;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author huanghaiquan
 *
 */
@Component
public class ConsensusMessageDispatcher implements MessageHandle {

	@Autowired
	private TransactionEngine txEngine;

	// todo 可能存在内存溢出的问题
	private final Map<String, RealmProcessor> realmProcessorMap = new ConcurrentHashMap<>();

	private final ReentrantLock beginLock = new ReentrantLock();

	@Override
	public String beginBatch(String realmName) {
		RealmProcessor realmProcessor = realmProcessorMap.get(realmName);
		if (realmProcessor == null) {
			beginLock.lock();
			try {
				realmProcessor = realmProcessorMap.get(realmName);
				if (realmProcessor == null) {
					realmProcessor = initRealmProcessor(realmName);
					realmProcessorMap.put(realmName, realmProcessor);
				}
			} finally {
				beginLock.unlock();
			}
		}
		return realmProcessor.newBatchId();
	}

	@Override
	public StateSnapshot getStateSnapshot(String realmName) {
		RealmProcessor realmProcessor = realmProcessorMap.get(realmName);
		if (realmProcessor == null) {
			throw new IllegalArgumentException("RealmName is not init!");
		}

		return realmProcessor.getStateSnapshot();

	}

	@Override
	public StateSnapshot getGenisStateSnapshot(String realmName) {
		RealmProcessor realmProcessor = realmProcessorMap.get(realmName);
		if (realmProcessor == null) {
			throw new IllegalArgumentException("RealmName is not init!");
		}
		return realmProcessor.getGenisStateSnapshot();
	}

	@Override
	public AsyncFuture<byte[]> processOrdered(int messageId, byte[] message, String realmName, String batchId) {
		// TODO 要求messageId在同一个批次不重复，但目前暂不验证
		RealmProcessor realmProcessor = realmProcessorMap.get(realmName);
		if (realmProcessor == null) {
			throw new IllegalArgumentException("RealmName is not init!");
		}
		if (!realmProcessor.getCurrBatchId().equalsIgnoreCase(batchId)) {
			throw new IllegalArgumentException("BatchId is not begin!");
		}
		TransactionRequest txRequest = BinaryProtocol.decode(message);
		return realmProcessor.schedule(txRequest);
	}

	@Override
	public StateSnapshot completeBatch(String realmName, String batchId) {
		RealmProcessor realmProcessor = realmProcessorMap.get(realmName);
		if (realmProcessor == null) {
			throw new IllegalArgumentException("RealmName is not init!");
		}
		if (!realmProcessor.getCurrBatchId().equalsIgnoreCase(batchId)) {
			throw new IllegalArgumentException("BatchId is not begin!");
		}
		return realmProcessor.complete();
	}

	@Override
	public void commitBatch(String realmName, String batchId) {
		RealmProcessor realmProcessor = realmProcessorMap.get(realmName);
		if (realmProcessor == null) {
			throw new IllegalArgumentException("RealmName is not init!");
		}
		if (!realmProcessor.getCurrBatchId().equalsIgnoreCase(batchId)) {
			throw new IllegalArgumentException("BatchId is not begin!");
		}
		realmProcessor.commit();
//		realmProcessorMap.remove(realmName);
	}

	@Override
	public void rollbackBatch(String realmName, String batchId, int reasonCode) {
		RealmProcessor realmProcessor = realmProcessorMap.get(realmName);
		if (realmProcessor == null) {
			throw new IllegalArgumentException("RealmName is not init!");
		}
		if (!realmProcessor.getCurrBatchId().equalsIgnoreCase(batchId)) {
			throw new IllegalArgumentException("BatchId is not begin!");
		}
		realmProcessor.rollback(reasonCode);
//		realmProcessorMap.remove(realmName);
	}

	@Override
	public AsyncFuture<byte[]> processUnordered(byte[] message) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException("Not implemented!");
	}

	private RealmProcessor initRealmProcessor(String realmName) {
		RealmProcessor realmProcessor = new RealmProcessor();
		byte[] hashBytes = Base58Utils.decode(realmName);
		HashDigest ledgerHash = new HashDigest(hashBytes);
		realmProcessor.realmName = realmName;
		realmProcessor.ledgerHash = ledgerHash;
		return realmProcessor;
	}

	private final class RealmProcessor {

		private final Lock realmLock = new ReentrantLock();

		private String currBatchId;

		private final ExecutorService txExecutor = Executors.newSingleThreadExecutor();

		// todo 暂不处理队列溢出导致的OOM问题
		private final ExecutorService asyncBlExecutor = Executors.newSingleThreadExecutor();

		private Map<TransactionResponse, CompletableAsyncFuture<byte[]>> txResponseMap;

		private TransactionBatchResultHandle batchResultHandle;

		private final AtomicLong batchIdIndex = new AtomicLong();

		private LedgerBlock currBlock;

        private TransactionBatchProcess txBatchProcess;

		HashDigest ledgerHash;

		String realmName;

		public String getRealmName() {
			return realmName;
		}

		public TransactionBatchProcess getTxBatchProcess() {
			return txBatchProcess;
		}

		public AtomicLong getBatchIdIndex() {
			return batchIdIndex;
		}

		public HashDigest getLedgerHash() {
			return ledgerHash;
		}

		public String getCurrBatchId() {
			return currBatchId;
		}

		public String newBatchId() {
			realmLock.lock();
			try {
				if (currBatchId == null) {
					currBatchId = getRealmName() + "-" + getBatchIdIndex().getAndIncrement();
				}
				if (txResponseMap == null) {
					txResponseMap = new ConcurrentHashMap<>();
				}
				if (txBatchProcess == null) {
					txBatchProcess = txEngine.createNextBatch(ledgerHash);
				}
			} finally {
				realmLock.unlock();
			}
			return currBatchId;
		}

		public StateSnapshot getStateSnapshot() {
			return new BlockStateSnapshot(((TransactionBatchProcessor)getTxBatchProcess()).getPreLatestBlockHeight(), ((TransactionBatchProcessor)getTxBatchProcess()).getPrevLatestBlockHash());
		}

		public StateSnapshot getGenisStateSnapshot() {
			return new BlockStateSnapshot(0, ((TransactionBatchProcessor)getTxBatchProcess()).getGenisBlockHash());
		}

		public AsyncFuture<byte[]> schedule(TransactionRequest txRequest) {
			CompletableAsyncFuture<byte[]> asyncTxResult = new CompletableAsyncFuture<>();
			TransactionResponse resp = getTxBatchProcess().schedule(txRequest);
			txResponseMap.put(resp, asyncTxResult);
//			txExecutor.execute(() -> {
//				TransactionResponse resp = getTxBatchProcess().schedule(txRequest);
//				txResponseMap.put(resp, asyncTxResult);
//			});
			return asyncTxResult;
		}

		public StateSnapshot complete() {
			batchResultHandle = getTxBatchProcess().prepare();
			currBlock = batchResultHandle.getBlock();
			long blockHeight = currBlock.getHeight();
			HashDigest blockHash = currBlock.getHash();
			asyncBlExecute(new HashMap<>(txResponseMap), blockHeight, blockHash);
			BlockStateSnapshot blockStateSnapshot = new BlockStateSnapshot(blockHeight, blockHash);
			return blockStateSnapshot;

//
//
//			CompletableAsyncFuture<StateSnapshot> asyncStateSnapshot = new CompletableAsyncFuture<>();
//			txExecutor.execute(() -> {
//				batchResultHandle = getTxBatchProcess().prepare();
//				currBlock = batchResultHandle.getBlock();
//				long blockHeight = currBlock.getHeight();
//				HashDigest blockHash = currBlock.getHash();
//				asyncBlExecute(new HashMap<>(txResponseMap), blockHeight, blockHash);
//				BlockStateSnapshot blockStateSnapshot = new BlockStateSnapshot(blockHeight, blockHash);
//				asyncStateSnapshot.complete(blockStateSnapshot);
//			});
//			return asyncStateSnapshot.get();
		}

		public void commit() {
			realmLock.lock();
			try {
				if (batchResultHandle == null) {
					throw new IllegalArgumentException("BatchResultHandle is null, complete() is not execute !");
				}
				batchResultHandle.commit();
				currBatchId = null;
				txResponseMap = null;
				txBatchProcess = null;
				batchResultHandle =null;
			} finally {
				realmLock.unlock();
			}
		}

		public void rollback(int reasonCode) {
			realmLock.lock();
			try {
				if (batchResultHandle != null) {
					batchResultHandle.cancel(TransactionState.valueOf((byte)reasonCode));
				}
				currBatchId = null;
				txResponseMap = null;
				txBatchProcess = null;
				batchResultHandle =  null;
				((TransactionEngineImpl) (txEngine)).freeBatch(ledgerHash);
				((TransactionEngineImpl) (txEngine)).resetNewBlockEditor(ledgerHash);
			} finally {
				realmLock.unlock();
			}
		}

		private void asyncBlExecute(Map<TransactionResponse, CompletableAsyncFuture<byte[]>> asyncMap,
									long blockHeight, HashDigest blockHash) {
			asyncBlExecutor.execute(() -> {
				// 填充应答结果
				for (Map.Entry<TransactionResponse, CompletableAsyncFuture<byte[]>> entry : asyncMap.entrySet()) {
					CompletableAsyncFuture<byte[]> asyncResult = entry.getValue();
					TxResponse txResponse = new TxResponse(entry.getKey());
					txResponse.setBlockHeight(blockHeight);
					txResponse.setBlockHash(blockHash);
					asyncResult.complete(BinaryProtocol.encode(txResponse, TransactionResponse.class));
				}
			});
		}

		private final class TxResponse implements TransactionResponse {

			private long blockHeight;

			private HashDigest blockHash;

			private TransactionResponse txResp;

			public TxResponse(TransactionResponse txResp) {
				this.txResp = txResp;
			}

			public void setBlockHeight(long blockHeight) {
				this.blockHeight = blockHeight;
			}

			public void setBlockHash(HashDigest blockHash) {
				this.blockHash = blockHash;
			}

			@Override
			public HashDigest getContentHash() {
				return this.txResp.getContentHash();
			}

			@Override
			public TransactionState getExecutionState() {
				return this.txResp.getExecutionState();
			}

			@Override
			public HashDigest getBlockHash() {
				return this.blockHash;
			}

			@Override
			public long getBlockHeight() {
				return this.blockHeight;
			}

			@Override
			public boolean isSuccess() {
				return this.txResp.isSuccess();
			}

			@Override
			public OperationResult[] getOperationResults() {
				return txResp.getOperationResults();
			}
		}

		private final class BlockStateSnapshot implements StateSnapshot {

			private long id;

			private byte[] snapshotBytes;

			public BlockStateSnapshot(long id, byte[] snapshotBytes) {
				this.id = id;
				this.snapshotBytes = snapshotBytes;
			}

			public BlockStateSnapshot(long id, HashDigest hash) {
				this(id, hash.toBytes());
			}

			@Override
			public long getId() {
				return id;
			}

			@Override
			public byte[] getSnapshot() {
				return snapshotBytes;
			}
		}
	}
}
