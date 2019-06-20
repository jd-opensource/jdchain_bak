package com.jd.blockchain.mocker.contracts;

import com.jd.blockchain.contract.ContractEventContext;
import com.jd.blockchain.contract.ContractException;
import com.jd.blockchain.contract.EventProcessingAware;

public class WriteContractImpl implements EventProcessingAware, WriteContract {

    private ContractEventContext eventContext;

    @Override
    public void print(String name) {
        System.out.printf("My Name is %s \r\n", name);
        System.out.printf("My Ledger Hash is %s \r\n", eventContext.getCurrentLedgerHash().toBase58());
    }

    @Override
    public String writeKv(String address, String key, String value) {
        eventContext.getLedger().dataAccount(address).setText(key, value, -1);
        return String.format("address = %s, key = %s, value = %s, version = %s", address, key, value, 0);
    }

    @Override
    public void beforeEvent(ContractEventContext eventContext) {
        this.eventContext = eventContext;
    }

    @Override
    public void postEvent(ContractEventContext eventContext, Exception error) {
        System.out.println("----- postEvent1 -----");
    }

}
