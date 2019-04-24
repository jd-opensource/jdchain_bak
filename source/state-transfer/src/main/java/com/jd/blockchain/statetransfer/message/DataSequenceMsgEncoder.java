package com.jd.blockchain.statetransfer.message;

import com.jd.blockchain.statetransfer.DataSequenceElement;
import com.jd.blockchain.statetransfer.callback.DataSequenceReader;
import com.jd.blockchain.statetransfer.callback.DataSequenceWriter;
import com.jd.blockchain.statetransfer.process.DSTransferProcess;
import com.jd.blockchain.utils.io.BytesUtils;
import com.jd.blockchain.utils.serialize.binary.BinarySerializeUtils;

public class DataSequenceMsgEncoder {

    private int heightSize = 8;
    private int msgTypeSize = 1;

    private DataSequenceWriter dsWriter;
    private DataSequenceReader dsReader;

    public DataSequenceMsgEncoder(DataSequenceWriter dsWriter, DataSequenceReader dsReader) {
        this.dsWriter = dsWriter;
        this.dsReader = dsReader;
    }

    /**
     * 目前暂时考虑fromHeight与toHeight相同的情况，即每次只对一个高度的差异编码并响应
     *
     */
    public byte[] encode(DSTransferProcess.DataSequenceMsgType msgType, String id, long fromHeight, long toHeight) {

        try {

            int dataLength;
            int idSize = id.getBytes().length;
            byte[] loadMessage = null;

            // different encoding methods for different message types
            if (msgType == DSTransferProcess.DataSequenceMsgType.CMD_DSINFO_REQUEST) {

                // CMD_DSINFO_REQUEST Message parts : 4 bytes total message size, 1 byte message type coe,
                // 4 bytes id length, id content size bytes

                dataLength = 4 + msgTypeSize + 4 + idSize;

                loadMessage = new byte[dataLength];

                System.arraycopy(BytesUtils.toBytes(dataLength), 0, loadMessage, 0, 4);
                loadMessage[4] = msgType.CODE;
                System.arraycopy(BytesUtils.toBytes(idSize), 0, loadMessage, 4 + msgTypeSize, 4);
                System.arraycopy(id.getBytes(), 0, loadMessage, 4 + msgTypeSize + 4, idSize);
            } else if (msgType == DSTransferProcess.DataSequenceMsgType.CMD_GETDSDIFF_REQUEST) {

                // CMD_GETDSDIFF_REQUEST Message parts : 4 bytes total message size, 1 byte message type coe, 8 bytes from height,
                // 8 bytes to height, 4 bytes id length, id content size bytes

                dataLength = 4 + msgTypeSize + heightSize + heightSize + 4 + idSize;

                loadMessage = new byte[dataLength];

                System.arraycopy(BytesUtils.toBytes(dataLength), 0, loadMessage, 0, 4);
                loadMessage[4] = msgType.CODE;
                System.arraycopy(BytesUtils.toBytes(fromHeight), 0, loadMessage, 4 + msgTypeSize, heightSize);
                System.arraycopy(BytesUtils.toBytes(toHeight), 0, loadMessage, 4 + msgTypeSize + heightSize, heightSize);
                System.arraycopy(BytesUtils.toBytes(idSize), 0, loadMessage, 4 + msgTypeSize + heightSize + heightSize, 4);
                System.arraycopy(id.getBytes(), 0, loadMessage, 4 + msgTypeSize + heightSize + heightSize + 4, idSize);
            } else if (msgType == DSTransferProcess.DataSequenceMsgType.CMD_DSINFO_RESPONSE) {

                // CMD_DSINFO_RESPONSE Message parts : 4 bytes total message size, 1 byte message type coe, 8 bytes data sequence local height,
                // 4 bytes id length, id content size bytes

                dataLength = 4 + msgTypeSize + heightSize + 4 + idSize;

                loadMessage = new byte[dataLength];

                System.arraycopy(BytesUtils.toBytes(dataLength), 0, loadMessage, 0, 4);
                loadMessage[4] = msgType.CODE;
                System.arraycopy(BytesUtils.toBytes(dsReader.getDSInfo(id).getHeight()), 0, loadMessage, 4 + msgTypeSize, heightSize);

                System.arraycopy(BytesUtils.toBytes(idSize), 0, loadMessage, 4 + msgTypeSize + heightSize, 4);
                System.arraycopy(id.getBytes(), 0, loadMessage, 4 + msgTypeSize + heightSize + 4, idSize);

            } else if (msgType == DSTransferProcess.DataSequenceMsgType.CMD_GETDSDIFF_RESPONSE) {
                if (fromHeight != toHeight) {
                    throw new IllegalArgumentException("Height parameter error!");
                }

                // CMD_DSINFO_RESPONSE Message parts : 4 bytes total message size, 1 byte message type coe,
                // 4 bytes diffElem size, diff content size;

                // 回调reader,获得这个高度上的所有差异的数据序列内容，并组织成DataSequenceElement结构
                DataSequenceElement element = dsReader.getDSDiffContent(id, fromHeight);

                byte[] diffElem = BinarySerializeUtils.serialize(element);

                dataLength = 4 + msgTypeSize + 4 + diffElem.length;
                loadMessage = new byte[dataLength];

                System.arraycopy(BytesUtils.toBytes(dataLength), 0, loadMessage, 0, 4); //total size
                loadMessage[4] = msgType.CODE; //msgType size
                System.arraycopy(BytesUtils.toBytes(diffElem.length), 0, loadMessage, 4 + msgTypeSize, 4); // diffElem size
                System.arraycopy(diffElem, 0, loadMessage, 4 + msgTypeSize + 4, diffElem.length); // diffElem bytes
            }
            else {
                System.out.println("Unknown message type!");
                throw new IllegalArgumentException();
            }

            return loadMessage;

        } catch (Exception e) {
            System.out.println("Error to encode message type : " + msgType + "!");
            e.printStackTrace();
        }

        return null;
    }

}
