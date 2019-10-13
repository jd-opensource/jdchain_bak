package com.jd.blockchain.sdk.samples;

import com.jd.blockchain.contract.RandomContract;
import com.jd.blockchain.contract.TransferContract;
import com.jd.blockchain.ledger.*;
import com.jd.blockchain.transaction.GenericValueHolder;
import com.jd.blockchain.transaction.LongValueHolder;
import com.jd.blockchain.utils.Bytes;

import java.util.Random;

import static com.jd.blockchain.sdk.samples.SDKDemo_Constant.readChainCodes;
import static com.jd.blockchain.transaction.ContractReturnValue.decode;

public class SDK_Contract_Random_Demo extends SDK_Base_Demo {

	public static void main(String[] args) throws Exception {
		new SDK_Contract_Random_Demo().executeContract();
	}

	public void executeContract() throws Exception {

		// 发布jar包
		// 定义交易模板
		TransactionTemplate txTpl = blockchainService.newTransaction(ledgerHash);

		// 将jar包转换为二进制数据
		byte[] contractCode = readChainCodes("random.jar");

		// 生成一个合约账号
		BlockchainKeypair contractDeployKey = BlockchainKeyGenerator.getInstance().generate();

		// 生成发布合约操作
		txTpl.contracts().deploy(contractDeployKey.getIdentity(), contractCode);

		// 生成预发布交易；
		PreparedTransaction ptx = txTpl.prepare();

		// 对交易进行签名
		ptx.sign(adminKey);

		// 提交并等待共识返回；
		TransactionResponse txResp = ptx.commit();

		// 获取合约地址
		Bytes contractAddress = contractDeployKey.getAddress();

		// 打印交易返回信息
		System.out.printf("Tx[%s] -> BlockHeight = %s, BlockHash = %s, isSuccess = %s, ExecutionState = %s \r\n",
				txResp.getContentHash().toBase58(), txResp.getBlockHeight(), txResp.getBlockHash().toBase58(),
				txResp.isSuccess(), txResp.getExecutionState());

		// 打印合约地址
		System.out.printf("ContractAddress = %s \r\n", contractAddress.toBase58());

		String result = create("LdeNzfhZd2qiBRk3YrEX6GZgiVRZJaf3MKJAY", "zhangshuang", "jingdong", contractAddress);


		Thread.sleep(5000);
		System.out.println(result);
	}

	private String readAll(String address, String account, Bytes contractAddress) {
		TransactionTemplate txTpl = blockchainService.newTransaction(ledgerHash);
		// 使用合约创建
		TransferContract transferContract = txTpl.contract(contractAddress, TransferContract.class);
		GenericValueHolder<String> result = decode(transferContract.readAll(address, account));
		commit(txTpl);
		return result.get();
	}

	private long readByContract(String address, String account, Bytes contractAddress) {
		TransactionTemplate txTpl = blockchainService.newTransaction(ledgerHash);
		// 使用合约创建
		TransferContract transferContract = txTpl.contract(contractAddress, TransferContract.class);
		LongValueHolder result = decode(transferContract.read(address, account));
		commit(txTpl);
		return result.get();
	}

	private long readByKvOperation(String address, String account) {
		KVDataEntry[] kvDataEntries = blockchainService.getDataEntries(ledgerHash, address, account);
		if (kvDataEntries == null || kvDataEntries.length == 0) {
			throw new IllegalStateException(String.format("Ledger %s Service inner Error !!!", ledgerHash.toBase58()));
		}
		KVDataEntry kvDataEntry = kvDataEntries[0];
		if (kvDataEntry.getVersion() == -1) {
			return 0L;
		}
		return (long) (kvDataEntry.getValue());
	}

	private String transfer(String address, String from, String to, long money, Bytes contractAddress) {
		TransactionTemplate txTpl = blockchainService.newTransaction(ledgerHash);
		// 使用合约创建
		TransferContract transferContract = txTpl.contract(contractAddress, TransferContract.class);
		GenericValueHolder<String> result = decode(transferContract.transfer(address, from, to, money));
		commit(txTpl);
		return result.get();
	}

	private BlockchainKeypair createDataAccount() {
		// 首先注册一个数据账户
		BlockchainKeypair newDataAccount = BlockchainKeyGenerator.getInstance().generate();

		TransactionTemplate txTpl = blockchainService.newTransaction(ledgerHash);
		txTpl.dataAccounts().register(newDataAccount.getIdentity());
		commit(txTpl);
		return newDataAccount;
	}

	private String create(String address, String account, String value, Bytes contractAddress) {
		TransactionTemplate txTpl = blockchainService.newTransaction(ledgerHash);
		// 使用合约创建
		RandomContract randomContract = txTpl.contract(contractAddress, RandomContract.class);
		GenericValueHolder<String> result = decode(randomContract.putAndGet(address, account, value));
		commit(txTpl);
		return result.get();
	}
}
