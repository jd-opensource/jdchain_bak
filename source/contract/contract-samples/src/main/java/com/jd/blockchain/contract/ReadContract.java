package com.jd.blockchain.contract;


@Contract
public interface ReadContract {

    @ContractEvent(name = "read-key")
    String read(String address, String key);
}

