package com.jd.blockchain.ledger;


public class OperationResultData implements OperationResult {

    private int index;

    private byte[] result;

    public OperationResultData() {
    }

    public OperationResultData(int index, byte[] result) {
        this.index = index;
        this.result = result;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public byte[] getResult() {
        return result;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setResult(byte[] result) {
        this.result = result;
    }
}
