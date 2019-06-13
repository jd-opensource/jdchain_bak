package com.jd.blockchain.mocker.contracts;

import com.jd.blockchain.contract.Contract;
import com.jd.blockchain.contract.ContractEvent;

@Contract
public interface WriteContract {

    @ContractEvent(name = "print")
    void print(String name);

    @ContractEvent(name = "writeKv")
    String writeKv(String address, String key, String value);
}
