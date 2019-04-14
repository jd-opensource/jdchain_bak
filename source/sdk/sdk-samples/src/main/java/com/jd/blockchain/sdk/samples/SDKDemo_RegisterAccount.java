package com.jd.blockchain.sdk.samples;

import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.crypto.AsymmetricKeypair;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.*;
import com.jd.blockchain.sdk.BlockchainService;
import com.jd.blockchain.sdk.client.GatewayServiceFactory;
import com.jd.blockchain.utils.ConsoleUtils;

public class SDKDemo_RegisterAccount {

    public static void main(String[] args) {

        String GATEWAY_IPADDR = "127.0.0.1";
        int GATEWAY_PORT = 8081;
        if (args != null && args.length == 2) {
            GATEWAY_IPADDR = args[0];
            GATEWAY_PORT = Integer.parseInt(args[1]);
        }

        DataContractRegistry.register(TransactionContent.class);
        DataContractRegistry.register(TransactionContentBody.class);
        DataContractRegistry.register(TransactionRequest.class);
        DataContractRegistry.register(NodeRequest.class);
        DataContractRegistry.register(EndpointRequest.class);
        DataContractRegistry.register(TransactionResponse.class);

        BlockchainKeyPair CLIENT_CERT = new BlockchainKeyPair(SDKDemo_Params.pubKey0, SDKDemo_Params.privkey0);
        boolean SECURE = false;
        GatewayServiceFactory serviceFactory = GatewayServiceFactory.connect(GATEWAY_IPADDR, GATEWAY_PORT, SECURE,
                CLIENT_CERT);
        BlockchainService service = serviceFactory.getBlockchainService();

        HashDigest[] ledgerHashs = service.getLedgerHashs();
        // 在本地定义注册账号的 TX；
        TransactionTemplate txTemp = service.newTransaction(ledgerHashs[0]);

        //existed signer
        AsymmetricKeypair keyPair = new BlockchainKeyPair(SDKDemo_Params.pubKey1, SDKDemo_Params.privkey1);

        BlockchainKeyPair dataAcount = BlockchainKeyGenerator.getInstance().generate();

        // 注册
        txTemp.dataAccounts().register(dataAcount.getIdentity());

        // TX 准备就绪；
        PreparedTransaction prepTx = txTemp.prepare();

        prepTx.sign(keyPair);

        // 提交交易；
        TransactionResponse transactionResponse = prepTx.commit();

        ConsoleUtils.info("register dataaccount complete, result is [%s]", transactionResponse.isSuccess());
    }
}
