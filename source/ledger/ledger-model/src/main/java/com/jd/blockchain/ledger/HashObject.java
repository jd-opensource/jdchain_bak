package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.consts.DataCodes;
import com.jd.blockchain.crypto.HashDigest;

/**
 * HashObject 表示以“哈希值”作为唯一标识的对象；
 * 
 * @author huanghaiquan
 *
 */
@DataContract(code= DataCodes.HASH_OBJECT)
public interface HashObject {

	/**
	 * 哈希值；
	 * 
	 * @return
	 */
	//no need annotation
	HashDigest getHash();

	// /**
	// * 哈希算法；
	// *
	// * @return
	// */
	// HashAlgorithm getHashAlgorithm();

	// /**
	// * 进行哈希运算的数据；
	// * @return
	// */
	// ByteArray getHashData();
	
}
