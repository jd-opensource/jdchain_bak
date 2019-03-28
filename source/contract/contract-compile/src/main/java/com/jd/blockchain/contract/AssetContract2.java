package com.jd.blockchain.contract;

import com.jd.blockchain.contract.model.*;
import com.jd.blockchain.crypto.hash.HashDigest;
import com.jd.blockchain.ledger.KVDataEntry;
import com.jd.blockchain.utils.BaseConstant;

/**
 * 模拟用智能合约;
 */
@Contract
public class AssetContract2 implements EventProcessingAwire {
	//	private static final Logger LOGGER = LoggerFactory.getLogger(AssetContract.class);
	private static final String KEY_TOTAL = "TOTAL";
	private static final String LEDGER_HASH = "ledgerHash";

	@ContractEvent(name = "issue-asset")
	public void test1(ContractEventContext eventContext) throws Exception{
		byte [] args_ = eventContext.getArgs();
		if(args_ == null){
			return;
		}
		String[] args = new String(args_).split(BaseConstant.DELIMETER_DOUBLE_ALARM);

		long amount = Long.parseLong(args[0]);
		long amount1 = Long.parseLong(args[1]);
		String contractDataAddress = args[2];
		System.out.println("in contract2,invoke test1(),amountAdd:"+(amount+amount1)+",contractDataAddress="+contractDataAddress);

		//test invoke;
		HashDigest hashDigest = eventContext.getCurrentLedgerHash();
		KVDataEntry[] kvEntries = eventContext.getLedger().getDataEntries(hashDigest, contractDataAddress,
				KEY_TOTAL,LEDGER_HASH); //,"latestBlockHash"
		//当前mock设定值为：TOTAL="total value,dataAccount";abc="abc value,dataAccount";

		//
//		assert ByteArray.toHex("total value,dataAccount".getBytes()).equals(kvEntries[0].getValue())
//				&& ByteArray.toHex("abc value,dataAccount".getBytes()).equals(kvEntries[1].getValue()) :
//				"getDataEntries() test,期望值=设定值;";
		System.out.println("in dataSet,KEY_TOTAL="+new String(kvEntries[0].getValue().toString()));
		System.out.println("in dataSet,LEDGER_HASH="+new String(kvEntries[1].getValue().toString()));
	}

	@Override
	public void beforeEvent(ContractEventContext contractEventContext) {

	}

	@Override
	public void postEvent() {

	}

	@Override
	public void postEvent(ContractEventContext contractEventContext, ContractException contractError) {

	}

	@Override
	public void postEvent(ContractException contractError) {

	}
}
