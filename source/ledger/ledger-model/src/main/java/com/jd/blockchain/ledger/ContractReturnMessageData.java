package com.jd.blockchain.ledger;

import java.util.concurrent.CompletableFuture;

public class ContractReturnMessageData implements ContractReturnMessage {

    private int operationIndex;

    private CompletableFuture<String> returnMsgFuture;

    public ContractReturnMessageData() {
    }

    public ContractReturnMessageData(int operationIndex, CompletableFuture<String> returnMsgFuture) {
        this.operationIndex = operationIndex;
        this.returnMsgFuture = returnMsgFuture;
    }

    public void setOperationIndex(int operationIndex) {
        this.operationIndex = operationIndex;
    }

    public void setReturnMsgFuture(CompletableFuture<String> returnMsgFuture) {
        this.returnMsgFuture = returnMsgFuture;
    }

    @Override
    public int getOperationIndex() {
        return operationIndex;
    }

    @Override
    public String getReturnMessage() {
        try {
            return returnMsgFuture.get();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }
}
