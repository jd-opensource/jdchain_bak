package com.jd.blockchain.ledger;


public class OperationResultData implements OperationResult {

    private int index;

    private BytesValue result;

    public OperationResultData() {
    }

    public OperationResultData(int index, BytesValue result) {
        this.index = index;
        this.result = result;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public BytesValue getResult() {
        return result;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setResult(BytesValue result) {
        this.result = result;
    }
}
