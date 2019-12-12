package test.com.jd.blockchain.ledger;
//package test.com.jd.blockchain.ledger.data;
//
//import static org.junit.Assert.*;
//
//import java.io.IOException;
//
//import org.junit.Test;
//
//import com.jd.blockchain.ledger.StateOpType;
//import com.jd.blockchain.ledger.Operation;
//import com.jd.blockchain.ledger.OperationType;
//import com.jd.blockchain.ledger.data.OpBlob;
//
//import my.utils.io.ByteArray;
//import my.utils.io.BytesUtils;
//import my.utils.security.RandomUtils;
//
//public class OpBlobTest {
//
//	/**
//	 * 验证无子操作的情形；
//	 * 
//	 * @throws IOException
//	 */
//	@Test
//	public void testSerializeNoSubOP() throws IOException {
//		OpBlob op = new OpBlob();
//		byte[] opArg = RandomUtils.generateRandomBytes(16);
//		op.setOperation(OperationType.REGISTER_USER.CODE, ByteArray.wrap(opArg));
//
//		byte[] opBytes = BytesUtils.toBytes(op);
//
//		assertEquals(OperationType.REGISTER_USER.CODE, opBytes[0]);
//
//		OpBlob resolvedOP = new OpBlob();
//		resolvedOP.resolvFrom(ByteArray.wrap(opBytes).asInputStream());
//
//		assertEquals(op.getCode(), resolvedOP.getCode());
//		assertEquals(op.getArgCount(), resolvedOP.getArgCount());
//		assertEquals(0, resolvedOP.getSubOperations().length);
//		assertEquals(op.getSubOperations().length, resolvedOP.getSubOperations().length);
//
//	}
//
//	/**
//	 * 验证有子操作的情形；
//	 * 
//	 * @throws IOException
//	 */
//	@Test
//	public void testSerializeWithSubOP() throws IOException {
//		OpBlob op = new OpBlob();
//		byte[] opArg = RandomUtils.generateRandomBytes(16);
//		op.setOperation(OperationType.REGISTER_USER.CODE, ByteArray.wrap(opArg));
//
//		byte[] subopArg = RandomUtils.generateRandomBytes(16);
//		op.addSubOperation(StateOpType.SET, ByteArray.wrap(subopArg));
//
//		byte[] opBytes = BytesUtils.toBytes(op);
//
//		assertEquals(OperationType.REGISTER_USER.CODE, opBytes[0]);
//
//		OpBlob resolvedOP = new OpBlob();
//		resolvedOP.resolvFrom(ByteArray.wrap(opBytes).asInputStream());
//
//		assertEquals(op.getCode(), resolvedOP.getCode());
//		assertEquals(op.getArgCount(), resolvedOP.getArgCount());
//
//		Operation[] subOps = op.getSubOperations();
//		Operation[] resolvedSubOps = resolvedOP.getSubOperations();
//		assertEquals(1, subOps.length);
//		assertEquals(subOps.length, resolvedSubOps.length);
//		
//		for (int i = 0; i < resolvedSubOps.length; i++) {
//			assertEquals(subOps[i].getCode(), resolvedSubOps[i].getCode());
//			assertEquals(1, resolvedSubOps[i].getArgCount());
//			assertEquals(subOps[i].getArgCount(), resolvedSubOps[i].getArgCount());
//			assertEquals(subOps[i].getArg(0), resolvedSubOps[i].getArg(0));
//		}
//	}
//
//}
