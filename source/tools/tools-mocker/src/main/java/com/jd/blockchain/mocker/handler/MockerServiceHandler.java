package com.jd.blockchain.mocker.handler;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.KeyGenUtils;
import com.jd.blockchain.crypto.PrivKey;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.ledger.*;
import com.jd.blockchain.mocker.config.MockerConstant;
import com.jd.blockchain.mocker.data.KvData;
import com.jd.blockchain.mocker.data.ResponseData;
import com.jd.blockchain.sdk.BlockchainService;
import com.jd.blockchain.sdk.client.GatewayServiceFactory;

public class MockerServiceHandler {

    private BlockchainKeypair defaultParticipant;

    private BlockchainKeypair defaultUser;

    private BlockchainKeypair defaultDataAccount;

    private String gatewayHost;

    private int gatewayPort;

    private GatewayServiceFactory gatewayServiceFactory;

    private BlockchainService blockchainService;

    private HashDigest ledgerHash;

    public MockerServiceHandler(String gatewayHost, int gatewayPort) {
        this.gatewayHost = gatewayHost;
        this.gatewayPort = gatewayPort;
        init();
    }

    public BlockchainKeypair getDefaultParticipant() {
        return defaultParticipant;
    }

    public BlockchainKeypair getDefaultUser() {
        return defaultUser;
    }

    public BlockchainKeypair getDefaultDataAccount() {
        return defaultDataAccount;
    }

    public String getDefaultDataAccountAddress() {
        return defaultDataAccount.getAddress().toBase58();
    }

    public String getGatewayHost() {
        return gatewayHost;
    }

    public int getGatewayPort() {
        return gatewayPort;
    }

    public GatewayServiceFactory getGatewayServiceFactory() {
        return gatewayServiceFactory;
    }

    public BlockchainService getBlockchainService() {
        return blockchainService;
    }

    public HashDigest getLedgerHash() {
        return ledgerHash;
    }

    private void init() {

        defaultParticipant = defaultParticipant();

        gatewayServiceFactory = GatewayServiceFactory.connect(gatewayHost, gatewayPort,
                false, defaultParticipant);

        blockchainService = gatewayServiceFactory.getBlockchainService();

        HashDigest[] ledgerHashs = blockchainService.getLedgerHashs();

        ledgerHash = ledgerHashs[0];

        // 默认注册部分内容
        // 注册一个用户和一个数据账户
        TransactionTemplate txTemplate = blockchainService.newTransaction(ledgerHash);

        defaultUser = newKeypair();

        defaultDataAccount = newKeypair();

        // 注册用户
        txTemplate.users().register(defaultUser.getIdentity());

        // 注册数据账户
        txTemplate.dataAccounts().register(defaultDataAccount.getIdentity());

        // TX 准备就绪；
        PreparedTransaction prepTx = txTemplate.prepare();

        // 使用私钥进行签名；
        prepTx.sign(defaultParticipant);

        // 提交交易；
        TransactionResponse txResponse = prepTx.commit();

        System.out.printf("Commit Transaction Result = %s \r\n", txResponse.isSuccess());
    }

    public BlockchainKeypair newKeypair() {
        return BlockchainKeyGenerator.getInstance().generate();
    }

    private BlockchainKeypair defaultParticipant() {
        PrivKey privKey = KeyGenUtils.decodePrivKeyWithRawPassword(MockerConstant.PRIVATE_KEYS[0], MockerConstant.PASSWORD);
        PubKey pubKey = KeyGenUtils.decodePubKey(MockerConstant.PUBLIC_KEYS[0]);
        return new BlockchainKeypair(pubKey, privKey);
    }

    public ResponseData<KvData> writeKv(String key, byte[] value) {
        return writeKv(key, value, -1);
    }

    public ResponseData<KvData> writeKv(String key, byte[] value, long version) {
        return writeKv(getDefaultDataAccountAddress(), key, value, version);
    }

    public ResponseData<KvData> writeKv(String dataAccount, String key, byte[] value) {
        return writeKv(dataAccount, key, value, -1);
    }

    public ResponseData<KvData> writeKv(String dataAccount, String key, byte[] value, long version) {

        TransactionTemplate txTemplate = newTxTemplate();

        txTemplate.dataAccount(dataAccount).setBytes(key, value, version);

        TransactionResponse txResponse = txPrepareAndCommit(txTemplate);

        long saveVersion = version;

        if (txResponse.isSuccess()) {
            saveVersion = version + 1;
        }

        KvData kvData = new KvData(dataAccount, key, value, saveVersion);

        return new ResponseData(txResponse, kvData);
    }

    public ResponseData<BlockchainKeypair> registerUser() {

        BlockchainKeypair newUser = BlockchainKeyGenerator.getInstance().generate();

        return registerUser(newUser);
    }

    public ResponseData<BlockchainKeypair> registerUser(BlockchainKeypair user) {

        TransactionTemplate txTemplate = newTxTemplate();

        // 注册
        txTemplate.users().register(user.getIdentity());

        TransactionResponse txResponse = txPrepareAndCommit(txTemplate);

        return new ResponseData(txResponse, user);
    }

    public ResponseData<BlockchainKeypair> registerDataAccount() {

        BlockchainKeypair newDataAccount = BlockchainKeyGenerator.getInstance().generate();

        return registerDataAccount(newDataAccount);
    }

    public ResponseData<BlockchainKeypair> registerDataAccount(BlockchainKeypair dataAccount) {

        TransactionTemplate txTemplate = newTxTemplate();

        // 注册
        txTemplate.dataAccounts().register(dataAccount.getIdentity());

        TransactionResponse txResponse = txPrepareAndCommit(txTemplate);

        return new ResponseData(txResponse, dataAccount);
    }

    public ResponseData<Object> deployContract() {
        return null;
    }

    public ResponseData<Object> executeContract() {
        return null;
    }

    private TransactionTemplate newTxTemplate() {
        return blockchainService.newTransaction(ledgerHash);
    }

    private TransactionResponse txPrepareAndCommit(TransactionTemplate txTemplate) {
        // TX 准备就绪；
        PreparedTransaction prepTx = txTemplate.prepare();

        // 使用私钥进行签名；
        prepTx.sign(defaultParticipant);

        // 提交交易；
        TransactionResponse txResponse = prepTx.commit();

        return txResponse;
    }
}
