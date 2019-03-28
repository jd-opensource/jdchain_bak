package com.jd.blockchain.ledger.core;

import com.jd.blockchain.ledger.DigitalSignature;

/**
 * {@link Authorization} 抽象了对特定用户/角色的授权信息；
 * 
 * @author huanghaiquan
 *
 */
public interface Authorization {

	/**
	 * 被授权用户/角色的地址；
	 * 
	 * @return
	 */
	String getAddress();

	/**
	 * 授权码；<br>
	 * 
	 * @return
	 */
	byte[] getCode();

	/**
	 * 授权者的签名；
	 * 
	 * @return
	 */
	DigitalSignature getSignature();

	// /**
	// * 授权生成的时间戳；
	// * @return
	// */
	// long getTs();

}