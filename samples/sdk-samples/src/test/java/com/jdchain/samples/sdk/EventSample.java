package com.jdchain.samples.sdk;

import com.jd.blockchain.ledger.BlockchainKeyGenerator;
import com.jd.blockchain.ledger.BlockchainKeypair;
import com.jd.blockchain.ledger.BytesValue;
import com.jd.blockchain.ledger.Event;
import com.jd.blockchain.ledger.PreparedTransaction;
import com.jd.blockchain.ledger.SystemEvent;
import com.jd.blockchain.ledger.TransactionResponse;
import com.jd.blockchain.ledger.TransactionTemplate;
import org.junit.Assert;
import org.junit.Test;
import utils.Bytes;
import utils.io.BytesUtils;

import java.util.concurrent.CountDownLatch;

/**
 * 事件账户相关操作示例：
 * 事件账户创建，事件发布，事件监听
 */
public class EventSample extends SampleBase {

    /**
     * 事件监听
     */
    @Test
    public void testEventListen() {

        // 事件监听会创建子线程，为阻止子线程被直接关闭，加入等待
        CountDownLatch cdl = new CountDownLatch(1);

        // 监听系统事件，目前仅有新区快产生事件
        blockchainService.monitorSystemEvent(ledger,
                SystemEvent.NEW_BLOCK_CREATED, 0, (eventMessages, eventContext) -> {
                    for (Event eventMessage : eventMessages) {
                        // content中存放的是当前链上最新高度
                        System.out.println("New block:" + eventMessage.getSequence() + ":" + BytesUtils.toLong(eventMessage.getContent().getBytes().toBytes()));
                    }
                });

        // 监听用户自定义事件
        blockchainService.monitorUserEvent(ledger, "LdeNr7H1CUbqe3kWjwPwiqHcmd86zEQz2VRye", "sample-event", 0, (eventMessage, eventContext) -> {

            BytesValue content = eventMessage.getContent();
            switch (content.getType()) {
                case TEXT:
                case XML:
                case JSON:
                    System.out.println(eventMessage.getName() + ":" + eventMessage.getSequence() + ":" + content.getBytes().toUTF8String());
                    break;
                case INT64:
                case TIMESTAMP:
                    System.out.println(eventMessage.getName() + ":" + eventMessage.getSequence() + ":" + BytesUtils.toLong(content.getBytes().toBytes()));
                    break;
                default: // byte[], Bytes
                    System.out.println(eventMessage.getName() + ":" + eventMessage.getSequence() + ":" + content.getBytes().toBase58());
                    break;
            }
        });

        try {
            cdl.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 注册事件账户
     */
    @Test
    public void testRegisterEventAccount() {
        // 新建交易
        TransactionTemplate txTemp = blockchainService.newTransaction(ledger);
        // 生成事件账户
        BlockchainKeypair eventAccount = BlockchainKeyGenerator.getInstance().generate();
        System.out.println("事件账户地址：" + eventAccount.getAddress());
        // 注册事件账户
        txTemp.eventAccounts().register(eventAccount.getIdentity());
        // 交易准备
        PreparedTransaction ptx = txTemp.prepare();
        // 提交交易
        TransactionResponse response = ptx.commit();
        Assert.assertTrue(response.isSuccess());
    }

    /**
     * 发布事件
     */
    @Test
    public void testPublishEvent() {
        // 新建交易
        TransactionTemplate txTemp = blockchainService.newTransaction(ledger);

        // 请正确填写数据账户地址
        // sequence是针对此消息name的插入更新操作次数严格递增，初始为-1，再次运行本测试用例请修改该值，否则服务端将报版本冲突异常。
        txTemp.eventAccount(Bytes.fromBase58("LdeNr7H1CUbqe3kWjwPwiqHcmd86zEQz2VRye"))
                .publish("topic1", "content1", -1)
                .publish("topic1", "content2", 0)
                .publish("topic1", "content3", 1)
                .publish("topic2", "content", -1)
                .publish("topic3", 1, -1)
                .publish("topic4", Bytes.fromInt(1), -1);
        // 交易准备
        PreparedTransaction ptx = txTemp.prepare();
        // 提交交易
        TransactionResponse response = ptx.commit();
        Assert.assertTrue(response.isSuccess());
    }

    /**
     * 注册事件账户的同时发布事件，一个事务内
     */
    @Test
    public void testRegisterEventAccountAndPublishEvent() {
        // 新建交易
        TransactionTemplate txTemp = blockchainService.newTransaction(ledger);
        // 生成事件账户
        BlockchainKeypair eventAccount = BlockchainKeyGenerator.getInstance().generate();
        System.out.println("事件账户地址：" + eventAccount.getAddress());
        // 注册事件账户
        txTemp.eventAccounts().register(eventAccount.getIdentity());
        // 发布事件
        txTemp.eventAccount(eventAccount.getAddress()).publish("topic", "content", -1);
        // 交易准备
        PreparedTransaction ptx = txTemp.prepare();
        // 提交交易
        TransactionResponse response = ptx.commit();
        Assert.assertTrue(response.isSuccess());
    }

    /**
     * 更新事件账户权限
     */
    @Test
    public void updateDPermission() {
        // 新建交易
        TransactionTemplate txTemp = blockchainService.newTransaction(ledger);
        // 配置事件账户权限
        // 如下配置表示仅有 ROLE 角色用户才有写入 LdeNr7H1CUbqe3kWjwPwiqHcmd86zEQz2VRye 权限
        txTemp.eventAccount("LdeNr7H1CUbqe3kWjwPwiqHcmd86zEQz2VRye").permission().mode(70).role("ROLE");
        // 交易准备
        PreparedTransaction ptx = txTemp.prepare();
        // 提交交易
        TransactionResponse response = ptx.commit();
        Assert.assertTrue(response.isSuccess());
    }
}
