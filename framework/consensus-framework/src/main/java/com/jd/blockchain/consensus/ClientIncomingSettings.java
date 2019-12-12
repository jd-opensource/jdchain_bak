package com.jd.blockchain.consensus;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.binaryproto.PrimitiveType;
import com.jd.blockchain.consts.DataCodes;

/**
 * 共识网络的客户接入参数；
 * 
 * @author huanghaiquan
 *
 */
@DataContract(code = DataCodes.CONSENSUS_CLI_INCOMING_SETTINGS)
public interface ClientIncomingSettings {

	/**
	 * 分配的客户端ID；
	 * 
	 * @return
	 */
	@DataField(order = 0, primitiveType = PrimitiveType.INT32)
	int getClientId();

	/**
	 * ProviderName
	 *
	 * @return
	 */
	@DataField(order = 1, primitiveType = PrimitiveType.TEXT)
	String getProviderName();

	/**
	 * 共识网络的配置参数；
	 * 
	 * @return
	 */
	@DataField(order = 2, refContract = true, genericContract = true)
	ConsensusSettings getConsensusSettings();

}
