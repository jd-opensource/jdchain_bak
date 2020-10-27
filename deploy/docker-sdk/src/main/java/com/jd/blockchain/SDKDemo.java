package com.jd.blockchain;

import com.jd.blockchain.ledger.*;
import org.apache.commons.codec.binary.Base64;

import java.util.Random;
import java.util.UUID;

public class SDKDemo extends SDK_Base_Demo{
    public static void main(String[] args) {
        SDKDemo sdkDemo = new SDKDemo();
        //注册用户;
        sdkDemo.registerUsers();
        //构建数据账户;
        sdkDemo.genDataAccount();
        //发布和执行合约;
        sdkDemo.deployContract();
    }

    //注册用户;
    public void registerUsers(){
        this.registerUser();
    }

    //构建数据账户;
    public void genDataAccount(){
        byte[] arr = new byte[1024];
        new Random().nextBytes(arr);
        String value = Base64.encodeBase64String(arr);
        this.insertData(null,null,"key1",value,-1);
    }

    public BlockchainKeypair insertData(BlockchainKeypair dataAccount, BlockchainKeypair signAdminKey,
                                        String key, String value, long version) {
        // 在本地定义注册账号的 TX；
        TransactionTemplate txTemp = blockchainService.newTransaction(ledgerHash);
        //采用KeyGenerator来生成BlockchainKeypair;
        if(dataAccount == null){
            dataAccount = BlockchainKeyGenerator.getInstance().generate();
            txTemp.dataAccounts().register(dataAccount.getIdentity());
        }

        System.out.println("current dataAccount=" + dataAccount.getAddress());
        txTemp.dataAccount(dataAccount.getAddress()).setText(key, value, version);
        txTemp.dataAccount(dataAccount.getAddress()).setTimestamp(UUID.randomUUID().toString(),System.currentTimeMillis(),-1);

        // TX 准备就绪
        commit(txTemp,signAdminKey);

        //get the version
        TypedKVEntry[] kvData = blockchainService.getDataEntries(ledgerHash,
                dataAccount.getAddress().toBase58(), key);
        System.out.println(String.format("key1 info:key=%s,value=%s,version=%d",
                kvData[0].getKey(),kvData[0].getValue().toString(),kvData[0].getVersion()));

        return dataAccount;
    }

    public void deployContract(){
        ContractParams contractParams = new ContractParams();
        contractParams.setContractZipName("contract-compile-1.3.0.RELEASE.car").setDeploy(true).setExecute(false);
        BlockchainIdentity contractAddress =
                this.contractHandle(contractParams);
        contractParams.setContractIdentity(contractAddress);
        this.contractHandle(contractParams);
        this.contractHandle(contractParams.setExecute(true));
    }
}
