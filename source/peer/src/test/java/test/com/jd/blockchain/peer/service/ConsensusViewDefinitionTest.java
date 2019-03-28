package test.com.jd.blockchain.peer.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.jd.blockchain.peer.consensus.ConsensusViewDefinition;
import com.jd.blockchain.utils.net.NetworkAddress;

public class ConsensusViewDefinitionTest {

	@Test
	public void test() {
		ConsensusViewDefinition viewDef = new ConsensusViewDefinition();
		
		assertEquals(0, viewDef.getNodeCount());
		assertEquals(1, viewDef.getN());
		assertEquals(0, viewDef.getF());
		
		viewDef.addNode(new NetworkAddress("localhost", 10001));
		assertEquals(1, viewDef.getNodeCount());
		assertEquals(1, viewDef.getN());
		assertEquals(0, viewDef.getF());
		
		viewDef.addNode(new NetworkAddress("localhost", 10002));
		assertEquals(2, viewDef.getNodeCount());
		assertEquals(1, viewDef.getN());
		assertEquals(0, viewDef.getF());
		
		viewDef.addNode(new NetworkAddress("localhost", 10003));
		assertEquals(3, viewDef.getNodeCount());
		assertEquals(1, viewDef.getN());
		assertEquals(0, viewDef.getF());
		
		viewDef.addNode(new NetworkAddress("localhost", 10004));
		assertEquals(4, viewDef.getNodeCount());
		assertEquals(4, viewDef.getN());
		assertEquals(1, viewDef.getF());
		
		viewDef.addNode(new NetworkAddress("localhost", 10005));
		assertEquals(5, viewDef.getNodeCount());
		assertEquals(4, viewDef.getN());
		assertEquals(1, viewDef.getF());
		
	}

}
