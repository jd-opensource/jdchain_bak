package test.com.jd.blockchain.contract;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.mocker.MockerNodeContext;
import com.jd.blockchain.mocker.contracts.AccountContract;
import com.jd.blockchain.mocker.contracts.AccountContractImpl;
import org.junit.Before;
import org.junit.Test;

public class AccountMockerTest {

    String accountFrom = "zhangsan";

    String accountTo = "lisi";

    MockerNodeContext mockerNodeContext = null;

    HashDigest ledgerHash = null;

    @Before
    public void init() {
        mockerNodeContext = new MockerNodeContext().build();
        ledgerHash = mockerNodeContext.getLedgerHash();
    }

    @Test
    public void test() {
        // 首先创建一个数据账户
        String address = mockerNodeContext.registerDataAccount();

        // 处理合约
        AccountContract accountContract = new AccountContractImpl();

        // 发布合约
        accountContract = mockerNodeContext.deployContract(accountContract);

        //首先创建账户
        accountContract.create(address, accountFrom, 1000L);

        accountContract.create(address, accountTo, 1000L);

//        accountContract.print(address, accountFrom, accountTo);

        // 开始转账
        accountContract.transfer(address, accountFrom, accountTo, 500);

        // 打印转账后结果
        accountContract.print(address, accountFrom, accountTo);
    }
}
