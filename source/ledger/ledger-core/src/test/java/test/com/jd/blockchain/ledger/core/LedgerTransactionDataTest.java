/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: test.com.jd.blockchain.ledger.data.LedgerTransactionImplTest
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/8/30 上午9:48
 * Description:
 */
package test.com.jd.blockchain.ledger.core;

import static org.junit.Assert.assertEquals;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import com.jd.blockchain.binaryproto.BinaryProtocol;
import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.crypto.SignatureDigest;
import com.jd.blockchain.crypto.service.classic.ClassicAlgorithm;
import com.jd.blockchain.ledger.BlockchainKeyGenerator;
import com.jd.blockchain.ledger.BlockchainKeypair;
import com.jd.blockchain.ledger.DataAccountKVSetOperation;
import com.jd.blockchain.ledger.DigitalSignature;
import com.jd.blockchain.ledger.EndpointRequest;
import com.jd.blockchain.ledger.HashObject;
import com.jd.blockchain.ledger.LedgerDataSnapshot;
import com.jd.blockchain.ledger.LedgerTransaction;
import com.jd.blockchain.ledger.NodeRequest;
import com.jd.blockchain.ledger.Transaction;
import com.jd.blockchain.ledger.TransactionContent;
import com.jd.blockchain.ledger.TransactionRequest;
import com.jd.blockchain.ledger.TransactionState;
import com.jd.blockchain.ledger.core.LedgerTransactionData;
import com.jd.blockchain.ledger.core.TransactionStagedSnapshot;
import com.jd.blockchain.transaction.BlockchainOperationFactory;
import com.jd.blockchain.transaction.DigitalSignatureBlob;
import com.jd.blockchain.transaction.TxContentBlob;
import com.jd.blockchain.transaction.TxRequestMessage;
import com.jd.blockchain.utils.io.ByteArray;

/**
 *
 * @author shaozhuguang
 * @create 2018/8/30
 * @since 1.0.0
 */

public class LedgerTransactionDataTest {

	private LedgerTransactionData data;

	@Before
	public void initLedgerTransactionImpl() throws Exception {
		DataContractRegistry.register(LedgerTransaction.class);
		DataContractRegistry.register(Transaction.class);
		DataContractRegistry.register(LedgerDataSnapshot.class);
		DataContractRegistry.register(NodeRequest.class);
		DataContractRegistry.register(EndpointRequest.class);
		DataContractRegistry.register(HashObject.class);
		DataContractRegistry.register(DataAccountKVSetOperation.class);

		TransactionRequest txRequestMessage = initTxRequestMessage();

		long blockHeight = 9986L;
		data = new LedgerTransactionData(blockHeight, txRequestMessage, TransactionState.SUCCESS,
				initTransactionStagedSnapshot(), null);

		HashDigest hash = new HashDigest(ClassicAlgorithm.SHA256, "zhangsan".getBytes());
		HashDigest adminAccountHash = new HashDigest(ClassicAlgorithm.SHA256, "lisi".getBytes());
		HashDigest userAccountSetHash = new HashDigest(ClassicAlgorithm.SHA256, "wangwu".getBytes());
		HashDigest dataAccountSetHash = new HashDigest(ClassicAlgorithm.SHA256, "zhaoliu".getBytes());
		HashDigest contractAccountSetHash = new HashDigest(ClassicAlgorithm.SHA256, "sunqi".getBytes());

		data.setHash(hash);
		// data.setBlockHeight(blockHeight);
		data.setAdminAccountHash(adminAccountHash);
		data.setUserAccountSetHash(userAccountSetHash);
		data.setDataAccountSetHash(dataAccountSetHash);
		data.setContractAccountSetHash(contractAccountSetHash);
	}

	@Test
	public void testSerialize_LedgerTransaction() throws Exception {
		byte[] serialBytes = BinaryProtocol.encode(data, LedgerTransaction.class);
		LedgerTransaction resolvedData = BinaryProtocol.decode(serialBytes);

		System.out.println("------Assert start ------");
		assertEquals(resolvedData.getAdminAccountHash(), data.getAdminAccountHash());
		assertEquals(resolvedData.getContractAccountSetHash(), data.getContractAccountSetHash());
		assertEquals(resolvedData.getDataAccountSetHash(), data.getDataAccountSetHash());
		assertEquals(resolvedData.getUserAccountSetHash(), data.getUserAccountSetHash());
		assertEquals(resolvedData.getExecutionState(), data.getExecutionState());
		assertEquals(resolvedData.getHash(), data.getHash());
		assertEquals(resolvedData.getBlockHeight(), data.getBlockHeight());

		// EndpointSignatures 验证
		DigitalSignature[] dataEndpointSignatures = data.getEndpointSignatures();
		DigitalSignature[] resolvedEndpointSignatures = resolvedData.getEndpointSignatures();
		for (int i = 0; i < dataEndpointSignatures.length; i++) {
			assertEquals(dataEndpointSignatures[i].getPubKey(), resolvedEndpointSignatures[i].getPubKey());
			assertEquals(dataEndpointSignatures[i].getDigest(), resolvedEndpointSignatures[i].getDigest());
		}

		// NodeSignatures 验证
		DigitalSignature[] dataNodeSignatures = data.getNodeSignatures();
		DigitalSignature[] resolvedNodeSignatures = resolvedData.getNodeSignatures();
		for (int i = 0; i < dataNodeSignatures.length; i++) {
			assertEquals(dataNodeSignatures[i].getPubKey(), resolvedNodeSignatures[i].getPubKey());
			assertEquals(dataNodeSignatures[i].getDigest(), resolvedNodeSignatures[i].getDigest());
		}

		assertEqual(data.getTransactionContent(), resolvedData.getTransactionContent());
		System.out.println("------Assert OK ------");
	}

