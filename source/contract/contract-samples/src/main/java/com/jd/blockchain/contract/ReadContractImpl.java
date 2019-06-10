package com.jd.blockchain.contract;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.KVDataEntry;

@Contract
public class ReadContractImpl implements EventProcessingAwire, ReadContract {

    private ContractEventContext eventContext;

    @Override
    public void beforeEvent(ContractEventContext eventContext) {
        this.eventContext = eventContext;
    }

    @Override
    public void postEvent(ContractEventContext eventContext, ContractException error) {

    }

    @Override
    public void postEvent(ContractException error) {

    }

    @Override
    public void postEvent() {

    }

    @Override
    @ContractEvent(name = "read-key")
    public String read(String address, String key) {
        HashDigest ledgerHash = eventContext.getCurrentLedgerHash();

        KVDataEntry[] kvDataEntries = eventContext.getLedger().getDataEntries(ledgerHash, address, key);

        if (kvDataEntries != null && kvDataEntries.length == 1) {
            return kvDataEntries[0].getValue().toString();
        }
        return null;
    }
}
