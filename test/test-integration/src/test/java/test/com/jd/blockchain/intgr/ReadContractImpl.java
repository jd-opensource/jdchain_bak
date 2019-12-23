package test.com.jd.blockchain.intgr;

import com.jd.blockchain.contract.Contract;
import com.jd.blockchain.contract.ContractEvent;
import com.jd.blockchain.contract.ContractEventContext;
import com.jd.blockchain.contract.EventProcessingAware;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.TypedKVEntry;

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
        TypedKVEntry[] kvDataEntries = eventContext.getLedger().getDataEntries(ledgerHash, address, key);

        if (kvDataEntries != null && kvDataEntries.length == 1) {
            return kvDataEntries[0].getValue().toString();
        }
        return null;
    }

    @Override
    @ContractEvent(name = "version-key")
    public Long readVersion(String address, String key) {
        TypedKVEntry[] kvDataEntries = eventContext.getLedger().getDataEntries(ledgerHash, address, key);

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
