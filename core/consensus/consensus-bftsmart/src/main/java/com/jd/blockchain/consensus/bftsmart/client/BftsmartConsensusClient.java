package com.jd.blockchain.consensus.bftsmart.client;

import com.jd.blockchain.consensus.MessageService;
import com.jd.blockchain.consensus.client.ClientSettings;
import com.jd.blockchain.consensus.client.ConsensusClient;
import java.util.concurrent.atomic.AtomicInteger;

public class BftsmartConsensusClient implements ConsensusClient {


    private final AtomicInteger addId = new AtomicInteger();

    private BftsmartPeerProxyPool asyncPeerProxyPool;

    private int gatewayId;

    private ClientSettings clientSettings;

    public BftsmartConsensusClient(ClientSettings clientSettings) {

        this.clientSettings = clientSettings;
        this.gatewayId = clientSettings.getClientId();
    }


    public BftsmartPeerProxyPool getConsensusClientPool() {
        return this.asyncPeerProxyPool;
    }

    @Override
    public MessageService getMessageService() {
        return new BftsmartMessageService(asyncPeerProxyPool);
    }

    @Override
    public ClientSettings getSettings() {
        return clientSettings;
    }

    @Override
    public boolean isConnected() {
        return this.asyncPeerProxyPool != null;
    }

    @Override
    public synchronized void connect() {

        //consensus client pool
        BftsmartPeerProxyFactory peerProxyFactory = new BftsmartPeerProxyFactory((BftsmartClientSettings)clientSettings, gatewayId);
        this.asyncPeerProxyPool = new BftsmartPeerProxyPool(peerProxyFactory);

//        MemoryBasedViewStorage viewStorage = new MemoryBasedViewStorage(((BftsmartClientSettings)clientSettings).getTopology().getView());
//        TOMConfiguration tomConfiguration = ((BftsmartConsensusConfig)clientSettings.getConsensusSettings()).getBftsmartConfig();
//
//        //by serialize keep origin tom config
//        byte[] tomBytes = BinarySerializeUtils.serialize(tomConfiguration);
//        TOMConfiguration decodeTom = BinarySerializeUtils.deserialize(tomBytes);
//
//        int clientId = gatewayId *100 + addId.incrementAndGet();
//
//        //every proxy client has unique id;
//        decodeTom.setProcessId(clientId);
//        this.peerProxy = new AsynchServiceProxy(decodeTom, viewStorage);

    }

    @Override
    public void close() {
        if (asyncPeerProxyPool != null) {
            asyncPeerProxyPool.close();
        }
    }

//    public void asyncSendOrdered(byte[] message, AsyncCallback<byte[]> callback) {
//        AsyncReplier replier = new AsyncReplier(callback, peerProxy);
//        peerProxy.invokeAsynchRequest(message, replier, TOMMessageType.ORDERED_REQUEST);
//    }

//    private static class AsyncReplier implements ReplyListener {
//
//        private AsynchServiceProxy peerProxy;
//
//        private AtomicInteger replies = new AtomicInteger(0);
//
//        private AsyncCallback<byte[]> messageHandle;
//
//        public AsyncReplier(AsyncCallback<byte[]> messageHandle, AsynchServiceProxy peerProxy) {
//            this.messageHandle = messageHandle;
//            this.peerProxy = peerProxy;
//        }
//
//        @Override
//        public void reset() {
//            replies.set(0);
//        }
//
//        @Override
//        public void replyReceived(RequestContext context, TOMMessage reply) {
//            int replyCount = replies.incrementAndGet();
//
//            double q = Math.ceil((double) (peerProxy.getViewManager().getCurrentViewN()
//                    + peerProxy.getViewManager().getCurrentViewF() + 1) / 2.0);
//
//            if (replyCount >= q) {
//                peerProxy.cleanAsynchRequest(context.getOperationId());
//                messageHandle.complete(reply.getContent(), null);
//            }
//        }
//
//    }

//    private static class BftsmartAsyncFuture<T> extends CompletableAsyncFuture<T> {
//        @Override
//        public void setSuccess(T value) {
//            super.setSuccess(value);
//        }
//
//        @Override
//        public void setError(Throwable ex) {
//            super.setError(ex);
//        }
//
//        @Override
//        public void setError(String errorCode) {
//            super.setError(errorCode);
//        }
//    }

}
