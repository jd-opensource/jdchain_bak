package test.com.jd.blockchain.ledger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.Set;

import org.junit.Test;

import com.jd.blockchain.contract.ContractException;
import com.jd.blockchain.contract.ContractType;

public class ContractTypeTest {

	/**
	 * 正常测试；
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 */
	@Test
	public void normalTest() throws NoSuchMethodException, SecurityException {
		ContractType contractType = ContractType.resolve(NormalContract.class);

		assertEquals("NORMAL-CONTRACT", contractType.getName());

		Set<String> events = contractType.getEvents();
		assertEquals(5, events.size());
		assertTrue(events.contains("issue"));
		assertTrue(events.contains("get_amount"));
		assertTrue(events.contains("get_balance"));
		assertTrue(events.contains("assign"));
		assertTrue(events.contains("transfer"));

		Method issueMethod = contractType.getHandleMethod("issue");
		assertNotNull(issueMethod);
		Method getAmountMethod = contractType.getHandleMethod("get_amount");
		assertNotNull(getAmountMethod);
		Method getBalanceMethod = contractType.getHandleMethod("get_balance");
		assertNotNull(getBalanceMethod);
		Method assignMethod = contractType.getHandleMethod("assign");
		assertNotNull(assignMethod);
		Method transferMethod = contractType.getHandleMethod("transfer");
		assertNotNull(transferMethod);

		assertEquals("issue", contractType.getEvent(issueMethod));
		assertEquals("get_amount", contractType.getEvent(getAmountMethod));
		assertEquals("get_balance", contractType.getEvent(getBalanceMethod));
		assertEquals("assign", contractType.getEvent(assignMethod));
		assertEquals("transfer", contractType.getEvent(transferMethod));
		
		Method toStringMethod = NormalContractImpl.class.getMethod("toString");
		assertNull(contractType.getEvent(toStringMethod));
		assertNull(contractType.getHandleMethod("NotExist"));
		
		//解析非合约声明接口类型时，应该正常，只需要实现接口（该接口定义了具体的合约类型）即可
		ContractException ex = null;
		try {
			ContractType.resolve(NormalContractImpl.class);
		} catch (ContractException e) {
			ex = e;
		}
		assertNull(ex);
	}

}
