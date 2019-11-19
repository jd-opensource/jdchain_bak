package com.jd.blockchain.ledger.core;

import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.HashFunction;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.io.BytesUtils;
import com.jd.blockchain.utils.io.NumberMask;



/**
 * A copy of previous version of com.jd.blockchain.ledger.core.MerkleTree.DataNode;
 * 
 * @author huanghaiquan
 *
 */
public class PreviousDataNode  {

	private HashDigest nodeHash;

	private long sn;

	private Bytes key;

	private long version;

	private byte[] dataNodeBytes;

	private PreviousDataNode(long sn, Bytes key, long version, HashDigest dataHash, byte[] dataBytes) {
		this.sn = sn;
		this.key = key;
		this.version = version;
		this.nodeHash = dataHash;
		this.dataNodeBytes = dataBytes;
	}

	static PreviousDataNode newDataNode(CryptoAlgorithm hashAlgorithm, long sn, Bytes key, long version,
			byte[] hashedData) {
		return newDataNode(hashAlgorithm.code(), sn, key, version, hashedData);
	}

	static PreviousDataNode newDataNode(short hashAlgorithm, long sn, Bytes key, long version, byte[] hashedData) {
		// byte[] keyStrBytes = BytesUtils.toBytes(key);
		// int maskSize = NumberMask.SHORT.getMaskLength(keyStrBytes.length);
		int keySize = key.size();
		int maskSize = NumberMask.SHORT.getMaskLength(keySize);

		// int bodySize = 8 + maskSize + keyStrBytes.length + 8;// sn + key + version;
		int bodySize = 8 + maskSize + keySize + 8;// sn + key + version;
		byte[] bodyBytes = new byte[bodySize];

		int offset = 0;
		offset += BytesUtils.toBytes(sn, bodyBytes, 0);

		// NumberMask.SHORT.writeMask(keyStrBytes.length, bodyBytes, offset);
		NumberMask.SHORT.writeMask(keySize, bodyBytes, offset);
		offset += maskSize;

		// System.arraycopy(keyStrBytes, 0, bodyBytes, offset, keyStrBytes.length);
		// System.arraycopy(keyStrBytes, 0, bodyBytes, offset, keyStrBytes.length);
		// offset += keyStrBytes.length;
		offset += key.copyTo(bodyBytes, offset, keySize);

		// TODO: version;
		offset += BytesUtils.toBytes(version, bodyBytes, offset);

		byte[] dataBytes = BytesUtils.concat(bodyBytes, hashedData);

		HashFunction hashFunc = Crypto.getHashFunction(hashAlgorithm);
		HashDigest dataHash = hashFunc.hash(dataBytes);

		int hashMaskSize = NumberMask.TINY.getMaskLength(dataHash.size());
		int dataNodeSize = bodySize + hashMaskSize + dataHash.size();
		byte[] dataNodeBytes = new byte[dataNodeSize];

		offset = 0;
		System.arraycopy(bodyBytes, 0, dataNodeBytes, offset, bodySize);
		offset += bodySize;
		NumberMask.TINY.writeMask(dataHash.size(), dataNodeBytes, offset);
		offset += hashMaskSize;
		System.arraycopy(dataHash.toBytes(), 0, dataNodeBytes, offset, dataHash.size());

		return new PreviousDataNode(sn, key, version, dataHash, dataNodeBytes);
	}

	public HashDigest getNodeHash() {
		return nodeHash;
	}

	protected long getStartingSN() {
		return sn;
	}

	protected long getDataCount() {
		return 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jd.blockchain.ledger.core.MerkleDataNode#getLevel()
	 */
	public int getLevel() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jd.blockchain.ledger.core.MerkleDataNode#getSN()
	 */
	public long getSN() {
		return sn;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jd.blockchain.ledger.core.MerkleDataNode#getKey()
	 */
	public Bytes getKey() {
		return key;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jd.blockchain.ledger.core.MerkleDataNode#getVersion()
	 */
	public long getVersion() {
		return version;
	}

	public byte[] toBytes() {
		return dataNodeBytes;
	}

	static PreviousDataNode parse(byte[] bytes) {
		// InputStream in = new ByteArrayInputStream(bytes);

		int offset = 0;
		long sn = BytesUtils.toLong(bytes, offset);
		offset += 8;

		// byte[] keyBytes = BytesEncoding.read(NumberMask.SHORT, in);
		// String key = BytesUtils.toString(keyBytes);
		int keySize = NumberMask.SHORT.resolveMaskedNumber(bytes, offset);
		offset += NumberMask.SHORT.getMaskLength(keySize);
		byte[] keyBytes = new byte[keySize];
		System.arraycopy(bytes, offset, keyBytes, 0, keySize);
		offset += keySize;
		// String key = BytesUtils.toString(keyBytes);
		Bytes key = new Bytes(keyBytes);

		// long version = BytesUtils.readLong(in);
		long version = BytesUtils.toLong(bytes, offset);
		offset += 8;

		// byte[] dataHashBytes = BytesEncoding.read(NumberMask.SHORT, in);
		int hashSize = NumberMask.TINY.resolveMaskedNumber(bytes, offset);
		offset += NumberMask.TINY.getMaskLength(hashSize);
		byte[] dataHashBytes = new byte[hashSize];
		System.arraycopy(bytes, offset, dataHashBytes, 0, hashSize);
		offset += hashSize;
		HashDigest dataHash = new HashDigest(dataHashBytes);
		return new PreviousDataNode(sn, key, version, dataHash, bytes);
	}

	@Override
	public int hashCode() {
		return nodeHash.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj instanceof PreviousDataNode) {
			PreviousDataNode node1 = (PreviousDataNode) obj;
			return this.nodeHash.equals(node1.nodeHash);
		}
		return false;
	}

}