package com.jd.blockchain.contract.samples;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.jd.blockchain.contract.ContractEventContext;
import com.jd.blockchain.contract.ContractException;
import com.jd.blockchain.contract.EventProcessingAware;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.BlockchainIdentity;
import com.jd.blockchain.ledger.KVDataEntry;
import com.jd.blockchain.ledger.KVDataObject;
import com.jd.blockchain.utils.Bytes;

/**
 * 示例：一个“资产管理”智能合约的实现；
 * 
 * 注： 1、实现 EventProcessingAwire 接口以便合约实例在运行时可以从上下文获得合约生命周期事件的通知； 2、实现
 * AssetContract 接口定义的合约方法；
 * 
 * @author huanghaiquan
 *
 */
public class AssetContractImpl implements EventProcessingAware, AssetContract {
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
		eventContext.getLedger().dataAccount(ASSET_ADDRESS).setInt64(KEY_TOTAL, newTotal, currTotal.getVersion());
		
		// 分配到持有者账户；
		KVDataObject holderAmount = (KVDataObject) kvEntries[1];
		long newHodlerAmount = holderAmount.longValue() + amount;
		eventContext.getLedger().dataAccount(ASSET_ADDRESS)
				.setInt64(assetHolderAddress, newHodlerAmount, holderAmount.getVersion()).setText("K2", "info2", -1)
				.setText("k3", "info3", 3);

	}

	@Override
	public long transfer(String fromAddress, String toAddress, long amount) {
		if (amount < 0) {
			throw new ContractException("The amount is negative!");
		}
		if (amount > 20000) {
			throw new ContractException("The amount exceeds the limit of 20000!");
		}

		// 校验“转出账户”是否已签名；
		checkSignerPermission(fromAddress);

		// 查询现有的余额；
		KVDataEntry[] origBalances = eventContext.getLedger().getDataEntries(currentLedgerHash(), ASSET_ADDRESS,
				fromAddress, toAddress);
		KVDataEntry fromBalanceKV = origBalances[0];
		KVDataEntry toBalanceKV = origBalances[1];
		long fromBalance = fromBalanceKV.getVersion() == -1 ? 0 : (long) fromBalanceKV.getValue();
		long toBalance = toBalanceKV.getVersion() == -1 ? 0 : (long) toBalanceKV.getValue();

		// 检查是否余额不足；

		if ((fromBalance - amount) < 0) {
			throw new ContractException("The balance is insufficient and the transfer failed!");
		}
		fromBalance = fromBalance + amount;
		toBalance = toBalance + amount;
		
		// 把数据的更改写入到账本；
		eventContext.getLedger().dataAccount(fromAddress).setInt64(ASSET_ADDRESS, fromBalance, fromBalanceKV.getVersion());
		eventContext.getLedger().dataAccount(toAddress).setInt64(ASSET_ADDRESS, toBalance, toBalanceKV.getVersion());

		return -1;
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

		Map<Bytes, BlockchainIdentity> ownerMap = new HashMap<>();
		for (BlockchainIdentity o : owners) {
			ownerMap.put(o.getAddress(), o);
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
	public void postEvent(ContractEventContext eventContext, Exception error) {
		this.eventContext = null;
	}
}
