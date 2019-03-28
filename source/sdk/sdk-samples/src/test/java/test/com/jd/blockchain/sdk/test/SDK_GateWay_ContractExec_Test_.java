/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: test.com.jd.blockchain.sdk.test.SDK_GateWay_InsertData_Test
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/9/4 上午11:06
 * Description: 插入数据测试
 */
package test.com.jd.blockchain.sdk.test;

import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.asymmetric.AsymmetricCryptography;
import com.jd.blockchain.crypto.asymmetric.CryptoKeyPair;
import com.jd.blockchain.crypto.asymmetric.SignatureFunction;
import com.jd.blockchain.crypto.hash.HashDigest;
import com.jd.blockchain.crypto.impl.AsymmtricCryptographyImpl;
import com.jd.blockchain.ledger.*;
import com.jd.blockchain.ledger.data.TxResponseMessage;
import com.jd.blockchain.sdk.BlockchainService;
import com.jd.blockchain.sdk.BlockchainTransactionService;
import com.jd.blockchain.sdk.client.GatewayServiceFactory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * 插入数据测试
 * @author shaozhuguang
 * @create 2018/9/4
 * @since 1.0.0
 */

public class SDK_GateWay_ContractExec_Test_ {

    private BlockchainKeyPair CLIENT_CERT = null;

    private String GATEWAY_IPADDR = null;

    private int GATEWAY_PORT;

    private boolean SECURE;

    private BlockchainService service;

    private AsymmetricCryptography asymmetricCryptography = new AsymmtricCryptographyImpl();

    @Before
    public void init() {
        CLIENT_CERT = BlockchainKeyGenerator.getInstance().generate(CryptoAlgorithm.ED25519);
        GATEWAY_IPADDR = "127.0.0.1";
        GATEWAY_PORT = 8000;
        SECURE = false;
        GatewayServiceFactory serviceFactory = GatewayServiceFactory.connect(
                GATEWAY_IPADDR, GATEWAY_PORT, SECURE, CLIENT_CERT);
        service = serviceFactory.getBlockchainService();
    }

    @Test
    public void contractExec_Test() {
        HashDigest ledgerHash = getLedgerHash();
        // 在本地定义TX模板
        TransactionTemplate txTemp = service.newTransaction(ledgerHash);

        // 合约地址
        String contractAddressBase58 = "";

        // Event
        String event = "";

        // args（注意参数的格式）
        byte[] args = "20##30##abc".getBytes();


        // 提交合约执行代码
        txTemp.contractEvents().send(contractAddressBase58, event, args);

        // TX 准备就绪；
        PreparedTransaction prepTx = txTemp.prepare();

        // 生成私钥并使用私钥进行签名；
        CryptoKeyPair keyPair = getSponsorKey();

        prepTx.sign(keyPair);

        // 提交交易；
        TransactionResponse transactionResponse = prepTx.commit();

        assertTrue(transactionResponse.isSuccess());
    }

    private HashDigest getLedgerHash() {
        HashDigest ledgerHash = new HashDigest(CryptoAlgorithm.SHA256, "jd-gateway".getBytes());
        return ledgerHash;
    }


    private CryptoKeyPair getSponsorKey() {
        SignatureFunction signatureFunction = asymmetricCryptography.getSignatureFunction(CryptoAlgorithm.ED25519);
        return signatureFunction.generateKeyPair();
	}
}