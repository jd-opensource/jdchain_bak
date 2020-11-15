package com.jd.blockchain.sdk.samples;

import com.jd.blockchain.ledger.PreparedTransaction;
import com.jd.blockchain.ledger.TransactionResponse;
import com.jd.blockchain.ledger.TransactionTemplate;
import com.jd.blockchain.utils.Property;

import java.util.ArrayList;
import java.util.List;

public class SDK_ConsensusSettings_Update_Demo extends SDK_Base_Demo {

	public static void main(String[] args) {
		new SDK_ConsensusSettings_Update_Demo().updateSettings();
	}

	public void updateSettings() {

		List<Property> properties = new ArrayList<Property>();

		// 修改bftsmart.conf配置文件中的选项；
		properties.add(new Property("system.communication.useSenderThread",  "false"));

		Property[] propertiesArray = properties.toArray(new Property[properties.size()]);

		TransactionTemplate txTpl = blockchainService.newTransaction(ledgerHash);

		txTpl.settings().update(propertiesArray);

		// TX 准备就绪；
		PreparedTransaction prepTx = txTpl.prepare();

		// 使用私钥进行签名；
		prepTx.sign(adminKey);

		// 提交交易；
		TransactionResponse transactionResponse = prepTx.commit();

		System.out.println(transactionResponse.isSuccess());

	}
}
