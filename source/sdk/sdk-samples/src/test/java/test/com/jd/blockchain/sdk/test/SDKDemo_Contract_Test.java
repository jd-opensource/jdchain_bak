package test.com.jd.blockchain.sdk.test;

import static com.jd.blockchain.sdk.samples.SDKDemo_Constant.readChainCodes;
import static com.jd.blockchain.transaction.ContractReturns.decode;

import org.junit.Before;
import org.junit.Test;

import com.jd.blockchain.contract.TransferContract;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.PrivKey;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.ledger.BlockchainKeyGenerator;
import com.jd.blockchain.ledger.BlockchainKeypair;
import com.jd.blockchain.ledger.PreparedTransaction;
import com.jd.blockchain.ledger.TransactionResponse;
import com.jd.blockchain.ledger.TransactionTemplate;
import com.jd.blockchain.sdk.BlockchainService;
import com.jd.blockchain.sdk.client.GatewayServiceFactory;
import com.jd.blockchain.sdk.samples.SDKDemo_Constant;
import com.jd.blockchain.tools.keygen.KeyGenCommand;
import com.jd.blockchain.transaction.ContractReturns.ReturnLongValue;
import com.jd.blockchain.transaction.ContractReturns.ReturnValue;
import com.jd.blockchain.utils.Bytes;

public class SDKDemo_Contract_Test {

	private BlockchainKeypair adminKey;

	private HashDigest ledgerHash;

	private BlockchainService blockchainService;

	@Before
	public void init() {
		// 生成连接网关的账号
		PrivKey privKey = KeyGenCommand.decodePrivKeyWithRawPassword(SDKDemo_Constant.PRIV_KEYS[0],
				SDKDemo_Constant.PASSWORD);

		PubKey pubKey = KeyGenCommand.decodePubKey(SDKDemo_Constant.PUB_KEYS[0]);

		adminKey = new BlockchainKeypair(pubKey, privKey);

		// 连接网关
		GatewayServiceFactory serviceFactory = GatewayServiceFactory.connect(SDKDemo_Constant.GW_IPADDR,
				SDKDemo_Constant.GW_PORT, false, adminKey);

		blockchainService = serviceFactory.getBlockchainService();

		HashDigest[] ledgerHashs = blockchainService.getLedgerHashs();

		ledgerHash = ledgerHashs[0];
	}

	@Test
	public void testContract() {

		// 发布jar包
		// 定义交易；
		TransactionTemplate txTpl = blockchainService.newTransaction(ledgerHash);

		byte[] contractCode = readChainCodes("transfer.jar");

		// 生成一个合约账号
		BlockchainKeypair contractDeployKey = BlockchainKeyGenerator.getInstance().generate();

		txTpl.contracts().deploy(contractDeployKey.getIdentity(), contractCode);

		// 签名；
		PreparedTransaction ptx = txTpl.prepare();

		ptx.sign(adminKey);

		// 提交并等待共识返回；
		TransactionResponse txResp = ptx.commit();

		System.out.println(txResp.isSuccess());

		// 首先注册一个数据账户
		BlockchainKeypair dataAccount = createDataAccount();

		String dataAddress = dataAccount.getAddress().toBase58();

		Bytes contractAddress = contractDeployKey.getAddress();

		// 创建两个账号：
		String account0 = "jd_zhangsan", account1 = "jd_lisi";
		long account0Money = 3000L, account1Money = 2000L;
		// 创建两个账户
		// 使用KV创建一个账户
		System.out.println(create(dataAddress, account0, account0Money, false, null));
		// 使用合约创建一个账户
		System.out.println(create(dataAddress, account1, account1Money, true, contractAddress));

		// 转账，使得双方钱达到一致
		System.out.println(transfer(dataAddress, account0, account1, 500L, contractAddress));

		// 读取当前结果
		System.out.println(read(dataAddress, account0, contractAddress));
		System.out.println(read(dataAddress, account1, contractAddress));

		// 读取账号历史信息
		System.out.println(readAll(dataAddress, account0, contractAddress));
		System.out.println(readAll(dataAddress, account1, contractAddress));
	}

	private String readAll(String address, String account, Bytes contractAddress) {
		TransactionTemplate txTpl = blockchainService.newTransaction(ledgerHash);
		// 使用合约创建
		TransferContract transferContract = txTpl.contract(contractAddress, TransferContract.class);
		ReturnValue<String> result = decode(transferContract.readAll(address, account));
		commit(txTpl);
		return result.get();
	}

	private long read(String address, String account, Bytes contractAddress) {
		TransactionTemplate txTpl = blockchainService.newTransaction(ledgerHash);
		// 使用合约创建
		TransferContract transferContract = txTpl.contract(contractAddress, TransferContract.class);
		ReturnLongValue result = decode(transferContract.read(address, account));
		commit(txTpl);
		return result.get();
	}

	private String transfer(String address, String from, String to, long money, Bytes contractAddress) {
		TransactionTemplate txTpl = blockchainService.newTransaction(ledgerHash);
		// 使用合约创建
		TransferContract transferContract = txTpl.contract(contractAddress, TransferContract.class);
		ReturnValue<String> result = decode(transferContract.transfer(address, from, to, money));
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

	private String create(String address, String account, long money, boolean useContract, Bytes contractAddress) {
		TransactionTemplate txTpl = blockchainService.newTransaction(ledgerHash);
		if (useContract) {
			// 使用合约创建
			TransferContract transferContract = txTpl.contract(contractAddress, TransferContract.class);
			ReturnValue<String> result = decode(transferContract.create(address, account, money));
			commit(txTpl);
			return result.get();
		} else {
			// 通过KV创建
			txTpl.dataAccount(address).setInt64(account, money, -1);
			TransactionResponse txResp = commit(txTpl);
			return account + money;
		}
	}

	private TransactionResponse commit(TransactionTemplate txTpl) {
		PreparedTransaction ptx = txTpl.prepare();
		ptx.sign(adminKey);
		return ptx.commit();
	}
}
