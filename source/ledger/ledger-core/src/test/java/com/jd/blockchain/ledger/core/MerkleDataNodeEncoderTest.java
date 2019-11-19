package com.jd.blockchain.ledger.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Random;

import org.junit.Test;

import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.HashFunction;
import com.jd.blockchain.crypto.service.classic.ClassicAlgorithm;
import com.jd.blockchain.ledger.core.MerkleTree.DataNode;
import com.jd.blockchain.utils.Bytes;

public class MerkleDataNodeEncoderTest {

	@Test
	public void testEnocoderV0() {
		MerkleDataNodeEncoder encoderV0 = new MerkleDataNodeEncoder_V0();

		Random rand = new Random();

		byte[] data = new byte[512];
		byte[] key = new byte[256];

		rand.nextBytes(data);
		rand.nextBytes(key);

		long sn = 1024;
		long version = 1;

		DataNode nodeV0 = encoderV0.create(ClassicAlgorithm.SHA256.code(), sn, new Bytes(key), version, data);

		assertNull(nodeV0.getValueHash());

		assertEquals(sn, nodeV0.getSN());
		assertEquals(version, nodeV0.getVersion());
		assertEquals(new Bytes(key), nodeV0.getKey());

		byte[] nodeBytes = nodeV0.toBytes();

		DataNode nodeV0_reversed = encoderV0.resolve(nodeBytes);
		assertNull(nodeV0_reversed.getValueHash());

		assertEquals(nodeV0.getNodeHash(), nodeV0_reversed.getNodeHash());
		assertEquals(encoderV0.getFormatVersion(), nodeBytes[0]);


		assertEquals(sn, nodeV0_reversed.getSN());
		assertEquals(version, nodeV0_reversed.getVersion());
		assertEquals(new Bytes(key), nodeV0_reversed.getKey());
	}

	@Test
	public void testEnocoderV1() {
		MerkleDataNodeEncoder encoderV1 = new MerkleDataNodeEncoder_V1();

		Random rand = new Random();

		byte[] data = new byte[512];
		byte[] key = new byte[256];

		rand.nextBytes(data);
		rand.nextBytes(key);

		HashFunction hashFunc = Crypto.getHashFunction(ClassicAlgorithm.SHA256);
		HashDigest dataHash = hashFunc.hash(data);

		long sn = 1024;
		long version = 1;

		DataNode node = encoderV1.create(ClassicAlgorithm.SHA256.code(), sn, new Bytes(key), version, data);

		assertEquals(dataHash, node.getValueHash());

		assertEquals(sn, node.getSN());
		assertEquals(version, node.getVersion());
		assertEquals(new Bytes(key), node.getKey());

		byte[] nodeBytes = node.toBytes();

		DataNode node_reversed = encoderV1.resolve(nodeBytes);

		assertEquals(dataHash, node_reversed.getValueHash());
		assertEquals(node.getNodeHash(), node_reversed.getNodeHash());
		assertEquals(encoderV1.getFormatVersion(), nodeBytes[0]);

		assertEquals(sn, node_reversed.getSN());
		assertEquals(version, node_reversed.getVersion());
		assertEquals(new Bytes(key), node_reversed.getKey());

	}

}
