package com.jd.blockchain.sdk.samples;

import com.jd.blockchain.ledger.*;

public class SDK_RoleConfig_Demo extends SDK_Base_Demo {

	public static void main(String[] args) {
		new SDK_RoleConfig_Demo().executeRoleConfig();
	}

	public void executeRoleConfig() {

		// 定义交易模板
		TransactionTemplate txTpl = blockchainService.newTransaction(ledgerHash);

		// 新增加一个角色

		txTpl.security().roles().configure("MyRole")
				.enable(LedgerPermission.APPROVE_TX, LedgerPermission.CONSENSUS_TX)
				.disable(TransactionPermission.CONTRACT_OPERATION);
		TransactionResponse txResp = commit(txTpl);

		System.out.println(txResp.isSuccess());

	}
}
