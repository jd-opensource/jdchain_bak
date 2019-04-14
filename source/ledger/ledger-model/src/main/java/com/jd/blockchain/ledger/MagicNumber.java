package com.jd.blockchain.ledger;

/**
 * 魔数表；
 * 
 * @author huanghaiquan
 *
 */
public class MagicNumber {
	
	/**
	 * JD区块链系统标识的高位，即小写字母 j 的 ASCII；
	 * 
	 */
	public static final byte JD_HIGH = 0x6A;
	
	/**
	 * JD区块链系统标识的低位， 即小写字母 d 的 ASCII；
	 */
	public static final byte JD_LOW = 0x64;
	
	/**
	 * 创世区块标识；
	 */
	public static final byte GENESIS_BLOCK = 0x00;
	
	/**
	 * 子区块标识；
	 * 
	 * 注：“子区块”是除了“创世区块”之外其它的区块；
	 */
	public static final byte CHILD_BLOCK = 0x01;
	
	/**
	 * 交易内容标识；
	 */
	public static final byte TX_CONTENT = 0x10;
	
	/**
	 * 交易请求标识；
	 */
	public static final byte TX_REQUEST = 0x11;

	/**
	 * 交易持久标识；
	 */
	public static final byte TX_PERSISTENCE = 0x12;
	
	/**
	 * 数字签名标识；
	 */
	public static final byte SIGNATURE = 0x20;
	
//	/**
//	 * 公钥标识；
//	 */
//	public static final byte PUB_KEY = 0x21;
//	
//	/**
//	 * 私钥标识；
//	 */
//	public static final byte PRIV_KEY = 0x22;
	
	
	
	
}
