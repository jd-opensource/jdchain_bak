/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: test.com.jd.blockchain.ledger.data.DigitalSignatureBlobTest
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/8/30 下午2:12
 * Description:
 */
package test.com.jd.blockchain.ledger.data;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.jd.blockchain.binaryproto.BinaryEncodingUtils;
import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.crypto.asymmetric.SignatureDigest;
import com.jd.blockchain.ledger.DigitalSignature;
import com.jd.blockchain.ledger.DigitalSignatureBody;
import com.jd.blockchain.ledger.data.DigitalSignatureBlob;

/**
 *
 * @author shaozhuguang
 * @create 2018/8/30
 * @since 1.0.0
 */

public class DigitalSignatureBlobTest {

    private DigitalSignatureBlob data;

    @Before
    public void initDigitalSignatureBlob() throws Exception {
        DataContractRegistry.register(DigitalSignature.class);
        DataContractRegistry.register(DigitalSignatureBody.class);
        PubKey pubKey = new PubKey(CryptoAlgorithm.ED25519, "jd.com".getBytes());
        SignatureDigest digest = new SignatureDigest(CryptoAlgorithm.ED25519, "zhangsan".getBytes());
        data = new DigitalSignatureBlob(pubKey, digest);
    }

    @Test
    public void testSerialize_DigitalSignature() throws Exception {
        byte[] serialBytes = BinaryEncodingUtils.encode(data, DigitalSignature.class);
        DigitalSignature resolvedData = BinaryEncodingUtils.decode(serialBytes);
        System.out.println("------Assert start ------");
        assertEquals(resolvedData.getDigest(), data.getDigest());
        assertEquals(resolvedData.getPubKey(), data.getPubKey());
        System.out.println("------Assert OK ------");
    }

    @Test
    public void testSerialize_DigitalSignatureBody() throws Exception {
        byte[] serialBytes = BinaryEncodingUtils.encode(data, DigitalSignatureBody.class);
        DigitalSignatureBody resolvedData = BinaryEncodingUtils.decode(serialBytes);
        System.out.println("------Assert start ------");
        assertEquals(resolvedData.getDigest(), data.getDigest());
        assertEquals(resolvedData.getPubKey(), data.getPubKey());
        System.out.println("------Assert OK ------");
    }
}