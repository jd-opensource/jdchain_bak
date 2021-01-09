package com.jdchain.samples.sdk;

import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.BlockchainIdentity;
import com.jd.blockchain.ledger.BytesValue;
import com.jd.blockchain.ledger.ContractInfo;
import com.jd.blockchain.ledger.DataAccountInfo;
import com.jd.blockchain.ledger.Event;
import com.jd.blockchain.ledger.KVDataVO;
import com.jd.blockchain.ledger.KVInfoVO;
import com.jd.blockchain.ledger.LedgerAdminInfo;
import com.jd.blockchain.ledger.LedgerBlock;
import com.jd.blockchain.ledger.LedgerInfo;
import com.jd.blockchain.ledger.LedgerMetadata;
import com.jd.blockchain.ledger.LedgerPermission;
import com.jd.blockchain.ledger.LedgerTransaction;
import com.jd.blockchain.ledger.ParticipantNode;
import com.jd.blockchain.ledger.PrivilegeSet;
import com.jd.blockchain.ledger.TransactionPermission;
import com.jd.blockchain.ledger.TransactionState;
import com.jd.blockchain.ledger.TypedKVEntry;
import com.jd.blockchain.ledger.UserInfo;
import com.jd.blockchain.ledger.UserPrivilegeSet;

import utils.codec.Base58Utils;
import utils.io.BytesUtils;

import org.junit.Assert;
import org.junit.Test;

/**
 * 查询样例
 */
public class QuerySample extends SampleBase {

    HashDigest sampleHash = Crypto.resolveAsHashDigest(Base58Utils.decode("j5sTuEAWmLWKFwXgpdUCxbQN1XmZfkQdC94UT2AqQEt7hp"));
    String sampleAddress = "LdeNr7H1CUbqe3kWjwPwiqHcmd86zEQz2VRye";
    String sampleKey = "sample-key";
    String sampleEvent = "sample-event";
    long sampleVersion = 0;
    String sampleRoleName = "SAMPLE-ROLE";

    /**
     * 查询账本列表
     */
    @Test
    public void getLedgerHashs() {
        HashDigest[] digests = blockchainService.getLedgerHashs();
        for (HashDigest digest : digests) {
            System.out.println(digest.toBase58());
        }
    }

    /**
     * 查询账本信息，区块hash，区块高度
     */
    @Test
    public void getLedger() {
        LedgerInfo ledgerInfo = blockchainService.getLedger(ledger);
        System.out.println(ledgerInfo.getHash());
    }

    /**
     * 查询账本信息，元数据，参与方，账本配置等
     */
    @Test
    public void getLedgerAdminInfo() {
        LedgerAdminInfo adminInfo = blockchainService.getLedgerAdminInfo(ledger);
        System.out.println(adminInfo.getParticipantCount());
    }

    /**
     * 查询共识参与方
     */
    @Test
    public void getConsensusParticipants() {
        ParticipantNode[] nodes = blockchainService.getConsensusParticipants(ledger);
        for (ParticipantNode node : nodes) {
            System.out.println("ID: " + node.getId());
            System.out.println("Address: " + node.getAddress().toString());
            System.out.println("PubKey: " + node.getPubKey().toString());
            System.out.println("State: " + node.getParticipantNodeState());
        }
    }

    /**
     * 查询账本的元数据
     */
    @Test
    public void getLedgerMetadata() {
        LedgerMetadata metadata = blockchainService.getLedgerMetadata(ledger);
        System.out.println(Base58Utils.encode(metadata.getSeed()));
        System.out.println(metadata.getParticipantsHash().toBase58());
        System.out.println(metadata.getSettingsHash().toBase58());
    }

    /**
     * 根据高度查询区块
     */
    @Test
    public void getBlockByHeight() {
        LedgerBlock block1 = blockchainService.getBlock(ledger, -1);
        LedgerBlock block2 = blockchainService.getBlock(ledger, Integer.MAX_VALUE);
        Assert.assertNotNull(block1);
        Assert.assertEquals(block1.getHash(), block2.getHash());
        LedgerBlock block3 = blockchainService.getBlock(ledger, 0);
        Assert.assertTrue(block1.getHeight() >= block3.getHeight());
    }

    /**
     * 根据hash查询区块
     */
    @Test
    public void getBlockByHash() {
        LedgerBlock block = blockchainService.getBlock(ledger, sampleHash);
        Assert.assertNull(block);
    }

