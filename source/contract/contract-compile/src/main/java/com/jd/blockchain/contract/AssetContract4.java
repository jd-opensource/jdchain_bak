package com.jd.blockchain.contract;

import com.jd.blockchain.contract.model.*;
import com.jd.blockchain.ledger.BlockchainKeyGenerator;
import com.jd.blockchain.ledger.BlockchainKeyPair;
import com.jd.blockchain.ledger.KVDataEntry;
import com.jd.blockchain.utils.BaseConstant;
import com.jd.blockchain.utils.io.ByteArray;

/**
 * 模拟用智能合约;
 * 测试从链中取数据，然后对比是否与预定值一致；param1Val 的值要与IntegrationTest中保持一致;
 */
@Contract
public class AssetContract4 implements EventProcessingAwire {
	String param1 = "param1";
	String param1Val = "param1Val";

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
		System.out.println("###@@@,in contract4,invoke test1(),amountAdd:"+(amount+amount1)+
				",contractDataAddress= "+contractDataAddress);

		BlockchainKeyPair dataAccount = BlockchainKeyGenerator.getInstance().generate();
		//TODO:register牵扯到账本中的事务处理，需要优化;
//		contractEventContext.getLedger().dataAccounts().register(dataAccount.getIdentity());
//		contractEventContext.getLedger().dataAccount(dataAccount.getAddress()).
//				set(param1,param1Val.getBytes(),-1).getOperation();
		System.out.println("data address="+dataAccount.getAddress());
	}

	@ContractEvent(name = "event2")
	public void test2(ContractEventContext eventContext) throws Exception{
		byte [] args_ = eventContext.getArgs();
		if(args_ == null){
			return;
		}
		String[] args = new String(args_).split(BaseConstant.DELIMETER_DOUBLE_ALARM);

		long amount = Long.parseLong(args[0]);
		long amount1 = Long.parseLong(args[1]);
		String contractDataAddress = args[2];
		System.out.println("###!!!!!!!!,in contract3,invoke test2(),amount Multi:"+(amount*amount1)+
				",contractDataAddress= "+contractDataAddress);

		KVDataEntry[] kvEntries = eventContext.getLedger().getDataEntries(eventContext.getCurrentLedgerHash(),
				contractDataAddress, param1);
		if (ByteArray.toHex(param1Val.getBytes()).equals(kvEntries[0].getValue())){
			System.out.println("getDataEntries() test,期望值==设定值;");
		} else {
            System.out.println("getDataEntries() test,期望值!=设定值;");
        }
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
