//package com.jd.blockchain.peer.consensus;
//
//import com.jd.blockchain.binaryproto.BinaryEncodingUtils;
//import com.jd.blockchain.consensus.event.EventEntity;
//import com.jd.blockchain.consensus.event.EventProducer;
//import com.jd.blockchain.consensus.service.MessageHandle;
//import com.jd.blockchain.consensus.service.StateSnapshot;
//import com.jd.blockchain.crypto.hash.HashDigest;
//import com.jd.blockchain.ledger.LedgerBlock;
//import com.jd.blockchain.ledger.TransactionRequest;
//import com.jd.blockchain.ledger.TransactionResponse;
//import com.jd.blockchain.ledger.TransactionState;
//import com.jd.blockchain.ledger.service.TransactionBatchProcess;
//import com.jd.blockchain.ledger.service.TransactionBatchResultHandle;
//import com.jd.blockchain.ledger.service.TransactionEngine;
//import com.lmax.disruptor.BlockingWaitStrategy;
//import com.lmax.disruptor.EventFactory;
//import com.lmax.disruptor.EventHandler;
//import com.lmax.disruptor.RingBuffer;
//import com.lmax.disruptor.dsl.Disruptor;
//import com.lmax.disruptor.dsl.ProducerType;
//import my.utils.codec.Base58Utils;
//import my.utils.concurrent.AsyncFuture;
//import my.utils.concurrent.CompletableAsyncFuture;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.Iterator;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.atomic.AtomicLong;
//import java.util.concurrent.locks.ReentrantLock;
//
///**
// * @author huanghaiquan
// *
// */
//@Component
//public class ConsensusMessageDispatcher2 implements MessageHandle {
//
//	@Autowired
//	private TransactionEngine txEngine;
//
//	private Map<String, RealmProcessor> realmProcessorMap = new ConcurrentHashMap<>();
//
//	private ReentrantLock beginLock = new ReentrantLock();
//
//	@Override
//	public String beginBatch(String realmName) {
//		RealmProcessor realmProcessor = realmProcessorMap.get(realmName);
//		if (realmProcessor == null) {
//			try {
//				beginLock.lock();
//				realmProcessor = realmProcessorMap.get(realmName);
//				if (realmProcessor == null) {
//					realmProcessor = initRealmProcessor(realmName);
//					realmProcessorMap.put(realmName, realmProcessor);
//				}
//			} finally {
//				beginLock.unlock();
//			}
//		}
//		return realmProcessor.newBatchId();
//	}
//
//	@Override
//	public AsyncFuture<byte[]> processOrdered(int messageId, byte[] message, String realmName, String batchId) {
//		// TODO 要求messageId在同一个批次不重复，但目前暂不验证
//		RealmProcessor realmProcessor = realmProcessorMap.get(realmName);
//		if (realmProcessor == null) {
//			throw new IllegalArgumentException("RealmName is not init!");
//		}
//		if (!realmProcessor.getCurrBatchId().equalsIgnoreCase(batchId)) {
//			throw new IllegalArgumentException("BatchId is not begin!");
//		}
//		TransactionRequest txRequest = BinaryEncodingUtils.decode(message);
//		return realmProcessor.schedule(txRequest);
//	}
//
//	@Override
//	public StateSnapshot completeBatch(String realmName, String batchId) {
//		RealmProcessor realmProcessor = realmProcessorMap.get(realmName);
//		if (realmProcessor == null) {
//			throw new IllegalArgumentException("RealmName is not init!");
//		}
//		if (!realmProcessor.getCurrBatchId().equalsIgnoreCase(batchId)) {
//			throw new IllegalArgumentException("BatchId is not begin!");
//		}
//		return realmProcessor.complete();
//	}
//
//	@Override
//	public void commitBatch(String realmName, String batchId) {
//		RealmProcessor realmProcessor = realmProcessorMap.get(realmName);
//		if (realmProcessor == null) {
//			throw new IllegalArgumentException("RealmName is not init!");
//		}
//		if (!realmProcessor.getCurrBatchId().equalsIgnoreCase(batchId)) {
//			throw new IllegalArgumentException("BatchId is not begin!");
//		}
//		realmProcessor.commit();
//	}
//
//	@Override
//	public void rollbackBatch(String realmName, String batchId, int reasonCode) {
//		RealmProcessor realmProcessor = realmProcessorMap.get(realmName);
//		if (realmProcessor == null) {
//			throw new IllegalArgumentException("RealmName is not init!");
//		}
//		if (!realmProcessor.getCurrBatchId().equalsIgnoreCase(batchId)) {
//			throw new IllegalArgumentException("BatchId is not begin!");
//		}
//		realmProcessor.rollback(reasonCode);
//	}
//
//	@Override
//	public AsyncFuture<byte[]> processUnordered(byte[] message) {
//		// TODO Auto-generated method stub
//		throw new IllegalArgumentException("Not implemented!");
//	}
//
//	private RealmProcessor initRealmProcessor(String realmName) {
//		RealmProcessor realmProcessor = new RealmProcessor();
//		byte[] hashBytes = Base58Utils.decode(realmName);
//		HashDigest ledgerHash = new HashDigest(hashBytes);
//		realmProcessor.realmName = realmName;
//		realmProcessor.ledgerHash = ledgerHash;
//		return realmProcessor;
//	}
//
//	private final class RealmProcessor {
//
//		private String currBatchId;
//
////		private final ExecutorService txExecutor = Executors.newFixedThreadPool(4);
//
////		private Map<TransactionResponse, CompletableAsyncFuture<byte[]>> txResponseMap;
//
//		private LinkedList<TxResponseExtension> txResponseExtensions;
//
//		private TransactionBatchResultHandle batchResultHandle;
//
//		private final AtomicLong batchIdIndex = new AtomicLong();
//
////		private LedgerBlock currBlock;
//
//        private TransactionBatchProcess txBatchProcess;
//
//        private EventProducer eventProducer;
//
//		HashDigest ledgerHash;
//
//		String realmName;
//
//		public RealmProcessor() {
//			BlockEventHandler eventHandler = new BlockEventHandler();
//			Disruptor<EventEntity<BlockResponse>> disruptor =
//					new Disruptor<>(new BlockEventFactory(),
//							BlockEventFactory.BUFFER_SIZE, r -> {
//						return new Thread(r);
//					}, ProducerType.SINGLE, new BlockingWaitStrategy());
//
//			disruptor.handleEventsWith(eventHandler);
//			disruptor.start();
//			RingBuffer<EventEntity<BlockResponse>> ringBuffer = disruptor.getRingBuffer();
//			this.eventProducer = new BlockEventProducer(ringBuffer);
//		}
//
//		public String getRealmName() {
//			return realmName;
//		}
//
//		public TransactionBatchProcess getTxBatchProcess() {
//			return txBatchProcess;
//		}
//
//		public AtomicLong getBatchIdIndex() {
//			return batchIdIndex;
//		}
//
//		public HashDigest getLedgerHash() {
//			return ledgerHash;
//		}
//
//		public String getCurrBatchId() {
//			return currBatchId;
//		}
//
//		public synchronized String newBatchId() {
//			if (currBatchId == null) {
//				currBatchId = getRealmName() + "-" + getBatchIdIndex().getAndIncrement();
//			}
//			if (txBatchProcess == null) {
//				txBatchProcess = txEngine.createNextBatch(ledgerHash);
//			}
////			if (txResponseMap == null) {
////				txResponseMap = new ConcurrentHashMap<>();
////			}
//			if (txResponseExtensions == null) {
//				txResponseExtensions = new LinkedList<>();
//			}
//			return currBatchId;
//		}
//
//		public AsyncFuture<byte[]> schedule(TransactionRequest txRequest) {
//			CompletableAsyncFuture<byte[]> asyncTxResult = new CompletableAsyncFuture<>();
//			TransactionResponse resp = getTxBatchProcess().schedule(txRequest);
//			TxResponseExtension extension = new TxResponseExtension(resp, asyncTxResult);
//			txResponseExtensions.addFirst(extension);
////			txResponseMap.put(resp, asyncTxResult);
////			txExecutor.execute(() -> {
////			    if (txBatchProcess == null) {
////                    txBatchProcess = txEngine.createNextBatch(ledgerHash);
////                }
////				TransactionResponse resp = getTxBatchProcess().schedule(txRequest);
////			    if (txResponseMap == null) {
////			    	txResponseMap = new ConcurrentHashMap<>();
////				}
////				txResponseMap.put(resp, asyncTxResult);
////			});
//			return asyncTxResult;
//		}
//
//		public StateSnapshot complete() {
////			CompletableAsyncFuture<StateSnapshot> asyncStateSnapshot = new CompletableAsyncFuture<>();
//			batchResultHandle = getTxBatchProcess().prepare();
//			LedgerBlock currBlock = batchResultHandle.getBlock();
////			List<TxResponseExtension> extensions = new ArrayList<>();
////			Collections.copy(extensions, new ArrayList<>(txResponseExtensions));
//			BlockResponse blockResponse = new BlockResponse(currBlock, new LinkedList<>(txResponseExtensions));
//			this.eventProducer.publish(blockResponse);
////			handleResponse(currBlock, new LinkedList<>(txResponseExtensions));
//			txResponseExtensions = null;
//
////
////
////			// 填充应答结果
////			for (Map.Entry<TransactionResponse, CompletableAsyncFuture<byte[]>> entry : txResponseMap.entrySet()) {
////				CompletableAsyncFuture<byte[]> asyncResult = entry.getValue();
////				TxResponse txResponse = new TxResponse(entry.getKey());
////				txResponse.setBlockHeight(currBlock.getHeight());
////				txResponse.setBlockHash(currBlock.getHash());
////				asyncResult.complete(BinaryEncodingUtils.encode(txResponse, TransactionResponse.class));
////			}
//			BlockStateSnapshot blockStateSnapshot = new BlockStateSnapshot(currBlock.getHeight(), currBlock.getHash());
////			asyncStateSnapshot.complete(blockStateSnapshot);
//
////			txExecutor.execute(() -> {
////				batchResultHandle = getTxBatchProcess().prepare();
////				currBlock = batchResultHandle.getBlock();
////				// 填充应答结果
////				for (Map.Entry<TransactionResponse, CompletableAsyncFuture<byte[]>> entry : txResponseMap.entrySet()) {
////					CompletableAsyncFuture<byte[]> asyncResult = entry.getValue();
////					TxResponse txResponse = new TxResponse(entry.getKey());
////					txResponse.setBlockHeight(currBlock.getHeight());
////					txResponse.setBlockHash(currBlock.getHash());
////					asyncResult.complete(BinaryEncodingUtils.encode(txResponse, TransactionResponse.class));
////				}
////				BlockStateSnapshot blockStateSnapshot = new BlockStateSnapshot(currBlock.getHeight(), currBlock.getHash());
////				asyncStateSnapshot.complete(blockStateSnapshot);
////			});
////			return asyncStateSnapshot.get();
//			return blockStateSnapshot;
//		}
//
//		public void commit() {
//			if (batchResultHandle == null) {
//				throw new IllegalArgumentException("BatchResultHandle is null, complete() is not execute !");
//			}
//			batchResultHandle.commit();
////			txResponseMap = null;
//			currBatchId = null;
//			txBatchProcess = null;
//		}
//
//		public void rollback(int reasonCode) {
//			batchResultHandle.cancel(TransactionState.valueOf((byte)reasonCode));
//		}
//
////		private void handleResponse(final LedgerBlock block, final List<TxResponseExtension> txResponseExtensions) {
////			txExecutor.execute(() -> {
////				Iterator<TxResponseExtension> iterator = txResponseExtensions.iterator();
////				while(iterator.hasNext()){
////					TxResponseExtension data = iterator.next();
////					CompletableAsyncFuture<byte[]> asyncResult = data.getAsyncTxResult();
////					TxResponse txResponse = new TxResponse(data.getResponse());
////					txResponse.setBlockHeight(block.getHeight());
////					txResponse.setBlockHash(block.getHash());
////					asyncResult.complete(BinaryEncodingUtils.encode(txResponse, TransactionResponse.class));
////				}
////			});
////		}
//
//		private final class TxResponse implements TransactionResponse {
//
//			private long blockHeight;
//
//			private HashDigest blockHash;
//
//			private TransactionResponse txResp;
//
//			public TxResponse(TransactionResponse txResp) {
//				this.txResp = txResp;
//			}
//
//			public void setBlockHeight(long blockHeight) {
//				this.blockHeight = blockHeight;
//			}
//
//			public void setBlockHash(HashDigest blockHash) {
//				this.blockHash = blockHash;
//			}
//
//			@Override
//			public HashDigest getContentHash() {
//				return this.txResp.getContentHash();
//			}
//
//			@Override
//			public TransactionState getExecutionState() {
//				return this.txResp.getExecutionState();
//			}
//
//			@Override
//			public HashDigest getBlockHash() {
//				return this.blockHash;
//			}
//
//			@Override
//			public long getBlockHeight() {
//				return this.blockHeight;
//			}
//
//			@Override
//			public boolean isSuccess() {
//				return this.txResp.isSuccess();
//			}
//		}
//
//		private final class TxResponseExtension {
//
//			private TransactionResponse response;
//
//			private CompletableAsyncFuture<byte[]> asyncTxResult;
//
//			public TxResponseExtension(TransactionResponse response, CompletableAsyncFuture<byte[]> asyncTxResult) {
//				this.response = response;
//				this.asyncTxResult = asyncTxResult;
//			}
//
//			public TransactionResponse getResponse() {
//				return response;
//			}
//
//			public void setResponse(TransactionResponse response) {
//				this.response = response;
//			}
//
//			public CompletableAsyncFuture<byte[]> getAsyncTxResult() {
//				return asyncTxResult;
//			}
//
//			public void setAsyncTxResult(CompletableAsyncFuture<byte[]> asyncTxResult) {
//				this.asyncTxResult = asyncTxResult;
//			}
//		}
//
//		private final class BlockResponse {
//
//			private LedgerBlock block;
//
//			private LinkedList<TxResponseExtension> txResponseExtensions;
//
//			public BlockResponse(LedgerBlock block, LinkedList<TxResponseExtension> txResponseExtensions) {
//				this.block = block;
//				this.txResponseExtensions = txResponseExtensions;
//			}
//
//			public LedgerBlock getBlock() {
//				return block;
//			}
//
//			public void setBlock(LedgerBlock block) {
//				this.block = block;
//			}
//
//			public LinkedList<TxResponseExtension> getTxResponseExtensions() {
//				return txResponseExtensions;
//			}
//
//			public void setTxResponseExtensions(LinkedList<TxResponseExtension> txResponseExtensions) {
//				this.txResponseExtensions = txResponseExtensions;
//			}
//		}
//
//		private final class BlockStateSnapshot implements StateSnapshot {
//
//			private long id;
//
//			private byte[] snapshotBytes;
//
//			public BlockStateSnapshot(long id, byte[] snapshotBytes) {
//				this.id = id;
//				this.snapshotBytes = snapshotBytes;
//			}
//
//			public BlockStateSnapshot(long id, HashDigest hash) {
//				this(id, hash.toBytes());
//			}
//
//			@Override
//			public long getId() {
//				return id;
//			}
//
//			@Override
//			public byte[] getSnapshot() {
//				return snapshotBytes;
//			}
//		}
//
//		private final class BlockEventHandler implements EventHandler<EventEntity<BlockResponse>> {
//
//			@Override
//			public void onEvent(EventEntity<BlockResponse> event, long sequence, boolean endOfBatch) throws Exception {
//				BlockResponse blockResponse = event.getEntity();
//				final LedgerBlock block = blockResponse.getBlock();
//
//				final List<TxResponseExtension> txResponseExtensions = blockResponse.getTxResponseExtensions();
//				Iterator<TxResponseExtension> iterator = txResponseExtensions.iterator();
//				while(iterator.hasNext()){
//					TxResponseExtension data = iterator.next();
//					CompletableAsyncFuture<byte[]> asyncResult = data.getAsyncTxResult();
//					TxResponse txResponse = new TxResponse(data.getResponse());
//					txResponse.setBlockHeight(block.getHeight());
//					txResponse.setBlockHash(block.getHash());
//					asyncResult.complete(BinaryEncodingUtils.encode(txResponse, TransactionResponse.class));
//				}
//			}
//		}
//
//		private class BlockEventFactory implements EventFactory<EventEntity<BlockResponse>> {
//
//			public static final int BUFFER_SIZE = 64 * 1024;
//
//			@Override
//			public EventEntity<BlockResponse> newInstance() {
//				return new EventEntity<>();
//			}
//		}
//
//		private class BlockEventProducer implements EventProducer<BlockResponse> {
//
//			private final RingBuffer<EventEntity<BlockResponse>> ringBuffer;
//
//			public BlockEventProducer(RingBuffer<EventEntity<BlockResponse>> ringBuffer) {
//				this.ringBuffer = ringBuffer;
//			}
//
//			@Override
//			public void publish(BlockResponse entity) {
//				long sequence = ringBuffer.next();
//				try {
//					EventEntity<BlockResponse> event = ringBuffer.get(sequence);
//					event.setEntity(entity);
//				} finally {
//					this.ringBuffer.publish(sequence);
//				}
//			}
//		}
//	}
//}
