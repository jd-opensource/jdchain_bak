package com.jdchain.samples.sdk;

import com.jd.blockchain.contract.OnLineContractProcessor;
import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.BlockchainIdentity;
import com.jd.blockchain.ledger.BytesValue;
import com.jd.blockchain.ledger.ConsensusSettingsUpdateOperation;
import com.jd.blockchain.ledger.ContractCodeDeployOperation;
import com.jd.blockchain.ledger.ContractEventSendOperation;
import com.jd.blockchain.ledger.ContractInfo;
import com.jd.blockchain.ledger.DataAccountInfo;
import com.jd.blockchain.ledger.DataAccountKVSetOperation;
import com.jd.blockchain.ledger.DataAccountRegisterOperation;
import com.jd.blockchain.ledger.DigitalSignature;
import com.jd.blockchain.ledger.Event;
import com.jd.blockchain.ledger.EventAccountRegisterOperation;
import com.jd.blockchain.ledger.EventPublishOperation;
import com.jd.blockchain.ledger.KVDataVO;
import com.jd.blockchain.ledger.KVInfoVO;
import com.jd.blockchain.ledger.LedgerAdminInfo;
import com.jd.blockchain.ledger.LedgerBlock;
import com.jd.blockchain.ledger.LedgerInfo;
import com.jd.blockchain.ledger.LedgerInitOperation;
import com.jd.blockchain.ledger.LedgerMetadata;
import com.jd.blockchain.ledger.LedgerPermission;
import com.jd.blockchain.ledger.LedgerTransaction;
import com.jd.blockchain.ledger.Operation;
import com.jd.blockchain.ledger.ParticipantNode;
import com.jd.blockchain.ledger.ParticipantRegisterOperation;
import com.jd.blockchain.ledger.PrivilegeSet;
import com.jd.blockchain.ledger.RolesConfigureOperation;
import com.jd.blockchain.ledger.TransactionContent;
import com.jd.blockchain.ledger.TransactionPermission;
import com.jd.blockchain.ledger.TransactionRequest;
import com.jd.blockchain.ledger.TransactionResult;
import com.jd.blockchain.ledger.TransactionState;
import com.jd.blockchain.ledger.TypedKVEntry;
import com.jd.blockchain.ledger.UserAuthorizeOperation;
import com.jd.blockchain.ledger.UserInfo;
import com.jd.blockchain.ledger.UserPrivilegeSet;
import com.jd.blockchain.ledger.UserRegisterOperation;
import org.junit.Assert;
import org.junit.Test;
import utils.Property;
import utils.codec.Base58Utils;
import utils.io.BytesUtils;

import java.util.Arrays;

/**
 * 查询样例
 */
public class QuerySample extends SampleBase {

