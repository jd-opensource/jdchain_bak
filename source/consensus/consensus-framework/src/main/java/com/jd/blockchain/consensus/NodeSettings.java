package com.jd.blockchain.consensus;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.binaryproto.PrimitiveType;
import com.jd.blockchain.consts.DataCodes;
import com.jd.blockchain.crypto.PubKey;

/**
 * 节点的配置参数；
 * 
 * @author huanghaiquan
 *
 */
@DataContract(code=DataCodes.CONSENSUS_NODE_SETTINGS)
public interface NodeSettings {

	/**
	 * 用于标识一个节点的地址；<br>
	 * 
	 * 该值没有一个通用定义，可以是具体的通讯地址，也可以只是标识符，或者区块链地址，而是由特定的共识服务提供者的实现进行定义；
	 * 
	 * @return
	 */
	@DataField(order=0, primitiveType=PrimitiveType.TEXT)
	String getAddress();

	/**
	 * Base58 格式的公钥；
	 * 
	 * @return
	 */
	@DataField(order = 1, primitiveType = PrimitiveType.BYTES)
	PubKey getPubKey();
}
