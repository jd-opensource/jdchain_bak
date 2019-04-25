package com.jd.blockchain.statetransfer.message;

import com.jd.blockchain.statetransfer.callback.DataSequenceReader;
import com.jd.blockchain.statetransfer.callback.DataSequenceWriter;

/**
 * 数据序列消息解析器工厂
 * @author zhangshuang
 * @create 2019/4/18
 * @since 1.0.0
 *
 */
public class DSMsgResolverFactory {

    /**
     * 获得数据序列消息编码器实例
     * @param dsWriter 差异请求者执行数据序列更新的执行器
     * @param dsReader 差异响应者执行数据序列读取的执行器
     * @return 消息编码器实例
     */
    public static DataSequenceMsgEncoder getEncoder(DataSequenceWriter dsWriter, DataSequenceReader dsReader) {
        return new DataSequenceMsgEncoder(dsWriter, dsReader);
    }

    /**
     * 获得数据序列消息解码器实例
     * @param dsWriter 差异请求者执行数据序列更新的执行器
     * @param dsReader 差异响应者执行数据序列读取的执行器
     * @return 消息解码器实例
     */
    public static DataSequenceMsgDecoder getDecoder(DataSequenceWriter dsWriter, DataSequenceReader dsReader) {
        return new DataSequenceMsgDecoder(dsWriter, dsReader);
    }
}
