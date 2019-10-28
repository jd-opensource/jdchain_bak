package com.jd.blockchain.ledger;

public interface MerkleProof extends HashProof {

	/**
	 * 所证明的数据节点的序列号；
	 * 
	 * @return
	 */
	long getSN();

//	/**
//	 * 最大层级数； <br>
//	 * 叶子节点(即数据节点)的层级为 0，数据节点之上的每一级父节点的层级加 1, 最大层级便是根节点的层级；
//	 * 
//	 * @return
//	 * 
//	 * @see MerkleTree#getLevel()
//	 */
//	int getLevels();
//
//	/**
//	 * 返回证明中指定层级的节点的哈希；
//	 * <p>
//	 * 
//	 * @param level
//	 *            参数值为 0 返回的是数据节点的哈希; <br>
//	 *            参数值为 {@link #getLevels()} 返回的是根节点的哈希；
//	 * @return
//	 */
//	HashDigest getHash(int level);

	MerkleNode getNode(int level);

	/**
//	 * 返回根节点的哈希；
//	 * 
//	 * @return
//	 */
//	default HashDigest getRootHash() {
//		return getHash(getLevels());
//	}
//
//	/**
//	 * 返回数据节点的哈希；
//	 * 
//	 * @return
//	 */
//	default HashDigest getDataHash() {
//		return getHash(0);
//	}
	
	default MerkleDataNode getDataNode() {
		return (MerkleDataNode)getNode(0);
	}

	/**
	 * 返回字符串形式的哈希路径；
	 * 
	 * @return
	 */
	@Override
	String toString();
}