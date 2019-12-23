/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.capability.service.RemoteTransactionService
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/12/26 下午5:22
 * Description:
 */
package com.jd.blockchain.capability.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import com.jd.blockchain.binaryproto.BinaryProtocol;
import com.jd.blockchain.capability.settings.CapabilitySettings;
import com.jd.blockchain.consensus.mq.factory.MsgQueueFactory;
import com.jd.blockchain.consensus.mq.producer.MsgQueueProducer;
import com.jd.blockchain.crypto.AsymmetricKeypair;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.BlockchainIdentity;
import com.jd.blockchain.ledger.BlockchainKeyGenerator;
import com.jd.blockchain.ledger.BlockchainKeypair;
import com.jd.blockchain.ledger.TransactionRequest;
import com.jd.blockchain.ledger.TransactionRequestBuilder;
import com.jd.blockchain.transaction.TxBuilder;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.ConsoleUtils;
import com.jd.blockchain.utils.io.BytesUtils;

/**
 *
 * @author shaozhuguang
 * @create 2018/12/26
 * @since 1.0.0
 */

public class RemoteTransactionService {

	private MsgQueueProducer txProducer;

	private final ExecutorService instanceFactory = Executors.newSingleThreadExecutor();

	private final ArrayBlockingQueue<List<byte[]>> txBlockingQueue = new ArrayBlockingQueue(1);

	private final LinkedList<Bytes> dataAccountAddress = new LinkedList<>();

	private Bytes defaultDataAccount;

	private static final AtomicLong keyPrefix = new AtomicLong();

	public void userRegister(int count) throws Exception {
		initTxProducer();
		int loop = 0;
		if (count <= 0) {
			userCreate();
		} else {
			loop = count / CapabilitySettings.TX_SIZE_PER_SEND;
			userCreate(loop);
		}
		// 从队列中获取数据
		if (count <= 0) {
			for (;;) {
				txRequestSend();
			}
		} else {
			for (int i = 0; i < loop; i++) {
				txRequestSend();
			}
		}
		closeTxProducer();
	}

	public void dataAccountRegister(int count) throws Exception {
		initTxProducer();
		int loop = 0;
		if (count <= 0) {
			dataAccountCreate();
		} else {
			loop = count / CapabilitySettings.TX_SIZE_PER_SEND;
			dataAccountCreate(loop);
		}
		// 从队列中获取数据
		if (count <= 0) {
			for (;;) {
				txRequestSend();
			}
		} else {
			for (int i = 0; i < loop; i++) {
				txRequestSend();
			}
		}
		closeTxProducer();
	}

	public void userAndDataAccountRegister(int userCount, int dataAccountCount) throws Exception {
		if (userCount <= 0 || dataAccountCount <= 0) {
			throw new IllegalArgumentException("userCount and dataAccountCount can not be 0!!!");
		}
		initTxProducer();
		int userLoop = userCount / CapabilitySettings.TX_SIZE_PER_SEND;
		int dataAccountLoop = dataAccountCount / CapabilitySettings.TX_SIZE_PER_SEND;
		userCreate(userLoop);
		dataAccountCreate(dataAccountLoop);
		for (int i = 0, totalLoop = userLoop + dataAccountCount; i < totalLoop; i++) {
			txRequestSend();
		}
		closeTxProducer();
	}

	public void dataAccountRegisterAndKvStorage(int dataAccountCount, int kvCount) throws Exception {
		if (kvCount <= 0 || dataAccountCount <= 0) {
			throw new IllegalArgumentException("userCount and dataAccountCount can not be 0!!!");
		}
		initTxProducer();
		int dataAccountLoop = dataAccountCount / CapabilitySettings.TX_SIZE_PER_SEND;
		dataAccountCreate(dataAccountLoop);
		// 首先将数据账户写入
		for (int i = 0; i < dataAccountLoop; i++) {
			txRequestSend();
		}
		int kvLoop = kvCount / CapabilitySettings.TX_SIZE_PER_SEND;
		// 然后将每个数据账户都写入指定数量的kv
		Iterator<Bytes> iterator = dataAccountAddress.iterator();
		while (iterator.hasNext()) {
			Bytes address = iterator.next();
			kvStorageCreate(kvCount, address);
		}
		for (int i = 0, loop = kvLoop * dataAccountCount; i < loop; i++) {
			txRequestSend();
		}

		closeTxProducer();
	}

