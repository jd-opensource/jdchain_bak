package com.jd.blockchain.ledger;

import java.util.ArrayList;
import java.util.List;

public class TransactionReturnMessageData implements TransactionReturnMessage {

    private List<ContractReturnMessage> contractReturnMessages = new ArrayList<>();

    public void addContractReturnMessage(ContractReturnMessage contractReturnMessage) {
        contractReturnMessages.add(contractReturnMessage);
    }

    public boolean isContractReturnEmpty() {
        return contractReturnMessages.isEmpty();
    }

    @Override
    public ContractReturnMessage[] getContractReturn() {
        if (isContractReturnEmpty()) {
            return null;
        }
        ContractReturnMessage[] crms = new ContractReturnMessage[contractReturnMessages.size()];
        return contractReturnMessages.toArray(crms);
    }
}
