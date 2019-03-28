/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: test.com.jd.blockchain.ledger.data.DataAccountRegisterOpTemplateTest
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/8/30 上午11:03
 * Description:
 */
package test.com.jd.blockchain.ledger.data;

import com.jd.blockchain.binaryproto.BinaryEncodingUtils;
import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.asymmetric.PubKey;
import com.jd.blockchain.ledger.*;
import com.jd.blockchain.ledger.data.DataAccountRegisterOpTemplate;
import com.jd.blockchain.utils.io.ByteArray;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author shaozhuguang
 * @create 2018/8/30
 * @since 1.0.0
 */

public class DataAccountRegisterOpTemplateTest {

    private DataAccountRegisterOpTemplate data;

    @Before
    public void initDataAccountRegisterOpTemplate() {
        DataContractRegistry.register(DataAccountRegisterOperation.class);
        DataContractRegistry.register(Operation.class);
        String pubKeyVal = "jd.com";
        PubKey pubKey = new PubKey(CryptoAlgorithm.ED25519, pubKeyVal.getBytes());
        BlockchainIdentity contractID = new BlockchainIdentityData(pubKey);
        data = new DataAccountRegisterOpTemplate(contractID);

    }

    @Test
    public void testSerialize_DataAccountRegisterOperation() throws Exception {
        byte[] serialBytes = BinaryEncodingUtils.encode(data, DataAccountRegisterOperation.class);
        DataAccountRegisterOperation resolvedData = BinaryEncodingUtils.decode(serialBytes);
        System.out.println("------Assert start ------");
        assertEquals(resolvedData.getAccountID().getAddress(), data.getAccountID().getAddress());
        assertEquals(resolvedData.getAccountID().getPubKey(), data.getAccountID().getPubKey());

        System.out.println("------Assert OK ------");
    }

    @Test
    public void testSerialize_Operation() throws Exception {
        byte[] serialBytes = BinaryEncodingUtils.encode(data, Operation.class);
        Operation resolvedData = BinaryEncodingUtils.decode(serialBytes);
        System.out.println("------Assert start ------");
        System.out.println("serialBytesLength=" + serialBytes.length);
        System.out.println(resolvedData);
        System.out.println("------Assert OK ------");
    }
}