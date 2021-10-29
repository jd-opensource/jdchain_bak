package com.jdchain.samples.sdk;

import com.jd.blockchain.ledger.AccountState;
import com.jd.blockchain.ledger.BlockchainKeyGenerator;
import com.jd.blockchain.ledger.BlockchainKeypair;
import com.jd.blockchain.ledger.LedgerPermission;
import com.jd.blockchain.ledger.PreparedTransaction;
import com.jd.blockchain.ledger.TransactionPermission;
import com.jd.blockchain.ledger.TransactionResponse;
import com.jd.blockchain.ledger.TransactionTemplate;
import org.junit.Assert;
import org.junit.Test;
import utils.Bytes;

/**
 * 用户账户相关操作示例：
 * 用户注册，角色创建，权限设置
 */
public class UserSample extends SampleBase {

    /**
     * 注册用户
     */
    @Test
    public void registerUser() {
        // 新建交易
        TransactionTemplate txTemp = blockchainService.newTransaction(ledger);
        // 生成用户
        BlockchainKeypair user = BlockchainKeyGenerator.getInstance().generate();
        System.out.println("用户地址：" + user.getAddress());
        // 注册用户
        txTemp.users().register(user.getIdentity());
        // 交易准备
        PreparedTransaction ptx = txTemp.prepare();
        // 提交交易
        TransactionResponse response = ptx.commit();
        Assert.assertTrue(response.isSuccess());
    }

    /**
     * 创建角色
     */
    @Test
    public void createRole() {
        // 新建交易
        TransactionTemplate txTemp = blockchainService.newTransaction(ledger);

        // 创建角色 MANAGER ，并设置可以写数据账户，能执行交易
        txTemp.security().roles().configure("MANAGER")
                .enable(LedgerPermission.WRITE_DATA_ACCOUNT)
                .enable(TransactionPermission.DIRECT_OPERATION);
        // 交易准备
        PreparedTransaction ptx = txTemp.prepare();
        // 提交交易
        TransactionResponse response = ptx.commit();
        Assert.assertTrue(response.isSuccess());
    }

    /**
     * 配置角色权限
     */
    @Test
    public void configUserRole() {
        // 新建交易
        TransactionTemplate txTemp = blockchainService.newTransaction(ledger);

        // 给用户设置 MANAGER 角色权限
        txTemp.security().authorziations().forUser(Bytes.fromBase58("LdeNr7H1CUbqe3kWjwPwiqHcmd86zEQz2VRye")).authorize("MANAGER");
        // 交易准备
        PreparedTransaction ptx = txTemp.prepare();
        // 提交交易
        TransactionResponse response = ptx.commit();
        Assert.assertTrue(response.isSuccess());
    }

    /**
     * 注册用户的同时配置角色权限，同一事务内
     */
    @Test
    public void testRegisterUserAndConfigRole() {
        // 新建交易
        TransactionTemplate txTemp = blockchainService.newTransaction(ledger);
        // 生成用户
        BlockchainKeypair user = BlockchainKeyGenerator.getInstance().generate();
        System.out.println("用户地址：" + user.getAddress());
        // 注册用户
        txTemp.users().register(user.getIdentity());

        // 创建角色 MANAGER
        txTemp.security().roles().configure("MANAGER")
                .enable(LedgerPermission.WRITE_DATA_ACCOUNT)
                .enable(TransactionPermission.DIRECT_OPERATION);

        // 设置用户角色权限
        txTemp.security().authorziations().forUser(user.getAddress()).authorize("MANAGER");

        // 交易主恩贝
        PreparedTransaction ptx = txTemp.prepare();
        // 提交交易
        TransactionResponse response = ptx.commit();
        Assert.assertTrue(response.isSuccess());
    }

    /**
     * 更新用户状态
     */
    @Test
    public void updateUserState() {
        // 新建交易
        TransactionTemplate txTemp = blockchainService.newTransaction(ledger);
        // 用户（证书）状态分为：NORMAL（正常） FREEZE（冻结） REVOKE（销毁）
        // 冻结用户（证书）
        txTemp.user("LdeNr7H1CUbqe3kWjwPwiqHcmd86zEQz2VRye").state(AccountState.FREEZE);
        // 交易准备
        PreparedTransaction ptx = txTemp.prepare();
        // 提交交易
        TransactionResponse response = ptx.commit();
        Assert.assertTrue(response.isSuccess());
    }
}
