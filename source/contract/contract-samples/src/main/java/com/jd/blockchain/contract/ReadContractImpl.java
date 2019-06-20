package com.jd.blockchain.contract;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.KVDataEntry;

@Contract
public class ReadContractImpl implements EventProcessingAware, ReadContract {

    private ContractEventContext eventContext;

    private HashDigest ledgerHash;

    @Override
    public void beforeEvent(ContractEventContext eventContext) {
        this.eventContext = eventContext;
        this.ledgerHash = eventContext.getCurrentLedgerHash();
    }

    @Override
    public void postEvent(ContractEventContext eventContext, Exception error) {

    }

    @Override
    @ContractEvent(name = "read-key")
    public String read(String address, String key) {
        KVDataEntry[] kvDataEntries = eventContext.getLedger().getDataEntries(ledgerHash, address, key);

        if (kvDataEntries != null && kvDataEntries.length == 1) {
            return kvDataEntries[0].getValue().toString();
        }
        return null;
    }

    @Override
    @ContractEvent(name = "version-key")
    public Long readVersion(String address, String key) {
        KVDataEntry[] kvDataEntries = eventContext.getLedger().getDataEntries(ledgerHash, address, key);

        if (kvDataEntries != null && kvDataEntries.length == 1) {
            return kvDataEntries[0].getVersion();
        }
        return -1L;
    }

    @Override
    public int test() {
        return 0;
    }
}
