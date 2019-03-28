//package com.jd.blockchain.ledger;
//
//import com.jd.blockchain.ledger.data.AccountUpdateOperationBuilder;
//
///**
// * 合约代码部署操作；
// * 
// * @author huanghaiquan
// *
// */
//public interface CodeDeployOperation extends AccountUpdateOperationBuilder {
//
//	/**
//	 * 修改脚本；
//	 * 
//	 * @param code
//	 *            合约代码；
//	 * @param codeVersion
//	 *            预期的当前的代码的版本；如果指定为 -1，则不进行版本检查；
//	 */
//	void set(BlockchainIdentity id, String code, long codeVersion);
//
//}
