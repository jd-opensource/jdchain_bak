package com.jd.blockchain.ledger;

import com.jd.blockchain.crypto.HashDigest;

/**
 * @Author: zhangshuang
 * @Date: 2020/5/27 10:34 AM
 * Version 1.0
 */
public class ContractExecuteException extends LedgerException {

    private static final long serialVersionUID = 8685914012112243776L;

    public ContractExecuteException(String message, Throwable cause) {
        super(message, cause);
        setState(TransactionState.CONTRACT_EXECUTE_ERROR);
    }

    public ContractExecuteException() {
        this(TransactionState.CONTRACT_EXECUTE_ERROR, null);
    }

    public ContractExecuteException(String message) {
        this(TransactionState.CONTRACT_EXECUTE_ERROR, message);
    }

    private ContractExecuteException(TransactionState state, String message) {
        super(message);
        assert TransactionState.SUCCESS != state;
        setState(state);
    }
}
