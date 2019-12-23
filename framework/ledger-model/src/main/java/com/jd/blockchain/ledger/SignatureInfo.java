package com.jd.blockchain.ledger;

public interface SignatureInfo {

	/**
	 * 签署账户的地址；
	 * 
	 * @return
	 */
	String getAddress();

	/**
	 * 签名的摘要；
	 * 
	 * 注：采用Base64编码;
	 * 
	 * @return
	 */
	String getDigest();

}
