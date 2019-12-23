package com.jd.blockchain.mocker.data;

import com.jd.blockchain.ledger.TransactionResponse;

public class ResponseData<T> {

    private TransactionResponse txResponse;

    private T data;

    public ResponseData() {
    }

    public ResponseData(TransactionResponse txResponse) {
        this.txResponse = txResponse;
    }

    public ResponseData(TransactionResponse txResponse, T data) {
        this.txResponse = txResponse;
        this.data = data;
    }

    public TransactionResponse getTxResponse() {
        return txResponse;
    }

    public T getData() {
        return data;
    }

    public void setTxResponse(TransactionResponse txResponse) {
        this.txResponse = txResponse;
    }

    public void setData(T data) {
        this.data = data;
    }
}
