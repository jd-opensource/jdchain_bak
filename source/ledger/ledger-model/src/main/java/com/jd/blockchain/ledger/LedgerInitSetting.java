package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.consts.TypeCodes;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.ValueType;

/**
 * 账本初始化配置；
 * 
 * @author huanghaiquan
 *
 */
@DataContract(code = TypeCodes.METADATA_INIT_SETTING)
public interface LedgerInitSetting {

	/**
	 * 账本的种子；
	 * @return
	 */
	@DataField(order = 1, primitiveType = ValueType.BYTES)
	byte[] getLedgerSeed();

	/**
	 * 共识参与方的列表；
	 * 
	 * @return
	 */
	@DataField(order = 2, list = true, refContract = true)
	ParticipantNode[] getConsensusParticipants();

	/**
	 * 密码算法配置；
	 * 
	 * @return
	 */
	@DataField(order = 3, refContract = true)
	CryptoSetting getCryptoSetting();
	
	
	@DataField(order = 4, primitiveType=ValueType.TEXT)
	String getConsensusProvider();

	/**
	 * 共识算法配置；
	 * 
	 * @return
	 */
	@DataField(order = 5, primitiveType=ValueType.BYTES)
	Bytes getConsensusSettings();

}
