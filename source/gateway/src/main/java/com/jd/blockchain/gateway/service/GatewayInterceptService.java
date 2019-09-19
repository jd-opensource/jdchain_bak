package com.jd.blockchain.gateway.service;

import com.jd.blockchain.ledger.TransactionRequest;

public interface GatewayInterceptService {

    void intercept(TransactionRequest txRequest);
}
