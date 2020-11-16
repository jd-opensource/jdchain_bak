package com.jd.blockchain.sdk.samples;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.KeyGenUtils;
import com.jd.blockchain.crypto.PrivKey;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.ledger.BlockchainKeypair;
import com.jd.blockchain.ledger.PreparedTransaction;
import com.jd.blockchain.ledger.TransactionResponse;
import com.jd.blockchain.ledger.TransactionTemplate;
import com.jd.blockchain.sdk.BlockchainService;
import com.jd.blockchain.sdk.client.GatewayServiceFactory;

public abstract class SDK_Base_Demo {

    protected BlockchainKeypair adminKey;

    protected HashDigest ledgerHash;

    protected BlockchainService blockchainService;

    public SDK_Base_Demo() {
        init();
    }

    public void init() {
        // 生成连接网关的账号
        PrivKey privKey = KeyGenUtils.decodePrivKeyWithRawPassword(SDKDemo_Constant.PRIV_KEYS[0], SDKDemo_Constant.PASSWORD);

        PubKey pubKey = KeyGenUtils.decodePubKey(SDKDemo_Constant.PUB_KEYS[0]);

        adminKey = new BlockchainKeypair(pubKey, privKey);

        // 连接网关
        GatewayServiceFactory serviceFactory = GatewayServiceFactory.connect(SDKDemo_Constant.GW_IPADDR,
                SDKDemo_Constant.GW_PORT, false, adminKey);

        // 获取网关对应的Service处理类
        blockchainService = serviceFactory.getBlockchainService();

        HashDigest[] ledgerHashs = blockchainService.getLedgerHashs();
        // 获取当前账本Hash
        ledgerHash = ledgerHashs[0];
    }

    public TransactionResponse commit(TransactionTemplate txTpl) {
        PreparedTransaction ptx = txTpl.prepare();
        ptx.sign(adminKey);
        return ptx.commit();
    }

    protected void printTxResponse(TransactionResponse response) {
        System.out.printf("TxResponse's state = [%s][%s], blockHeight = [%s] ! \r\n",
                response.isSuccess(), response.getExecutionState(), response.getBlockHeight());
    }
}
