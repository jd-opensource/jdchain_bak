package com.jdchain.samples.sdk;

import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.KeyGenUtils;
import com.jd.blockchain.crypto.PrivKey;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.ledger.BlockchainKeypair;
import com.jd.blockchain.sdk.BlockchainService;
import com.jd.blockchain.sdk.client.GatewayServiceFactory;

import utils.codec.Base58Utils;

import java.util.Properties;

public class SampleBase {

    // 交易签名用户
    protected static BlockchainKeypair adminKey;
    // 网关IP
    protected static String gatewayHost;
    // 网关端口
    protected static int gatewayPort;
    // 账本Hash
    protected static HashDigest ledger;
    // 区块链服务
    protected static BlockchainService blockchainService;

    static {
        try {
            // 读取配置文件
            Properties properties = new Properties();
            properties.load(SampleBase.class.getClassLoader().getResourceAsStream("config.properties"));

            // 初始配置交易签名用户信息
            PubKey pubKey = KeyGenUtils.decodePubKey(properties.getProperty("pubkey"));
            PrivKey privKey = KeyGenUtils.decodePrivKey(properties.getProperty("privkey"), properties.getProperty("password"));
            adminKey = new BlockchainKeypair(pubKey, privKey);

            // 读取网关配置
            gatewayHost = properties.getProperty("gateway.host");
            gatewayPort = Integer.parseInt(properties.getProperty("gateway.port"));

            // 读取账本配置
            String ledgerHash = properties.getProperty("ledger");

            // 初始化区块链服务
            blockchainService = GatewayServiceFactory.connect(gatewayHost, gatewayPort, false, adminKey).getBlockchainService();

            // 初始配置账本，从配置文件中读取，未设置获取账本列表第一个
            if (!ledgerHash.isEmpty()) {
                ledger = Crypto.resolveAsHashDigest(Base58Utils.decode(ledgerHash));
            } else {
                ledger = blockchainService.getLedgerHashs()[0];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
