/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: test.com.jd.blockchain.sdk.test.SDK_GateWay_InsertData_Test
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/9/4 上午11:06
 * Description: 插入数据测试
 */
package test.com.jd.blockchain.sdk.test;

import com.jd.blockchain.crypto.asymmetric.*;
import com.jd.blockchain.crypto.hash.HashDigest;
import com.jd.blockchain.ledger.*;
import com.jd.blockchain.sdk.BlockchainService;
import com.jd.blockchain.sdk.client.GatewayServiceFactory;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * 插入数据测试
 * @author shaozhuguang
 * @create 2018/9/4
 * @since 1.0.0
 */

public class SDK_GateWay_User_Test_ {

    private PrivKey privKey;

    private PubKey pubKey;

    private BlockchainKeyPair CLIENT_CERT = null;

    private String GATEWAY_IPADDR = null;

    private int GATEWAY_PORT;

    private boolean SECURE;

    private BlockchainService service;

    @Before
    public void init() {

        privKey = SDK_GateWay_KeyPair_Para.privkey1;
        pubKey = SDK_GateWay_KeyPair_Para.pubKey1;

        CLIENT_CERT = new BlockchainKeyPair(SDK_GateWay_KeyPair_Para.pubKey0, SDK_GateWay_KeyPair_Para.privkey0);
        GATEWAY_IPADDR = "127.0.0.1";
        GATEWAY_PORT = 8081;
        SECURE = false;
        GatewayServiceFactory serviceFactory = GatewayServiceFactory.connect(
                GATEWAY_IPADDR, GATEWAY_PORT, SECURE, CLIENT_CERT);
        service = serviceFactory.getBlockchainService();
    }

    @Test
    public void registerUser_Test() {
        HashDigest[] ledgerHashs = service.getLedgerHashs();
        // 在本地定义TX模板
        TransactionTemplate txTemp = service.newTransaction(ledgerHashs[0]);

        //existed signer
        CryptoKeyPair keyPair = new BlockchainKeyPair(pubKey, privKey);

        BlockchainKeyPair user = BlockchainKeyGenerator.getInstance().generate();

        // 注册
        txTemp.users().register(user.getIdentity());

        // TX 准备就绪；
        PreparedTransaction prepTx = txTemp.prepare();

        // 使用私钥进行签名；
        prepTx.sign(keyPair);

        // 提交交易；
        TransactionResponse transactionResponse = prepTx.commit();
        assertTrue(transactionResponse.isSuccess());
    }
}