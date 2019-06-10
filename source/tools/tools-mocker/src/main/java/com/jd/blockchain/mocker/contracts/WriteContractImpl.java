package com.jd.blockchain.mocker.contracts;

import com.jd.blockchain.contract.ContractEventContext;
import com.jd.blockchain.contract.ContractException;
import com.jd.blockchain.contract.EventProcessingAwire;

public class WriteContractImpl implements EventProcessingAwire, WriteContract {

    private ContractEventContext eventContext;

    @Override
    public void print(String name) {
        System.out.printf("My Name is %s \r\n", name);
        System.out.printf("My Ledger Hash is %s \r\n", eventContext.getCurrentLedgerHash().toBase58());
    }

    @Override
    public void writeKv(String address, String key, String value) {
        eventContext.getLedger().dataAccount(address).setText(key, value, -1);
    }

    @Override
    public void beforeEvent(ContractEventContext eventContext) {
        this.eventContext = eventContext;
    }

    @Override
    public void postEvent(ContractEventContext eventContext, ContractException error) {
        System.out.println("----- postEvent1 -----");
    }

    @Override
    public void postEvent(ContractException error) {
        System.out.println("----- postEvent2 -----");
    }

    @Override
    public void postEvent() {
        System.out.println("----- postEvent3 -----");
    }
}
