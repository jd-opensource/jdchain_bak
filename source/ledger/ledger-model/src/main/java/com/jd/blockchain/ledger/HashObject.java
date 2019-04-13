package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.consts.TypeCodes;
import com.jd.blockchain.crypto.HashDigest;

/**
 * HashObject 由一个哈希摘要唯一地标识对象；
 * 
 * @author huanghaiquan
 *
 */
@DataContract(code= TypeCodes.HASH_OBJECT)
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
