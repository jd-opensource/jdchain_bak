package com.jd.blockchain.mocker.contracts;

import com.jd.blockchain.contract.Contract;
import com.jd.blockchain.contract.ContractEvent;

@Contract
public interface AccountContract {

    @ContractEvent(name = "create")
    void create(String address, String account, long money);

    @ContractEvent(name = "transfer")
    void transfer(String address, String from, String to, long money);

    @ContractEvent(name = "print")
    void print(String address, String from, String to);
}
