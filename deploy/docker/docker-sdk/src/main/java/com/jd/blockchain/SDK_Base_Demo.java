package com.jd.blockchain;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.*;
import com.jd.blockchain.sdk.BlockchainService;
import com.jd.blockchain.sdk.client.GatewayServiceFactory;
import com.jd.blockchain.transaction.GenericValueHolder;
import com.jd.blockchain.utils.Bytes;
import com.jd.chain.contract.TransferContract;

import static com.jd.blockchain.SDKDemo_Constant.readChainCodes;
import static com.jd.blockchain.transaction.ContractReturnValue.decode;

public abstract class SDK_Base_Demo {
    protected BlockchainKeypair adminKey;

    protected HashDigest ledgerHash;

    protected BlockchainService blockchainService;

    public SDK_Base_Demo() {
        init();
    }

    public void init() {
        // 生成连接网关的账号
        adminKey = SDKDemo_Constant.adminKey;

        // 连接网关
        GatewayServiceFactory serviceFactory = GatewayServiceFactory.connect(SDKDemo_Constant.GW_IPADDR,
                SDKDemo_Constant.GW_PORT, false, adminKey);

        // 获取网关对应的Service处理类
        blockchainService = serviceFactory.getBlockchainService();

        HashDigest[] ledgerHashs = blockchainService.getLedgerHashs();
        // 获取当前账本Hash
        ledgerHash = ledgerHashs[0];
    }

    public TransactionResponse commit(TransactionTemplate txTpl){
        return this.commitA(txTpl,null);
    }

    /**
     * 默认使用A方式commit;
     * @param txTpl
     * @param signAdminKey
     * @return
     */
    public TransactionResponse commit(TransactionTemplate txTpl, BlockchainKeypair signAdminKey){
        return commitA(txTpl, signAdminKey);
    }

    /**
     * 采用A方式提交；
     * @param txTpl
     * @param signAdminKey
     * @return
     */
    public TransactionResponse commitA(TransactionTemplate txTpl, BlockchainKeypair signAdminKey) {
        PreparedTransaction ptx = txTpl.prepare();

        if(signAdminKey != null){
            System.out.println("signAdminKey's pubKey = "+signAdminKey.getIdentity().getPubKey());
            ptx.sign(signAdminKey);
        }else {
            System.out.println("adminKey's pubKey = "+adminKey.getIdentity().getPubKey());
            ptx.sign(adminKey);
        }
        TransactionResponse transactionResponse = ptx.commit();

        if (transactionResponse.isSuccess()) {
            System.out.println(String.format("height=%d, ###OK#, contentHash=%s, executionState=%s",
                    transactionResponse.getBlockHeight(),
                    transactionResponse.getContentHash(), transactionResponse.getExecutionState().toString()));
        } else {
            System.out.println(String.format("height=%d, ###exception#, contentHash=%s, executionState=%s",
                    transactionResponse.getBlockHeight(),
                    transactionResponse.getContentHash(), transactionResponse.getExecutionState().toString()));
        }
        return transactionResponse;
    }

    /**
     * 生成一个区块链用户，并注册到区块链；
     */
    public BlockchainKeypair registerUser() {
        return this.registerUser(null,null,null);
    }

    public BlockchainKeypair registerUser(String cryptoType, BlockchainKeypair signAdminKey, BlockchainKeypair userKeypair) {
        // 在本地定义注册账号的 TX；
        TransactionTemplate txTemp = blockchainService.newTransaction(ledgerHash);
        if(userKeypair == null){
            if("SM2".equals(cryptoType)){
                userKeypair = BlockchainKeyGenerator.getInstance().generate(cryptoType);
            }else {
                userKeypair = BlockchainKeyGenerator.getInstance().generate();
            }
        }
        System.out.println("user'address="+userKeypair.getAddress());
        txTemp.users().register(userKeypair.getIdentity());
        // TX 准备就绪；
        commit(txTemp,signAdminKey);
        return userKeypair;
    }

    public BlockchainKeypair registerUser(BlockchainKeypair signAdminKey, BlockchainKeypair userKeypair) {
        return registerUser(null,signAdminKey,userKeypair);
    }

    /**
     * 生成一个区块链用户，并注册到区块链；
     */
    public BlockchainKeypair registerUserByNewSigner(BlockchainKeypair signer) {
        return this.registerUser(signer,null);
    }

    public BlockchainIdentity createDataAccount() {
        // 首先注册一个数据账户
        BlockchainKeypair newDataAccount = BlockchainKeyGenerator.getInstance().generate();

        TransactionTemplate txTpl = blockchainService.newTransaction(ledgerHash);
        txTpl.dataAccounts().register(newDataAccount.getIdentity());
        commit(txTpl);
        return newDataAccount.getIdentity();
    }

    public String create1(Bytes contractAddress, String address, String account, String content) {
        System.out.println(String.format("params,String address=%s, String account=%s, String content=%s, Bytes contractAddress=%s",
                address,account,content,contractAddress.toBase58()));
        TransactionTemplate txTpl = blockchainService.newTransaction(ledgerHash);
        // 使用合约创建
        TransferContract guanghu = txTpl.contract(contractAddress, TransferContract.class);
        GenericValueHolder<String> result = decode(guanghu.putval(address, account, content, System.currentTimeMillis()));
        commit(txTpl);
        return result.get();
    }

    public BlockchainIdentity contractHandle(ContractParams contractParams) {
        if(contractParams.getContractZipName() == null){
            contractParams.setContractZipName("contract-JDChain-Contract.jar");
        }
        // 发布jar包
        // 定义交易模板
        TransactionTemplate txTpl = blockchainService.newTransaction(ledgerHash);
        Bytes contractAddress = null;
        if(contractParams.getContractIdentity() != null){
            contractAddress = contractParams.getContractIdentity().getAddress();
        }

        if(contractParams.isDeploy){
            // 将jar包转换为二进制数据
            byte[] contractCode = readChainCodes(contractParams.getContractZipName());

            // 生成一个合约账号
            if(contractParams.getContractIdentity() == null){
                contractParams.setContractIdentity(BlockchainKeyGenerator.getInstance().generate().getIdentity());
            }
            contractAddress = contractParams.getContractIdentity().getAddress();
            System.out.println("contract's address=" + contractAddress);

            // 生成发布合约操作
            txTpl.contracts().deploy(contractParams.contractIdentity, contractCode);

            // 生成预发布交易；
            commit(txTpl,contractParams.getSignAdminKey());
        }

        if(contractParams.isExecute){
            // 注册一个数据账户
            if(contractParams.dataAccount == null){
                contractParams.dataAccount = createDataAccount();
                contractParams.key = "jd_zhangsan";
                contractParams.value = "{\"dest\":\"KA006\",\"id\":\"cc-fin08-01\",\"items\":\"FIN001|3030\",\"source\":\"FIN001\"}";
            }
            // 获取数据账户地址x
            String dataAddress = contractParams.dataAccount.getAddress().toBase58();
            // 打印数据账户地址
            System.out.printf("DataAccountAddress = %s \r\n", dataAddress);
            System.out.println("return value = "+create1(contractAddress, dataAddress, contractParams.key, contractParams.value));
        }
        return contractParams.contractIdentity;
    }
}
