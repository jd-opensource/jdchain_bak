/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: test.com.jd.blockchain.ledger.data.DataAccountKVSetOpTemplateTest
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/8/30 上午10:59
 * Description:
 */
package test.com.jd.blockchain.ledger.data;

import com.jd.blockchain.binaryproto.BinaryProtocol;
import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.ledger.BytesValueEntry;
import com.jd.blockchain.ledger.DataAccountKVSetOperation;
import com.jd.blockchain.ledger.BytesValueType;
import com.jd.blockchain.ledger.Operation;
import com.jd.blockchain.transaction.DataAccountKVSetOpTemplate;
import com.jd.blockchain.transaction.KVData;
import com.jd.blockchain.utils.Bytes;

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
//todo 尚未测试通过，等张爽处理完再测试
public class DataAccountKVSetOpTemplateTest {

    private DataAccountKVSetOpTemplate data;

    @Before
    public void initDataAccountKVSetOpTemplate() {
        DataContractRegistry.register(DataAccountKVSetOperation.class);
        DataContractRegistry.register(DataAccountKVSetOperation.KVWriteEntry.class);
        DataContractRegistry.register(Operation.class);
        String accountAddress = "zhangsandhakhdkah";
        data = new DataAccountKVSetOpTemplate(Bytes.fromString(accountAddress));
        KVData kvData1 =
                new KVData("test1", new BytesValueEntry(BytesValueType.TEXT, "zhangsan".getBytes()), 9999L);
        KVData kvData2 =
                new KVData("test2", new BytesValueEntry(BytesValueType.TEXT, "lisi".getBytes()), 9990L);
        KVData kvData3 =
                new KVData("test3", new BytesValueEntry(BytesValueType.TEXT, "wangwu".getBytes()), 1990L);
        data.set(kvData1);
        data.set(kvData2);
        data.set(kvData3);
    }


    @Test
    public void testSerialize_DataAccountKVSetOperation() throws Exception {
        byte[] serialBytes = BinaryProtocol.encode(data, DataAccountKVSetOperation.class);
        DataAccountKVSetOperation resolvedData = BinaryProtocol.decode(serialBytes);
        System.out.println("------Assert start ------");
        assertEquals(resolvedData.getAccountAddress(), data.getAccountAddress());
        DataAccountKVSetOperation.KVWriteEntry[] resolvedKv = resolvedData.getWriteSet();
        DataAccountKVSetOperation.KVWriteEntry[] dataKv = data.getWriteSet();
        assertEquals(dataKv.length, resolvedKv.length);
        for (int i = 0; i < dataKv.length; i++) {
            assertEquals(dataKv[i].getKey(), resolvedKv[i].getKey());
            assertArrayEquals(dataKv[i].getValue().getValue().toBytes(), resolvedKv[i].getValue().getValue().toBytes());
            assertEquals(dataKv[i].getValue().getType().CODE, resolvedKv[i].getValue().getType().CODE);

            assertEquals(dataKv[i].getExpectedVersion(), resolvedKv[i].getExpectedVersion());
        }
        System.out.println("------Assert OK ------");
    }

    @Test
    public void testSerialize_Operation() throws Exception {
        byte[] serialBytes = BinaryProtocol.encode(data, Operation.class);
        Operation resolvedData = BinaryProtocol.decode(serialBytes);
        System.out.println("------Assert start ------");
        System.out.println(resolvedData);
        System.out.println("serialBytesLength=" + serialBytes.length);
        System.out.println("------Assert OK ------");
    }
}