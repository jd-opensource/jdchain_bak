package test.com.jd.blockchain.ledger;

import com.jd.blockchain.contract.ContractEventContext;
import com.jd.blockchain.contract.ContractLifecycleAware;
import com.jd.blockchain.contract.EventProcessingAware;
import com.jd.blockchain.ledger.TypedKVEntry;
import com.jd.blockchain.utils.Bytes;

public class TxTestContractImpl implements TxTestContract, ContractLifecycleAware, EventProcessingAware {

	private ContractEventContext eventContext;

	private Bytes dataAddress;

	public static String KEY = "k1";

	@Override
	public boolean testReadable() {
		TypedKVEntry v1 = eventContext.getLedger().getDataEntries(eventContext.getCurrentLedgerHash(),
				dataAddress.toBase58(), KEY)[0];
		String text1 = (String) v1.getValue();
		System.out.printf("k1=%s, version=%s \r\n", text1, v1.getVersion());

		text1 = null == text1 ? "v" : text1;
		String newValue = text1 + "-" + (v1.getVersion() + 1);
		System.out.printf("new value = %s\r\n", newValue);
		eventContext.getLedger().dataAccount(dataAddress).setText(KEY, newValue, v1.getVersion());

		TypedKVEntry v2 = eventContext.getLedger().getDataEntries(eventContext.getCurrentLedgerHash(),
				dataAddress.toBase58(), KEY)[0];
		System.out.printf("---- read new value ----\r\nk1=%s, version=%s \r\n", v2.getValue(), v2.getVersion());

		String text2 = (String) v2.getValue();
		return text1.equals(text2);
	}
	
	@Override
	public void testRollbackWhileVersionConfliction(String address, String key, String value, long version) {
		eventContext.getLedger().dataAccount(address).setText(key, value, version);
	}


	@Override
	public void postConstruct() {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeDestroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeEvent(ContractEventContext eventContext) {
		this.eventContext = eventContext;
	}

	@Override
	public void postEvent(ContractEventContext eventContext, Exception error) {
		this.eventContext = null;
	}

	public Bytes getDataAddress() {
		return dataAddress;
	}

	public void setDataAddress(Bytes dataAddress) {
		this.dataAddress = dataAddress;
	}


}
