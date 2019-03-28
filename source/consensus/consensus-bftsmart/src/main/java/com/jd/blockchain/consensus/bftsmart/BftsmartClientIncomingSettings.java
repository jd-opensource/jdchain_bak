package com.jd.blockchain.consensus.bftsmart;

import com.jd.blockchain.base.data.TypeCodes;
import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.consensus.ClientIncomingSettings;
import com.jd.blockchain.crypto.asymmetric.PubKey;
import com.jd.blockchain.utils.ValueType;

@DataContract(code = TypeCodes.CONSENSUS_BFTSMART_CLI_INCOMING_SETTINGS)
public interface BftsmartClientIncomingSettings extends ClientIncomingSettings {

	@DataField(order = 1, primitiveType = ValueType.BYTES)
	byte[] getTopology();

	@DataField(order = 2, primitiveType = ValueType.BYTES)
	byte[] getTomConfig();

	@DataField(order = 3, primitiveType=ValueType.BYTES)
	PubKey getPubKey();

}
