package com.jd.blockchain.consensus.bftsmart.client;

import bftsmart.tom.AsynchServiceProxy;
import com.jd.blockchain.consensus.MessageService;
import com.jd.blockchain.utils.concurrent.AsyncFuture;
import com.jd.blockchain.utils.concurrent.CompletableAsyncFuture;
import com.jd.blockchain.utils.io.BytesUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BftsmartMessageService implements MessageService {

    private BftsmartPeerProxyPool asyncPeerProxyPool;

    public BftsmartMessageService(BftsmartPeerProxyPool peerProxyPool) {
        this.asyncPeerProxyPool = peerProxyPool;
    }

    @Override
    public AsyncFuture<byte[]> sendOrdered(byte[] message) {
        return sendOrderedMessage(message);
    }

    private AsyncFuture<byte[]> sendOrderedMessage(byte[] message) {
        CompletableAsyncFuture<byte[]> asyncFuture = new CompletableAsyncFuture<>();
        AsynchServiceProxy asynchServiceProxy = null;
        try {
            asynchServiceProxy = asyncPeerProxyPool.borrowObject();
//            //0: Transaction msg, 1: Commitblock msg
//            byte[] msgType = BytesUtils.toBytes(0);
//            byte[] wrapMsg = new byte[message.length + 4];
//            System.arraycopy(message, 0, wrapMsg, 4, message.length);
//            System.arraycopy(msgType, 0, wrapMsg, 0, 4);
//
//            System.out.printf("BftsmartMessageService invokeOrdered time = %s, id = %s threadId = %s \r\n",
//                    System.currentTimeMillis(),  asynchServiceProxy.getProcessId(), Thread.currentThread().getId());

            byte[] result = asynchServiceProxy.invokeOrdered(message);
            asyncFuture.complete(result);

        } catch (Exception e) {
            throw new RuntimeException(e);

        } finally {
            asyncPeerProxyPool.returnObject(asynchServiceProxy);
        }

        return asyncFuture;
    }

    @Override
    public AsyncFuture<byte[]> sendUnordered(byte[] message) {
        return sendUnorderedMessage(message);
    }

    private AsyncFuture<byte[]> sendUnorderedMessage(byte[] message) {
        CompletableAsyncFuture<byte[]> asyncFuture = new CompletableAsyncFuture<>();
            AsynchServiceProxy asynchServiceProxy = null;
            try {
                asynchServiceProxy = asyncPeerProxyPool.borrowObject();
                byte[] result = asynchServiceProxy.invokeUnordered(message);
                asyncFuture.complete(result);

            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                asyncPeerProxyPool.returnObject(asynchServiceProxy);
            }
        return asyncFuture;
    }

}
