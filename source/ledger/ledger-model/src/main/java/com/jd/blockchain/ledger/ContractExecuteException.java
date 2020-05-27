package com.jd.blockchain.ledger;

import com.jd.blockchain.crypto.HashDigest;

/**
 * @Author: zhangshuang
 * @Date: 2020/5/27 10:34 AM
 * Version 1.0
 */
public class ContractExecuteException extends LedgerException {

    private static final long serialVersionUID = 8685914012112243776L;

    public ContractExecuteException(String message) {
        super(message);
    }

    public ContractExecuteException(String message, Throwable cause) {
        super(message, cause);
    }

}
