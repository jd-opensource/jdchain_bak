package com.jd.blockchain.ledger;

import com.jd.blockchain.contract.ContractSerializeUtils;

public class OperationResultData implements OperationResult {

    private int index;

    private byte[] result;

    public OperationResultData() {
    }

    public OperationResultData(OperationResult operationResult) {
        this(operationResult.getIndex(), operationResult.getResult());
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

    @Override
    public <T> T getResultData() {
        return (T) ContractSerializeUtils.resolve(result);
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setResult(byte[] result) {
        this.result = result;
    }
}
