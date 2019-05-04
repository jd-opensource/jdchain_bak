package com.jd.blockchain.ledger.core;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.binaryproto.DataType;
import com.jd.blockchain.consts.DataCodes;
import com.jd.blockchain.ledger.CryptoSetting;
import com.jd.blockchain.utils.Bytes;

@DataContract(code = DataCodes.METADATA_LEDGER_SETTING)
public interface LedgerSetting {
	
	@DataField(order=0, primitiveType=DataType.TEXT)
	String getConsensusProvider();

    @DataField(order=1, primitiveType=DataType.BYTES)
	Bytes getConsensusSetting();

	@DataField(order=2, refContract=true)
	CryptoSetting getCryptoSetting();

//	PrivilegeModelSetting getPrivilegesModelSetting();
	
}