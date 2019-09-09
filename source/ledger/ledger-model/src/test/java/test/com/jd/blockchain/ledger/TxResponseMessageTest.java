/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: test.com.jd.blockchain.ledger.data.TxResponseMessageTest
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/9/6 上午11:00
 * Description:
 */
package test.com.jd.blockchain.ledger;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.jd.blockchain.binaryproto.BinaryProtocol;
import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.HashFunction;
import com.jd.blockchain.ledger.TransactionResponse;
import com.jd.blockchain.ledger.TransactionState;
import com.jd.blockchain.transaction.TxResponseMessage;

/**
 *
 * @author shaozhuguang
 * @create 2018/9/6
 * @since 1.0.0
 */

public class TxResponseMessageTest {

	private TxResponseMessage data;

	@Before
	public void initTxRequestMessage() throws Exception {
		DataContractRegistry.register(TransactionResponse.class);
		HashFunction hashFunc = Crypto.getHashFunction("SHA256");
		HashDigest contentHash = hashFunc.hash("jd-content".getBytes());

		HashDigest blockHash = hashFunc.hash("jd-block".getBytes());

		long blockHeight = 9999L;
		data = new TxResponseMessage(contentHash);
		data.setBlockHash(blockHash);
		data.setBlockHeight(blockHeight);
		data.setExecutionState(TransactionState.SUCCESS);
	}

	@Test
	public void testSerialize_TransactionResponse() {
		byte[] serialBytes = BinaryProtocol.encode(data, TransactionResponse.class);
		TransactionResponse resolvedData = BinaryProtocol.decode(serialBytes);
		System.out.println("------Assert start ------");
		assertEquals(resolvedData.getBlockHash(), data.getBlockHash());
		assertEquals(resolvedData.getBlockHeight(), data.getBlockHeight());
		assertEquals(resolvedData.getContentHash(), data.getContentHash());
		assertEquals(resolvedData.getExecutionState(), data.getExecutionState());
		assertEquals(resolvedData.isSuccess(), data.isSuccess());
		System.out.println("------Assert OK ------");
	}
}