	@Test
	public void testSerialize_Transaction() throws Exception {
		byte[] serialBytes = BinaryProtocol.encode(data, Transaction.class);
		Transaction resolvedData = BinaryProtocol.decode(serialBytes);

		System.out.println("------Assert start ------");
		assertEquals(resolvedData.getExecutionState(), data.getExecutionState());
		assertEquals(resolvedData.getHash(), data.getHash());
		assertEquals(resolvedData.getBlockHeight(), data.getBlockHeight());

		// EndpointSignatures 验证
		DigitalSignature[] dataEndpointSignatures = data.getEndpointSignatures();
		DigitalSignature[] resolvedEndpointSignatures = resolvedData.getEndpointSignatures();
		for (int i = 0; i < dataEndpointSignatures.length; i++) {
			assertEquals(dataEndpointSignatures[i].getPubKey(), resolvedEndpointSignatures[i].getPubKey());
			assertEquals(dataEndpointSignatures[i].getDigest(), resolvedEndpointSignatures[i].getDigest());
		}

		// NodeSignatures 验证
		DigitalSignature[] dataNodeSignatures = data.getNodeSignatures();
		DigitalSignature[] resolvedNodeSignatures = resolvedData.getNodeSignatures();
		for (int i = 0; i < dataNodeSignatures.length; i++) {
			assertEquals(dataNodeSignatures[i].getPubKey(), resolvedNodeSignatures[i].getPubKey());
			assertEquals(dataNodeSignatures[i].getDigest(), resolvedNodeSignatures[i].getDigest());
		}

		assertEqual(data.getTransactionContent(), resolvedData.getTransactionContent());
		System.out.println("------Assert OK ------");
	}

	@Test
	public void testSerialize_LedgerDataSnapshot() throws Exception {
		byte[] serialBytes = BinaryProtocol.encode(data, LedgerDataSnapshot.class);
		LedgerDataSnapshot resolvedData = BinaryProtocol.decode(serialBytes);

		System.out.println("------Assert start ------");
		assertEquals(resolvedData.getAdminAccountHash(), data.getAdminAccountHash());
		assertEquals(resolvedData.getContractAccountSetHash(), data.getContractAccountSetHash());
		assertEquals(resolvedData.getDataAccountSetHash(), data.getDataAccountSetHash());
		assertEquals(resolvedData.getUserAccountSetHash(), data.getUserAccountSetHash());
		System.out.println("------Assert OK ------");
	}

	@Test
	public void testSerialize_NodeRequest() throws Exception {
		byte[] serialBytes = BinaryProtocol.encode(data, NodeRequest.class);
		NodeRequest resolvedData = BinaryProtocol.decode(serialBytes);

		System.out.println("------Assert start ------");
		// EndpointSignatures 验证
		DigitalSignature[] dataEndpointSignatures = data.getEndpointSignatures();
		DigitalSignature[] resolvedEndpointSignatures = resolvedData.getEndpointSignatures();
		for (int i = 0; i < dataEndpointSignatures.length; i++) {
			assertEquals(dataEndpointSignatures[i].getPubKey(), resolvedEndpointSignatures[i].getPubKey());
			assertEquals(dataEndpointSignatures[i].getDigest(), resolvedEndpointSignatures[i].getDigest());
		}

		// NodeSignatures 验证
		DigitalSignature[] dataNodeSignatures = data.getNodeSignatures();
		DigitalSignature[] resolvedNodeSignatures = resolvedData.getNodeSignatures();
		for (int i = 0; i < dataNodeSignatures.length; i++) {
			assertEquals(dataNodeSignatures[i].getPubKey(), resolvedNodeSignatures[i].getPubKey());
			assertEquals(dataNodeSignatures[i].getDigest(), resolvedNodeSignatures[i].getDigest());
		}

		assertEqual(data.getTransactionContent(), resolvedData.getTransactionContent());
		System.out.println("------Assert OK ------");
	}

