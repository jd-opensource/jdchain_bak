package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.binaryproto.DataType;
import com.jd.blockchain.consts.DataCodes;
import com.jd.blockchain.crypto.PubKey;

/**
 * 参与方节点；
 * 
 * @author huanghaiquan
 *
 */
@DataContract(code = DataCodes.METADATA_CONSENSUS_PARTICIPANT)
public interface ParticipantNode {// extends ConsensusNode, ParticipantInfo {

	/**
	 * 节点的顺序编号；<br>
	 * 
	 * 注：此字段并非固定不变的；在序列化和反序列化时不包含此字段；
	 * 
	 * @return
	 */
	int getId();

	/**
	 * 节点的虚拟地址，根据公钥生成；
	 * 
	 * @return
	 */
	@DataField(order = 1, primitiveType = DataType.TEXT)
	String getAddress();

	/**
	 * 参与者名称；
	 * 
	 * @return
	 */
	@DataField(order = 2, primitiveType = DataType.TEXT)
	String getName();

	/**
	 * 节点消息认证的公钥；
	 * 
	 * @return
	 */
	@DataField(order = 3, primitiveType = DataType.BYTES)
	PubKey getPubKey();
}