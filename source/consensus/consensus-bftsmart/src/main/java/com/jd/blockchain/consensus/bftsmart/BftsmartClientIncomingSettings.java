package com.jd.blockchain.consensus.bftsmart;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.binaryproto.DataType;
import com.jd.blockchain.consensus.ClientIncomingSettings;
import com.jd.blockchain.consts.DataCodes;
import com.jd.blockchain.crypto.PubKey;

@DataContract(code = DataCodes.CONSENSUS_BFTSMART_CLI_INCOMING_SETTINGS)
public interface BftsmartClientIncomingSettings extends ClientIncomingSettings {

	@DataField(order = 1, primitiveType = DataType.BYTES)
	byte[] getTopology();

	@DataField(order = 2, primitiveType = DataType.BYTES)
	byte[] getTomConfig();

	@DataField(order = 3, primitiveType=DataType.BYTES)
	PubKey getPubKey();

}
