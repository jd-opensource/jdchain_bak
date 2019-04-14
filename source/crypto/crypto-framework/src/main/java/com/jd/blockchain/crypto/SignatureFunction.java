package com.jd.blockchain.crypto;

public interface SignatureFunction extends AsymmetricKeypairGenerator, CryptoFunction {

	/**
	 * 计算指定数据的 hash；
	 *
	 * @param data 被签名消息
	 * @return SignatureDigest形式的签名摘要
	 */
	SignatureDigest sign(PrivKey privKey, byte[] data);
	
	/**
	 * 校验签名摘要和数据是否一致；
	 * 
	 * @param digest 待验证的签名摘要
	 * @param data 被签名信息
	 * @return 是否验证通过
	 */
	boolean verify(SignatureDigest digest, PubKey pubKey, byte[] data);

	/**
	 * 使用私钥恢复公钥；
	 *
	 * @param privKey PrivKey形式的私钥信息
	 * @return PubKey形式的公钥信息
	 */
	PubKey retrievePubKey(PrivKey privKey);

	/**
	 * 校验私钥格式是否满足要求；
	 *
	 * @param privKeyBytes 包含算法标识、密钥掩码和私钥的字节数组
	 * @return 是否满足指定算法的私钥格式
	 */
	boolean supportPrivKey(byte[] privKeyBytes);

	/**
	 * 将字节数组形式的私钥转换成PrivKey格式；
	 *
	 * @param privKeyBytes 包含算法标识、密钥掩码和私钥的字节数组
	 * @return PrivKey形式的私钥
	 */
	PrivKey resolvePrivKey(byte[] privKeyBytes);

	/**
	 * 校验公钥格式是否满足要求；
	 *
	 * @param pubKeyBytes 包含算法标识、密钥掩码和公钥的字节数组
	 * @return 是否满足指定算法的公钥格式
	 */
	boolean supportPubKey(byte[] pubKeyBytes);

	/**
	 * 将字节数组形式的密钥转换成PubKey格式；
	 *
	 * @param pubKeyBytes 包含算法标识、密钥掩码和公钥的字节数组
	 * @return PubKey形式的公钥
	 */
	PubKey resolvePubKey(byte[] pubKeyBytes);

	/**
	 * 校验字节数组形式的签名摘要的格式是否满足要求；
	 *
	 * @param digestBytes 包含算法标识和签名摘要的字节数组
	 * @return 是否满足指定算法的签名摘要格式
	 */

	boolean supportDigest(byte[] digestBytes);

	/**
	 * 将字节数组形式的签名摘要转换成SignatureDigest格式；
	 *
	 * @param digestBytes 包含算法标识和签名摘要的字节数组
	 * @return SignatureDigest形式的签名摘要
	 */
	SignatureDigest resolveDigest(byte[] digestBytes);
}
