package com.jd.blockchain.ledger.core;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.consts.TypeCodes;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.SignatureDigest;
import com.jd.blockchain.utils.ValueType;

/**
 * 账本初始化决定；
 * 
 * @author huanghaiquan
 *
 */
@DataContract(code = TypeCodes.METADATA_INIT_DECISION)
public interface LedgerInitDecision {

	/**
	 * 做出许可的参与方 ID；
	 * 
	 * @return
	 */
	@DataField(order=1, primitiveType=ValueType.INT32)
	int getParticipantId();
	
	/**
	 * 新建账本的哈希；
	 * @return
	 */
	@DataField(order=2, primitiveType = ValueType.BYTES)
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
	@DataField(order=3, primitiveType = ValueType.BYTES)
	SignatureDigest getSignature();

}
