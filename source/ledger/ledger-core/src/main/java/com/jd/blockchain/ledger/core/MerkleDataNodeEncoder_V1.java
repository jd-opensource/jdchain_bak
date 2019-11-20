package com.jd.blockchain.ledger.core;

import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.HashFunction;
import com.jd.blockchain.ledger.core.MerkleTree.DataNode;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.io.BytesUtils;
import com.jd.blockchain.utils.io.NumberMask;

/**
 * The second version of the DataNode binary sequence encoder, which's version
 * number is 1.
 * 
 * <p>
 * This version of DataNode binary sequence is composed of sn(8 bytes),
 * key(variable size), version(8 bytes), hash of original value the key, and
 * node hash;
 * 
 * <p>
 * In this version, the node hash is computed from bytes sequence composing of
 * sn, key, version , hash of original value of the key;
 * 
 * <p>
 * For the purpose of upgrading the version of DataNode binary format, we use
 * the first byte of the binary sequence as the tag to identify the version of
 * DataNode binary format, and reduce the maximum value of the valid range of SN
 * to 2^56. <br>
 * Other versions of the implementation also follow the above rules, the version
 * of the data node binary format is marked from 0, incremented by 1.
 * 
 * @author huanghaiquan
 *
 */
class MerkleDataNodeEncoder_V1 implements MerkleDataNodeEncoder {

	@Override
	public byte getFormatVersion() {
		return 1;
	}

	/**
	 * Parse DataNode from it's bytes sequence;
	 * <p>
	 * the bytes sequence is: sn + key + version + data_hash;
	 * 
	 * @param bytes
	 * @return
	 */
	@Override
	public DataNode resolve(byte[] bytes) {
		if (bytes[0] != getFormatVersion()) {
			throw new IllegalArgumentException("Unsupported version of data node bytes sequence[" + bytes[0] + "]! ");
		}

		// resolve SN;
		byte[] snBytes = new byte[8];
		snBytes[0] = 0x0;
		System.arraycopy(bytes, 1, snBytes, 1, 7);
		long sn = BytesUtils.toLong(snBytes);

		// skip bytes of SN;
		int offset = 8;

		// resolve key of data;
		// First, resolve the number mask of the key size;
		// Second, read the key bytes;
		int keySize = NumberMask.SHORT.resolveMaskedNumber(bytes, offset);
		offset += NumberMask.SHORT.getMaskLength(keySize);
		byte[] keyBytes = new byte[keySize];
		System.arraycopy(bytes, offset, keyBytes, 0, keySize);
		offset += keySize;
		Bytes key = new Bytes(keyBytes);

		// Resolve version of key;
		long version = BytesUtils.toLong(bytes, offset);
		offset += 8;

		// resovle data hash;
		int dataHashSize = NumberMask.TINY.resolveMaskedNumber(bytes, offset);
		offset += NumberMask.TINY.getMaskLength(dataHashSize);
		byte[] dataHashBytes = new byte[dataHashSize];
		System.arraycopy(bytes, offset, dataHashBytes, 0, dataHashSize);
		offset += dataHashSize;
		HashDigest dataHash = new HashDigest(dataHashBytes);

		// resovle node hash;
		int nodeHashSize = NumberMask.TINY.resolveMaskedNumber(bytes, offset);
		offset += NumberMask.TINY.getMaskLength(nodeHashSize);
		byte[] nodeHashBytes = new byte[nodeHashSize];
		System.arraycopy(bytes, offset, nodeHashBytes, 0, nodeHashSize);
		offset += nodeHashSize;
		HashDigest nodeHash = new HashDigest(nodeHashBytes);

		return new DataNode(nodeHash, sn, key, version, dataHash, bytes);
	}

	public DataNode newDataNode(short hashAlgorithm, long sn, Bytes key, long version, HashDigest dataHash) {
		HashFunction hashFunc = Crypto.getHashFunction(hashAlgorithm);
		return create(hashFunc, sn, key, version, dataHash);
	}

