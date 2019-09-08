package test.com.jd.blockchain.ledger;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.jd.blockchain.binaryproto.BinaryProtocol;
import com.jd.blockchain.ledger.LedgerPermission;
import com.jd.blockchain.ledger.SecurityInitData;
import com.jd.blockchain.ledger.SecurityInitSettings;
import com.jd.blockchain.ledger.TransactionPermission;
import com.jd.blockchain.utils.serialize.json.JSONSerializeUtils;

public class SecurityInitDataTest {
	
	@Test
	public void testEnumsSerialization() {
		LedgerPermission[] permissions = JSONSerializeUtils.deserializeFromJSON("[\"REGISTER_USER\",\"REGISTER_DATA_ACCOUNT\"]", LedgerPermission[].class);
		assertNotNull(permissions);
		assertEquals(2, permissions.length);
		assertEquals(LedgerPermission.REGISTER_USER, permissions[0]);
		assertEquals(LedgerPermission.REGISTER_DATA_ACCOUNT, permissions[1]);
		
		LedgerPermission[] permissions2 = JSONSerializeUtils.deserializeFromJSON("['REGISTER_USER', 'REGISTER_DATA_ACCOUNT']", LedgerPermission[].class);
		assertNotNull(permissions2);
		assertEquals(2, permissions2.length);
		assertEquals(LedgerPermission.REGISTER_USER, permissions2[0]);
		assertEquals(LedgerPermission.REGISTER_DATA_ACCOUNT, permissions2[1]);
		
		LedgerPermission[] allLedgerPermissions = LedgerPermission.values();
		String jsonLedgerPersioms = JSONSerializeUtils.serializeToJSON(allLedgerPermissions);
		
		TransactionPermission[] allTransactionPermissions = TransactionPermission.values();
		String jsonTransactionPersioms = JSONSerializeUtils.serializeToJSON(allTransactionPermissions);
		
		System.out.println("----------- Ledger Permissions JSON ------------");
		System.out.println(jsonLedgerPersioms);
		System.out.println("-----------------------");
		System.out.println("----------- Transaction Permissions JSON ------------");
		System.out.println(jsonTransactionPersioms);
		System.out.println("-----------------------");
	}

	@Test
	public void testSecurityInitDataSerialization() {
		
		SecurityInitData securityInitData = new SecurityInitData();

		securityInitData.addRole("DEFAULT",
				new LedgerPermission[] { LedgerPermission.REGISTER_USER, LedgerPermission.REGISTER_DATA_ACCOUNT },
				new TransactionPermission[] { TransactionPermission.CONTRACT_OPERATION });
		securityInitData.addRole("ADMIN",
				new LedgerPermission[] { LedgerPermission.REGISTER_USER, LedgerPermission.REGISTER_DATA_ACCOUNT },
				new TransactionPermission[] { TransactionPermission.DIRECT_OPERATION,
						TransactionPermission.CONTRACT_OPERATION });
		securityInitData.addRole("R1",
				new LedgerPermission[] { LedgerPermission.REGISTER_USER, LedgerPermission.REGISTER_DATA_ACCOUNT },
				null);
		securityInitData.addRole("R2", null, new TransactionPermission[] { TransactionPermission.DIRECT_OPERATION,
				TransactionPermission.CONTRACT_OPERATION });

		String json = JSONSerializeUtils.serializeToJSON(securityInitData, true);
		System.out.println("----------- JSON ------------");
		System.out.println(json);
		System.out.println("-----------------------");

		SecurityInitData desSecurityInitData = JSONSerializeUtils.deserializeFromJSON(json, SecurityInitData.class);
		
		String json2 = JSONSerializeUtils.serializeToJSON(desSecurityInitData, true);
		System.out.println("----------- JSON2 ------------");
		System.out.println(json2);
		System.out.println("-----------------------");
		
		assertEquals(json, json2);
		
		byte[] bytes = BinaryProtocol.encode(securityInitData, SecurityInitSettings.class);
		
		SecurityInitSettings securityInitData2 = BinaryProtocol.decode(bytes);
		
		byte[] bytes2 = BinaryProtocol.encode(securityInitData2, SecurityInitSettings.class);
		
		assertArrayEquals(bytes, bytes2);
		
		assertEquals(4, securityInitData2.getRoles().length);
		assertEquals(securityInitData.getRoles().length, securityInitData2.getRoles().length);
		assertEquals(LedgerPermission.REGISTER_USER, securityInitData2.getRoles()[1].getLedgerPermissions()[0]);
		assertEquals(securityInitData.getRoles()[1].getLedgerPermissions()[0], securityInitData2.getRoles()[1].getLedgerPermissions()[0]);
		assertEquals(securityInitData.getRoles()[1].getLedgerPermissions()[1], securityInitData2.getRoles()[1].getLedgerPermissions()[1]);
		
	}

}
