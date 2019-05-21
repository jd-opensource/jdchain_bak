package com.jd.blockchain.crypto;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.binaryproto.PrimitiveType;
import com.jd.blockchain.consts.DataCodes;

@DataContract(code = DataCodes.METADATA_CRYPTO_SETTING_PROVIDER)
public interface CryptoProvider {

	@DataField(order = 0, primitiveType = PrimitiveType.TEXT)
	String getName();

	@DataField(order = 1, list = true, refContract = true)
	CryptoAlgorithm[] getAlgorithms();

}
