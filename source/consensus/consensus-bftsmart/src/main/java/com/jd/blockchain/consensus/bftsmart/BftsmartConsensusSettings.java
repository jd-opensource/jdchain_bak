package com.jd.blockchain.consensus.bftsmart;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.binaryproto.PrimitiveType;
import com.jd.blockchain.consensus.ConsensusSettings;
import com.jd.blockchain.consts.DataCodes;
import com.jd.blockchain.utils.Property;
import com.jd.blockchain.utils.serialize.binary.BinarySerializeUtils;

@DataContract(code = DataCodes.CONSENSUS_BFTSMART_SETTINGS)
public interface BftsmartConsensusSettings extends ConsensusSettings {

	@DataField(order = 1, primitiveType = PrimitiveType.BYTES, list=true)
	Property[] getSystemConfigs();

//	@DataField(order = 2, refContract = true)
//	BftsmartCommitBlockSettings getCommitBlockSettings();

}
