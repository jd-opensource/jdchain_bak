//package com.jd.blockchain.peer.statetransfer;
//
//import com.jd.blockchain.consensus.service.MessageHandle;
//import com.jd.blockchain.ledger.TransactionState;
//import com.jd.blockchain.statetransfer.DataSequenceElement;
//import com.jd.blockchain.statetransfer.DataSequenceInfo;
//import com.jd.blockchain.statetransfer.callback.DataSequenceWriter;
//import com.jd.blockchain.statetransfer.comparator.DataSequenceComparator;
//
//import java.util.ArrayList;
//import java.util.Collections;
//
///**
// *数据序列差异的请求者需要使用的回调接口实现类
// * @author zhangshuang
// * @create 2019/4/11
// * @since 1.0.0
// */
//public class DataSequenceWriterImpl implements DataSequenceWriter {
//
//    private long currHeight;
//    private ArrayList<DataSequenceElement> deceidedElements = new ArrayList<DataSequenceElement>();
//
//    private MessageHandle batchMessageHandle;
//
//
//    public DataSequenceWriterImpl(MessageHandle batchMessageHandle) {
//        this.batchMessageHandle = batchMessageHandle;
//    }
//
//    /**
//     * 检查数据序列差异元素中的高度是否合理；
//     * @param currHeight 当前结点的账本高度
//     * @param dsUpdateElements 需要更新到本地结点的数据序列元素List
//     * @return
//     */
//    private int checkElementsHeight(long currHeight, ArrayList<DataSequenceElement> dsUpdateElements) {
//        boolean lossMiddleElements = false;
//
//        // lose first element
//        if (currHeight + 1 < dsUpdateElements.get(0).getHeight()){
//            System.out.println("Diff response loss first element error!");
//            return DataSequenceErrorType.DATA_SEQUENCE_LOSS_FIRST_ELEMENT.CODE;
//        }
//        else {
//            for (int i = 0; i < dsUpdateElements.size(); i++) {
//                if (dsUpdateElements.get(i).getHeight() == currHeight + 1 + i) {
//                    deceidedElements.add(dsUpdateElements.get(i));
//                }
//                // lose middle elements
//                else {
//                    lossMiddleElements = true;
//                    break;
//                }
//            }
//
//            if (lossMiddleElements) {
//                System.out.println("Diff response loss middle elements error!");
//                return DataSequenceErrorType.DATA_SEQUENCE_LOSS_MIDDLE_ELEMENT.CODE;
//            }
//
//            System.out.println("Diff response elements height normal!");
//            return DataSequenceErrorType.DATA_SEQUENCE_ELEMENT_HEIGHT_NORMAL.CODE;
//        }
//
//    }
//
//    /**
//     * 对本地结点执行账本更新
//     * @param realmName  账本哈希的Base58编码
//     * @return void
//     */
//    private void exeUpdate(String realmName) {
//
//        for (int i = 0; i < deceidedElements.size(); i++) {
//            byte[][] element = deceidedElements.get(i).getData();
//
//            String batchId = batchMessageHandle.beginBatch(realmName);
//            try {
//                int msgId = 0;
//                for (byte[] txContent : element) {
//                    batchMessageHandle.processOrdered(msgId++, txContent, realmName, batchId);
//                }
//                // 结块
//                batchMessageHandle.completeBatch(realmName, batchId);
//                batchMessageHandle.commitBatch(realmName, batchId);
//            } catch (Exception e) {
//                // todo 需要处理应答码 404
//                batchMessageHandle.rollbackBatch(realmName, batchId, TransactionState.DATA_SEQUENCE_UPDATE_ERROR.CODE);
//            }
//        }
//
//    }
//
//    /**
//     * @param dsInfo 当前结点的数据序列信息
//     * @param diffContents 数据序列差异的数据元素数组
//     * @return int 更新结果码
//     */
//    @Override
//    public int updateDSInfo(DataSequenceInfo dsInfo, DataSequenceElement[] diffContents) {
//        int result = 0;
//
//        try {
//            ArrayList<DataSequenceElement> dsUpdateElements = new ArrayList<DataSequenceElement>();
//            //remove unexpected elements
//            for (int i = 0 ; i < diffContents.length; i++) {
//                if (diffContents[i].getId().equals(dsInfo.getId())) {
//                    dsUpdateElements.add(diffContents[i]);
//                }
//            }
//
//            // sort elements by height
//            Collections.sort(dsUpdateElements, new DataSequenceComparator());
//
//            currHeight = dsInfo.getHeight();
//
//            // check element's height
//            result = checkElementsHeight(currHeight, dsUpdateElements);
//
//            // cann't exe update
//            if (result == DataSequenceErrorType.DATA_SEQUENCE_LOSS_FIRST_ELEMENT.CODE) {
//                return result;
//            }
//            // exe elements update
//            else {
//                exeUpdate(dsInfo.getId());
//                return result;
//            }
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//            e.printStackTrace();
//        }
//
//        return result;
//    }
//
//    @Override
//    public int updateDSInfo(DataSequenceInfo dsInfo, DataSequenceElement diffContents) {
//        return 0;
//    }
//
//
//    /**
//     * 数据序列更新错误码
//     * @param
//     * @return
//     */
//    public enum DataSequenceErrorType {
//        DATA_SEQUENCE_LOSS_FIRST_ELEMENT((byte) 0x1),
//        DATA_SEQUENCE_LOSS_MIDDLE_ELEMENT((byte) 0x2),
//        DATA_SEQUENCE_ELEMENT_HEIGHT_NORMAL((byte) 0x3),
//        ;
//        public final int CODE;
//
//        private DataSequenceErrorType(byte code) {
//            this.CODE = code;
//        }
//
//        public static DataSequenceErrorType valueOf(byte code) {
//            for (DataSequenceErrorType errorType : DataSequenceErrorType.values()) {
//                if (errorType.CODE == code) {
//                    return errorType;
//                }
//            }
//            throw new IllegalArgumentException("Unsupported code[" + code + "] of errorType!");
//        }
//    }
//
//}
