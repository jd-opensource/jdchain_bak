package test.com.jd.blockchain.ledger;

import com.jd.blockchain.contract.Contract;
import com.jd.blockchain.contract.ContractEvent;

@Contract
public interface TxTestContract {
	
	@ContractEvent(name = "testReadable")
	boolean testReadable();

//	@ContractEvent(name = "prepareData")
//	String[] prepareData(String address);
//
//	@ContractEvent(name = "doVersionConflictedWritting")
//	void doVersionConflictedWritting(String key, String value, long version);

}
