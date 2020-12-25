package com.jdchain.samples.sdk;

import com.jd.blockchain.ledger.BlockchainKeyGenerator;
import com.jd.blockchain.ledger.BlockchainKeypair;
import com.jd.blockchain.ledger.PreparedTransaction;
import com.jd.blockchain.ledger.TransactionResponse;
import com.jd.blockchain.ledger.TransactionTemplate;
import com.jd.blockchain.utils.io.FileUtils;
import com.jdchain.samples.contract.SampleContract;
import org.junit.Assert;
import org.junit.Test;

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
        txTemp.contracts().deploy(contractAccount.getIdentity(), FileUtils.readBytes("src/main/resources/contract-samples-1.4.0.RELEASE.car"));
        // 准备交易
        PreparedTransaction ptx = txTemp.prepare();
        // 交易签名
        ptx.sign(adminKey);
        // 提交交易
        TransactionResponse response = ptx.commit();
        Assert.assertTrue(response.isSuccess());
    }

    /**
     * 合约调用
     */
    @Test
    public void testExecute() {
        // 新建交易
        TransactionTemplate txTemp = blockchainService.newTransaction(ledger);

        // 运行前，填写正确的合约地址
        // 调用合约的 registerUser 方法
        txTemp.contract("LdeNr7H1CUbqe3kWjwPwiqHcmd86zEQz2VRye", SampleContract.class).registerUser("至少32位字节数-----------------------------");
        // 准备交易
        PreparedTransaction ptx = txTemp.prepare();
        // 交易签名
        ptx.sign(adminKey);
        // 提交交易
        TransactionResponse response = ptx.commit();
        Assert.assertTrue(response.isSuccess());

        Assert.assertEquals(1, response.getOperationResults().length);
        System.out.println(response.getOperationResults()[0].getResult().getBytes().toString());
    }

}
