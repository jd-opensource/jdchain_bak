package com.jd.blockchain.ledger;

import java.util.ArrayList;
import java.util.List;

public class TransactionReturnMessageData implements TransactionReturnMessage {

    private List<OperationResult> contractReturnMessages = new ArrayList<>();

    public void addContractReturnMessage(OperationResult contractReturnMessage) {
        contractReturnMessages.add(contractReturnMessage);
    }

    public boolean isContractReturnEmpty() {
        return contractReturnMessages.isEmpty();
    }

    @Override
    public OperationResult[] getContractReturn() {
        if (isContractReturnEmpty()) {
            return null;
        }
        OperationResult[] crms = new OperationResult[contractReturnMessages.size()];
        return contractReturnMessages.toArray(crms);
    }
}
