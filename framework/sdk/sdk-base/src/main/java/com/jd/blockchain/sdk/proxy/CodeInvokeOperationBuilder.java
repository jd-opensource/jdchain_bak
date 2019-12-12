//package com.jd.blockchain.sdk.proxy;
//
//import com.jd.blockchain.ledger.CodeDeployOperation;
//import com.jd.blockchain.ledger.ContractEventOperation;
//import com.jd.blockchain.ledger.MapStateOperationBuilder;
//import com.jd.blockchain.ledger.ScriptInvokingOperation;
//import com.jd.blockchain.ledger.data.BlockchainOperationFactory;
//import com.jd.blockchain.ledger.data.OpBlob;
//import com.jd.blockchain.ledger.data.PrivilegeSettingOperationBuilder;
//
//import my.utils.io.ByteArray;
//
//public class CodeInvokeOperationBuilder implements ScriptInvokingOperation {
//	private TxTemplate txTemp;
//
//	public CodeInvokeOperationBuilder(TxTemplate txTemp) {
//		this.txTemp = txTemp;
//	}
//
//	@Override
//	public void invoke(String address, String[] args) {
//		ContractEventOperation operation = BlockchainOperationFactory.getInstance().event(address, new ByteArray[] {});
//
//		txTemp.addOperation((OpBlob) operation.getOperation());
//	}
//
//	/**
//	 * 配置特权操作；
//	 *
//	 * @param accountAddress
//	 *            账户地址；
//	 * @return
//	 */
//	@Override
//	public PrivilegeSettingOperationBuilder configPrivilege(String accountAddress) {
//		return null;
//	}
//
//	/**
//	 *
//	 * @param accountAddress
//	 *            要操作的账户地址；
//	 * @return
//	 */
//	@Override
//	public MapStateOperationBuilder updateState(String accountAddress) {
//		return null;
//	}
//
//	/**
//	 * 执行定义账户的合约脚本的操作；
//	 *
//	 * @param accountAddress
//	 *            要操作的账户地址；
//	 * @return
//	 */
//	@Override
//	public CodeDeployOperation defineScript(String accountAddress) {
//		return null;
//	}
//
//	/**
//	 * 执行调用账户的合约脚本的方法的操作；
//	 *
//	 * @param accountAddress
//	 *            要操作的账户地址；
//	 * @return
//	 */
//	@Override
//	public ScriptInvokingOperation executeScript(String accountAddress) {
//		return null;
//	}
//}
