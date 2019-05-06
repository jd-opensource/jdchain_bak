package com.jd.blockchain.statetransfer.message;

import com.jd.blockchain.statetransfer.callback.DataSequenceReader;
import com.jd.blockchain.statetransfer.callback.DataSequenceWriter;
import com.jd.blockchain.statetransfer.process.DSTransferProcess;
import com.jd.blockchain.statetransfer.result.DSDiffRequestResult;
import com.jd.blockchain.stp.communication.MessageExecutor;
import com.jd.blockchain.stp.communication.RemoteSession;

/**
 * 数据序列差异提供者使用，解析收到的差异请求消息并产生响应
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
     * 对状态机复制的差异请求进行响应
     * @param key 请求消息的Key
     * @param data 需要解码的字节数组
     * @param session 指定响应需要使用的目标结点会话
     * @return 配置为自动响应时，返回值为响应的字节数组，配置为手动响应时，不需要关注返回值
     */

    @Override
    public byte[] receive(String key, byte[] data, RemoteSession session) {

        try {
            Object object = DSMsgResolverFactory.getDecoder(dsWriter, dsReader).decode(data);

            // 解析CMD_DSINFO_REQUEST 请求的情况
            if (object instanceof String) {
                String id = (String)object;
                byte[] respLoadMsg = DSMsgResolverFactory.getEncoder(dsWriter, dsReader).encode(DSTransferProcess.DataSequenceMsgType.CMD_DSINFO_RESPONSE, id, 0, 0);
                session.reply(key, new DataSequenceLoadMessage(respLoadMsg));
            }
            // 解析CMD_GETDSDIFF_REQUEST 请求的情况
            else if (object instanceof DSDiffRequestResult) {

                DSDiffRequestResult requestResult = (DSDiffRequestResult)object;
                String id = requestResult.getId();
                long fromHeight = requestResult.getFromHeight();
                long toHeight = requestResult.getToHeight();
                //每个高度的数据序列差异元素进行一次响应的情况
                for (long i = fromHeight; i < toHeight + 1; i++) {
                    byte[] respLoadMsg = DSMsgResolverFactory.getEncoder(dsWriter, dsReader).encode(DSTransferProcess.DataSequenceMsgType.CMD_GETDSDIFF_RESPONSE, id, i, i);
                    session.reply(key, new DataSequenceLoadMessage(respLoadMsg));
                }
                //所有差异进行一次响应的情况
            }
            else {
                throw new IllegalArgumentException("Receive data exception, unknown message type!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 响应类型设置
     * 分手动响应，自动响应两种类型
     */
    @Override
    public REPLY replyType() {
        return REPLY.MANUAL;
    }

}
