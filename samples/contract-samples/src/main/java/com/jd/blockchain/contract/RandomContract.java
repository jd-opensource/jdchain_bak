package com.jd.blockchain.contract;

@Contract
public interface RandomContract {

    @ContractEvent(name = "random-put")
    void put(String address, String key, String value);

    @ContractEvent(name = "random-putAndGet")
    String putAndGet(String address, String key, String value);
}
