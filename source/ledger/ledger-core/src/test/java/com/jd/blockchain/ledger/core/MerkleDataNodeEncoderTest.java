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
		

		Random rand = new Random();

		byte[] data = new byte[512];
		byte[] keyBytes = new byte[256];

		rand.nextBytes(data);
		rand.nextBytes(keyBytes);
		Bytes key = new Bytes(keyBytes);

		long sn = 1024;
		long version = 1;
		doTestV0(sn, version, key, data);
		
		sn = 0;
		version = 1000;
		doTestV0(sn, version, key, data);
		
		sn = (1 << 56) -1;
		version = 1000;
		doTestV0(sn, version, key, data);
	}
	
	private void doTestV0(long sn, long version, Bytes key, byte[] data) {
		MerkleDataNodeEncoder encoderV0 = new MerkleDataNodeEncoder_V0();
		DataNode nodeV0 = encoderV0.create(ClassicAlgorithm.SHA256.code(), sn, key, version, data);

		assertNull(nodeV0.getValueHash());

		assertEquals(sn, nodeV0.getSN());
		assertEquals(version, nodeV0.getVersion());
		assertEquals(key, nodeV0.getKey());

		byte[] nodeBytes = nodeV0.toBytes();

		DataNode nodeV0_reversed = encoderV0.resolve(nodeBytes);
		assertNull(nodeV0_reversed.getValueHash());

		assertEquals(nodeV0.getNodeHash(), nodeV0_reversed.getNodeHash());
		assertEquals(encoderV0.getFormatVersion(), nodeBytes[0]);

		assertEquals(sn, nodeV0_reversed.getSN());
		assertEquals(version, nodeV0_reversed.getVersion());
		assertEquals(key, nodeV0_reversed.getKey());
	}

	@Test
	public void testEnocoderV1() {
		Random rand = new Random();

		byte[] data = new byte[512];
		byte[] keyBytes = new byte[256];

		rand.nextBytes(data);
		rand.nextBytes(keyBytes);
		Bytes key = new Bytes(keyBytes);

		long sn = 1024;
		long version = 1;
		doTestV1(sn, version, key, data);
		
		sn = 0;
		version = 10088;
		doTestV1(sn, version, key, data);
		
		sn = (1 << 56) -1;
		version = 1000;
		doTestV1(sn, version, key, data);
	}
	
	private void doTestV1(long sn, long version, Bytes key, byte[] data) {
		HashFunction hashFunc = Crypto.getHashFunction(ClassicAlgorithm.SHA256);
		HashDigest dataHash = hashFunc.hash(data);
		
		MerkleDataNodeEncoder encoderV1 = new MerkleDataNodeEncoder_V1();
		DataNode node = encoderV1.create(ClassicAlgorithm.SHA256.code(), sn, key, version, data);

		assertEquals(dataHash, node.getValueHash());

		assertEquals(sn, node.getSN());
		assertEquals(version, node.getVersion());
		assertEquals(key, node.getKey());

		byte[] nodeBytes = node.toBytes();

		DataNode node_reversed = encoderV1.resolve(nodeBytes);

		assertEquals(dataHash, node_reversed.getValueHash());
		assertEquals(node.getNodeHash(), node_reversed.getNodeHash());
		assertEquals(encoderV1.getFormatVersion(), nodeBytes[0]);

		assertEquals(sn, node_reversed.getSN());
		assertEquals(version, node_reversed.getVersion());
		assertEquals(key, node_reversed.getKey());
	}

	@Test
	public void testCompatibility() {
		Random rand = new Random();

		byte[] data = new byte[512];
		byte[] keyBytes = new byte[256];

		rand.nextBytes(data);
		rand.nextBytes(keyBytes);

		Bytes key = new Bytes(keyBytes);

		long sn = 1024;
		long version = 1;


		PreviousDataNode pdataNode = PreviousDataNode.newDataNode(ClassicAlgorithm.SHA256.code(), sn, key, version,
				data);
		
		MerkleDataNodeEncoder encoderV0 = new MerkleDataNodeEncoder_V0();
		DataNode dataNode = encoderV0.create(ClassicAlgorithm.SHA256.code(), sn, key, version, data);
		
		assertEquals(pdataNode.getNodeHash(), dataNode.getNodeHash());
		assertEquals(pdataNode.getSN(), dataNode.getSN());
		assertEquals(pdataNode.getVersion(), dataNode.getVersion());
		assertEquals(pdataNode.getKey(), dataNode.getKey());
		
		DataNode dataNode_reversed = encoderV0.resolve(pdataNode.toBytes());
		
		assertNull(dataNode_reversed.getValueHash());
		assertEquals(pdataNode.getNodeHash(), dataNode_reversed.getNodeHash());
		assertEquals(pdataNode.getSN(), dataNode_reversed.getSN());
		assertEquals(pdataNode.getVersion(), dataNode_reversed.getVersion());
		assertEquals(pdataNode.getKey(), dataNode_reversed.getKey());
	}
}
