package com.jd.blockchain.contract;

@Contract
public interface ComplexContract {
    @ContractEvent(name = "read-key")
    String read(String address, String key);
}
