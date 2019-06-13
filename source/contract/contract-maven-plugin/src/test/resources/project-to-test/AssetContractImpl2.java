package com.jd.blockchain.contract;

import com.jd.blockchain.ledger.KVDataEntry;
import com.jd.blockchain.ledger.KVDataObject;
import com.jd.blockchain.utils.Bytes;

import java.util.Arrays;

/**
 * 示例：一个“资产管理”智能合约的实现；
 * 
 * 注： 1、实现 EventProcessingAwire 接口以便合约实例在运行时可以从上下文获得合约生命周期事件的通知； 2、实现
 * AssetContract 接口定义的合约方法；
 * 
 * @author huanghaiquan
 *
 */
@Contract
public class AssetContractImpl2 implements EventProcessingAwire, AssetContract2 {
	public static String KEY_TOTAL = "total";
	// 合约事件上下文；
	private ContractEventContext eventContext;

	@Override
	@ContractEvent(name = "issue-asset-0")
	public void issue(ContractBizContent contractBizContent, String assetHolderAddress) {
		System.out.println("input addr="+ Arrays.toString(contractBizContent.getAttrs()));
	}

	@Override
	@ContractEvent(name = "issue-asset")
	public void issue(ContractBizContent contractBizContent, String assetHolderAddress, long cashNumber) {
		System.out.println("eventContext="+eventContext.getCurrentLedgerHash().toBase58());
		System.out.println("getAttrs: "+Arrays.toString(contractBizContent.getAttrs())+",address="+assetHolderAddress+",cashNumber="+cashNumber);

		eventContext.getLedger().dataAccount(assetHolderAddress).set(contractBizContent.getAttrs()[0], "value1",-1);
		eventContext.getLedger().dataAccount(assetHolderAddress).set(contractBizContent.getAttrs()[1], 888,-1);
	}

	@Override
	@ContractEvent(name = "issue-asset-2")
	public void issue(Bytes bytes, String assetHolderAddress, long cashNumber){
		System.out.println(String.format("bytes=%s,assetHolderAddress=%s,cashNumber=%d",new String(bytes.toBytes()),assetHolderAddress,cashNumber));
	}

    @ContractEvent(name = "issue-asset-3")
    @Override
    public void issue(Byte byteObj, String assetHolderAddress, long cashNumber) {
        System.out.println(String.format("issue(),bytes=%d,assetHolderAddress=%s,cashNumber=%d",byteObj.intValue(),assetHolderAddress,cashNumber));
    }

	@ContractEvent(name = "issue-asset-4")
	@Override
	public void issue(Byte byteObj, String assetHolderAddress, Bytes cashNumber) {
		System.out.println(String.format("issue(),bytes=%d,assetHolderAddress=%s,cashNumber=%s",byteObj.intValue(),assetHolderAddress,cashNumber.toString()));
		System.out.println("current LedgerHash="+eventContext.getCurrentLedgerHash().toBase58());
		// 查询当前值；
        KVDataEntry[] kvEntries = eventContext.getLedger().getDataEntries(eventContext.getCurrentLedgerHash(), assetHolderAddress, KEY_TOTAL);
//      计算资产的发行总数；
        KVDataObject curTotal = (KVDataObject) kvEntries[0];
		System.out.println("currTotal version="+curTotal.getVersion()+",value="+curTotal.getValue().toString());
		eventContext.getLedger().dataAccount(assetHolderAddress).set(KEY_TOTAL, 100,curTotal.getVersion());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.jd.blockchain.contract.model.EventProcessingAwire#beforeEvent(com.jd.
	 * blockchain.contract.model.ContractEventContext)
	 */
	@Override
	public void beforeEvent(ContractEventContext eventContext) {
		this.eventContext = eventContext;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jd.blockchain.contract.model.EventProcessingAwire#postEvent(com.jd.
	 * blockchain.contract.model.ContractEventContext,
	 * com.jd.blockchain.contract.model.ContractError)
	 */
	@Override
	public void postEvent(ContractEventContext eventContext, ContractException error) {
		this.eventContext = null;
	}

	@Override
	public void postEvent(ContractException error) {

	}

	@Override
	public void postEvent() {

	}
}
