package com.jd.blockchain.ledger.core;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.binaryproto.PrimitiveType;
import com.jd.blockchain.consts.DataCodes;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.SignatureDigest;

/**
 * 账本初始化决定；
 * 
 * @author huanghaiquan
 *
 */
@DataContract(code = DataCodes.METADATA_INIT_DECISION)
public interface LedgerInitDecision {

	/**
	 * 做出许可的参与方 ID；
	 * 
	 * @return
	 */
	@DataField(order=1, primitiveType=PrimitiveType.INT32)
	int getParticipantId();
	
	/**
	 * 新建账本的哈希；
	 * @return
	 */
	@DataField(order=2, primitiveType = PrimitiveType.BYTES)
	HashDigest getLedgerHash();

	/**
	 * 参数方的签名；
	 * 
	 * <br>
	 * 
	 * 这是对“参与方ID({@link #getParticipantId()})”+“新账本的哈希({@link #getLedgerHash()})”做出的签名；
	 * 
	 * @return
	 */
	@DataField(order=3, primitiveType = PrimitiveType.BYTES)
	SignatureDigest getSignature();

}
