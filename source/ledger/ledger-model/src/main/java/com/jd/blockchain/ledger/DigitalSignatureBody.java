package com.jd.blockchain.ledger;

import com.jd.blockchain.base.data.TypeCodes;
import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.crypto.asymmetric.PubKey;
import com.jd.blockchain.crypto.asymmetric.SignatureDigest;
import com.jd.blockchain.utils.ValueType;

/**
 * 数字签名；
 * 
 * @author huanghaiquan
 *
 */
@DataContract(code= TypeCodes.DIGITALSIGNATURE_BODY)
public interface DigitalSignatureBody {

	/**
	 * 公钥；
	 * 
	 * 注：公钥的编码方式中包含了算法标识；
	 * 
	 * @return
	 */
	@DataField(order=1, primitiveType = ValueType.BYTES)
	PubKey getPubKey();

	/**
	 * 摘要；
	 * 
	 * @return
	 */
	@DataField(order=2, primitiveType = ValueType.BYTES )
	SignatureDigest getDigest();

}
