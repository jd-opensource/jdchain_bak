/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.ledger.core.impl.TransactionRespHandle
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/11/13 下午10:57
 * Description:
 */
package com.jd.blockchain.ledger;

import com.jd.blockchain.crypto.HashDigest;

/**
 *
 * @author shaozhuguang
 * @create 2018/11/13
 * @since 1.0.0
 */

public class TransactionRespHandle implements TransactionResponse {

    private TransactionRequest request;

    private TransactionState result;

    private LedgerBlock block;

    private TransactionState globalResult;

    private OperationResult[] operationResults;

    public TransactionRespHandle(TransactionRequest request, TransactionState result, TransactionState globalResult) {
        this.request = request;
        this.result = result;
        this.globalResult = globalResult;
    }

    public TransactionRequest getRequest() {
        return request;
    }

    public void setRequest(TransactionRequest request) {
        this.request = request;
    }

    public TransactionState getResult() {
        return result;
    }

    public void setResult(TransactionState result) {
        this.result = result;
    }

    public void setOperationResults(OperationResult[] operationResults) {
        this.operationResults = operationResults;
    }

    public LedgerBlock getBlock() {
        return block;
    }

    public void setBlock(LedgerBlock block) {
        this.block = block;
    }

    public TransactionState getGlobalResult() {
        return globalResult;
    }

    public void setGlobalResult(TransactionState globalResult) {
        this.globalResult = globalResult;
    }

    @Override
    public HashDigest getContentHash() {
        return this.request.getTransactionContent().getHash();
    }

    @Override
    public TransactionState getExecutionState() {
        return this.result;
    }

    @Override
    public HashDigest getBlockHash() {
        return this.block == null ? null : this.block.getHash();
    }

    @Override
    public long getBlockHeight() {
        return this.block == null ? -1 : this.block.getHeight();
    }

    @Override
    public boolean isSuccess() {
        return globalResult == null ? result == TransactionState.SUCCESS : globalResult == TransactionState.SUCCESS;
    }

    @Override
    public OperationResult[] getOperationResults() {
        return operationResults;
    }
}