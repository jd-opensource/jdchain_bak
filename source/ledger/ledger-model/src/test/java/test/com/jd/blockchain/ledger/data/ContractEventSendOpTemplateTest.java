/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: test.com.jd.blockchain.ledger.data.ContractEventSendOpTemplateTest
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/8/30 上午10:56
 * Description:
 */
package test.com.jd.blockchain.ledger.data;

import com.jd.blockchain.binaryproto.BinaryProtocol;
import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.ledger.ContractEventSendOperation;
import com.jd.blockchain.ledger.DataAccountKVSetOperation;
import com.jd.blockchain.ledger.Operation;
import com.jd.blockchain.transaction.ContractEventSendOpTemplate;
import com.jd.blockchain.transaction.DataAccountKVSetOpTemplate;
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

public class ContractEventSendOpTemplateTest {

    private ContractEventSendOpTemplate data;

    @Before
    public void initContractEventSendOpTemplate() {
        DataContractRegistry.register(ContractEventSendOperation.class);
        DataContractRegistry.register(Operation.class);
        String contractAddress = "zhangsan-address", event = "zhangsan-event";
        byte[] args = "zhangsan-args".getBytes();
        data = new ContractEventSendOpTemplate(Bytes.fromString(contractAddress), event, args);
    }

    @Test
    public void testSerialize_ContractEventSendOperation() throws Exception {
        byte[] serialBytes = BinaryProtocol.encode(data, ContractEventSendOperation.class);
        ContractEventSendOperation resolvedData = BinaryProtocol.decode(serialBytes);
        System.out.println("------Assert start ------");
        assertEquals(resolvedData.getContractAddress(), data.getContractAddress());
        assertEquals(resolvedData.getEvent(), data.getEvent());
        assertArrayEquals(resolvedData.getArgs(), data.getArgs());
        System.out.println("------Assert OK ------");
    }

    @Test
    public void testSerialize_Operation() throws Exception {
        byte[] serialBytes = BinaryProtocol.encode(data, Operation.class);
        Operation resolvedData = BinaryProtocol.decode(serialBytes);
        System.out.println("------Assert start ------");
        System.out.println(resolvedData);
        System.out.println("------Assert OK ------");
    }
}