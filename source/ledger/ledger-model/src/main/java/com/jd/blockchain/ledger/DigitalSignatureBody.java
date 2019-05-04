package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.binaryproto.DataType;
import com.jd.blockchain.consts.DataCodes;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.crypto.SignatureDigest;

/**
 * 数字签名；
 * 
 * @author huanghaiquan
 *
 */
@DataContract(code= DataCodes.DIGITALSIGNATURE_BODY)
public interface DigitalSignatureBody {

	/**
	 * 公钥；
	 * 
	 * 注：公钥的编码方式中包含了算法标识；
	 * 
	 * @return
	 */
	@DataField(order=1, primitiveType = DataType.BYTES)
	PubKey getPubKey();

	/**
	 * 摘要；
	 * 
	 * @return
	 */
	@DataField(order=2, primitiveType = DataType.BYTES )
	SignatureDigest getDigest();

}
