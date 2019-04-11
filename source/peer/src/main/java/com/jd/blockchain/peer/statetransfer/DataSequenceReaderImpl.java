package com.jd.blockchain.peer.statetransfer;

import com.jd.blockchain.statetransfer.DataSequenceElement;
import com.jd.blockchain.statetransfer.DataSequenceInfo;
import com.jd.blockchain.statetransfer.DataSequenceReader;

/**
 *数据序列差异的提供者需要使用的回调接口实现类
 * @author zhangshuang
 * @create 2019/4/11
 * @since 1.0.0
 *
 */
public class DataSequenceReaderImpl implements DataSequenceReader {

    @Override
    public DataSequenceInfo getDSInfo(String id) {
        return null;
    }

    @Override
    public DataSequenceElement[] getDSContent(String id, long from, long to) {
        return  null;
    }
}
