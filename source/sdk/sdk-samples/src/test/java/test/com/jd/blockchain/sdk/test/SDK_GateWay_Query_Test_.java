/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: test.com.jd.blockchain.sdk.test.SDK_GateWay_InsertData_Test
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/9/4 上午11:06
 * Description: 插入数据测试
 */
package test.com.jd.blockchain.sdk.test;

import org.junit.Before;
import org.junit.Test;

import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.crypto.AsymmetricKeypair;
import com.jd.blockchain.crypto.CryptoServiceProviders;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.HashFunction;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.crypto.SignatureDigest;
import com.jd.blockchain.crypto.SignatureFunction;
import com.jd.blockchain.crypto.serialize.ByteArrayObjectDeserializer;
import com.jd.blockchain.crypto.serialize.ByteArrayObjectSerializer;
import com.jd.blockchain.ledger.AccountHeader;
import com.jd.blockchain.ledger.BlockchainKeyGenerator;
import com.jd.blockchain.ledger.BlockchainKeypair;
import com.jd.blockchain.ledger.DigitalSignature;
import com.jd.blockchain.ledger.EndpointRequest;
import com.jd.blockchain.ledger.KVDataEntry;
import com.jd.blockchain.ledger.LedgerBlock;
import com.jd.blockchain.ledger.LedgerInfo;
import com.jd.blockchain.ledger.LedgerTransaction;
import com.jd.blockchain.ledger.NodeRequest;
import com.jd.blockchain.ledger.ParticipantNode;
import com.jd.blockchain.ledger.Transaction;
import com.jd.blockchain.ledger.TransactionContent;
import com.jd.blockchain.ledger.TransactionContentBody;
import com.jd.blockchain.ledger.TransactionRequest;
import com.jd.blockchain.ledger.TransactionResponse;
import com.jd.blockchain.ledger.TransactionState;
import com.jd.blockchain.ledger.data.TxResponseMessage;
import com.jd.blockchain.sdk.BlockchainService;
import com.jd.blockchain.sdk.client.GatewayServiceFactory;
import com.jd.blockchain.utils.serialize.json.JSONSerializeUtils;

/**
 * 插入数据测试
 * 
 * @author shaozhuguang
 * @create 2018/9/4
 * @since 1.0.0
 */

public class SDK_GateWay_Query_Test_ {

	private static Class<?>[] byteArrayClasss = new Class<?>[] { HashDigest.class, PubKey.class,
			SignatureDigest.class };

	static {
		for (Class<?> byteArrayClass : byteArrayClasss) {
			JSONSerializeUtils.configSerialization(byteArrayClass,
					ByteArrayObjectSerializer.getInstance(byteArrayClass),
					ByteArrayObjectDeserializer.getInstance(byteArrayClass));
		}
	}

	private BlockchainKeypair CLIENT_CERT = null;

	private String GATEWAY_IPADDR = null;

	private int GATEWAY_PORT;

	private boolean SECURE;

	private BlockchainService service;

	@Before
	public void init() {
		CLIENT_CERT = BlockchainKeyGenerator.getInstance().generate("ED25519");
		GATEWAY_IPADDR = "127.0.0.1";
		GATEWAY_PORT = 11000;
		SECURE = false;
		GatewayServiceFactory serviceFactory = GatewayServiceFactory.connect(GATEWAY_IPADDR, GATEWAY_PORT, SECURE,
				CLIENT_CERT);
		service = serviceFactory.getBlockchainService();

		DataContractRegistry.register(TransactionContent.class);
		DataContractRegistry.register(TransactionContentBody.class);
		DataContractRegistry.register(TransactionRequest.class);
		DataContractRegistry.register(NodeRequest.class);
		DataContractRegistry.register(EndpointRequest.class);
		DataContractRegistry.register(TransactionResponse.class);
	}

