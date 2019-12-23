package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.binaryproto.PrimitiveType;
import com.jd.blockchain.consts.DataCodes;

@DataContract(code=DataCodes.LEDGER_ADMIN_INFO)
public interface LedgerAdminInfo {

	@DataField(order=1, refContract = true)
	LedgerMetadata_V2 getMetadata();

	@DataField(order=2, refContract = true)
	LedgerSettings getSettings();

	@DataField(order=3, refContract = true, list = true)
	ParticipantNode[] getParticipants();
	
	@DataField(order=4, primitiveType = PrimitiveType.INT64)
	long getParticipantCount();

}