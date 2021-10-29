package com.jdchain.samples.sdk;

import com.jd.blockchain.ledger.BlockchainKeyGenerator;
import com.jd.blockchain.ledger.BlockchainKeypair;
import com.jd.blockchain.ledger.PreparedTransaction;
import com.jd.blockchain.ledger.TransactionResponse;
import com.jd.blockchain.ledger.TransactionTemplate;
import org.junit.Assert;
import org.junit.Test;
import utils.Bytes;

/**
 * 数据账户相关操作示例：
 * 创建数据账户，写入KV数据
 */
public class DataAccountSample extends SampleBase {

    /**
     * 注册数据账户
     */
    @Test
    public void testRegisterDataAccount() {
        // 新建交易
        TransactionTemplate txTemp = blockchainService.newTransaction(ledger);
        // 生成数据账户
        BlockchainKeypair dataAccount = BlockchainKeyGenerator.getInstance().generate();
        System.out.println("数据账户地址：" + dataAccount.getAddress());
        // 注册数据账户
        txTemp.dataAccounts().register(dataAccount.getIdentity());
        // 交易准备
        PreparedTransaction ptx = txTemp.prepare();
        // 提交交易
        TransactionResponse response = ptx.commit();
        Assert.assertTrue(response.isSuccess());
    }

    /**
     * 设置KV
     */
    @Test
    public void testSetKV() {
        // 新建交易
        TransactionTemplate txTemp = blockchainService.newTransaction(ledger);

        // 请正确填写数据账户地址
        // expVersion是针对此key的插入更新操作次数严格递增，初始为-1，再次运行本测试用例请修改该值，否则服务端将报版本冲突异常。
        txTemp.dataAccount(Bytes.fromBase58("LdeNr7H1CUbqe3kWjwPwiqHcmd86zEQz2VRye"))
                .setText("key1", "value1", -1)
                .setInt64("key2", 1, -1)
                .setJSON("key3", "{}", -1)
                .setBytes("key4", Bytes.fromInt(2), -1);
        // 交易准备
        PreparedTransaction ptx = txTemp.prepare();
        // 提交交易
        TransactionResponse response = ptx.commit();
        Assert.assertTrue(response.isSuccess());

    }

    /**
     * 注册数据账户的同时设置KV，一个事务内
     */
    @Test
    public void testRegisterDataAccountAndSetKV() {
        // 新建交易
        TransactionTemplate txTemp = blockchainService.newTransaction(ledger);
        // 生成数据账户
        BlockchainKeypair dataAccount = BlockchainKeyGenerator.getInstance().generate();
        System.out.println("数据账户地址：" + dataAccount.getAddress());
        // 注册数据账户
        txTemp.dataAccounts().register(dataAccount.getIdentity());
        // 设置KV
        txTemp.dataAccount(dataAccount.getAddress())
                .setText("key", "value", -1);
        // 交易准备
        PreparedTransaction ptx = txTemp.prepare();
        // 提交交易
        TransactionResponse response = ptx.commit();
        Assert.assertTrue(response.isSuccess());
    }

    /**
     * 更新数据账户权限
     */
    @Test
    public void updateDPermission() {
        // 新建交易
        TransactionTemplate txTemp = blockchainService.newTransaction(ledger);
        // 配置数据账户权限
        // 如下配置表示仅有 ROLE 角色用户才有写入 LdeNr7H1CUbqe3kWjwPwiqHcmd86zEQz2VRye 权限
        txTemp.dataAccount("LdeNr7H1CUbqe3kWjwPwiqHcmd86zEQz2VRye").permission().mode(70).role("ROLE");
        // 交易准备
        PreparedTransaction ptx = txTemp.prepare();
        // 提交交易
        TransactionResponse response = ptx.commit();
        Assert.assertTrue(response.isSuccess());
    }
}
