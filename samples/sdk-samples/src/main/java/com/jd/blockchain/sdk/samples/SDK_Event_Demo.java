package com.jd.blockchain.sdk.samples;

import com.jd.blockchain.ledger.BlockchainKeyGenerator;
import com.jd.blockchain.ledger.BlockchainKeypair;
import com.jd.blockchain.ledger.BytesValue;
import com.jd.blockchain.ledger.Event;
import com.jd.blockchain.ledger.SystemEvent;
import com.jd.blockchain.ledger.TransactionTemplate;
import com.jd.blockchain.sdk.EventContext;
import com.jd.blockchain.sdk.EventListenerHandle;
import com.jd.blockchain.sdk.SystemEventListener;
import com.jd.blockchain.sdk.SystemEventPoint;
import com.jd.blockchain.sdk.UserEventListener;
import com.jd.blockchain.sdk.UserEventPoint;
import com.jd.blockchain.utils.io.BytesUtils;

public class SDK_Event_Demo extends SDK_Base_Demo {

    // 注册事件账户
    private BlockchainKeypair createEventAccount() {
        BlockchainKeypair eventAccount = BlockchainKeyGenerator.getInstance().generate();
        TransactionTemplate txTpl = blockchainService.newTransaction(ledgerHash);
        txTpl.eventAccounts().register(eventAccount.getIdentity());
        commit(txTpl);
        return eventAccount;
    }

    /**
     * 发布事件
     *
     * @param eventAccount 事件账户
     */
    private void publishEvent(BlockchainKeypair eventAccount) {
        TransactionTemplate txTpl = blockchainService.newTransaction(ledgerHash);
        // sequence传递当前事件名链上最新序号，不存在时传-1
        Event event = blockchainService.getLatestEvent(ledgerHash, eventAccount.getAddress().toBase58(), "name");
        long sequence = null != event ? event.getSequence() : -1;
        txTpl.eventAccount(eventAccount.getAddress()).publish("name", "string", sequence + 1);
        txTpl.eventAccount(eventAccount.getAddress()).publish("name", 0, sequence + 2);
        commit(txTpl);
    }

    /**
     * 监听用户自定义事件
     *
     * @param eventAccount 事件账户
     * @param eventName    事件名
     */
    private void monitorUserEvent(BlockchainKeypair eventAccount, String eventName) {
        EventListenerHandle<UserEventPoint> handler = blockchainService.monitorUserEvent(ledgerHash, eventAccount.getAddress().toBase58(), eventName, 0, new UserEventListener<UserEventPoint>() {
            @Override
            public void onEvent(Event eventMessage, EventContext<UserEventPoint> eventContext) {
                BytesValue content = eventMessage.getContent();
                switch (content.getType()) {
                    case TEXT:
                        System.out.println(content.getBytes().toUTF8String());
                        break;
                    case INT64:
                        System.out.println(BytesUtils.toLong(content.getBytes().toBytes()));
                        break;
                    default:
                        break;
                }

                // 关闭监听的两种方式：1
                eventContext.getHandle().cancel();
            }
        });

        // 关闭监听的两种方式：2
        handler.cancel();
    }

    /**
     * 监听新区块生成事件
     */
    private void monitorNewBlockCreatedEvent() {
        EventListenerHandle<SystemEventPoint> handler = blockchainService.monitorSystemEvent(ledgerHash, SystemEvent.NEW_BLOCK_CREATED, 0, new SystemEventListener<SystemEventPoint>() {
            @Override
            public void onEvents(Event[] eventMessages, EventContext<SystemEventPoint> eventContext) {
                for (Event eventMessage : eventMessages) {
                    BytesValue content = eventMessage.getContent();
                    // content中存放的是当前链上最新高度
                    System.out.println(BytesUtils.toLong(content.getBytes().toBytes()));
                }

                // 关闭监听的两种方式：1
                eventContext.getHandle().cancel();
            }
        });

        // 关闭监听的两种方式：2
        handler.cancel();
    }

}
