package com.jd.blockchain.consensus.bftsmart;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.binaryproto.PrimitiveType;
import com.jd.blockchain.consensus.ClientIncomingSettings;
import com.jd.blockchain.consts.DataCodes;
import com.jd.blockchain.crypto.PubKey;

@DataContract(code = DataCodes.CONSENSUS_BFTSMART_CLI_INCOMING_SETTINGS)
public interface BftsmartClientIncomingSettings extends ClientIncomingSettings {

	@DataField(order = 1, primitiveType = PrimitiveType.BYTES)
	byte[] getTopology();

	@DataField(order = 2, primitiveType = PrimitiveType.BYTES)
	byte[] getTomConfig();

	@DataField(order = 3, primitiveType=PrimitiveType.BYTES)
	PubKey getPubKey();

}