    /**
     * 查询某一高度（包括）之前所有交易数
     */
    @Test
    public void getTransactionCountByHeight() {
        long count = blockchainService.getTransactionCount(ledger, -1);
        Assert.assertEquals(0, count);
        count = blockchainService.getTransactionCount(ledger, 1);
        Assert.assertNotEquals(0, count);
    }

    /**
     * 查询某一区块（包括）之前所有交易数
     */
    @Test
    public void getTransactionCountByHash() {
        long count = blockchainService.getTransactionCount(ledger, sampleHash);
        Assert.assertEquals(0, count);
    }

    /**
     * 查询交易总数
     */
    @Test
    public void getTransactionTotalCount() {
        long count = blockchainService.getTransactionTotalCount(ledger);
        Assert.assertNotEquals(0, count);
    }

    /**
     * 查询某一高度（包括）之前数据账户数
     */
    @Test
    public void getDataAccountCountByHeight() {
        long count = blockchainService.getDataAccountCount(ledger, 0);
        Assert.assertEquals(0, count);
    }

    /**
     * 查询某一区块（包括）之前数据账户数
     */
    @Test
    public void getDataAccountCountByHash() {
        long count = blockchainService.getDataAccountCount(ledger, sampleHash);
        Assert.assertEquals(0, count);
    }

    /**
     * 查询数据账户总数
     */
    @Test
    public void getDataAccountTotalCount() {
        long count = blockchainService.getDataAccountTotalCount(ledger);
        System.out.println("Total DataAccount count: " + count);
    }

    /**
     * 查询某一高度（包括）之前用户数
     */
    @Test
    public void getUserCountByHeight() {
        long count = blockchainService.getUserCount(ledger, 0);
        Assert.assertEquals(4, count);
    }

    /**
     * 查询某一区块（包括）之前用户数
     */
    @Test
    public void getUserCountByHash() {
        long count = blockchainService.getUserCount(ledger, sampleHash);
        Assert.assertEquals(0, count);
    }

    /**
     * 查询用户总数
     */
    @Test
    public void getUserTotalCount() {
        long count = blockchainService.getUserTotalCount(ledger);
        System.out.println("Total User count: " + count);
    }

    /**
     * 查询某一高度（包括）之前合约数
     */
    @Test
    public void getContractCountByHeight() {
        long count = blockchainService.getContractCount(ledger, 0);
        Assert.assertEquals(0, count);
    }

    /**
     * 查询某一区块（包括）之前合约数
     */
    @Test
    public void getContractCountByHash() {
        long count = blockchainService.getContractCount(ledger, sampleHash);
        Assert.assertEquals(0, count);
    }

    /**
     * 查询合约总数
     */
    @Test
    public void getContractTotalCount() {
        long count = blockchainService.getContractTotalCount(ledger);
        System.out.println("Total Contract count: " + count);
    }

    /**
     * 分页查询交易某一高度（包括）之前的所有交易
     */
    @Test
    public void getTransactionsByHeight() {
        LedgerTransaction[] txs = blockchainService.getTransactions(ledger, 0, 0, 1);
        Assert.assertEquals(1, txs.length);
    }

    /**
     * 分页查询交易某一区块（包括）之前的所有交易
     */
    @Test
    public void getTransactionsByHash() {
        LedgerTransaction[] txs = blockchainService.getTransactions(ledger,
                sampleHash, 0, 1);
        Assert.assertNull(txs);
    }

    /**
     * 分页查询某一高度中的交易
     */
    @Test
    public void getAdditionalTransactionsByHeight() {
        LedgerTransaction[] txs = blockchainService.getTransactions(ledger, 0, 0, 1);
        Assert.assertEquals(1, txs.length);
    }

    /**
     * 分页查询某一区块中的交易
     */
    @Test
    public void getAdditionalTransactionsByHash() {
        LedgerTransaction[] txs = blockchainService.getTransactions(ledger, sampleHash, 0, 1);
        Assert.assertNull(txs);
    }

    /**
     * 根据交易hash查询交易详情
     */
    @Test
    public void getTransactionByContentHash() {
        LedgerTransaction tx = blockchainService.getTransactionByContentHash(ledger, sampleHash);
        Assert.assertNull(tx);
    }

    /**
     * 根据交易hash查询交易状态
     */
    @Test
    public void getTransactionStateByContentHash() {
        TransactionState state = blockchainService.getTransactionStateByContentHash(ledger, sampleHash);
        Assert.assertNull(state);
    }

    /**
     * 根据地址查询用户信息
     */
    @Test
    public void getUser() {
        UserInfo user = blockchainService.getUser(ledger, sampleAddress);
        if (null != user) {
            System.out.println(user.getAddress().toString());
        }
    }

