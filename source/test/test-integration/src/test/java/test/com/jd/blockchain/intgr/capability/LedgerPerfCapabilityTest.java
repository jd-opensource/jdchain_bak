/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: test.com.jd.blockchain.intgr.perf.LedgerPerfCapabilityTest
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/12/6 上午11:15
 * Description:
 */
package test.com.jd.blockchain.intgr.capability;

import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.ledger.LedgerInitOperation;
import com.jd.blockchain.ledger.UserRegisterOperation;
import org.junit.Test;
import test.com.jd.blockchain.intgr.perf.LedgerPerformanceTest;

/**
 *
 * @author shaozhuguang
 * @create 2018/12/6
 * @since 1.0.0
 */

public class LedgerPerfCapabilityTest {

    private final String CONSENSUS = "-mq";

    private final String SILENT = "-silent";

    private final String ROCKSDB = "-rocksdb";

    private final String USERTEST = "-usertest";

    static {
        DataContractRegistry.register(LedgerInitOperation.class);
        DataContractRegistry.register(UserRegisterOperation.class);
    }

    @Test
    public void testKvStorage4Memory() {
        LedgerPerformanceTest.test(new String[]{SILENT});
    }

    @Test
    public void testUserRegister4Memory() {
        LedgerPerformanceTest.test(new String[]{USERTEST, SILENT});
    }

    @Test
    public void testKvStorage4Rocksdb() {
        LedgerPerformanceTest.test(new String[]{ROCKSDB, SILENT});
    }

    @Test
    public void testUserRegister4Rocksdb() {
        LedgerPerformanceTest.test(new String[]{USERTEST, ROCKSDB, SILENT});
    }

    public void testUserRegister4Redis() {
        //test example not verify redis
        LedgerPerformanceTest.test(new String[]{"-redis", "-usertest"});
    }

    public void testContract(){LedgerPerformanceTest.test(new String[]{"-contract", "-silent"});}
}