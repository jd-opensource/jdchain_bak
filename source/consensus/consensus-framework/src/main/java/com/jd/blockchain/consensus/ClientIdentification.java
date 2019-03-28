package com.jd.blockchain.consensus;

import com.jd.blockchain.base.data.TypeCodes;
import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.crypto.asymmetric.PubKey;
import com.jd.blockchain.crypto.asymmetric.SignatureDigest;
import com.jd.blockchain.utils.ValueType;

/**
 * 客户端的身份证明；
 * 
 * @author huanghaiquan
 *
 */
@DataContract(code = TypeCodes.CLIENT_IDENTIFICATION)
public interface ClientIdentification {

	/**
	 * 身份信息；
	 * 
	 * @return
	 */
	@DataField(order = 0, primitiveType = ValueType.BYTES)
	byte[] getIdentityInfo();

	/**
	 * 客户端的公钥；
	 * 
	 * @return
	 */
	@DataField(order = 1, primitiveType = ValueType.BYTES)
	PubKey getPubKey();

	/**
	 * 客户端对认证信息的签名；
	 * 
	 * @return
	 */
	@DataField(order = 2, primitiveType = ValueType.BYTES)
	SignatureDigest getSignature();

	/**
	 * 具体实现类
	 *
	 * @return
	 */
	@DataField(order = 3, primitiveType = ValueType.TEXT)
	String getProviderName();
}
