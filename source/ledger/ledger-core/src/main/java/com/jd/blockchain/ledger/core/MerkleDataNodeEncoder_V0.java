package com.jd.blockchain.ledger.core;

import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.HashFunction;
import com.jd.blockchain.ledger.core.MerkleTree.DataNode;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.io.BytesUtils;
import com.jd.blockchain.utils.io.NumberMask;

/**
 * The first version of the DataNode binary sequence encoder, which's version
 * number is 0.
 * 
 * <p>
 * This version of DataNode binary sequence is composed of sn(8 bytes),
 * key(variable size), version(8 bytes) and node hash(32 bytes for SHA256);
 * 
 * <p>
 * In this version, the node hash is computed from bytes sequence composing of
 * sn, key, version and original value of the key;
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
class MerkleDataNodeEncoder_V0 implements MerkleDataNodeEncoder {

	@Override
	public byte getFormatVersion() {
		return 0;
	}

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
		byte[] nodeHashBytes = new byte[hashSize];
		System.arraycopy(bytes, offset, nodeHashBytes, 0, hashSize);
		offset += hashSize;
		HashDigest nodeHash = new HashDigest(nodeHashBytes);
		return new DataNode(nodeHash, sn, key, version, null, bytes);
	}

	@Deprecated
	@Override
	public DataNode create(short hashAlgorithm, long sn, Bytes key, long version, byte[] value) {
		// Header is composed of sn, key and version;
		// So the size of header is: 8 + "mask of key size" + "key bytes" + 8;
		int keySize = key.size();
		int maskSize = NumberMask.SHORT.getMaskLength(keySize);

		int headerSize = 8 + maskSize + keySize + 8;
		byte[] headerBytes = new byte[headerSize];

		int offset = 0;
		// write sn;
		offset += BytesUtils.toBytes(sn, headerBytes, 0);

		// write the size of key bytes;
		NumberMask.SHORT.writeMask(keySize, headerBytes, offset);
		offset += maskSize;

		// write the key bytes;
		offset += key.copyTo(headerBytes, offset, keySize);

		// version;
		offset += BytesUtils.toBytes(version, headerBytes, offset);

		// compute node hash from the combination of header and data value;
		byte[] dataBytes = BytesUtils.concat(headerBytes, value);

		HashFunction hashFunc = Crypto.getHashFunction(hashAlgorithm);
		HashDigest dataNodeHash = hashFunc.hash(dataBytes);

		// build bytes of data node, which is composed of sn, key, version and node
		// hash;
		int hashMaskSize = NumberMask.TINY.getMaskLength(dataNodeHash.size());
		int dataNodeSize = headerSize + hashMaskSize + dataNodeHash.size();
		byte[] nodeBytes = new byte[dataNodeSize];

		offset = 0;
		System.arraycopy(headerBytes, 0, nodeBytes, offset, headerSize);
		offset += headerSize;
		NumberMask.TINY.writeMask(dataNodeHash.size(), nodeBytes, offset);
		offset += hashMaskSize;
		System.arraycopy(dataNodeHash.toBytes(), 0, nodeBytes, offset, dataNodeHash.size());

		// No data hash has been computed and record in this old version of
		// implementation;
		return new DataNode(dataNodeHash, sn, key, version, null, nodeBytes);
	}
}
