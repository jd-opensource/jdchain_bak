package com.jd.blockchain.statetransfer.message;

import com.jd.blockchain.statetransfer.callback.DataSequenceReader;
import com.jd.blockchain.statetransfer.callback.DataSequenceWriter;
import com.jd.blockchain.statetransfer.process.DSTransferProcess;
import com.jd.blockchain.statetransfer.result.DSDiffRequestResult;
import com.jd.blockchain.stp.communication.MessageExecutor;
import com.jd.blockchain.stp.communication.RemoteSession;

/**
 *
 * @author zhangshuang
 * @create 2019/4/11
 * @since 1.0.0
 */
public class DSDefaultMessageExecutor implements MessageExecutor {

    DataSequenceReader dsReader;
    DataSequenceWriter dsWriter;

    public DSDefaultMessageExecutor(DataSequenceReader dsReader, DataSequenceWriter dsWriter) {
        this.dsReader = dsReader;
        this.dsWriter = dsWriter;
    }

    /**
     * 对状态机复制的请求进行响应
     *
     */

    @Override
    public byte[] receive(String key, byte[] data, RemoteSession session) {

        try {
            Object object = DSMsgResolverFactory.getDecoder(dsWriter, dsReader).decode(data);

            if (object instanceof String) {
                String id = (String)object;
                byte[] respLoadMsg = DSMsgResolverFactory.getEncoder(dsWriter, dsReader).encode(DSTransferProcess.DataSequenceMsgType.CMD_DSINFO_RESPONSE, id, 0, 0);
                session.reply(key, new DataSequenceLoadMessage(respLoadMsg));
            }
            else if (object instanceof DSDiffRequestResult) {

                DSDiffRequestResult requestResult = (DSDiffRequestResult)object;
                String id = requestResult.getId();
                long fromHeight = requestResult.getFromHeight();
                long toHeight = requestResult.getToHeight();
                for (long i = fromHeight; i < toHeight + 1; i++) {
                    byte[] respLoadMsg = DSMsgResolverFactory.getEncoder(dsWriter, dsReader).encode(DSTransferProcess.DataSequenceMsgType.CMD_GETDSDIFF_RESPONSE, id, i, i);
                    session.reply(key, new DataSequenceLoadMessage(respLoadMsg));
                }
            }
            else {
                throw new IllegalArgumentException("Receive data exception, unknown message type!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public REPLY replyType() {
        return REPLY.MANUAL;
    }

    /**
     *
     *
     */

}
