package com.jd.blockchain.contract.samples;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.consts.DataCodes;
import com.jd.blockchain.contract.ContractEvent;
import com.jd.blockchain.contract.ContractEventContext;
import com.jd.blockchain.contract.ContractException;
import com.jd.blockchain.contract.EventProcessingAwire;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.BlockchainIdentity;
import com.jd.blockchain.ledger.KVDataEntry;
import com.jd.blockchain.ledger.KVDataObject;
import com.jd.blockchain.ledger.TransactionContentBody;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.io.BytesUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 示例：一个“资产管理”智能合约的实现；
 * 
 * 注： 1、实现 EventProcessingAwire 接口以便合约实例在运行时可以从上下文获得合约生命周期事件的通知； 2、实现
 * AssetContract 接口定义的合约方法；
 * 
 * @author huanghaiquan
 *
 */
public class AssetContractImpl2 implements EventProcessingAwire, AssetContract2 {
	// 合约事件上下文；
	private ContractEventContext eventContext;

	@Override
	public void issue(TransactionContentBody transactionContentBody, String assetHolderAddress) {
		System.out.println(transactionContentBody.toString());
	}

	@Override
	@ContractEvent(name = "issue-asset")
	public void issue(@DataContract(code = DataCodes.TX_CONTENT_BODY) TransactionContentBody transactionContentBody,
					  @DataContract(code = DataCodes.CONTRACT_TEXT) String assetHolderAddress,
					  @DataContract(code = DataCodes.CONTRACT_INT64) long cashNumber) {
		System.out.println(transactionContentBody.toString()+",address="+assetHolderAddress+",cashNumber="+cashNumber);
	}

	@Override
	public void issue(Bytes bytes, String assetHolderAddress, long cashNumber) {

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
