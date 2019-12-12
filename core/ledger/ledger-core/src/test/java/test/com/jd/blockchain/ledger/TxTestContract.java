package test.com.jd.blockchain.ledger;

import com.jd.blockchain.contract.Contract;
import com.jd.blockchain.contract.ContractEvent;

@Contract
public interface TxTestContract {

	@ContractEvent(name = "testReadable")
	boolean testReadable();

	@ContractEvent(name = "testRollbackWhileVersionConfliction")
	void testRollbackWhileVersionConfliction(String address, String key, String value, long version);

}
