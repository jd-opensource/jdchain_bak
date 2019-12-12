package com.jd.blockchain.transaction;

public interface ContractEventExecutor<T> {

    T execute();
}
