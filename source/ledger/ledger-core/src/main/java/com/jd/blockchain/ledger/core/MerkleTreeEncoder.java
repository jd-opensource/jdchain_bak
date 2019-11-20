package com.jd.blockchain.ledger.core;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.jd.blockchain.ledger.core.MerkleTree.DataNode;

class MerkleTreeEncoder {

	static final MerkleDataNodeEncoder LATEST_DATANODE_ENCODER = new MerkleDataNodeEncoder_V1();

	static final MerkleDataNodeEncoder V0_DATANODE_ENCODER = new MerkleDataNodeEncoder_V0();

	static final List<MerkleDataNodeEncoder> DATANODE_ENCODERS = Collections
			.unmodifiableList(Arrays.asList(LATEST_DATANODE_ENCODER, V0_DATANODE_ENCODER));

	/**
	 * @param bytes
	 * @return
	 */
	static DataNode resolve(byte[] bytes) {
		for (MerkleDataNodeEncoder encoder : MerkleTreeEncoder.DATANODE_ENCODERS) {
			if (encoder.getFormatVersion() == bytes[0]) {
				return encoder.resolve(bytes);
			}
		}

		throw new IllegalStateException("Unsupported version of DataNode bytes sequence[" + bytes[0] + "]!");
	}
}