	@Test
	public void testSerialize_EndpointRequest() throws Exception {
		byte[] serialBytes = BinaryProtocol.encode(data, EndpointRequest.class);
		EndpointRequest resolvedData = BinaryProtocol.decode(serialBytes);

		System.out.println("------Assert start ------");
		// EndpointSignatures 验证
		DigitalSignature[] dataEndpointSignatures = data.getEndpointSignatures();
		DigitalSignature[] resolvedEndpointSignatures = resolvedData.getEndpointSignatures();
		for (int i = 0; i < dataEndpointSignatures.length; i++) {
			assertEquals(dataEndpointSignatures[i].getPubKey(), resolvedEndpointSignatures[i].getPubKey());
			assertEquals(dataEndpointSignatures[i].getDigest(), resolvedEndpointSignatures[i].getDigest());
		}
		assertEqual(data.getTransactionContent(), resolvedData.getTransactionContent());
		System.out.println("------Assert OK ------");
	}

	private void assertEqual(TransactionContent dataTxContent, TransactionContent resolvedTxContent) {
		assertEquals(dataTxContent.getHash(), resolvedTxContent.getHash());
		assertEquals(dataTxContent.getLedgerHash(), resolvedTxContent.getLedgerHash());
		// assertEquals(dataTxContent.getSequenceNumber(),
		// resolvedTxContent.getSequenceNumber());
		// assertEquals(dataTxContent.getSubjectAccount(),
		// resolvedTxContent.getSubjectAccount());
	}

	private TransactionStagedSnapshot initTransactionStagedSnapshot() {
		TransactionStagedSnapshot transactionStagedSnapshot = new TransactionStagedSnapshot();
		transactionStagedSnapshot.setAdminAccountHash(new HashDigest(ClassicAlgorithm.SHA256, "zhangsan".getBytes()));
		transactionStagedSnapshot.setContractAccountSetHash(new HashDigest(ClassicAlgorithm.SHA256, "lisi".getBytes()));
		transactionStagedSnapshot.setDataAccountSetHash(new HashDigest(ClassicAlgorithm.SHA256, "wangwu".getBytes()));
		transactionStagedSnapshot.setUserAccountSetHash(new HashDigest(ClassicAlgorithm.SHA256, "zhaoliu".getBytes()));
		return transactionStagedSnapshot;
	}

	private TxRequestMessage initTxRequestMessage() throws Exception {
		TxRequestMessage txRequestMessage = new TxRequestMessage(initTransactionContent());

		SignatureDigest digest1 = new SignatureDigest(ClassicAlgorithm.ED25519, "zhangsan".getBytes());
		SignatureDigest digest2 = new SignatureDigest(ClassicAlgorithm.ED25519, "lisi".getBytes());
		DigitalSignatureBlob endPoint1 = new DigitalSignatureBlob(
				new PubKey(ClassicAlgorithm.ED25519, "jd1.com".getBytes()), digest1);
		DigitalSignatureBlob endPoint2 = new DigitalSignatureBlob(
				new PubKey(ClassicAlgorithm.ED25519, "jd2.com".getBytes()), digest2);
		txRequestMessage.addEndpointSignatures(endPoint1);
		txRequestMessage.addEndpointSignatures(endPoint2);

		SignatureDigest digest3 = new SignatureDigest(ClassicAlgorithm.ED25519, "wangwu".getBytes());
		SignatureDigest digest4 = new SignatureDigest(ClassicAlgorithm.ED25519, "zhaoliu".getBytes());
		DigitalSignatureBlob node1 = new DigitalSignatureBlob(
				new PubKey(ClassicAlgorithm.ED25519, "jd3.com".getBytes()), digest3);
		DigitalSignatureBlob node2 = new DigitalSignatureBlob(
				new PubKey(ClassicAlgorithm.ED25519, "jd4.com".getBytes()), digest4);
		txRequestMessage.addNodeSignatures(node1);
		txRequestMessage.addNodeSignatures(node2);

		return txRequestMessage;
	}

	private TransactionContent initTransactionContent() throws Exception {
		TxContentBlob contentBlob = null;
		BlockchainKeypair id = BlockchainKeyGenerator.getInstance().generate(ClassicAlgorithm.ED25519);
		HashDigest ledgerHash = Crypto.getHashFunction("SHA256")
				.hash(UUID.randomUUID().toString().getBytes("UTF-8"));
		BlockchainOperationFactory opFactory = new BlockchainOperationFactory();
		contentBlob = new TxContentBlob(ledgerHash);
		contentBlob.setHash(new HashDigest(ClassicAlgorithm.SHA256, "jd.com".getBytes()));
		// contentBlob.setSubjectAccount(id.getAddress());
		// contentBlob.setSequenceNumber(1);
		DataAccountKVSetOperation kvsetOP = opFactory.dataAccount(id.getAddress())
				.setText("Name", "AAA", -1).getOperation();
		contentBlob.addOperation(kvsetOP);
		return contentBlob;
	}
}