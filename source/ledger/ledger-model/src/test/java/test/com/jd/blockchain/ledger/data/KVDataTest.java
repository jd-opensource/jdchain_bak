/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: test.com.jd.blockchain.ledger.data.KVDataTest
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/8/30 上午11:08
 * Description:
 */
package test.com.jd.blockchain.ledger.data;

import com.jd.blockchain.binaryproto.BinaryEncodingUtils;
import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.ledger.BytesValueImpl;
import com.jd.blockchain.ledger.DataAccountKVSetOperation;
import com.jd.blockchain.ledger.DataType;
import com.jd.blockchain.ledger.data.DataAccountKVSetOpTemplate;
import com.jd.blockchain.ledger.data.KVData;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author shaozhuguang
 * @create 2018/8/30
 * @since 1.0.0
 */

public class KVDataTest {
    private KVData kvData;

    @Before
    public void initKVData() throws Exception {
        DataContractRegistry.register(DataAccountKVSetOperation.KVWriteEntry.class);
        String key = "test-key";
        byte[] value = "test-value".getBytes();
        long expectedVersion = 9999L;

        kvData = new KVData(key, new BytesValueImpl(DataType.BYTES, value), expectedVersion);
    }

    @Test
    public void testSerialize_KVEntry() throws Exception {
        byte[] serialBytes = BinaryEncodingUtils.encode(kvData, DataAccountKVSetOperation.KVWriteEntry.class);
        DataAccountKVSetOpTemplate.KVWriteEntry resolvedKvData = BinaryEncodingUtils.decode(serialBytes);
        System.out.println("------Assert start ------");
        assertEquals(resolvedKvData.getKey(), kvData.getKey());
        assertEquals(resolvedKvData.getExpectedVersion(), kvData.getExpectedVersion());
        assertArrayEquals(resolvedKvData.getValue().getValue().toBytes(), kvData.getValue().getValue().toBytes());
        System.out.println("------Assert OK ------");
    }
}