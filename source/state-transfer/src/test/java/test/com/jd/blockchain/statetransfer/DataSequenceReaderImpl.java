package test.com.jd.blockchain.statetransfer;

import com.jd.blockchain.statetransfer.DataSequenceElement;
import com.jd.blockchain.statetransfer.DataSequenceInfo;
import com.jd.blockchain.statetransfer.callback.DataSequenceReader;

import java.net.InetSocketAddress;
import java.util.LinkedList;

/**
 * 数据序列差异的提供者需要使用的回调接口实现类(测试)
 * @author zhangshuang
 * @create 2019/4/22
 * @since 1.0.0
 */

public class DataSequenceReaderImpl implements DataSequenceReader {

    DataSequence currDataSequence;

    public DataSequenceReaderImpl(DataSequence currDataSequence) {
        this.currDataSequence = currDataSequence;
    }

    @Override
    public DataSequenceInfo getDSInfo(String id) {
        return currDataSequence.getDSInfo();
    }

    @Override
    public DataSequenceElement[] getDSDiffContent(String id, long from, long to) {
        DataSequenceElement[] elements = new DataSequenceElement[(int)(to - from + 1)];

        int i = 0;
        LinkedList<DataSequenceElement> dataSequenceElements = currDataSequence.getDataSequenceElements();
        for (DataSequenceElement element : dataSequenceElements) {
            if (element.getHeight() < from || element.getHeight() > to) {
                continue;
            }
            else {
                elements[i++] = element;
            }
        }

        return elements;

    }

    @Override
    public DataSequenceElement getDSDiffContent(String id, long height) {
        for(DataSequenceElement dataSequenceElement : currDataSequence.getDataSequenceElements()) {
            if (dataSequenceElement.getHeight() == height) {
                return dataSequenceElement;

            }
        }
        return null;
    }
}
