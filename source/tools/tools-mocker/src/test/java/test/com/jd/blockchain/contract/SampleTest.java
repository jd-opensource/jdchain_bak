package test.com.jd.blockchain.contract;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.KVDataEntry;
import com.jd.blockchain.mocker.MockerNodeContext;
import com.jd.blockchain.mocker.contracts.WriteContract;
import com.jd.blockchain.mocker.contracts.WriteContractImpl;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SampleTest {

    MockerNodeContext mockerNodeContext = null;

    HashDigest ledgerHash = null;

    @Before
    public void init() {
        mockerNodeContext = new MockerNodeContext().build();
        ledgerHash = mockerNodeContext.getLedgerHash();
    }

    @Test
    public void writeTest() {

        String key = "MyKey-" + System.currentTimeMillis(), value = "JDChain";

        WriteContract writeContract = new WriteContractImpl();

        String dataAccountAddress = mockerNodeContext.registerDataAccount();

        writeContract = mockerNodeContext.deployContract(writeContract);

        String result = writeContract.writeKv(dataAccountAddress, key, value);

        System.out.println(result);

        // 查询结果
        KVDataEntry[] dataEntries = mockerNodeContext.getDataEntries(ledgerHash, dataAccountAddress, key);

        for (KVDataEntry kvDataEntry : dataEntries) {
            assertEquals(key, kvDataEntry.getKey());
            assertEquals(value, kvDataEntry.getValue());
            System.out.printf("Key = %s, Value = %s \r\n", kvDataEntry.getKey(), kvDataEntry.getValue());
        }
    }
}
