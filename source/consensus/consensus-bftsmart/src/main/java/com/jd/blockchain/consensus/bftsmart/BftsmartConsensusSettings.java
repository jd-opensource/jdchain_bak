package com.jd.blockchain.consensus.bftsmart;

import com.jd.blockchain.base.data.TypeCodes;
import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.consensus.ConsensusSettings;
import com.jd.blockchain.utils.Property;
import com.jd.blockchain.utils.ValueType;
import com.jd.blockchain.utils.serialize.binary.BinarySerializeUtils;

@DataContract(code = TypeCodes.CONSENSUS_BFTSMART_SETTINGS)
public interface BftsmartConsensusSettings extends ConsensusSettings {

	@DataField(order = 1, primitiveType = ValueType.BYTES, list=true)
	Property[] getSystemConfigs();

	@DataField(order = 2, refContract = true)
	BftsmartCommitBlockSettings getCommitBlockSettings();

}
