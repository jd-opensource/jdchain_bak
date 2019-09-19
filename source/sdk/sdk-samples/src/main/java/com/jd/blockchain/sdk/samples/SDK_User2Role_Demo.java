package com.jd.blockchain.sdk.samples;

import com.jd.blockchain.ledger.*;
import com.jd.blockchain.utils.Bytes;

public class SDK_User2Role_Demo extends SDK_Base_Demo {

	public static void main(String[] args) {
		new SDK_User2Role_Demo().executeUser2Role();
	}

	public void executeUser2Role() {

		// 定义交易模板
		TransactionTemplate txTpl = blockchainService.newTransaction(ledgerHash);

		// 注册一个用户
		BlockchainKeypair user = createUser();

		Bytes userAddress = user.getAddress();
		// 获取数据账户地址
		System.out.printf("UserAddress = %s \r\n", userAddress.toBase58());


		txTpl.security().authorziations().forUser(user.getIdentity())
				.authorize("MYROLE")
				.setPolicy(RolesPolicy.UNION)
				.unauthorize("MYROLE");
		TransactionResponse txResp = commit(txTpl);

		System.out.println(txResp.isSuccess());

	}

	private BlockchainKeypair createUser() {
		// 首先注册一个数据账户
		BlockchainKeypair newUser = BlockchainKeyGenerator.getInstance().generate();

		TransactionTemplate txTpl = blockchainService.newTransaction(ledgerHash);
		txTpl.users().register(newUser.getIdentity());
		commit(txTpl);
		return newUser;
	}
}
