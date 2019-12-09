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
import com.jd.blockchain.crypto.*;
import com.jd.blockchain.ledger.*;
import com.jd.blockchain.sdk.BlockchainService;
import com.jd.blockchain.sdk.client.GatewayServiceFactory;
import com.jd.blockchain.transaction.TxResponseMessage;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * 注册具有无效签名的用户账户
 * @author zhangshuang
 * @create 2019/12/6
 * @since 1.0.0
 */

public class SDK_GateWay_Invalid_Signer_Test_ {

    private PrivKey privKey;
    private PubKey pubKey;

    private BlockchainKeypair CLIENT_CERT = null;

    private String GATEWAY_IPADDR = null;

    private int GATEWAY_PORT;

    private boolean SECURE;

    private BlockchainService service;

    public static final String PASSWORD = "abc";

    public static final String PUB_KEYS = "3snPdw7i7Pb3B5AxpSXy6YVruvftugNQ7rB7k2KWukhBwKQhFBFagT";
    public static final String PRIV_KEYS = "177gjtSgSdUF3LwRFGhzbpZZxmXXChsnwbuuLCG1V9KYfVuuxLwXGmZCp5FGUvsenhwBQLV";

    @Before
    public void init() {

        privKey = KeyGenUtils.decodePrivKeyWithRawPassword(PRIV_KEYS, PASSWORD);
        pubKey = KeyGenUtils.decodePubKey(PUB_KEYS);

        CLIENT_CERT = new BlockchainKeypair(SDK_GateWay_KeyPair_Para.pubKey0, SDK_GateWay_KeyPair_Para.privkey0);
        GATEWAY_IPADDR = "localhost";
        GATEWAY_PORT = 11000;
        SECURE = false;
        GatewayServiceFactory serviceFactory = GatewayServiceFactory.connect(GATEWAY_IPADDR, GATEWAY_PORT, SECURE,
                CLIENT_CERT);
        service = serviceFactory.getBlockchainService();

        DataContractRegistry.register(TransactionContent.class);
        DataContractRegistry.register(TransactionContentBody.class);
        DataContractRegistry.register(TransactionRequest.class);
        DataContractRegistry.register(NodeRequest.class);
        DataContractRegistry.register(EndpointRequest.class);
        DataContractRegistry.register(TransactionResponse.class);
    }

    @Test
    public void registerUser_Test() {
        HashDigest[] ledgerHashs = service.getLedgerHashs();
        // 在本地定义注册账号的 TX；
        TransactionTemplate txTemp = service.newTransaction(ledgerHashs[0]);

        //existed signer
        AsymmetricKeypair keyPair = new BlockchainKeypair(pubKey, privKey);

        BlockchainKeypair user = BlockchainKeyGenerator.getInstance().generate();

        // 注册
        txTemp.users().register(user.getIdentity());

        // TX 准备就绪；
        PreparedTransaction prepTx = txTemp.prepare();

        // 使用私钥进行签名；
        prepTx.sign(keyPair);

        // 提交交易；
        TransactionResponse transactionResponse = prepTx.commit();

        assertTrue(transactionResponse.getExecutionState().CODE == TransactionState.IGNORED_BY_BLOCK_FULL_ROLLBACK.CODE);

    }
}