	@Override
	public DataNode create(short hashAlgorithm, long sn, Bytes key, long version, byte[] data) {
		HashFunction hashFunc = Crypto.getHashFunction(hashAlgorithm);
		HashDigest dataHash = hashFunc.hash(data);

		return create(hashFunc, sn, key, version, dataHash);
	}

	/**
	 * Data node's bytes sequence is composited by header( reference:
	 * {@link #buildKeyHeaderBytes(long, Bytes, long)} ) and data hash;
	 * 
	 * <p>
	 * In general, the bytes sequence is: sn + key + version + data_hash +
	 * node_hash;
	 * 
	 * @param hashFunc
	 * @param sn
	 * @param key
	 * @param version
	 * @param dataHash
	 * @return
	 */
	private DataNode create(HashFunction hashFunc, long sn, Bytes key, long version, HashDigest dataHash) {
		byte[] headerBytes = buildKeyHeaderBytes(sn, key, version);
		int headerSize = headerBytes.length;

		// 单独对头部和数据进行哈希，以便在提供 Merkle 证明时能够不必传递原始数据即可进行哈希验证；
		HashDigest headerHash = hashFunc.hash(headerBytes);
		byte[] dataHashBytes = BytesUtils.concat(headerHash.getRawDigest(), dataHash.getRawDigest());

		HashDigest dataNodeHash = hashFunc.hash(dataHashBytes);

		int dataHashSize = dataHash.size();
		int nodeHashSize = dataNodeHash.size();
		int dataHashMaskSize = NumberMask.TINY.getMaskLength(dataHashSize);
		int nodeHashMaskSize = NumberMask.TINY.getMaskLength(nodeHashSize);
		int nodeSize = headerSize + dataHashMaskSize + dataHashSize + nodeHashMaskSize + nodeHashSize;
		byte[] nodeBytes = new byte[nodeSize];

		// write header;
		int offset = 0;
		System.arraycopy(headerBytes, 0, nodeBytes, offset, headerSize);
		offset += headerSize;

		// write data hash;
		NumberMask.TINY.writeMask(dataHashSize, nodeBytes, offset);
		offset += dataHashMaskSize;
		System.arraycopy(dataHash.toBytes(), 0, nodeBytes, offset, dataHashSize);
		offset += dataHashSize;

		// write node hash;
		NumberMask.TINY.writeMask(nodeHashSize, nodeBytes, offset);
		offset += nodeHashMaskSize;
		System.arraycopy(dataNodeHash.toBytes(), 0, nodeBytes, offset, nodeHashSize);

		// set format version;
		nodeBytes[0] = getFormatVersion();

		return new DataNode(dataNodeHash, sn, key, version, dataHash, nodeBytes);
	}

	/**
	 * Header is composited by sn + key + version; Bytes sequence: sn_size(8) +
	 * number_mask_of_key_size + key_bytes + version_size(8);
	 * 
	 * @param sn
	 * @param key
	 * @param version
	 * @return
	 */
	private static byte[] buildKeyHeaderBytes(long sn, Bytes key, long version) {
		int keySize = key.size();
		int maskSize = NumberMask.SHORT.getMaskLength(keySize);

		// Size Of header = sn + key + version;
		// sn_size(8) + mask_size + key_size + version_size(8);
		int headerSize = 8 + maskSize + keySize + 8;
		byte[] headerBytes = new byte[headerSize];

		// write bytes of sn;
		int offset = 0;
		offset += BytesUtils.toBytes(sn, headerBytes, 0);

		// write bytes of key mask;
		NumberMask.SHORT.writeMask(keySize, headerBytes, offset);
		offset += maskSize;

		// write bytes of key;
		offset += key.copyTo(headerBytes, offset, keySize);

		// write bytes of version;
		offset += BytesUtils.toBytes(version, headerBytes, offset);

		return headerBytes;
	}

}
