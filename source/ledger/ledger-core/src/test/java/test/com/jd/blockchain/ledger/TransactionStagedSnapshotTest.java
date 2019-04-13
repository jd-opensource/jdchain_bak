/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: test.com.jd.blockchain.ledger.TransactionStagedSnapshotTest
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/8/30 上午10:49
 * Description:
 */
package test.com.jd.blockchain.ledger;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.jd.blockchain.binaryproto.BinaryEncodingUtils;
import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.service.classic.ClassicCryptoService;
import com.jd.blockchain.ledger.LedgerDataSnapshot;
import com.jd.blockchain.ledger.core.impl.TransactionStagedSnapshot;

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
        data.setAdminAccountHash(new HashDigest(ClassicCryptoService.SHA256_ALGORITHM, "zhangsan".getBytes()));
        data.setContractAccountSetHash(new HashDigest(ClassicCryptoService.SHA256_ALGORITHM, "lisi".getBytes()));
        data.setDataAccountSetHash(new HashDigest(ClassicCryptoService.SHA256_ALGORITHM, "wangwu".getBytes()));
        data.setUserAccountSetHash(new HashDigest(ClassicCryptoService.SHA256_ALGORITHM, "zhaoliu".getBytes()));
    }

    @Test
    public void testSerialize_LedgerDataSnapshot() throws Exception {
        byte[] serialBytes = BinaryEncodingUtils.encode(data, LedgerDataSnapshot.class);
        LedgerDataSnapshot resolvedData = BinaryEncodingUtils.decode(serialBytes);
        System.out.println("------Assert start ------");
        assertEquals(resolvedData.getAdminAccountHash(), data.getAdminAccountHash());
        assertEquals(resolvedData.getContractAccountSetHash(), data.getContractAccountSetHash());
        assertEquals(resolvedData.getDataAccountSetHash(), data.getDataAccountSetHash());
        assertEquals(resolvedData.getUserAccountSetHash(), data.getUserAccountSetHash());
        System.out.println("------Assert OK ------");
    }
}