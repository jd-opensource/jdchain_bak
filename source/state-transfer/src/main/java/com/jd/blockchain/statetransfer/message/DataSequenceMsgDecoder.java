package com.jd.blockchain.statetransfer.message;

import com.jd.blockchain.statetransfer.DataSequenceElement;
import com.jd.blockchain.statetransfer.DataSequenceInfo;
import com.jd.blockchain.statetransfer.callback.DataSequenceReader;
import com.jd.blockchain.statetransfer.callback.DataSequenceWriter;
import com.jd.blockchain.statetransfer.process.DSTransferProcess;
import com.jd.blockchain.statetransfer.result.DSDiffRequestResult;
import com.jd.blockchain.utils.io.BytesUtils;
import com.jd.blockchain.utils.serialize.binary.BinarySerializeUtils;

public class DataSequenceMsgDecoder {

    private int heightSize = 8;
    private int msgTypeSize = 1;

    private long respHeight;
    private long fromHeight;
    private long toHeight;
    private int idSize;
    private byte[] idBytes;
    private String id;
    private int diffElemSize;
    private byte[] diffElem;
    DataSequenceElement dsElement;

    private DataSequenceWriter dsWriter;
    private DataSequenceReader dsReader;

    public DataSequenceMsgDecoder(DataSequenceWriter dsWriter, DataSequenceReader dsReader) {
        this.dsWriter = dsWriter;
        this.dsReader = dsReader;
    }


    /**
     *
     *
     */
    public Object decode(byte[] loadMessage) {

        try {
            if (loadMessage.length <= 5) {
                System.out.println("LoadMessage size is less than 5!");
                throw new IllegalArgumentException();
            }

            int dataLength = BytesUtils.toInt(loadMessage, 0, 4);
            byte msgCode = loadMessage[4];

            if (msgCode == DSTransferProcess.DataSequenceMsgType.CMD_DSINFO_RESPONSE.CODE) {
                respHeight = BytesUtils.toLong(loadMessage, 4 + msgTypeSize);
                idSize = BytesUtils.toInt(loadMessage, 4 + msgTypeSize + heightSize, 4);
                idBytes = new byte[idSize];
                System.arraycopy(loadMessage, 4 + msgTypeSize + heightSize + 4, idBytes, 0, idSize);
                id = new String(idBytes);
                return new DataSequenceInfo(id, respHeight);
            } else if (msgCode == DSTransferProcess.DataSequenceMsgType.CMD_GETDSDIFF_RESPONSE.CODE) {
                diffElemSize = BytesUtils.toInt(loadMessage, 4 + msgTypeSize, 4);
                diffElem = new byte[diffElemSize];
                System.arraycopy(loadMessage, 4 + msgTypeSize + 4, diffElem, 0, diffElemSize);
                dsElement = BinarySerializeUtils.deserialize(diffElem);
                return dsElement;
            } else if (msgCode == DSTransferProcess.DataSequenceMsgType.CMD_DSINFO_REQUEST.CODE) {
                idSize = BytesUtils.toInt(loadMessage, 4 + msgTypeSize, 4);
                idBytes = new byte[idSize];
                System.arraycopy(loadMessage, 4 + msgTypeSize + 4, idBytes, 0, idSize);
                id = new String(idBytes);
                return id;
            } else if (msgCode == DSTransferProcess.DataSequenceMsgType.CMD_GETDSDIFF_REQUEST.CODE) {
                fromHeight = BytesUtils.toLong(loadMessage, 4 + msgTypeSize);
                toHeight = BytesUtils.toLong(loadMessage, 4 + msgTypeSize + heightSize);
                idSize = BytesUtils.toInt(loadMessage, 4 + msgTypeSize + heightSize + heightSize, 4);
                idBytes = new byte[idSize];
                System.arraycopy(loadMessage, 4 + msgTypeSize + heightSize + heightSize + 4, idBytes, 0, idSize);
                id = new String(idBytes);
                return new DSDiffRequestResult(id, fromHeight, toHeight);
            }
            else {
                System.out.println("Unknown message type!");
                throw new IllegalArgumentException();
            }

        } catch (Exception e) {
            System.out.println("Error to decode message: " + e.getMessage() + "!");
            e.printStackTrace();

        }

        return null;
    }



}
