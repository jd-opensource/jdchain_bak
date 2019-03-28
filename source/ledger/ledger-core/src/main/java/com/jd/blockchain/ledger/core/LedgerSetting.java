package com.jd.blockchain.ledger.core;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.consts.TypeCodes;
import com.jd.blockchain.ledger.CryptoSetting;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.ValueType;

@DataContract(code = TypeCodes.METADATA_LEDGER_SETTING)
public interface LedgerSetting {
	
	@DataField(order=0, primitiveType=ValueType.TEXT)
	String getConsensusProvider();

    @DataField(order=1, primitiveType=ValueType.BYTES)
	Bytes getConsensusSetting();

	@DataField(order=2, refContract=true)
	CryptoSetting getCryptoSetting();

//	PrivilegeModelSetting getPrivilegesModelSetting();
	
}