	@Test
	public void query_Test() {

		HashDigest ledgerHash = service.getLedgerHashs()[0];
		;
		// ParserConfig.global.setAutoTypeSupport(true);

		LedgerInfo ledgerInfo = service.getLedger(ledgerHash);
		long ledgerNumber = ledgerInfo.getLatestBlockHeight();
		System.out.println(ledgerNumber);
		HashDigest hashDigest = ledgerInfo.getHash();
		System.out.println(hashDigest);
		// 最新区块；
		LedgerBlock latestBlock = service.getBlock(ledgerHash, ledgerNumber);
		System.out.println("latestBlock.Hash=" + latestBlock.getHash());
		long count = service.getContractCount(ledgerHash, 3L);
		System.out.println("contractCount=" + count);
		count = service.getContractCount(ledgerHash, hashDigest);
		System.out.println("contractCount=" + count);
		AccountHeader contract = service.getContract(ledgerHash, "12345678");
		System.out.println(contract);

		LedgerBlock block = service.getBlock(ledgerHash, hashDigest);
		System.out.println("block.Hash=" + block.getHash());

		count = service.getDataAccountCount(ledgerHash, 123456);
		System.out.println("dataAccountCount=" + count);
		count = service.getDataAccountCount(ledgerHash, hashDigest);
		System.out.println("dataAccountCount=" + count);

		AccountHeader dataAccount = service.getDataAccount(ledgerHash, "1245633");
		System.out.println(dataAccount.getAddress());

		count = service.getTransactionCount(ledgerHash, hashDigest);
		System.out.println("transactionCount=" + count);
		count = service.getTransactionCount(ledgerHash, 12456);
		System.out.println("transactionCount=" + count);

		LedgerTransaction[] txList = service.getTransactions(ledgerHash, ledgerNumber, 0, 100);
		for (LedgerTransaction ledgerTransaction : txList) {
			System.out.println("ledgerTransaction.Hash=" + ledgerTransaction.getHash());
		}

		txList = service.getTransactions(ledgerHash, hashDigest, 0, 100);
		for (LedgerTransaction ledgerTransaction : txList) {
			System.out.println("ledgerTransaction.Hash=" + ledgerTransaction.getHash());
		}

		Transaction tx = service.getTransactionByContentHash(ledgerHash, hashDigest);
		DigitalSignature[] signatures = tx.getEndpointSignatures();
		for (DigitalSignature signature : signatures) {
			System.out.println(signature.getDigest().getAlgorithm());
		}
		System.out.println("transaction.blockHeight=" + tx.getBlockHeight());
		System.out.println("transaction.executionState=" + tx.getExecutionState());

		ParticipantNode[] participants = service.getConsensusParticipants(ledgerHash);
		for (ParticipantNode participant : participants) {
			System.out.println("participant.name=" + participant.getName());
			// System.out.println(participant.getConsensusAddress());
			// System.out.println("participant.host=" +
			// participant.getConsensusAddress().getHost());
			System.out.println("participant.getPubKey=" + participant.getPubKey());
			System.out.println("participant.getKeyType=" + participant.getPubKey().getKeyType());
			System.out.println("participant.getRawKeyBytes=" + participant.getPubKey().getRawKeyBytes());
			System.out.println("participant.algorithm=" + participant.getPubKey().getAlgorithm());
		}

		String commerceAccount = "GGhhreGeasdfasfUUfehf9932lkae99ds66jf==";
		String[] objKeys = new String[] { "x001", "x002" };
		KVDataEntry[] kvData = service.getDataEntries(ledgerHash, commerceAccount, objKeys);
		for (KVDataEntry kvDatum : kvData) {
			System.out.println("kvData.key=" + kvDatum.getKey());
			System.out.println("kvData.version=" + kvDatum.getVersion());
			System.out.println("kvData.value=" + kvDatum.getValue());
		}

		HashDigest[] hashs = service.getLedgerHashs();
		for (HashDigest hash : hashs) {
			System.out.println("hash.toBase58=" + hash.toBase58());
		}
	}

	private HashDigest getLedgerHash() {
		HashDigest ledgerHash = CryptoServiceProviders.getHashFunction("SHA256").hash("jd-gateway".getBytes());
		return ledgerHash;
	}

	private SignatureFunction getSignatureFunction() {
		return CryptoServiceProviders.getSignatureFunction("ED25519");
	}

	private BlockchainKeypair getSponsorKey() {
		SignatureFunction signatureFunction = getSignatureFunction();
		AsymmetricKeypair cryptoKeyPair = signatureFunction.generateKeypair();
		BlockchainKeypair blockchainKeyPair = new BlockchainKeypair(cryptoKeyPair.getPubKey(),
				cryptoKeyPair.getPrivKey());
		return blockchainKeyPair;
	}

	private TransactionResponse initResponse() {
		HashFunction hashFunc = CryptoServiceProviders.getHashFunction("SHA256");
		HashDigest contentHash = hashFunc.hash("contentHash".getBytes());
		HashDigest blockHash = hashFunc.hash("blockHash".getBytes());
		long blockHeight = 9998L;

		TxResponseMessage resp = new TxResponseMessage(contentHash);
		resp.setBlockHash(blockHash);
		resp.setBlockHeight(blockHeight);
		resp.setExecutionState(TransactionState.SUCCESS);
		return resp;
	}
}