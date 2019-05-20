package com.jd.blockchain.statetransfer.callback;

import com.jd.blockchain.statetransfer.DataSequence;
import com.jd.blockchain.statetransfer.DataSequenceElement;
import com.jd.blockchain.statetransfer.DataSequenceInfo;

import java.util.ArrayList;

/**
 * 数据序列差异的请求者需要使用的回调接口实现类
 * @author zhangshuang
 * @create 2019/4/22
 * @since 1.0.0
 */
public class DataSequenceWriterImpl implements DataSequenceWriter {

    private long currHeight;
    private DataSequence currDataSequence;
    private ArrayList<DataSequenceElement> deceidedElements = new ArrayList<DataSequenceElement>();

    public DataSequenceWriterImpl(DataSequence currDataSequence) {
        this.currDataSequence = currDataSequence;
    }

    /**
     * 检查数据序列差异元素中的高度是否合理；
     * @param currHeight 当前结点的账本高度
     * @param dsUpdateElements 需要更新到本地结点的数据序列元素List
     * @return
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

    @Override
    public int updateDSInfo(DataSequenceInfo dsInfo, DataSequenceElement[] diffContents) {

        int result = 0;

        try {
            ArrayList<DataSequenceElement> dsUpdateElements = new ArrayList<DataSequenceElement>();

            if (diffContents == null) {
                throw new IllegalArgumentException("Update diffContents is null!");
            }

            //remove unexpected elements
            for (int i = 0 ; i < diffContents.length; i++) {
                if (diffContents[i].getId().equals(dsInfo.getId())) {
                    dsUpdateElements.add(diffContents[i]);
                }
            }

            currHeight = dsInfo.getHeight();

            // check element's height
            result = checkElementsHeight(currHeight, dsUpdateElements);

            // cann't exe update
            if (result == DataSequenceErrorType.DATA_SEQUENCE_LOSS_FIRST_ELEMENT.CODE) {
                return result;
            }
            // exe elements update
            else {
                System.out.println("Old data sequence state:  ");
                System.out.println("   Current height =  " + currDataSequence.getDataSequenceElements().getLast().getHeight());
                currDataSequence.addElements(deceidedElements.toArray(new DataSequenceElement[deceidedElements.size()]));

                System.out.println("Update diffContents is completed!");
                System.out.println("New data sequence state:  ");
                System.out.println("   Current height =  " + currDataSequence.getDataSequenceElements().getLast().getHeight());

                return result;
            }



        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        return DataSequenceErrorType.DATA_SEQUENCE_ELEMENT_HEIGHT_NORMAL.CODE;

    }

//    @Override
//    public int updateDSInfo(DataSequenceInfo dsInfo, DataSequenceElement diffContent) {
//        currDataSequence.addElement(diffContent);
//        return DataSequenceErrorType.DATA_SEQUENCE_ELEMENT_HEIGHT_NORMAL.CODE;
//    }

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

