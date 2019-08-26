/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: test.com.jd.blockchain.ledger.data.TxRequestMessageTest
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/9/3 下午3:07
 * Description:
 */
package test.com.jd.blockchain.ledger;

import static org.junit.Assert.assertEquals;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import com.jd.blockchain.binaryproto.BinaryProtocol;
import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.crypto.AsymmetricKeypair;
import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.HashFunction;
import com.jd.blockchain.crypto.SignatureDigest;
import com.jd.blockchain.crypto.SignatureFunction;
import com.jd.blockchain.ledger.BlockchainKeyGenerator;
import com.jd.blockchain.ledger.BlockchainKeypair;
import com.jd.blockchain.ledger.DataAccountKVSetOperation;
import com.jd.blockchain.ledger.DigitalSignature;
import com.jd.blockchain.ledger.EndpointRequest;
import com.jd.blockchain.ledger.HashObject;
import com.jd.blockchain.ledger.NodeRequest;
import com.jd.blockchain.ledger.Operation;
import com.jd.blockchain.ledger.TransactionContent;
import com.jd.blockchain.ledger.TransactionRequest;
import com.jd.blockchain.transaction.BlockchainOperationFactory;
import com.jd.blockchain.transaction.DigitalSignatureBlob;
import com.jd.blockchain.transaction.TxContentBlob;
import com.jd.blockchain.transaction.TxRequestMessage;

/**
 *
 * @author shaozhuguang
 * @create 2018/9/3
 * @since 1.0.0
 */

public class TxRequestMessageTest {

	private TxRequestMessage data;

	@Before
	public void initTxRequestMessage() throws Exception {
		DataContractRegistry.register(TransactionRequest.class);
		DataContractRegistry.register(NodeRequest.class);
		DataContractRegistry.register(EndpointRequest.class);
		DataContractRegistry.register(HashObject.class);

		data = new TxRequestMessage(initTransactionContent());

		SignatureFunction signFunc = Crypto.getSignatureFunction("ED25519");
		AsymmetricKeypair key1 = signFunc.generateKeypair();
		AsymmetricKeypair key2 = signFunc.generateKeypair();
		AsymmetricKeypair key3 = signFunc.generateKeypair();
		AsymmetricKeypair key4 = signFunc.generateKeypair();

		SignatureDigest digest1 = signFunc.sign(key1.getPrivKey(), "zhangsan".getBytes());
		SignatureDigest digest2 = signFunc.sign(key2.getPrivKey(), "lisi".getBytes());
		DigitalSignatureBlob endPoint1 = new DigitalSignatureBlob(key1.getPubKey(), digest1);
		DigitalSignatureBlob endPoint2 = new DigitalSignatureBlob(key2.getPubKey(), digest2);
		data.addEndpointSignatures(endPoint1);
		data.addEndpointSignatures(endPoint2);

		SignatureDigest digest3 = signFunc.sign(key3.getPrivKey(), "wangwu".getBytes());
		SignatureDigest digest4 = signFunc.sign(key4.getPrivKey(), "zhaoliu".getBytes());
		DigitalSignatureBlob node1 = new DigitalSignatureBlob(key3.getPubKey(), digest3);
		DigitalSignatureBlob node2 = new DigitalSignatureBlob(key4.getPubKey(), digest4);
		data.addNodeSignatures(node1);
		data.addNodeSignatures(node2);

		HashDigest hash = Crypto.getHashFunction("SHA256").hash("DATA".getBytes());
		data.setHash(hash);
	}

