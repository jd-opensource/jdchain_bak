package test.com.jd.blockchain.statetransfer;

import com.jd.blockchain.statetransfer.DataSequenceElement;
import com.jd.blockchain.statetransfer.DataSequenceInfo;
import com.jd.blockchain.statetransfer.callback.DataSequenceWriter;

/**
 *数据序列差异的请求者需要使用的回调接口实现类
 * @author zhangshuang
 * @create 2019/4/22
 * @since 1.0.0
 *
 */
public class DataSequenceWriterImpl implements DataSequenceWriter {

    DataSequence currDataSequence;

    public DataSequenceWriterImpl(DataSequence currDataSequence) {
        this.currDataSequence = currDataSequence;
    }

    @Override
    public int updateDSInfo(DataSequenceInfo dsInfo, DataSequenceElement[] diffContents) {

        currDataSequence.addElements(diffContents);

        return DataSequenceErrorType.DATA_SEQUENCE_ELEMENT_HEIGHT_NORMAL.CODE;

    }

    @Override
    public int updateDSInfo(DataSequenceInfo id, DataSequenceElement diffContent) {
        currDataSequence.addElement(diffContent);
        return DataSequenceErrorType.DATA_SEQUENCE_ELEMENT_HEIGHT_NORMAL.CODE;
    }

    public enum DataSequenceErrorType {
        DATA_SEQUENCE_LOSS_FIRST_ELEMENT((byte) 0x1),
        DATA_SEQUENCE_LOSS_MIDDLE_ELEMENT((byte) 0x2),
        DATA_SEQUENCE_ELEMENT_HEIGHT_NORMAL((byte) 0x3),
        ;
        public final int CODE;

        private DataSequenceErrorType(byte code) {
            this.CODE = code;
        }

        public static DataSequenceErrorType valueOf(byte code) {
            for (DataSequenceErrorType errorType : DataSequenceErrorType.values()) {
                if (errorType.CODE == code) {
                    return errorType;
                }
            }
            throw new IllegalArgumentException("Unsupported code[" + code + "] of errorType!");
        }
    }

}
