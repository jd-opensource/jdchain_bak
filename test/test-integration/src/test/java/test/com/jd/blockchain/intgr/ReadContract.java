package test.com.jd.blockchain.intgr;

import com.jd.blockchain.contract.Contract;
import com.jd.blockchain.contract.ContractEvent;

@Contract
public interface ReadContract {

    @ContractEvent(name = "read-key")
    String read(String address, String key);

    @ContractEvent(name = "version-key")
    Long readVersion(String address, String key);

    int test();
}

