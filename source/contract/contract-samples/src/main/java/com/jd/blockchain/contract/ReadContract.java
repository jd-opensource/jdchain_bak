package com.jd.blockchain.contract;


@Contract
public interface ReadContract {

    @ContractEvent(name = "read-key")
    String read(String address, String key);

    @ContractEvent(name = "version-key")
    Long readVersion(String address, String key);

    int test();
}

