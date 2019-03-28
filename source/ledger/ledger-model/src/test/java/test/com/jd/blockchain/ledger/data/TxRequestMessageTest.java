/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: test.com.jd.blockchain.ledger.data.TxRequestMessageTest
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/9/3 下午3:07
 * Description:
 */
package test.com.jd.blockchain.ledger.data;

import static org.junit.Assert.assertEquals;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import com.jd.blockchain.binaryproto.BinaryEncodingUtils;
import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.CryptoUtils;
import com.jd.blockchain.crypto.asymmetric.PubKey;
import com.jd.blockchain.crypto.asymmetric.SignatureDigest;
import com.jd.blockchain.crypto.hash.HashDigest;
import com.jd.blockchain.ledger.BlockchainKeyGenerator;
import com.jd.blockchain.ledger.BlockchainKeyPair;
import com.jd.blockchain.ledger.DataAccountKVSetOperation;
import com.jd.blockchain.ledger.DigitalSignature;
import com.jd.blockchain.ledger.EndpointRequest;
import com.jd.blockchain.ledger.HashObject;
import com.jd.blockchain.ledger.NodeRequest;
import com.jd.blockchain.ledger.Operation;
import com.jd.blockchain.ledger.TransactionContent;
import com.jd.blockchain.ledger.TransactionRequest;
import com.jd.blockchain.ledger.data.BlockchainOperationFactory;
import com.jd.blockchain.ledger.data.DigitalSignatureBlob;
import com.jd.blockchain.ledger.data.TxContentBlob;
import com.jd.blockchain.ledger.data.TxRequestMessage;
import com.jd.blockchain.utils.io.ByteArray;

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

		SignatureDigest digest1 = new SignatureDigest(CryptoAlgorithm.ED25519, "zhangsan".getBytes());
		SignatureDigest digest2 = new SignatureDigest(CryptoAlgorithm.ED25519, "lisi".getBytes());
		DigitalSignatureBlob endPoint1 = new DigitalSignatureBlob(
				new PubKey(CryptoAlgorithm.ED25519, "jd1.com".getBytes()), digest1);
		DigitalSignatureBlob endPoint2 = new DigitalSignatureBlob(
				new PubKey(CryptoAlgorithm.ED25519, "jd2.com".getBytes()), digest2);
		data.addEndpointSignatures(endPoint1);
		data.addEndpointSignatures(endPoint2);

		SignatureDigest digest3 = new SignatureDigest(CryptoAlgorithm.ED25519, "wangwu".getBytes());
		SignatureDigest digest4 = new SignatureDigest(CryptoAlgorithm.ED25519, "zhaoliu".getBytes());
		DigitalSignatureBlob node1 = new DigitalSignatureBlob(new PubKey(CryptoAlgorithm.ED25519, "jd3.com".getBytes()),
				digest3);
		DigitalSignatureBlob node2 = new DigitalSignatureBlob(new PubKey(CryptoAlgorithm.ED25519, "jd4.com".getBytes()),
				digest4);
		data.addNodeSignatures(node1);
		data.addNodeSignatures(node2);

		HashDigest hash = new HashDigest(CryptoAlgorithm.SHA256, "sunqi".getBytes());
		data.setHash(hash);
	}

	@Test
	public void testSerialize_TransactionRequest() {
		byte[] serialBytes = BinaryEncodingUtils.encode(data, TransactionRequest.class);
		TransactionRequest resolvedData = BinaryEncodingUtils.decode(serialBytes);
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
		byte[] serialBytes = BinaryEncodingUtils.encode(data, NodeRequest.class);
		NodeRequest resolvedData = BinaryEncodingUtils.decode(serialBytes);
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
		byte[] serialBytes = BinaryEncodingUtils.encode(data, EndpointRequest.class);
		EndpointRequest resolvedData = BinaryEncodingUtils.decode(serialBytes);
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
		BlockchainKeyPair id = BlockchainKeyGenerator.getInstance().generate(CryptoAlgorithm.ED25519);
		HashDigest ledgerHash = CryptoUtils.hash(CryptoAlgorithm.SHA256)
				.hash(UUID.randomUUID().toString().getBytes("UTF-8"));
		BlockchainOperationFactory opFactory = new BlockchainOperationFactory();
		contentBlob = new TxContentBlob(ledgerHash);
		contentBlob.setHash(new HashDigest(CryptoAlgorithm.SHA256, "jd.com".getBytes()));
		// contentBlob.setSubjectAccount(id.getAddress());
		// contentBlob.setSequenceNumber(1);
		DataAccountKVSetOperation kvsetOP = opFactory.dataAccount(id.getAddress())
				.set("Name", ByteArray.fromString("AAA", "UTF-8"), -1).getOperation();
		contentBlob.addOperation(kvsetOP);
		return contentBlob;
	}
}