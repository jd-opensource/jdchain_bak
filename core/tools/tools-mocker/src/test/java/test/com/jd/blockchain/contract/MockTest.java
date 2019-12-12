package test.com.jd.blockchain.contract;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.BlockchainKeyGenerator;
import com.jd.blockchain.ledger.BlockchainKeypair;
import com.jd.blockchain.ledger.KVDataEntry;
import com.jd.blockchain.mocker.MockerNodeContext;
import com.jd.blockchain.mocker.config.MockerConstant;
import com.jd.blockchain.mocker.contracts.WriteContract;
import com.jd.blockchain.mocker.contracts.WriteContractImpl;
import org.junit.Test;

public class MockTest {

    @Test
    public void test() {
        // 准备环境
        BlockchainKeypair blockchainKeypair = BlockchainKeyGenerator.getInstance().generate();
        MockerNodeContext mockerNodeContext =
                new MockerNodeContext(MockerConstant.DEFAULT_LEDGER_SEED)
                .participants("zhangsan", blockchainKeypair)
                .build();
        HashDigest ledgerHash = mockerNodeContext.getLedgerHash();

        System.out.printf("LedgerHash = %s \r\n", ledgerHash.toBase58());
        System.out.printf("LedgerSeed = %s \r\n", mockerNodeContext.getLedgerSeed());

        // 注册用户
        String userAddress = mockerNodeContext.registerUser(BlockchainKeyGenerator.getInstance().generate());
        System.out.printf("----- 注册用户地址 {%s} -----\r\n", userAddress);

        // 注册数据账户
        String dataAccountAddress = mockerNodeContext.registerDataAccount(BlockchainKeyGenerator.getInstance().generate());
        System.out.printf("----- 注册数据账户地址 {%s} -----\r\n", dataAccountAddress);

        WriteContract writeContract = new WriteContractImpl();

        // 发布合约
        writeContract = mockerNodeContext.deployContract(writeContract);

        writeContract.print("张三");

        String key = "Hello", value = "World";

        writeContract.writeKv(dataAccountAddress, key, value);

        // 查询
        KVDataEntry[] kvDataEntries = mockerNodeContext.getDataEntries(ledgerHash, dataAccountAddress, key);

        for (KVDataEntry kvDataEntry : kvDataEntries) {
            System.out.printf("Key = %s, Value = %s \r\n", kvDataEntry.getKey(), kvDataEntry.getValue());
        }
    }
}
