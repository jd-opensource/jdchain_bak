package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.consts.TypeCodes;

@DataContract(code= TypeCodes.TX_OP_LEDGER_INIT)
public interface LedgerInitOperation extends Operation{
	
	@DataField(order=1, refContract=true)
	LedgerInitSetting getInitSetting();
	
}