	public void kvStorage(int kvCount) throws Exception {
		initTxProducer();

		dataAccountDefaultCreate();
		try {
			txRequestSend();
			// 确认结块成功
			Thread.sleep(10000);
		} catch (Exception e) {
			throw e;
		}
		int kvLoop = kvCount / CapabilitySettings.TX_SIZE_PER_SEND;
		// 然后将每个数据账户都写入指定数量的kv
		Bytes address = defaultDataAccount;
		kvStorageCreate(kvLoop, address);
		for (int i = 0; i < kvLoop; i++) {
			txRequestSend();
		}

		closeTxProducer();
	}

	private void txRequestSend() throws Exception {
		List<byte[]> txRequests = txBlockingQueue.take();
		if (txRequests != null && !txRequests.isEmpty()) {
			Iterator<byte[]> iterator = txRequests.iterator();
			while (iterator.hasNext()) {
				byte[] txRequest = iterator.next();
				try {
					txProducer.publish(txRequest);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			ConsoleUtils.info("[*] Transaction Request send success!!!");
		}
	}

	private void initTxProducer() throws Exception {
		txProducer = MsgQueueFactory.newProducer(CapabilitySettings.MSG_QUEUE_URL, CapabilitySettings.TX_TOPIC);
		txProducer.connect();
		ConsoleUtils.info("[*] Transaction Producer start success!!!");
	}

	private void closeTxProducer() throws Exception {
		txProducer.close();
	}

	private void userCreate(int loop) {
		instanceFactory.execute(() -> {
			for (int index = 0; index < loop; index++) {
				// 每次生产10000个，然后放入队列中
				try {
					LinkedList<byte[]> txSerializeBytes = userActiveCreate();
					txBlockingQueue.put(txSerializeBytes);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});
	}

	private void userCreate() {
		// 一直在生产用户
		instanceFactory.execute(() -> {
			for (;;) {
				// 每次生产10000个，然后放入队列中
				try {
					LinkedList<byte[]> txSerializeBytes = userActiveCreate();
					txBlockingQueue.put(txSerializeBytes);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});
	}

	private void dataAccountCreate(int loop) {
		dataAccountCreate(loop, false);
	}

	private void dataAccountCreate(int loop, final boolean isSave) {
		instanceFactory.execute(() -> {
			for (int index = 0; index < loop; index++) {
				// 每次生产10000个，然后放入队列中
				try {
					LinkedList<byte[]> txSerializeBytes = dataAccountActiveCreate(isSave);
					txBlockingQueue.put(txSerializeBytes);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});
	}

	private void dataAccountDefaultCreate() {
		instanceFactory.execute(() -> {
			List<byte[]> currentBytes = new ArrayList<>();
			TransactionRequest txRequest = dataAccountRegisterRequest(CapabilitySettings.ledgerHash,
					CapabilitySettings.adminKey);
			byte[] serializeBytes = BinaryProtocol.encode(txRequest, TransactionRequest.class);
			currentBytes.add(serializeBytes);
			try {
				txBlockingQueue.put(currentBytes);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
	}

	private void dataAccountCreate() {
		// 一直在生产用户
		instanceFactory.execute(() -> {
			for (;;) {
				// 每次生产10000个，然后放入队列中
				try {
					LinkedList<byte[]> txSerializeBytes = dataAccountActiveCreate();
					txBlockingQueue.put(txSerializeBytes);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});
	}

	private void kvStorageCreate(int loop, Bytes address) {
		// 一直在生产用户
		instanceFactory.execute(() -> {
			for (int index = 0; index < loop; index++) {
				// 每次生产10000个，然后放入队列中
				try {
					LinkedList<byte[]> txSerializeBytes = kvActiveCreate(address);
					txBlockingQueue.put(txSerializeBytes);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});
	}

	private LinkedList<byte[]> userActiveCreate() {
		// 每次生产10000个，然后放入队列中
		LinkedList<byte[]> txSerializeBytes = new LinkedList<>();
		for (int i = 0; i < CapabilitySettings.TX_SIZE_PER_SEND; i++) {
			TransactionRequest txRequest = userRegisterRequest(CapabilitySettings.ledgerHash,
					CapabilitySettings.adminKey);
			byte[] serializeBytes = BinaryProtocol.encode(txRequest, TransactionRequest.class);
			txSerializeBytes.addFirst(serializeBytes);
		}
		return txSerializeBytes;
	}

	private LinkedList<byte[]> dataAccountActiveCreate() {
		return dataAccountActiveCreate(false);
	}

	private LinkedList<byte[]> dataAccountActiveCreate(boolean isSave) {
		// 每次生产10000个，然后放入队列中
		LinkedList<byte[]> txSerializeBytes = new LinkedList<>();
		for (int i = 0; i < CapabilitySettings.TX_SIZE_PER_SEND; i++) {
			TransactionRequest txRequest = dataAccountRegisterRequest(CapabilitySettings.ledgerHash,
					CapabilitySettings.adminKey, isSave);
			byte[] serializeBytes = BinaryProtocol.encode(txRequest, TransactionRequest.class);
			txSerializeBytes.addFirst(serializeBytes);
		}
		return txSerializeBytes;
	}

	private LinkedList<byte[]> kvActiveCreate(Bytes address) {
		// 每次生产10000个，然后放入队列中
		LinkedList<byte[]> txSerializeBytes = new LinkedList<>();
		for (int i = 0; i < CapabilitySettings.TX_SIZE_PER_SEND; i++) {
			TransactionRequest txRequest = kvStorageRequest(address, CapabilitySettings.ledgerHash,
					CapabilitySettings.adminKey);
			byte[] serializeBytes = BinaryProtocol.encode(txRequest, TransactionRequest.class);
			txSerializeBytes.addFirst(serializeBytes);
		}
		return txSerializeBytes;
	}

	private TransactionRequest userRegisterRequest(HashDigest ledgerHash, AsymmetricKeypair adminKey) {
		TxBuilder txbuilder = new TxBuilder(ledgerHash);
		BlockchainKeypair userKey = BlockchainKeyGenerator.getInstance().generate();
		txbuilder.users().register(userKey.getIdentity());
		TransactionRequestBuilder reqBuilder = txbuilder.prepareRequest();
		reqBuilder.signAsEndpoint(adminKey);
		return reqBuilder.buildRequest();
	}

	private TransactionRequest dataAccountRegisterRequest(HashDigest ledgerHash, AsymmetricKeypair adminKey,
			boolean isSave) {
		TxBuilder txbuilder = new TxBuilder(ledgerHash);
		BlockchainKeypair dataAccountKey = BlockchainKeyGenerator.getInstance().generate();
		BlockchainIdentity identity = dataAccountKey.getIdentity();
		txbuilder.dataAccounts().register(identity);
		TransactionRequestBuilder reqBuilder = txbuilder.prepareRequest();
		reqBuilder.signAsEndpoint(adminKey);
		if (isSave) {
			dataAccountAddress.addFirst(identity.getAddress());
		}
		return reqBuilder.buildRequest();
	}

	private TransactionRequest dataAccountRegisterRequest(HashDigest ledgerHash, AsymmetricKeypair adminKey) {
		TxBuilder txbuilder = new TxBuilder(ledgerHash);
		BlockchainKeypair dataAccountKey = BlockchainKeyGenerator.getInstance().generate();
		BlockchainIdentity identity = dataAccountKey.getIdentity();
		txbuilder.dataAccounts().register(identity);
		TransactionRequestBuilder reqBuilder = txbuilder.prepareRequest();
		reqBuilder.signAsEndpoint(adminKey);
		defaultDataAccount = identity.getAddress();
		return reqBuilder.buildRequest();
	}

	private TransactionRequest kvStorageRequest(Bytes address, HashDigest ledgerHash, AsymmetricKeypair adminKey) {
		TxBuilder txbuilder = new TxBuilder(ledgerHash);
		long currValue = keyPrefix.getAndIncrement();
		txbuilder.dataAccount(address).setText("key-" + currValue + "-" + System.currentTimeMillis(),
				"value-" + currValue, -1L);
		TransactionRequestBuilder reqBuilder = txbuilder.prepareRequest();
		reqBuilder.signAsEndpoint(adminKey);
		return reqBuilder.buildRequest();
	}
}