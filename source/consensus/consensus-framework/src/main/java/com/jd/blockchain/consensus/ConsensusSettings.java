package com.jd.blockchain.consensus;

import com.jd.blockchain.base.data.TypeCodes;
import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;

/**
 * 共识网络的配置参数；
 * 
 * @author huanghaiquan
 *
 */
@DataContract(code = TypeCodes.CONSENSUS_SETTINGS)
public interface ConsensusSettings {

	/**
	 * 共识网络中的节点列表；
	 * 
	 * @return
	 */
	@DataField(order = 0, refContract = true, list = true, genericContract = true)
	NodeSettings[] getNodes();

}
