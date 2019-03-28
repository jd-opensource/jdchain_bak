package com.jd.blockchain.contract;

import com.jd.blockchain.contract.model.*;
import com.jd.blockchain.utils.BaseConstant;

/**
 * 模拟用智能合约;
 * 只做最简单的加法运算;
 */
@Contract
public class AssetContract3 implements EventProcessingAwire {

	@ContractEvent(name = "issue-asset")
	public void test1(ContractEventContext eventContext){
		byte [] args_ = eventContext.getArgs();
		if(args_ == null){
			return;
		}

//		KVDataEntry[] kvEntries = eventContext.getLedger().getDataEntries(eventContext.getCurrentLedgerHash(),
//				"", "");
		String[] args = new String(args_).split(BaseConstant.DELIMETER_DOUBLE_ALARM);

		long amount = Long.parseLong(args[0]);
		long amount1 = Long.parseLong(args[1]);
		String contractDataAddress = args[2];
		System.out.println("###@@@,in contract3,invoke test1(),amountAdd:"+(amount+amount1)+
				",contractDataAddress= "+contractDataAddress);
	}

	@ContractEvent(name = "multi")
	public void test2(ContractEventContext eventContext) throws Exception{
		byte [] args_ = eventContext.getArgs();
		if(args_ == null){
			return;
		}
		String[] args = new String(args_).split(BaseConstant.DELIMETER_DOUBLE_ALARM);

		long amount = Long.parseLong(args[0]);
		long amount1 = Long.parseLong(args[1]);
		String contractDataAddress = args[2];
		System.out.println("###test,in contract3,invoke test2(),amount Multi:"+(amount*amount1)+
				",contractDataAddress= "+contractDataAddress);
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
