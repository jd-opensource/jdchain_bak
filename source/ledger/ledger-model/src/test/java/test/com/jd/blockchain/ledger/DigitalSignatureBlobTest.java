/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: test.com.jd.blockchain.ledger.data.DigitalSignatureBlobTest
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/8/30 下午2:12
 * Description:
 */
package test.com.jd.blockchain.ledger;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.jd.blockchain.binaryproto.BinaryProtocol;
import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.AsymmetricKeypair;
import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.crypto.SignatureDigest;
import com.jd.blockchain.crypto.SignatureFunction;
import com.jd.blockchain.ledger.DigitalSignature;
import com.jd.blockchain.ledger.DigitalSignatureBody;
import com.jd.blockchain.transaction.DigitalSignatureBlob;

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
        SignatureFunction signFunc = Crypto.getSignatureFunction("ED25519");
        AsymmetricKeypair kp = signFunc.generateKeypair();
		PubKey pubKey = kp.getPubKey();
		
        SignatureDigest digest = signFunc.sign(kp.getPrivKey(), "zhangsan".getBytes());
        data = new DigitalSignatureBlob(pubKey, digest);
    }

    @Test
    public void testSerialize_DigitalSignature() throws Exception {
        byte[] serialBytes = BinaryProtocol.encode(data, DigitalSignature.class);
        DigitalSignature resolvedData = BinaryProtocol.decode(serialBytes);
        System.out.println("------Assert start ------");
        assertEquals(resolvedData.getDigest(), data.getDigest());
        assertEquals(resolvedData.getPubKey(), data.getPubKey());
        System.out.println("------Assert OK ------");
    }

    @Test
    public void testSerialize_DigitalSignatureBody() throws Exception {
        byte[] serialBytes = BinaryProtocol.encode(data, DigitalSignatureBody.class);
        DigitalSignatureBody resolvedData = BinaryProtocol.decode(serialBytes);
        System.out.println("------Assert start ------");
        assertEquals(resolvedData.getDigest(), data.getDigest());
        assertEquals(resolvedData.getPubKey(), data.getPubKey());
        System.out.println("------Assert OK ------");
    }
}