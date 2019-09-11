package test.com.jd.blockchain.sdk.test;

import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.crypto.*;
import com.jd.blockchain.ledger.*;
import com.jd.blockchain.sdk.BlockchainService;
import com.jd.blockchain.sdk.client.GatewayServiceFactory;
import com.jd.blockchain.sdk.samples.SDKDemo_Constant;
import com.jd.blockchain.tools.keygen.KeyGenCommand;
import com.jd.blockchain.utils.net.NetworkAddress;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * 参与方状态更新测试
 * @author zhangshuang
 * @create 2019/7/18
 * @since 1.0.0
 */
public class SDK_GateWay_Participant_State_Update_Test_ {
    private PrivKey privKey;
    private PubKey pubKey;

    private BlockchainKeypair CLIENT_CERT = null;

    private String GATEWAY_IPADDR = null;

    private int GATEWAY_PORT;

    private boolean SECURE;

    private BlockchainService service;

    //根据密码工具产生的公私钥
    static String PUB = "3snPdw7i7PkdgqiGX7GbZuFSi1cwZn7vtjw4vifb1YoXgr9k6Kfmis";
    String PRIV = "177gjtZu8w1phqHFVNiFhA35cfimXmP6VuqrBFhfbXBWK8s4TRwro2tnpffwP1Emwr6SMN6";

    @Before
    public void init() {

        privKey = SDK_GateWay_KeyPair_Para.privkey1;
        pubKey = SDK_GateWay_KeyPair_Para.pubKey1;

        CLIENT_CERT = new BlockchainKeypair(SDK_GateWay_KeyPair_Para.pubKey0, SDK_GateWay_KeyPair_Para.privkey0);
        GATEWAY_IPADDR = "127.0.0.1";
        GATEWAY_PORT = 11000;
        SECURE = false;
        GatewayServiceFactory serviceFactory = GatewayServiceFactory.connect(GATEWAY_IPADDR, GATEWAY_PORT, SECURE,
                CLIENT_CERT);
        service = serviceFactory.getBlockchainService();

        DataContractRegistry.register(TransactionContent.class);
        DataContractRegistry.register(TransactionContentBody.class);
        DataContractRegistry.register(TransactionRequest.class);
        DataContractRegistry.register(NodeRequest.class);
        DataContractRegistry.register(EndpointRequest.class);
        DataContractRegistry.register(TransactionResponse.class);
        DataContractRegistry.register(ParticipantRegisterOperation.class);
        DataContractRegistry.register(ParticipantStateUpdateOperation.class);
        DataContractRegistry.register(ParticipantStateUpdateOperation.class);
    }

    @Test
    public void updateParticipantState_Test() {
        HashDigest[] ledgerHashs = service.getLedgerHashs();
        // 在本地定义注册账号的 TX；
        TransactionTemplate txTemp = service.newTransaction(ledgerHashs[0]);

        //existed signer
        AsymmetricKeypair keyPair = new BlockchainKeypair(pubKey, privKey);

        PrivKey privKey = KeyGenUtils.decodePrivKeyWithRawPassword(PRIV, SDKDemo_Constant.PASSWORD);

        PubKey pubKey = KeyGenUtils.decodePubKey(PUB);

        System.out.println("Address = "+AddressEncoding.generateAddress(pubKey));

        BlockchainKeypair user = new BlockchainKeypair(pubKey, privKey);

        NetworkAddress networkAddress = new NetworkAddress("127.0.0.1", 20000);

        txTemp.states().update(user.getIdentity(),networkAddress, ParticipantNodeState.CONSENSUSED);

        // TX 准备就绪；
        PreparedTransaction prepTx = txTemp.prepare();

        // 使用私钥进行签名；
        prepTx.sign(keyPair);

        // 提交交易；
        TransactionResponse transactionResponse = prepTx.commit();
        assertTrue(transactionResponse.isSuccess());

    }
}
