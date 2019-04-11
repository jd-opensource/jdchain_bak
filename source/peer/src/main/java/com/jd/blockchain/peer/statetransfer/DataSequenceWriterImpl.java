package com.jd.blockchain.peer.statetransfer;

import com.jd.blockchain.statetransfer.DataSequenceElement;
import com.jd.blockchain.statetransfer.DataSequenceInfo;
import com.jd.blockchain.statetransfer.DataSequenceReader;
import com.jd.blockchain.statetransfer.DataSequenceWriter;

/**
 *数据序列差异的请求者需要使用的回调接口实现类
 * @author zhangshuang
 * @create 2019/4/11
 * @since 1.0.0
 *
 */
public class DataSequenceWriterImpl implements DataSequenceWriter {


    @Override
    public void updateDSInfo(String id, DataSequenceElement[] diffContents) {

    }
}
