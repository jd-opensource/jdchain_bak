package test.com.jd.blockchain.ledger.data;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import com.jd.blockchain.binaryproto.BinaryEncodingUtils;
import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.BlockchainKeyGenerator;
import com.jd.blockchain.ledger.BlockchainKeypair;
import com.jd.blockchain.ledger.DataAccountKVSetOperation;
import com.jd.blockchain.ledger.HashObject;
import com.jd.blockchain.ledger.Operation;
import com.jd.blockchain.ledger.TransactionContent;
import com.jd.blockchain.ledger.TransactionContentBody;
import com.jd.blockchain.ledger.data.BlockchainOperationFactory;
import com.jd.blockchain.ledger.data.TxContentBlob;
import com.jd.blockchain.utils.io.ByteArray;

public class TxContentBlobTest {

	private TxContentBlob contentBlob;

	@Before
	public void initTxContentBlob() throws Exception {
		DataContractRegistry.register(TransactionContentBody.class);
		DataContractRegistry.register(TransactionContent.class);
		DataContractRegistry.register(HashObject.class);

		BlockchainKeypair id = BlockchainKeyGenerator.getInstance().generate("ED25519");

		HashDigest ledgerHash = Crypto.getHashFunction("SHA256")
				.hash(UUID.randomUUID().toString().getBytes("UTF-8"));

		BlockchainOperationFactory opFactory = new BlockchainOperationFactory();

		contentBlob = new TxContentBlob(ledgerHash);

		HashDigest contentHash = Crypto.getHashFunction("SHA256")
				.hash("jd.com".getBytes());
		contentBlob.setHash(contentHash);

		DataAccountKVSetOperation kvsetOP = opFactory.dataAccount(id.getAddress())
				.set("Name", ByteArray.fromString("AAA", "UTF-8"), -1).getOperation();
		contentBlob.addOperation(kvsetOP);
	}

	@Test
	public void testSerialize_TransactionContentBody()
			throws IOException, InstantiationException, IllegalAccessException {

		byte[] bytesContent = BinaryEncodingUtils.encode(contentBlob, TransactionContentBody.class);
		TransactionContentBody resolvedContentBlob = BinaryEncodingUtils.decode(bytesContent);

		assertEquals(contentBlob.getLedgerHash(), resolvedContentBlob.getLedgerHash());
		// assertEquals(contentBlob.getSubjectAccount(),
		// resolvedContentBlob.getSubjectAccount());
		// assertEquals(contentBlob.getSequenceNumber(),
		// resolvedContentBlob.getSequenceNumber());
		assertEquals(contentBlob.getOperations().length, resolvedContentBlob.getOperations().length);

		assertEquals(contentBlob.getOperations().length, resolvedContentBlob.getOperations().length);
		Operation[] resolvedOperations = resolvedContentBlob.getOperations();
		Operation[] dataOperations = contentBlob.getOperations();
		for (int i = 0; i < dataOperations.length; i++) {
			DataAccountKVSetOperation resolvedOperation = (DataAccountKVSetOperation) resolvedOperations[i];
			DataAccountKVSetOperation dataOperation = (DataAccountKVSetOperation) dataOperations[i];
			assertEquals(dataOperation.getAccountAddress(), resolvedOperation.getAccountAddress());
			DataAccountKVSetOperation.KVWriteEntry[] dataKv = dataOperation.getWriteSet();
			DataAccountKVSetOperation.KVWriteEntry[] resolvedKv = resolvedOperation.getWriteSet();
			for (int j = 0; j < dataKv.length; j++) {
				assertEquals(dataKv[i].getKey(), resolvedKv[i].getKey());
				assertEquals(dataKv[i].getExpectedVersion(), resolvedKv[i].getExpectedVersion());
				assertArrayEquals(dataKv[i].getValue().getValue().toBytes(),
						resolvedKv[i].getValue().getValue().toBytes());
			}
		}
	}

	@Test
	public void testSerialize_TransactionContent() throws IOException, InstantiationException, IllegalAccessException {

		byte[] bytesContent = BinaryEncodingUtils.encode(contentBlob, TransactionContent.class);
		TransactionContentBody resolvedContentBlob = BinaryEncodingUtils.decode(bytesContent);

		assertEquals(contentBlob.getLedgerHash(), resolvedContentBlob.getLedgerHash());
		// assertEquals(contentBlob.getSubjectAccount(),
		// resolvedContentBlob.getSubjectAccount());
		// assertEquals(contentBlob.getSequenceNumber(),
		// resolvedContentBlob.getSequenceNumber());
		assertEquals(contentBlob.getOperations().length, resolvedContentBlob.getOperations().length);

		assertEquals(contentBlob.getOperations().length, resolvedContentBlob.getOperations().length);
		Operation[] resolvedOperations = resolvedContentBlob.getOperations();
		Operation[] dataOperations = contentBlob.getOperations();
		for (int i = 0; i < dataOperations.length; i++) {
			DataAccountKVSetOperation resolvedOperation = (DataAccountKVSetOperation) resolvedOperations[i];
			DataAccountKVSetOperation dataOperation = (DataAccountKVSetOperation) dataOperations[i];
			assertEquals(dataOperation.getAccountAddress(), resolvedOperation.getAccountAddress());
			DataAccountKVSetOperation.KVWriteEntry[] dataKv = dataOperation.getWriteSet();
			DataAccountKVSetOperation.KVWriteEntry[] resolvedKv = resolvedOperation.getWriteSet();
			for (int j = 0; j < dataKv.length; j++) {
				assertEquals(dataKv[i].getKey(), resolvedKv[i].getKey());
				assertEquals(dataKv[i].getExpectedVersion(), resolvedKv[i].getExpectedVersion());
				assertArrayEquals(dataKv[i].getValue().getValue().toBytes(),
						resolvedKv[i].getValue().getValue().toBytes());
			}
		}
	}
}
