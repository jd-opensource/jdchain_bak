//package com.jd.blockchain.peer.consensus;
//
//import com.jd.blockchain.binaryproto.BinaryEncodingUtils;
//import com.jd.blockchain.consensus.BatchConsensusListener;
//import com.jd.blockchain.consensus.action.ActionRequest;
//import com.jd.blockchain.consensus.action.ActionRequestData;
//import com.jd.blockchain.consensus.bft.*;
//import com.jd.blockchain.crypto.CryptoAlgorithm;
//import com.jd.blockchain.crypto.CryptoUtils;
//import com.jd.blockchain.ledger.*;
//import com.jd.blockchain.ledger.data.TxContentBlob;
//import com.jd.blockchain.ledger.data.TxRequestMessage;
//import my.utils.io.BytesEncoding;
//import my.utils.io.NumberMask;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.jd.blockchain.crypto.hash.HashDigest;
//import com.jd.blockchain.ledger.core.TransactionBatchProcess;
//import com.jd.blockchain.ledger.core.TransactionBatchResultHandle;
//import com.jd.blockchain.ledger.core.TransactionEngine;
//import com.jd.blockchain.sdk.bftsmart.ConsensusTransactionService;
//
//import java.io.ByteArrayOutputStream;
//import java.util.concurrent.*;
//import java.util.concurrent.atomic.AtomicBoolean;
//import java.util.concurrent.atomic.AtomicLong;
//
///**
// * @author huanghaiquan
// *
// */
//@Service
//public class BlockchainConsensusServiceImpl implements ConsensusTransactionService, BatchConsensusListener {
//
//	private static final Logger LOGGER = LoggerFactory.getLogger(BlockchainConsensusServiceImpl.class);
//
//	private final ExecutorService sendExecutorService = Executors.newFixedThreadPool(10);
//
//	private final ScheduledExecutorService timerEexecutorService = new ScheduledThreadPoolExecutor(1);
//	//结块之前接收的消息数
//	private int receiveCounts = 0;
//	//区块提交之后，后续第一个交易到达的时间
//	private long initTimeStamp = 0;
//	//达到一定的交易数后发送结块共识消息
//	private int TransactionCount = 100;
//	//两秒内没有收到新的共识消息，发送结块共识消息，把不足TransactionCount的消息结块入帐本，单位毫秒
//	private long TimeOut = 2000;
//
//	private final AtomicLong blockIndex = new AtomicLong();
//
//	private long currBlockIndex = 0L;
//
//	private final AtomicBoolean blockFlag = new AtomicBoolean(false);
//
//
//	@Autowired
//	private TransactionEngine txEngine;
//
//	//0.5 version implement
////	@Override
////	public void beforeBatch(byte[] groupId) {
////		// 以“账本哈希”为共识分组ID；
////		HashDigest ledgerHash = new HashDigest(groupId);
////		txEngine.createNextBatch(ledgerHash);
////
////		LOGGER.info("Create new block to process transaction batch! --[LedgerHash=" + ledgerHash.toBase58() + "]");
////	}
//
//	//0.6 version implement, not used
//	@Override
//	public void beforeBatch(byte[] groupId) {
////		// 以“账本哈希”为共识分组ID；
////		HashDigest ledgerHash = new HashDigest(groupId);
////		txEngine.createNextBatch(ledgerHash);
////
////		LOGGER.info("Create new block to process transaction batch! --[LedgerHash=" + ledgerHash.toBase58() + "]");
//	}
//
//	/**
//	 * 处理收到的交易消息，并返回结果；
//	 */
//	//0.5 version implement
////	@Override
////	public TransactionResponse process(TransactionRequest txRequest) {
////		TransactionBatchProcess txBatchProcess = txEngine.getBatch(txRequest.getTransactionContent().getLedgerHash());
////		return txBatchProcess.schedule(txRequest);
////	}
//
//	//由Leader Peer发送结块通知消息,需要PEER对此消息进行共识,达到共识结点结块的一致性
//	public void notifyCommit(final long currBlockIndex, HashDigest ledgerHash, BftsmartConsensusSetting bftsmartConsensusSetting, BftsmartTopology bftsmartTopology) {
//		//if peer is leader, send commit block consensus request
//		System.out.println(Thread.currentThread().getId() + " leader run notifyCommit = " + receiveCounts + " TransactionCount = " + TransactionCount);
//		if (receiveCounts >= TransactionCount) {
//			System.out.println(Thread.currentThread().getId() + "this.blockIndex = " + this.blockIndex.get() + ", currBlockIndex=" + currBlockIndex);
//			boolean isAdd = this.blockIndex.compareAndSet(currBlockIndex, currBlockIndex + 1);
//			System.out.println(Thread.currentThread().getId() + " leader run isadd = " + isAdd);
//			if (isAdd) {
//				sendExecutorService.execute(sendBlockMessage(ledgerHash, bftsmartConsensusSetting, bftsmartTopology));
//			}
//		}
//	}
//
//	private Runnable sendBlockMessage(HashDigest ledgerHash, BftsmartConsensusSetting bftsmartConsensusSetting, BftsmartTopology bftsmartTopology) {
//		Runnable runnable = () -> sendCommitBlockMessage(ledgerHash, bftsmartConsensusSetting, bftsmartTopology);
//		return runnable;
//	}
//    //发送结块通知消息
//	private void sendCommitBlockMessage(HashDigest ledgerHash, BftsmartConsensusSetting bftsmartConsensusSetting, BftsmartTopology bftsmartTopology) {
//
////		ActionRequestData request = new ActionRequestData();
////		request.setGroupId(ledgerHash.toBytes());
////		request.setHandleType("com.jd.blockchain.sdk.bftsmart.ConsensusTransactionService");
////		request.setHandleMethod("public abstract com.jd.blockchain.ledger.TransactionResponse com.jd.blockchain.sdk.bftsmart.ConsensusTransactionService.process(com.jd.blockchain.ledger.TransactionRequest)");
////        request.setTransactionType("COMMITBLOCK");
////
////		BlockchainKeyPair userKeyPeer = BlockchainKeyGenerator.getInstance().generate();
////
////		TxContentBlob txContentBlob = new TxContentBlob(ledgerHash);
////
////		byte[] reqBytes = BinaryEncodingUtils.encode(txContentBlob, TransactionContent.class);
////		HashDigest reqHash = CryptoUtils.hash(CryptoAlgorithm.SHA256).hash(reqBytes);
////		txContentBlob.setHash(reqHash);
////
////		TxRequestMessage transactionRequest = new TxRequestMessage(txContentBlob);
////
////		byte[] encodeBytes = BinaryEncodingUtils.encode(transactionRequest, TransactionRequest.class);
////
////		ByteArrayOutputStream out = new ByteArrayOutputStream();
////		BytesEncoding.write(encodeBytes, NumberMask.NORMAL, out);
////
////		request.setMessageBody(out.toByteArray());
////		byte[] commandReq = BinaryEncodingUtils.encode(request, ActionRequest.class);
////
////		BftsmartConsensusClient bftsmartConsensusClient = new BftsmartConsensusClient(0, bftsmartConsensusSetting, bftsmartTopology);
////		bftsmartConsensusClient.invokeOrdered(commandReq);
////
////		LOGGER.info(String.format("Send notify commit block msg success!"));
//	}
//
//    //0.6 version implement
//	@Override
//	public TransactionResponse process(TransactionRequest txRequest) {
////		System.out.println("peer process thread = " + Thread.currentThread().getId());
////		System.out.println("peer process object = " + this);
//		HashDigest ledgerHash = txRequest.getTransactionContent().getLedgerHash();
//		TransactionBatchProcess txBatchProcess = txEngine.getBatch(ledgerHash);
//
//		boolean isLeader = BftsmartConsensusUtils.getLeader();
//		BftsmartConsensusSetting bftsmartConsensusSetting =BftsmartConsensusUtils.getSetting();
//		BftsmartTopology bftsmartTopology = BftsmartConsensusUtils.getTopology();
//
//		if (txBatchProcess == null) {
//			txBatchProcess = txEngine.createNextBatch(ledgerHash);
//			currBlockIndex = txBatchProcess.blockHeight();
//			this.blockIndex.set(currBlockIndex);
//			receiveCounts = 0;
//
//			if (isLeader) {
//				long timerStart = System.currentTimeMillis();
//				System.out.println("first message time = "+timerStart);
//				timerEexecutorService.schedule(timeTask(currBlockIndex, ledgerHash, bftsmartConsensusSetting, bftsmartTopology), 500L, TimeUnit.MILLISECONDS);
//			}
//		}
//		receiveCounts++;
//		if (isLeader) {
//			notifyCommit(currBlockIndex, ledgerHash, bftsmartConsensusSetting, bftsmartTopology);
//		}
//		return txBatchProcess.schedule(txRequest);
//	}
//
//	@Override
//	public void afterBatch(byte[] groupId, Exception error) {
//		HashDigest ledgerHash = new HashDigest(groupId);
//		TransactionBatchProcess txBatchProcess = txEngine.getBatch(ledgerHash);
//
//		if (error != null) {
//			txBatchProcess.cancel(TransactionState.SYSTEM_ERROR);
//			LOGGER.error("Error occurred on executing batch transactions, so the new block is canceled! --" + error.getMessage(), error);
//			return;
//		}
//
//		//生成区块；
//		TransactionBatchResultHandle batchResultHandle = txBatchProcess.prepare();
//
//		LedgerBlock newBlock = batchResultHandle.getBlock();
//		// TODO: 对新区块进行最后的共识；
//		HashDigest blockHash = newBlock.getHash();
//
//		LOGGER.info(String.format(
//				"Create new block success! --[LedgerHash=%s][BlockHash=%s][BlockHeigth=%s]",
//				ledgerHash.toBase58(), blockHash.toBase58(), newBlock.getHeight()));
//
//		// 提交新区块；
//		batchResultHandle.commit();
//
//	}
//
//	private Runnable timeTask(final long currBlockIndex, HashDigest ledgerHash, BftsmartConsensusSetting bftsmartConsensusSetting, BftsmartTopology bftsmartTopology) {
//		Runnable task = () -> {
//		// todo
//		boolean isAdd = this.blockIndex.compareAndSet(currBlockIndex, currBlockIndex + 1);
//		if (isAdd) {
//			System.out.println(Thread.currentThread().getId() + " leader run isadd = " + isAdd + " timer send commitblock message ! curren time = " + System.currentTimeMillis());
//			sendCommitBlockMessage(ledgerHash, bftsmartConsensusSetting, bftsmartTopology);
//		}
//		};
//		return task;
//		}
//
//		}