    HashDigest sampleHash = Crypto.resolveAsHashDigest(Base58Utils.decode("j5sTuEAWmLWKFwXgpdUCxbQN1XmZfkQdC94UT2AqQEt7hp"));
    String sampleUserAddress = "LdeNr7H1CUbqe3kWjwPwiqHcmd86zEQz2VRye";
    String sampleDataAccountAddress = "LdeNr7H1CUbqe3kWjwPwiqHcmd86zEQz2VRye";
    String sampleContractAddress = "LdeNr7H1CUbqe3kWjwPwiqHcmd86zEQz2VRye";
    String sampleEventAddress = "LdeNr7H1CUbqe3kWjwPwiqHcmd86zEQz2VRye";
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
            System.out.println(digest);
        }
    }

    /**
     * 查询账本信息，区块hash，区块高度
     */
    @Test
    public void getLedger() {
        LedgerInfo ledgerInfo = blockchainService.getLedger(ledger);
        // 账本哈希
        System.out.println(ledgerInfo.getHash());
        // 最新区块哈希
        System.out.println(ledgerInfo.getLatestBlockHash());
        // 最新区块高度
        System.out.println(ledgerInfo.getLatestBlockHeight());
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
        // 账本哈希
        System.out.println(block1.getLedgerHash());
        // 区块高度
        System.out.println(block1.getHeight());
        // 区块时间
        System.out.println(block1.getTimestamp());
        // 区块哈希
        System.out.println(block1.getHash());
        // 上一区块哈希
        System.out.println(block1.getPreviousHash());
        // 交易数据集根哈希
        System.out.println(block1.getTransactionSetHash());
        // 用户角色权限数据集根哈希
        System.out.println(block1.getAdminAccountHash());
        // 合约数据集根哈希
        System.out.println(block1.getContractAccountSetHash());
        // 数据账户集根哈希
        System.out.println(block1.getDataAccountSetHash());
        // 系统时间集根哈希
        System.out.println(block1.getSystemEventSetHash());
        // 用户账户集根哈希
        System.out.println(block1.getUserAccountSetHash());
        // 用户事件账户根哈希
        System.out.println(block1.getUserEventSetHash());

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
        LedgerTransaction[] txs = blockchainService.getAdditionalTransactions(ledger, 0, 0, 1);
        Assert.assertEquals(1, txs.length);
        for (LedgerTransaction tx : txs) {
            /**
             * 交易执行结果
             */
            TransactionResult result = tx.getResult();
            // 交易最终状态
            System.out.println(result.getExecutionState());
            // 交易所在区块高度
            System.out.println(result.getBlockHeight());
            /**
             * 交易请求解析
             */
            TransactionRequest request = tx.getRequest();
            // 交易哈希
            System.out.println(request.getTransactionHash());
            // 终端用户签名信息
            DigitalSignature[] endpointSignatures = request.getEndpointSignatures();
            for (DigitalSignature signature : endpointSignatures) {
                // 签名
                System.out.println(signature.getDigest());
                // 公钥
                System.out.println(signature.getPubKey());
            }
            // 网关签名信息
            DigitalSignature[] nodeSignatures = request.getNodeSignatures();
            for (DigitalSignature signature : nodeSignatures) {
                // 签名
                System.out.println(signature.getDigest());
                // 公钥
                System.out.println(signature.getPubKey());
            }
            // 请求内容
            TransactionContent transactionContent = request.getTransactionContent();
            transactionContent.getTimestamp(); // 请求时间（客户端提交上来的时间）
            Operation[] operations = transactionContent.getOperations(); // 操作列表
            for (Operation operation : operations) {
                if (operation instanceof UserRegisterOperation) { // 注册用户
                    UserRegisterOperation userRegisterOperation = (UserRegisterOperation) operation;
                    // 地址
                    System.out.println(userRegisterOperation.getUserID().getAddress());
                    //公钥
                    System.out.println(userRegisterOperation.getUserID().getPubKey());
                } else if (operation instanceof DataAccountRegisterOperation) { // 注册数据账户
                    DataAccountRegisterOperation dataAccountRegisterOperation = (DataAccountRegisterOperation) operation;
                    // 地址
                    System.out.println(dataAccountRegisterOperation.getAccountID().getAddress());
                    // 公钥
                    System.out.println(dataAccountRegisterOperation.getAccountID().getPubKey());
                } else if (operation instanceof ContractCodeDeployOperation) { // 部署合约
                    ContractCodeDeployOperation contractCodeDeployOperation = (ContractCodeDeployOperation) operation;
                    // 地址
                    System.out.println(contractCodeDeployOperation.getContractID().getAddress());
                    // 公钥
                    System.out.println(contractCodeDeployOperation.getContractID().getPubKey());
                    // 合约代码
                    System.out.println(OnLineContractProcessor.getInstance().decompileEntranceClass(contractCodeDeployOperation.getChainCode()));
                    // 合约版本
                    System.out.println(contractCodeDeployOperation.getChainCodeVersion());
                } else if (operation instanceof EventAccountRegisterOperation) { // 注册事件账户
                    EventAccountRegisterOperation eventAccountRegisterOperation = (EventAccountRegisterOperation) operation;
                    // 地址
                    System.out.println(eventAccountRegisterOperation.getEventAccountID().getAddress());
                    // 公钥
                    System.out.println(eventAccountRegisterOperation.getEventAccountID().getPubKey());
                } else if (operation instanceof DataAccountKVSetOperation) { // 写入kv
                    DataAccountKVSetOperation kvSetOperation = (DataAccountKVSetOperation) operation;
                    // 数据账户地址
                    System.out.println(kvSetOperation.getAccountAddress());
                    // 写入kv数据
                    DataAccountKVSetOperation.KVWriteEntry[] kvs = kvSetOperation.getWriteSet();
                    for (DataAccountKVSetOperation.KVWriteEntry kv : kvs) {
                        // key
                        System.out.println(kv.getKey());
                        // 预期的上一个数据版本
                        System.out.println(kv.getExpectedVersion());
                        // value
                        BytesValue value = kv.getValue();
                        switch (value.getType()) {
                            case TEXT:
                            case XML:
                            case JSON:
                                System.out.println(value.getBytes().toString());
                                break;
                            case INT64:
                            case TIMESTAMP:
                                System.out.println(BytesUtils.toLong(value.getBytes().toBytes()));
                                break;
                            default: // byte[], Bytes, IMG
                                System.out.println(value.getBytes());
                                break;
                        }
                    }
                } else if (operation instanceof ContractEventSendOperation) { // 调用合约
                    ContractEventSendOperation contractEventSendOperation = (ContractEventSendOperation) operation;
                    // 合约地址
                    System.out.println(contractEventSendOperation.getContractAddress());
                    // 合约方法
                    System.out.println(contractEventSendOperation.getEvent());
                    // 合约参数
                    for (BytesValue arg : contractEventSendOperation.getArgs().getValues()) {
                        switch (arg.getType()) {
                            case TEXT:
                                System.out.println(BytesUtils.toString(arg.getBytes().toBytes()));
                                break;
                            case INT64:
                                System.out.println(BytesUtils.toLong(arg.getBytes().toBytes()));
                                break;
                            case BOOLEAN:
                                System.out.println(BytesUtils.toBoolean(arg.getBytes().toBytes()[0]));
                                break;
                            case BYTES:
                                System.out.println(arg.getBytes().toBytes());
                            default:
                                break;
                        }
                    }
                } else if (operation instanceof EventPublishOperation) { // 发布事件
                    EventPublishOperation eventPublishOperation = (EventPublishOperation) operation;
                    // 事件账户地址
                    System.out.println(eventPublishOperation.getEventAddress());
                    // 数据
                    EventPublishOperation.EventEntry[] events = eventPublishOperation.getEvents();
                    for (EventPublishOperation.EventEntry event : events) {
                        // topic
                        System.out.println(event.getName());
                        // 预期的上一个数据序列
                        System.out.println(event.getSequence());
                        // 内容
                        BytesValue value = event.getContent();
                        switch (value.getType()) {
                            case TEXT:
                            case XML:
                            case JSON:
                                System.out.println(value.getBytes().toString());
                                break;
                            case INT64:
                            case TIMESTAMP:
                                System.out.println(BytesUtils.toLong(value.getBytes().toBytes()));
                                break;
                            default: // byte[], Bytes, IMG
                                System.out.println(value.getBytes());
                                break;
                        }
                    }
                } else if (operation instanceof ConsensusSettingsUpdateOperation) { // 更新共识信息
                    ConsensusSettingsUpdateOperation consensusSettingsUpdateOperation = (ConsensusSettingsUpdateOperation) operation;
                    Property[] properties = consensusSettingsUpdateOperation.getProperties();
                    for (Property property : properties) {
                        System.out.println(property.getName());
                        System.out.println(property.getValue());
                    }
                } else if (operation instanceof LedgerInitOperation) { // 账本初始化
                    LedgerInitOperation ledgerInitOperation = (LedgerInitOperation) operation;
                    // 共识参与方的列表
                    ledgerInitOperation.getInitSetting().getConsensusParticipants();
                    // 密码算法配置
                    ledgerInitOperation.getInitSetting().getCryptoSetting();
                    // 账本的种子
                    ledgerInitOperation.getInitSetting().getLedgerSeed();
                    // ...
                } else if (operation instanceof ParticipantRegisterOperation) { // 注册参与方
                    ParticipantRegisterOperation participantRegisterOperation = (ParticipantRegisterOperation) operation;
                    // 参与方地址
                    System.out.println(participantRegisterOperation.getParticipantID().getAddress());
                    // 参与方公钥
                    System.out.println(participantRegisterOperation.getParticipantID().getPubKey());
                    // 参与方名称
                    System.out.println(participantRegisterOperation.getParticipantName());
                } else if (operation instanceof RolesConfigureOperation) { // 角色配置
                    RolesConfigureOperation rolesConfigureOperation = (RolesConfigureOperation) operation;
                    // 角色列表
                    RolesConfigureOperation.RolePrivilegeEntry[] roles = rolesConfigureOperation.getRoles();
                    for (RolesConfigureOperation.RolePrivilegeEntry role : roles) {
                        // 角色名称
                        System.out.println(role.getRoleName());
                        // 拥有的账本权限
                        System.out.println(Arrays.toString(role.getEnableLedgerPermissions()));
                        // 禁止的账本权限
                        System.out.println(Arrays.toString(role.getDisableLedgerPermissions()));
                        // 拥有的交易权限
                        System.out.println(Arrays.toString(role.getEnableTransactionPermissions()));
                        // 禁止的交易权限
                        System.out.println(Arrays.toString(role.getDisableTransactionPermissions()));
                    }
                } else if (operation instanceof UserAuthorizeOperation) { // 权限配置
                    UserAuthorizeOperation userAuthorizeOperation = (UserAuthorizeOperation) operation;
                    // 用户角色
                    UserAuthorizeOperation.UserRolesEntry[] userRoles = userAuthorizeOperation.getUserRolesAuthorizations();
                    for (UserAuthorizeOperation.UserRolesEntry userRole : userRoles) {
                        // 用户地址
                        System.out.println(Arrays.toString(userRole.getUserAddresses()));
                        // 多角色权限策略
                        System.out.println(userRole.getPolicy());
                        // 授权的角色清单
                        System.out.println(Arrays.toString(userRole.getAuthorizedRoles()));
                        // 取消授权的角色清单
                        System.out.println(Arrays.toString(userRole.getUnauthorizedRoles()));
                    }
                } else {
                    System.out.println("todo");
                }
            }
        }
    }

    /**
     * 分页查询某一区块中的交易
     */
    @Test
    public void getAdditionalTransactionsByHash() {
        LedgerTransaction[] txs = blockchainService.getAdditionalTransactions(ledger, sampleHash, 0, 1);
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
        UserInfo user = blockchainService.getUser(ledger, sampleUserAddress);
        if (null != user) {
            System.out.println(user.getAddress().toString());
        }
    }

    /**
     * 根据地址查询数据账户
     */
    @Test
    public void getDataAccount() {
        DataAccountInfo dataAccount = blockchainService.getDataAccount(ledger, sampleDataAccountAddress);
        if (null != dataAccount) {
            System.out.println(dataAccount.getAddress().toString());
        }
    }

    /**
     * 根据地址和键查询KV信息（只包含最高数据版本）
     */
    @Test
    public void getDataEntriesByKey() {
        TypedKVEntry[] kvs = blockchainService.getDataEntries(ledger, sampleDataAccountAddress, sampleKey);
        for (TypedKVEntry kv : kvs) {
            System.out.println(kv.getKey() + ":" + kv.getVersion() + ":" + kv.getValue());
        }
    }

    /**
     * 根据地址和指定键及数据版本查询KV信息
     */
    @Test
    public void getDataEntriesWithKeyAndVersion() {
        TypedKVEntry[] kvs = blockchainService.getDataEntries(ledger, sampleDataAccountAddress, new KVInfoVO(new KVDataVO[]{new KVDataVO(sampleKey, new long[]{0})}));
        for (TypedKVEntry kv : kvs) {
            System.out.println(kv.getKey() + ":" + kv.getVersion() + ":" + kv.getValue());
        }
    }

    /**
     * 查询数据账户KV总数
     */
    @Test
    public void getDataEntriesTotalCount() {
        long count = blockchainService.getDataEntriesTotalCount(ledger, sampleDataAccountAddress);
        System.out.println(count);
    }

    /**
     * 分页查询指定数据账户KV数据
     */
    @Test
    public void getDataEntries() {
        TypedKVEntry[] kvs = blockchainService.getDataEntries(ledger, sampleDataAccountAddress, 0, 1);
        for (TypedKVEntry kv : kvs) {
            System.out.println(kv.getKey() + ":" + kv.getVersion() + ":" + kv.getValue());
        }
    }

    /**
     * 查询合约信息
     */
    @Test
    public void getContract() {
        ContractInfo contract = blockchainService.getContract(ledger, sampleContractAddress);
        if (null != contract) {
            // 合约地址
            System.out.println(contract.getAddress());
            // 合约代码
            System.out.println(BytesUtils.toString(contract.getChainCode()));
            System.out.println(OnLineContractProcessor.getInstance().decompileEntranceClass(contract.getChainCode()));
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
        Event event = blockchainService.getLatestSystemEvent(ledger, sampleEvent);
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
        BlockchainIdentity id = blockchainService.getUserEventAccount(ledger, sampleEventAddress);
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
        long count = blockchainService.getUserEventNameTotalCount(ledger, sampleEventAddress);
        System.out.println(count);
    }

    /**
     * 分页查询指定用户事件账户下事件名
     */
    @Test
    public void getUserEventNames() {
        String[] names = blockchainService.getUserEventNames(ledger, sampleEventAddress, 0, 1);
        for (String name : names) {
            System.out.println(name);
        }
    }

    /**
     * 获取指定用户事件账户指定事件名下最新事件
     */
    @Test
    public void getLatestUserEvent() {
        Event event = blockchainService.getLatestUserEvent(ledger, sampleEventAddress, sampleEvent);
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
        long count = blockchainService.getUserEventsTotalCount(ledger, sampleEventAddress, sampleEvent);
        System.out.println(count);
    }

    /**
     * 分页查询指定用户事件账户指定事件名下事件
     */
    @Test
    public void getUserEvents() {
        Event[] events = blockchainService.getUserEvents(ledger, sampleEventAddress, sampleEvent, 0, 1);
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
        ContractInfo contract = blockchainService.getContract(ledger, sampleContractAddress, sampleVersion);
        if (null != contract) {
            System.out.println(contract.getAddress().toString());
            System.out.println(contract.getChainCodeVersion());
            System.out.println(OnLineContractProcessor.getInstance().decompileEntranceClass(contract.getChainCode()));
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
        UserPrivilegeSet userPrivileges = blockchainService.getUserPrivileges(ledger, sampleUserAddress);
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