	@Test
	public void testSerialize_TransactionRequest() {
		byte[] serialBytes = BinaryProtocol.encode(data, TransactionRequest.class);
		TransactionRequest resolvedData = BinaryProtocol.decode(serialBytes);
		System.out.println("------Assert start ------");
		assertEquals(resolvedData.getEndpointSignatures().length, data.getEndpointSignatures().length);
		assertEquals(resolvedData.getNodeSignatures().length, data.getNodeSignatures().length);

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

		TransactionContent dataTxContent = data.getTransactionContent();
		TransactionContent resolvedTxContent = data.getTransactionContent();

		Operation[] dataOperations = dataTxContent.getOperations();
		Operation[] resolvedOperations = resolvedTxContent.getOperations();
		assertEquals(dataOperations.length, resolvedOperations.length);
		for (int i = 0; i < dataOperations.length; i++) {
			assertEquals(dataOperations[i], resolvedOperations[i]);
		}
		assertEqual(dataTxContent, resolvedTxContent);

		assertEquals(resolvedData.getHash(), data.getHash());
		System.out.println("------Assert OK ------");
	}

	@Test
	public void testSerialize_NodeRequest() {
		byte[] serialBytes = BinaryProtocol.encode(data, NodeRequest.class);
		NodeRequest resolvedData = BinaryProtocol.decode(serialBytes);
		System.out.println("------Assert start ------");
		assertEquals(resolvedData.getEndpointSignatures().length, data.getEndpointSignatures().length);
		assertEquals(resolvedData.getNodeSignatures().length, data.getNodeSignatures().length);

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

		TransactionContent dataTxContent = data.getTransactionContent();
		TransactionContent resolvedTxContent = data.getTransactionContent();

		Operation[] dataOperations = dataTxContent.getOperations();
		Operation[] resolvedOperations = resolvedTxContent.getOperations();
		assertEquals(dataOperations.length, resolvedOperations.length);
		for (int i = 0; i < dataOperations.length; i++) {
			assertEquals(dataOperations[i], resolvedOperations[i]);
		}
		assertEqual(dataTxContent, resolvedTxContent);
		System.out.println("------Assert OK ------");
	}

	@Test
	public void testSerialize_EndpointRequest() {
		byte[] serialBytes = BinaryProtocol.encode(data, EndpointRequest.class);
		EndpointRequest resolvedData = BinaryProtocol.decode(serialBytes);
		System.out.println("------Assert start ------");
		assertEquals(resolvedData.getEndpointSignatures().length, data.getEndpointSignatures().length);
		DigitalSignature[] dataEndpointSignatures = data.getEndpointSignatures();
		DigitalSignature[] resolvedEndpointSignatures = resolvedData.getEndpointSignatures();
		for (int i = 0; i < dataEndpointSignatures.length; i++) {
			assertEquals(dataEndpointSignatures[i].getPubKey(), resolvedEndpointSignatures[i].getPubKey());
			assertEquals(dataEndpointSignatures[i].getDigest(), resolvedEndpointSignatures[i].getDigest());
		}
		TransactionContent dataTxContent = data.getTransactionContent();
		TransactionContent resolvedTxContent = data.getTransactionContent();

		Operation[] dataOperations = dataTxContent.getOperations();
		Operation[] resolvedOperations = resolvedTxContent.getOperations();
		assertEquals(dataOperations.length, resolvedOperations.length);
		for (int i = 0; i < dataOperations.length; i++) {
			assertEquals(dataOperations[i], resolvedOperations[i]);
		}
		assertEqual(dataTxContent, resolvedTxContent);
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

	private TransactionContent initTransactionContent() throws Exception {
		TxContentBlob contentBlob = null;
		BlockchainKeypair id = BlockchainKeyGenerator.getInstance().generate("ED25519");
		HashFunction hashFunc = Crypto.getHashFunction("SHA256");
		HashDigest ledgerHash = hashFunc.hash(UUID.randomUUID().toString().getBytes("UTF-8"));
		BlockchainOperationFactory opFactory = new BlockchainOperationFactory();
		contentBlob = new TxContentBlob(ledgerHash);
		contentBlob.setHash(hashFunc.hash("jd.com".getBytes()));
		// contentBlob.setSubjectAccount(id.getAddress());
		// contentBlob.setSequenceNumber(1);
		DataAccountKVSetOperation kvsetOP = opFactory.dataAccount(id.getAddress())
				.setText("Name","AAA", -1).getOperation();
		contentBlob.addOperation(kvsetOP);
		return contentBlob;
	}
}