    /**
     * 根据地址查询数据账户
     */
    @Test
    public void getDataAccount() {
        DataAccountInfo dataAccount = blockchainService.getDataAccount(ledger, sampleAddress);
        if (null != dataAccount) {
            System.out.println(dataAccount.getAddress().toString());
        }
    }

    /**
     * 根据地址和键查询KV信息（只包含最高数据版本）
     */
    @Test
    public void getDataEntriesByKey() {
        TypedKVEntry[] kvs = blockchainService.getDataEntries(ledger, sampleAddress, sampleKey);
        for (TypedKVEntry kv : kvs) {
            System.out.println(kv.getKey() + ":" + kv.getVersion() + ":" + kv.getValue());
        }
    }

    /**
     * 根据地址和指定键及数据版本查询KV信息
     */
    @Test
    public void getDataEntriesWithKeyAndVersion() {
        TypedKVEntry[] kvs = blockchainService.getDataEntries(ledger, sampleAddress, new KVInfoVO(new KVDataVO[]{new KVDataVO(sampleKey, new long[]{-1})}));
        for (TypedKVEntry kv : kvs) {
            System.out.println(kv.getKey() + ":" + kv.getVersion() + ":" + kv.getValue());
        }
    }

    /**
     * 查询数据账户KV总数
     */
    @Test
    public void getDataEntriesTotalCount() {
        long count = blockchainService.getDataEntriesTotalCount(ledger, sampleAddress);
        System.out.println(count);
    }

    /**
     * 分页查询指定数据账户KV数据
     */
    @Test
    public void getDataEntries() {
        TypedKVEntry[] kvs = blockchainService.getDataEntries(ledger, sampleAddress, 0, 1);
        for (TypedKVEntry kv : kvs) {
            System.out.println(kv.getKey() + ":" + kv.getVersion() + ":" + kv.getValue());
        }
    }

    /**
     * 查询合约信息
     */
    @Test
    public void getContract() {
        ContractInfo contract = blockchainService.getContract(ledger, sampleAddress);
        if (null != contract) {
            System.out.println(contract.getAddress().toString());
        }
    }

    /**
     * 分页查询指定系统事件名下所有消息
     */
    @Test
    public void getSystemEvents() {
        Event[] events = blockchainService.getSystemEvents(ledger, sampleEvent, 0, 1);
        Assert.assertTrue(null == events || events.length == 0);
    }

    /**
     * 查询系统事件名总数
     */
    @Test
    public void getSystemEventNameTotalCount() {
        long count = blockchainService.getSystemEventNameTotalCount(ledger);
        Assert.assertEquals(0, count);
    }

    /**
     * 分页查询系统事件名
     */
    @Test
    public void getSystemEventNames() {
        String[] names = blockchainService.getSystemEventNames(ledger, 0, 1);
        Assert.assertEquals(0, names.length);
    }

    /**
     * 查询指定系统事件名最新事件
     */
    @Test
    public void getLatestEvent() {
        Event event = blockchainService.getLatestEvent(ledger, sampleEvent);
        Assert.assertNull(event);
    }

    /**
     * 获取指定系统事件名下所有事件
     */
    @Test
    public void getSystemEventsTotalCount() {
        long count = blockchainService.getSystemEventsTotalCount(ledger, sampleEvent);
        Assert.assertEquals(0, count);
    }

    /**
     * 分页查询用户事件账户
     *
     * @return
     */
    @Test
    public void getUserEventAccounts() {
        BlockchainIdentity[] ids = blockchainService.getUserEventAccounts(ledger, 0, 1);
        System.out.println(ids.length);
    }

    /**
     * 获取用户事件账户
     */
    @Test
    public void getUserEventAccount() {
        BlockchainIdentity id = blockchainService.getUserEventAccount(ledger, sampleAddress);
        if (null != id) {
            System.out.println(id.getAddress().toString());
        }
    }

    /**
     * 获取用户事件账户总数
     */
    @Test
    public void getUserEventAccountTotalCount() {
        long count = blockchainService.getUserEventAccountTotalCount(ledger);
        System.out.println(count);
    }

    /**
     * 获取指定用户事件账户下事件名数
     */
    @Test
    public void getUserEventNameTotalCount() {
        long count = blockchainService.getUserEventNameTotalCount(ledger, sampleAddress);
        System.out.println(count);
    }

