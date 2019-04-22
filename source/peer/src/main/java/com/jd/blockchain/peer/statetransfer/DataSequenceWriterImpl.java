package com.jd.blockchain.peer.statetransfer;

import com.jd.blockchain.consensus.service.MessageHandle;
import com.jd.blockchain.ledger.TransactionState;
import com.jd.blockchain.statetransfer.DataSequenceElement;
import com.jd.blockchain.statetransfer.DataSequenceInfo;
import com.jd.blockchain.statetransfer.callback.DataSequenceWriter;
import com.jd.blockchain.statetransfer.comparator.DataSequenceComparator;

import java.util.ArrayList;
import java.util.Collections;

/**
 *数据序列差异的请求者需要使用的回调接口实现类
 * @author zhangshuang
 * @create 2019/4/11
 * @since 1.0.0
 *
 */
public class DataSequenceWriterImpl implements DataSequenceWriter {

    private long currHeight;
    private ArrayList<DataSequenceElement> deceidedElements = new ArrayList<DataSequenceElement>();

    private MessageHandle batchMessageHandle;


    public DataSequenceWriterImpl(MessageHandle batchMessageHandle) {
        this.batchMessageHandle = batchMessageHandle;
    }

    /**
     * check height to data sequence diff elements
     *
     */
    private int checkElementsHeight(long currHeight, ArrayList<DataSequenceElement> dsUpdateElements) {
        boolean lossMiddleElements = false;

        // lose first element
        if (currHeight + 1 < dsUpdateElements.get(0).getHeight()){
            System.out.println("Diff response loss first element error!");
            return DataSequenceErrorType.DATA_SEQUENCE_LOSS_FIRST_ELEMENT.CODE;
        }
        else {
            for (int i = 0; i < dsUpdateElements.size(); i++) {
                if (dsUpdateElements.get(i).getHeight() == currHeight + 1 + i) {
                    deceidedElements.add(dsUpdateElements.get(i));
                }
                // lose middle elements
                else {
                    lossMiddleElements = true;
                    break;
                }
            }

            if (lossMiddleElements) {
                System.out.println("Diff response loss middle elements error!");
                return DataSequenceErrorType.DATA_SEQUENCE_LOSS_MIDDLE_ELEMENT.CODE;
            }

            System.out.println("Diff response elements height normal!");
            return DataSequenceErrorType.DATA_SEQUENCE_ELEMENT_HEIGHT_NORMAL.CODE;
        }

    }

    /**
     *
     *
     */
    private void exeUpdate(String realmName) {
        for (int i = 0; i < deceidedElements.size(); i++) {
            byte[][] element = deceidedElements.get(i).getData();

            String batchId = batchMessageHandle.beginBatch(realmName);
            try {
                int msgId = 0;
                for (byte[] txContent : element) {
                    batchMessageHandle.processOrdered(msgId++, txContent, realmName, batchId);
                }
                batchMessageHandle.completeBatch(realmName, batchId);
                batchMessageHandle.commitBatch(realmName, batchId);
            } catch (Exception e) {
                // todo 需要处理应答码 404
                batchMessageHandle.rollbackBatch(realmName, batchId, TransactionState.DATA_SEQUENCE_UPDATE_ERROR.CODE);
            }
        }

    }

    /**
     *
     *
     */
    @Override
    public int updateDSInfo(DataSequenceInfo id, DataSequenceElement[] diffContents) {
        int result = 0;

        try {
            ArrayList<DataSequenceElement> dsUpdateElements = new ArrayList<DataSequenceElement>();
            //remove unexpected elements
            for (int i = 0 ; i < diffContents.length; i++) {
                if (diffContents[i].getId().equals(id.getId())) {
                    dsUpdateElements.add(diffContents[i]);
                }
            }

            // sort elements by height
            Collections.sort(dsUpdateElements, new DataSequenceComparator());

            currHeight = id.getHeight();

            // check element's height
            result = checkElementsHeight(currHeight, dsUpdateElements);

            // cann't exe update
            if (result == DataSequenceErrorType.DATA_SEQUENCE_LOSS_FIRST_ELEMENT.CODE) {
                return result;
            }
            // exe elements update
            else {
                exeUpdate(id.getId());
                return result;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public int updateDSInfo(DataSequenceInfo id, DataSequenceElement diffContents) {
        return 0;
    }


    /**
     * data sequence transfer error type
     *
     */
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
