package com.jd.blockchain.gateway.service;

import com.jd.blockchain.gateway.PeerService;
import com.jd.blockchain.ledger.ContractCodeDeployOperation;
import com.jd.blockchain.ledger.Operation;
import com.jd.blockchain.ledger.TransactionRequest;
import com.jd.blockchain.utils.IllegalDataException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GatewayInterceptServiceHandler implements GatewayInterceptService {

    @Autowired
    private PeerService peerService;

    @Override
    public void intercept(TransactionRequest txRequest) {
        // 当前仅处理合约发布的请求
        Operation[] operations = txRequest.getTransactionContent().getOperations();
        if (operations != null && operations.length > 0) {
            for (Operation op : operations) {
                if (ContractCodeDeployOperation.class.isAssignableFrom(op.getClass())) {
                    // 发布合约请求
                    contractCheck((ContractCodeDeployOperation)op);
                }
            }
        }
    }

    private void contractCheck(final ContractCodeDeployOperation contractOP) {
        //
        byte[] chainCode = contractOP.getChainCode();
        if (chainCode == null || chainCode.length == 0) {
            throw new IllegalDataException("Contract's content is empty !!!");
        }




    }
}
