package com.jd.blockchain.consensus;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.binaryproto.DataType;
import com.jd.blockchain.consts.DataCodes;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.crypto.SignatureDigest;

/**
 * 客户端的身份证明；
 * 
 * @author huanghaiquan
 *
 */
@DataContract(code = DataCodes.CLIENT_IDENTIFICATION)
public interface ClientIdentification {

	/**
	 * 身份信息；
	 * 
	 * @return
	 */
	@DataField(order = 0, primitiveType = DataType.BYTES)
	byte[] getIdentityInfo();

	/**
	 * 客户端的公钥；
	 * 
	 * @return
	 */
	@DataField(order = 1, primitiveType = DataType.BYTES)
	PubKey getPubKey();

	/**
	 * 客户端对认证信息的签名；
	 * 
	 * @return
	 */
	@DataField(order = 2, primitiveType = DataType.BYTES)
	SignatureDigest getSignature();

	/**
	 * 具体实现类
	 *
	 * @return
	 */
	@DataField(order = 3, primitiveType = DataType.TEXT)
	String getProviderName();
}
