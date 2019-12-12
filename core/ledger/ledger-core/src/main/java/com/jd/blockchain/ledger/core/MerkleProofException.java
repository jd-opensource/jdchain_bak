package com.jd.blockchain.ledger.core;

/**
 * 默克尔证明异常；
 * <p>
 * 表示 {@link MerkleTree} 在处理数据时数据无法通过校验，潜在的原因可能是数据已被修改或篡改；
 * 
 * @author huanghaiquan
 *
 */
public class MerkleProofException extends RuntimeException {

	private static final long serialVersionUID = 4110511167046780109L;

	public MerkleProofException(String message) {
		super(message);
	}

	public MerkleProofException(String message, Throwable cause) {
		super(message, cause);
	}

}
