package com.jd.chain.contract;

import com.jd.blockchain.contract.Contract;
import com.jd.blockchain.contract.ContractEvent;

@Contract
public interface TransferContract {

    @ContractEvent(name = "create")
    String create(String address, String account, long money);

    @ContractEvent(name = "transfer")
    String transfer(String address, String from, String to, long money);

    @ContractEvent(name = "read")
    long read(String address, String account);

    @ContractEvent(name = "readAll")
    String readAll(String address, String account);

    @ContractEvent(name = "putval1")
    String putval(String address, String account, String content, Long time);

    @ContractEvent(name = "putvalBif")
    String putvalBifurcation(String address, String account, String content, String isHalf);

    @ContractEvent(name = "getTxSigners")
    String getTxSigners(String input);

    @ContractEvent(name = "test")
    String test(String input);
}
