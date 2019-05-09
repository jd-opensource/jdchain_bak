package com.jd.blockchain.contract.samples;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.jd.blockchain.contract.ContractEventContext;
import com.jd.blockchain.contract.ContractException;
import com.jd.blockchain.contract.EventProcessingAwire;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.BlockchainIdentity;
import com.jd.blockchain.ledger.KVDataEntry;
import com.jd.blockchain.ledger.KVDataObject;
import com.jd.blockchain.utils.io.BytesUtils;

/**
 * 示例：一个“资产管理”智能合约的实现；
 * 
 * 注： 1、实现 EventProcessingAwire 接口以便合约实例在运行时可以从上下文获得合约生命周期事件的通知； 2、实现
 * AssetContract 接口定义的合约方法；
 * 
 * @author huanghaiquan
 *
 */
public class AssetContractImpl implements EventProcessingAwire, AssetContract {
	// 资产管理账户的地址；
	private static final String ASSET_ADDRESS = "2njZBNbFQcmKd385DxVejwSjy4driRzf9Pk";
	// 保存资产总数的键；
	private static final String KEY_TOTAL = "TOTAL";
	// 合约事件上下文；
	private ContractEventContext eventContext;

	/**
	 * ------------------- 定义可以由外部用户通过提交“交易”触发的调用方法 ------------------
	 */

	@Override
	public void issue(long amount, String assetHolderAddress) {
		checkAllOwnersAgreementPermission();

		// 新发行的资产数量；
		if (amount < 0) {
			throw new ContractException("The amount is negative!");
		}
		if (amount == 0) {
			return;
		}

		// 查询当前值；
		KVDataEntry[] kvEntries = eventContext.getLedger().getDataEntries(currentLedgerHash(), ASSET_ADDRESS, KEY_TOTAL,
				assetHolderAddress);
		
		// 计算资产的发行总数；
		KVDataObject currTotal = (KVDataObject) kvEntries[0];
		long newTotal = currTotal.longValue() + amount;
		eventContext.getLedger().dataAccount(ASSET_ADDRESS).set(KEY_TOTAL, BytesUtils.toBytes(newTotal),
				currTotal.getVersion());

		// 分配到持有者账户；
		KVDataObject holderAmount = (KVDataObject) kvEntries[1];
		long newHodlerAmount = holderAmount.longValue() + amount;
		eventContext.getLedger().dataAccount(ASSET_ADDRESS).set(assetHolderAddress, BytesUtils.toBytes(newHodlerAmount),
				holderAmount.getVersion()).set("K2", (byte[])null, -1).set("k3", (byte[])null, 3);
		
	}

	@Override
	public void transfer(String fromAddress, String toAddress, long amount) {
		// if (amount < 0) {
		// throw new ContractError("The amount is negative!");
		// }
		// if (amount == 0) {
		// return;
		// }
		//
		// //校验“转出账户”是否已签名；
		// checkSignerPermission(fromAddress);
		//
		// // 查询现有的余额；
		// Set<String> keys = new HashSet<>();
		// keys.add(fromAddress);
		// keys.add(toAddress);
		// StateMap origBalances =
		// eventContext.getLedger().getStates(currentLedgerHash(), ASSET_ADDRESS, keys);
		// KVDataObject fromBalance = origBalances.get(fromAddress);
		// KVDataObject toBalance = origBalances.get(toAddress);
		//
		// //检查是否余额不足；
		// if ((fromBalance.longValue() - amount) < 0) {
		// throw new ContractError("Insufficient balance!");
		// }
		//
		// // 把数据的更改写入到账本；
		// SimpleStateMap newBalances = new SimpleStateMap(origBalances.getAccount(),
		// origBalances.getAccountVersion(),
		// origBalances.getStateVersion());
		// KVDataObject newFromBalance = fromBalance.newLong(fromBalance.longValue() -
		// amount);
		// KVDataObject newToBalance = toBalance.newLong(toBalance.longValue() +
		// amount);
		// newBalances.setValue(newFromBalance);
		// newBalances.setValue(newToBalance);
		//
		// eventContext.getLedger().updateState(ASSET_ADDRESS).setStates(newBalances);
	}

	// -------------------------------------------------------------
	// ------------------- 定义只在合约内部调用的方法 ------------------
	// -------------------------------------------------------------

	/**
	 * 只有全部的合约拥有者同意才能通过校验；
	 */
	private void checkAllOwnersAgreementPermission() {
		Set<BlockchainIdentity> owners = eventContext.getContracOwners();
		Set<BlockchainIdentity> requestors = eventContext.getTxSigners();
		if (requestors.size() != owners.size()) {
			throw new ContractException("Permission Error! -- The requestors is not exactlly being owners!");
		}

		Map<String, BlockchainIdentity> ownerMap = new HashMap<>();
		for (BlockchainIdentity o : owners) {
			ownerMap.put(o.getAddress().toBase58(), o);
		}
		for (BlockchainIdentity r : requestors) {
			if (!ownerMap.containsKey(r.getAddress())) {
				throw new ContractException("Permission Error! -- No agreement of all owners!");
			}
		}
	}

	private HashDigest currentLedgerHash() {
		return eventContext.getCurrentLedgerHash();
	}

	/**
	 * 校验指定的账户是否签署了当前交易；
	 * 
	 * @param address
	 */
	private void checkSignerPermission(String address) {
		Set<BlockchainIdentity> requestors = eventContext.getTxSigners();
		for (BlockchainIdentity r : requestors) {
			if (r.getAddress().equals(address)) {
				return;
			}
		}
		throw new ContractException("Permission Error! -- No signature !");
	}

	// ----------------------------------------------------------
	// ------------------- 在运行时捕捉上下文事件 ------------------
	// ----------------------------------------------------------

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