    /**
     * 分页查询指定用户事件账户下事件名
     */
    @Test
    public void getUserEventNames() {
        String[] names = blockchainService.getUserEventNames(ledger, sampleAddress, 0, 1);
        for (String name : names) {
            System.out.println(name);
        }
    }

    /**
     * 获取指定用户事件账户指定事件名下最新事件
     */
    @Test
    public void getLatestUserEvent() {
        Event event = blockchainService.getLatestEvent(ledger, sampleAddress, sampleEvent);
        if (null != event) {
            BytesValue content = event.getContent();
            switch (content.getType()) {
                case TEXT:
                case XML:
                case JSON:
                    System.out.println(event.getName() + ":" + event.getSequence() + ":" + content.getBytes().toUTF8String());
                    break;
                case INT64:
                case TIMESTAMP:
                    System.out.println(event.getName() + ":" + event.getSequence() + ":" + BytesUtils.toLong(content.getBytes().toBytes()));
                    break;
                default: // byte[], Bytes
                    System.out.println(event.getName() + ":" + event.getSequence() + ":" + content.getBytes().toBase58());
                    break;
            }
        }
    }

    /**
     * 获取指定用户事件账户指定事件名下事件总数
     */
    @Test
    public void getUserEventsTotalCount() {
        long count = blockchainService.getUserEventsTotalCount(ledger, sampleAddress, sampleEvent);
        System.out.println(count);
    }

    /**
     * 分页查询指定用户事件账户指定事件名下事件
     */
    @Test
    public void getUserEvents() {
        Event[] events = blockchainService.getUserEvents(ledger, sampleAddress, sampleEvent, 0, 1);
        for (Event event : events) {
            BytesValue content = event.getContent();
            switch (content.getType()) {
                case TEXT:
                case XML:
                case JSON:
                    System.out.println(event.getName() + ":" + event.getSequence() + ":" + content.getBytes().toUTF8String());
                    break;
                case INT64:
                case TIMESTAMP:
                    System.out.println(event.getName() + ":" + event.getSequence() + ":" + BytesUtils.toLong(content.getBytes().toBytes()));
                    break;
                default: // byte[], Bytes
                    System.out.println(event.getName() + ":" + event.getSequence() + ":" + content.getBytes().toBase58());
                    break;
            }
        }
    }

    /**
     * 获取指定版本合约
     */
    @Test
    public void getContractByAddressAndVersion() {
        ContractInfo contract = blockchainService.getContract(ledger, sampleAddress, sampleVersion);
        if (null != contract) {
            System.out.println(contract.getAddress().toString());
            System.out.println(contract.getChainCodeVersion());
            System.out.println(contract.getChainCode());
        }
    }

    /**
     * 分页查询用户
     */
    @Test
    public void getUsers() {
        BlockchainIdentity[] ids = blockchainService.getUsers(ledger, 0, 1);
        Assert.assertEquals(1, ids.length);
    }

    /**
     * 分页查询数据账户
     */
    @Test
    public void getDataAccounts() {
        BlockchainIdentity[] ids = blockchainService.getDataAccounts(ledger, 0, 1);
        System.out.println(ids.length);
    }

    /**
     * 分页查询合约账户
     */
    @Test
    public void getContractAccounts() {
        BlockchainIdentity[] ids = blockchainService.getContractAccounts(ledger, 0, 1);
        System.out.println(ids.length);
    }

    /**
     * 查询指定角色权限信息
     */
    @Test
    public void getRolePrivileges() {
        PrivilegeSet privilegeSet = blockchainService.getRolePrivileges(ledger, sampleRoleName);
        if (null != privilegeSet) {
            for (LedgerPermission ledgerpermission : privilegeSet.getLedgerPrivilege().getPrivilege()) {
                System.out.println(ledgerpermission);
            }
            for (TransactionPermission transactionPermission : privilegeSet.getTransactionPrivilege().getPrivilege()) {
                System.out.println(transactionPermission);
            }
        }
    }

    /**
     * 查询指定用户权限信息
     */
    @Test
    public void getUserPrivileges() {
        UserPrivilegeSet userPrivileges = blockchainService.getUserPrivileges(ledger, sampleAddress);
        if (null != userPrivileges) {
            for (String role : userPrivileges.getUserRole()) {
                System.out.println(role);
            }
            for (LedgerPermission ledgerpermission : userPrivileges.getLedgerPrivilegesBitset().getPrivilege()) {
                System.out.println(ledgerpermission);
            }
            for (TransactionPermission transactionPermission : userPrivileges.getTransactionPrivilegesBitset().getPrivilege()) {
                System.out.println(transactionPermission);
            }
        }
    }
}
