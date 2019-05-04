package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.consts.DataCodes;

@DataContract(code= DataCodes.TX_OP_USER_REG)
public interface UserRegisterOperation extends Operation {
	
//	@Override
//	@DataField(order=1, refEnum = true)
//	default OperationType getType() {
//		return OperationType.REGISTER_DATA_ACCOUNT;
//	}
	
    @DataField(order=2, refContract = true)
	BlockchainIdentity getUserID();

}
