/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: test.com.jd.blockchain.ledger.TransactionStagedSnapshotTest
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/8/30 上午10:49
 * Description:
 */
package test.com.jd.blockchain.ledger.core;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.jd.blockchain.binaryproto.BinaryProtocol;
import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.service.classic.ClassicAlgorithm;
import com.jd.blockchain.ledger.LedgerDataSnapshot;
import com.jd.blockchain.ledger.core.TransactionStagedSnapshot;

/**
 *
 * @author shaozhuguang
 * @create 2018/8/30
 * @since 1.0.0
 */

public class TransactionStagedSnapshotTest {

	private TransactionStagedSnapshot data;

	@Before
	public void initTransactionStagedSnapshot() {
		DataContractRegistry.register(LedgerDataSnapshot.class);
		data = new TransactionStagedSnapshot();
		data.setAdminAccountHash(new HashDigest(ClassicAlgorithm.SHA256, "zhangsan".getBytes()));
		data.setContractAccountSetHash(new HashDigest(ClassicAlgorithm.SHA256, "lisi".getBytes()));
		data.setDataAccountSetHash(new HashDigest(ClassicAlgorithm.SHA256, "wangwu".getBytes()));
		data.setUserAccountSetHash(new HashDigest(ClassicAlgorithm.SHA256, "zhaoliu".getBytes()));
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
}