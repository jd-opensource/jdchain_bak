package com.jdchain.samples.sdk;

import com.jd.blockchain.crypto.KeyGenUtils;
import com.jd.blockchain.ledger.BlockchainIdentity;
import com.jd.blockchain.ledger.BlockchainIdentityData;
import com.jd.blockchain.ledger.BlockchainKeyGenerator;
import com.jd.blockchain.ledger.BlockchainKeypair;
import com.jd.blockchain.ledger.BytesDataList;
import com.jd.blockchain.ledger.BytesValue;
import com.jd.blockchain.ledger.PreparedTransaction;
import com.jd.blockchain.ledger.TransactionResponse;
import com.jd.blockchain.ledger.TransactionTemplate;
import com.jd.blockchain.ledger.TypedValue;
import com.jd.blockchain.transaction.ContractEventSendOperationBuilder;
import com.jd.blockchain.transaction.ContractReturnValue;
import com.jd.blockchain.transaction.GenericValueHolder;
import com.jdchain.samples.contract.SampleContract;

import utils.io.BytesUtils;
import utils.io.FileUtils;

import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;

/**
 * 合约相关操作示例：
 * 合约部署，合约调用
 */
public class ContractSample extends SampleBase {

    /**
     * 有两种方式部署合约：
     * 1. contract-samples模块下，配置好pom里面的参数，执行 mvn clean deploy 即可
     * 2. 打包contract-samples项目生成 car包，参考testDeploy测试代码部署
     */
    @Test
    public void testDeploy() {
        // 新建交易
        TransactionTemplate txTemp = blockchainService.newTransaction(ledger);
        // 生成合约账户
        BlockchainKeypair contractAccount = BlockchainKeyGenerator.getInstance().generate();
        System.out.println("合约地址：" + contractAccount.getAddress());
        // 部署合约
        txTemp.contracts().deploy(contractAccount.getIdentity(), FileUtils.readBytes("src/main/resources/contract-samples-1.5.0.RELEASE.car"));
        // 准备交易
        PreparedTransaction ptx = txTemp.prepare();
        // 提交交易
        TransactionResponse response = ptx.commit();
        Assert.assertTrue(response.isSuccess());
    }

    /**
     * 有两种方式更新合约代码：
     * 1. contract-samples模块下，配置好pom里面的参数，其中contractAddress设置为已部署上链合约公钥信息，执行 mvn clean deploy 即可
     * 2. 打包contract-samples项目生成 car包，参考testUpdate测试代码部署
     */
    @Test
    public void testUpdate() {
        // 新建交易
        TransactionTemplate txTemp = blockchainService.newTransaction(ledger);
        // 解析合约身份信息
        BlockchainIdentity contractIdentity = new BlockchainIdentityData(KeyGenUtils.decodePubKey("7VeRCfSaoBW3uRuvTqVb26PYTNwvQ1iZ5HBY92YKpEVN7Qht"));
        System.out.println("合约地址：" + contractIdentity.getAddress());
        // 部署合约
        txTemp.contracts().deploy(contractIdentity, FileUtils.readBytes("src/main/resources/contract-samples-1.5.0.RELEASE.car"));
        // 准备交易
        PreparedTransaction ptx = txTemp.prepare();
        // 提交交易
        TransactionResponse response = ptx.commit();
        Assert.assertTrue(response.isSuccess());
    }

    /**
     * 基于动态代理方式合约调用，需要依赖合约接口
     */
    @Test
    public void testExecuteByProxy() {
        // 新建交易
        TransactionTemplate txTemp = blockchainService.newTransaction(ledger);

        // 运行前，填写正确的合约地址
        // 一次交易中可调用多个（多次调用）合约方法
        // 调用合约的 registerUser 方法
        SampleContract sampleContract = txTemp.contract("LdeNr7H1CUbqe3kWjwPwiqHcmd86zEQz2VRye", SampleContract.class);
        GenericValueHolder<String> userAddress = ContractReturnValue.decode(sampleContract.registerUser(UUID.randomUUID().toString()));

        // 准备交易
        PreparedTransaction ptx = txTemp.prepare();
        // 提交交易
        TransactionResponse response = ptx.commit();
        Assert.assertTrue(response.isSuccess());

        // 获取返回值
        System.out.println(userAddress.get());
    }

    /**
     * 非动态代理方式合约调用，不需要依赖合约接口及实现
     */
    @Test
    public void testExecuteWithArgus() {
        // 新建交易
        TransactionTemplate txTemp = blockchainService.newTransaction(ledger);

        ContractEventSendOperationBuilder builder = txTemp.contract();
        // 运行前，填写正确的合约地址，数据账户地址等参数
        // 一次交易中可调用多个（多次调用）合约方法
        // 调用合约的 registerUser 方法，传入合约地址，合约方法名，合约方法参数列表
        builder.send("LdeNr7H1CUbqe3kWjwPwiqHcmd86zEQz2VRye", "registerUser",
                new BytesDataList(new TypedValue[]{
                        TypedValue.fromText(UUID.randomUUID().toString())
                })
        );
        // 准备交易
        PreparedTransaction ptx = txTemp.prepare();
        // 提交交易
        TransactionResponse response = ptx.commit();
        Assert.assertTrue(response.isSuccess());

        Assert.assertEquals(1, response.getOperationResults().length);
        // 解析合约方法调用返回值
        for (int i = 0; i < response.getOperationResults().length; i++) {
            BytesValue content = response.getOperationResults()[i].getResult();
            switch (content.getType()) {
                case TEXT:
                    System.out.println(content.getBytes().toUTF8String());
                    break;
                case INT64:
                    System.out.println(BytesUtils.toLong(content.getBytes().toBytes()));
                    break;
                case BOOLEAN:
                    System.out.println(BytesUtils.toBoolean(content.getBytes().toBytes()[0]));
                    break;
                default: // byte[], Bytes
                    System.out.println(content.getBytes().toBase58());
                    break;
            }
        }
    }

}
