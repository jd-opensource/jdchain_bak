package test.com.jd.blockchain.ledger;

import static org.junit.Assert.*;

import org.junit.Test;

import com.jd.blockchain.binaryproto.BinaryProtocol;
import com.jd.blockchain.ledger.LedgerPermission;
import com.jd.blockchain.ledger.PrivilegeSet;
import com.jd.blockchain.ledger.Privileges;
import com.jd.blockchain.ledger.TransactionPermission;

public class PrivilegesTest {

	@Test
	public void test() {
		// 正常情形；
		{
			Privileges privileges = Privileges.configure()
					.enable(LedgerPermission.REGISTER_USER, LedgerPermission.APPROVE_TX)
					.enable(TransactionPermission.DIRECT_OPERATION);

			byte[] bytes = BinaryProtocol.encode(privileges, PrivilegeSet.class);

			PrivilegeSet decodePrivileges = BinaryProtocol.decode(bytes);

			assertNotNull(decodePrivileges.getLedgerPrivilege());
			assertNotNull(decodePrivileges.getTransactionPrivilege());

			for (LedgerPermission p : LedgerPermission.values()) {
				if (p == LedgerPermission.REGISTER_USER || p == LedgerPermission.APPROVE_TX) {
					assertTrue(decodePrivileges.getLedgerPrivilege().isEnable(p));
				} else {
					assertFalse(decodePrivileges.getLedgerPrivilege().isEnable(p));
				}
			}
			for (TransactionPermission p : TransactionPermission.values()) {
				if (p == TransactionPermission.DIRECT_OPERATION) {
					assertTrue(decodePrivileges.getTransactionPrivilege().isEnable(p));
				} else {
					assertFalse(decodePrivileges.getTransactionPrivilege().isEnable(p));
				}
			}
		}
		// 只定义账本权限的情形；
		{
			Privileges privileges = Privileges.configure().enable(LedgerPermission.REGISTER_USER,
					LedgerPermission.APPROVE_TX);

			byte[] bytes = BinaryProtocol.encode(privileges, PrivilegeSet.class);

			PrivilegeSet decodePrivileges = BinaryProtocol.decode(bytes);

			assertNotNull(decodePrivileges.getLedgerPrivilege());
			assertNotNull(decodePrivileges.getTransactionPrivilege());

			for (LedgerPermission p : LedgerPermission.values()) {
				if (p == LedgerPermission.REGISTER_USER || p == LedgerPermission.APPROVE_TX) {
					assertTrue(decodePrivileges.getLedgerPrivilege().isEnable(p));
				} else {
					assertFalse(decodePrivileges.getLedgerPrivilege().isEnable(p));
				}
			}
			for (TransactionPermission p : TransactionPermission.values()) {
				assertFalse(decodePrivileges.getTransactionPrivilege().isEnable(p));
			}
		}
		// 只定义交易权限的情形；
		{
			Privileges privileges = Privileges.configure().enable(TransactionPermission.CONTRACT_OPERATION);

			byte[] bytes = BinaryProtocol.encode(privileges, PrivilegeSet.class);

			PrivilegeSet decodePrivileges = BinaryProtocol.decode(bytes);

			assertNotNull(decodePrivileges.getLedgerPrivilege());
			assertNotNull(decodePrivileges.getTransactionPrivilege());
			
			for (LedgerPermission p : LedgerPermission.values()) {
					assertFalse(decodePrivileges.getLedgerPrivilege().isEnable(p));
			}
			for (TransactionPermission p : TransactionPermission.values()) {
				if (p == TransactionPermission.CONTRACT_OPERATION) {
					assertTrue(decodePrivileges.getTransactionPrivilege().isEnable(p));
				} else {
					assertFalse(decodePrivileges.getTransactionPrivilege().isEnable(p));
				}
			}
		}
	}

}
