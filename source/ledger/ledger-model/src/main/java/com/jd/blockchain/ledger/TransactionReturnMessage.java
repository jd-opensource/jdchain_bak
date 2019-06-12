package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.consts.DataCodes;

@DataContract(code= DataCodes.TX_RETURN_MESSAGE)
public interface TransactionReturnMessage {

    /**
     * 合约返回值列表
     *
     * @return
     */
    @DataField(order=1, list = true, refContract=true)
    ContractReturnMessage[] getContractReturn();
}
