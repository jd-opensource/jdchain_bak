package com.jd.blockchain.sdk.samples;

import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.crypto.*;
import com.jd.blockchain.ledger.*;
import com.jd.blockchain.sdk.BlockchainService;
import com.jd.blockchain.sdk.client.GatewayServiceFactory;
import com.jd.blockchain.utils.net.NetworkAddress;

/**
 * @Author: zhangshuang
 * @Date: 2020/5/27 5:18 PM
 * Version 1.0
 */
public class SDKDemo_RegistParticipant {

    public static void main(String[] args) {
        PrivKey privKey;
        PubKey pubKey;

        BlockchainKeypair CLIENT_CERT = null;

        String GATEWAY_IPADDR = null;

        int GATEWAY_PORT;

        boolean SECURE;

        BlockchainService service;

        //根据密码工具产生的公私钥
        String PUB = "3snPdw7i7PkdgqiGX7GbZuFSi1cwZn7vtjw4vifb1YoXgr9k6Kfmis";
        String PRIV = "177gjtZu8w1phqHFVNiFhA35cfimXmP6VuqrBFhfbXBWK8s4TRwro2tnpffwP1Emwr6SMN6";

        privKey = SDKDemo_Params.privkey1;
        pubKey = SDKDemo_Params.pubKey1;

        CLIENT_CERT = new BlockchainKeypair(SDKDemo_Params.pubKey0, SDKDemo_Params.privkey0);
        GATEWAY_IPADDR = "127.0.0.1";
        GATEWAY_PORT = 11000;
        SECURE = false;
        GatewayServiceFactory serviceFactory = GatewayServiceFactory.connect(GATEWAY_IPADDR, GATEWAY_PORT, SECURE,
                CLIENT_CERT);
        service = serviceFactory.getBlockchainService();

        DataContractRegistry.register(TransactionContent.class);
        DataContractRegistry.register(TransactionRequest.class);
        DataContractRegistry.register(TransactionResponse.class);
        DataContractRegistry.register(ParticipantRegisterOperation.class);
        DataContractRegistry.register(ParticipantStateUpdateOperation.class);
        DataContractRegistry.register(ConsensusSettingsUpdateOperation.class);

        HashDigest[] ledgerHashs = service.getLedgerHashs();
        // 在本地定义注册账号的 TX；
        TransactionTemplate txTemp = service.newTransaction(ledgerHashs[0]);

        //existed signer
        AsymmetricKeypair keyPair = new BlockchainKeypair(pubKey, privKey);

        privKey = KeyGenUtils.decodePrivKeyWithRawPassword(PRIV, SDKDemo_Constant.PASSWORD);

        pubKey = KeyGenUtils.decodePubKey(PUB);

        System.out.println("Address = " + AddressEncoding.generateAddress(pubKey));

        BlockchainKeypair user = new BlockchainKeypair(pubKey, privKey);

        // 注册参与方
        txTemp.participants().register("Peer4", user.getIdentity());

        // TX 准备就绪；
        PreparedTransaction prepTx = txTemp.prepare();

        // 使用私钥进行签名；
        prepTx.sign(keyPair);

        // 提交交易；
        TransactionResponse transactionResponse = prepTx.commit();
    }


}
