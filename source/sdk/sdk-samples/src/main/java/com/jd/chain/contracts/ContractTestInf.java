package com.jd.chain.contracts;

import com.jd.blockchain.contract.Contract;
import com.jd.blockchain.contract.ContractEvent;

@Contract
public interface ContractTestInf {

    @ContractEvent(name = "print")
    void print(String name, int age);

    @ContractEvent(name = "random")
    String randomChars(int max);
}