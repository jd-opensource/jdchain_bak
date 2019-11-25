package test.com.jd.blockchain.sdk.test;

import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.crypto.*;
import com.jd.blockchain.ledger.*;
import com.jd.blockchain.sdk.BlockchainService;
import com.jd.blockchain.sdk.client.GatewayServiceFactory;
import com.jd.blockchain.transaction.TxResponseMessage;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

//Transaction rollback test example
public class SDK_GateWay_Tx_RollBack_Test_ {

    private PrivKey privKey;
    private PubKey pubKey;

    private BlockchainKeypair CLIENT_CERT = null;

    private String GATEWAY_IPADDR = null;

    private int GATEWAY_PORT;

    private boolean SECURE;

    private BlockchainService service;

    private BlockchainKeypair user;

    private BlockchainKeypair dataAccount;

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

        user = BlockchainKeyGenerator.getInstance().generate();

        dataAccount = BlockchainKeyGenerator.getInstance().generate();

    }

    @Test
    public void failedTxRollback_Test() {

        HashDigest[] ledgerHashs = service.getLedgerHashs();

        //Construct the first transaction
        TransactionTemplate txTemp = service.newTransaction(ledgerHashs[0]);

        AsymmetricKeypair keyPair = new BlockchainKeypair(pubKey, privKey);

        //Register user account
        txTemp.users().register(user.getIdentity());

        //Register data account
        txTemp.dataAccounts().register(dataAccount.getIdentity());

        String dataKey = "jd_code";
        String dataVal = "www.jd.com";

        // Construct error kv version
        txTemp.dataAccount(dataAccount.getAddress()).setText(dataKey, dataVal, 1);

        PreparedTransaction prepTx = txTemp.prepare();

        prepTx.sign(keyPair);

        //Commit transaction
        TransactionResponse transactionResponse = prepTx.commit();

        //The first transaction will rollback, due to version error
        assertEquals(transactionResponse.getExecutionState().CODE, TransactionState.DATA_VERSION_CONFLICT.CODE);

        //Construct the second transaction
        TransactionTemplate txTemp1 = service.newTransaction(ledgerHashs[0]);

        txTemp1.users().register(user.getIdentity());

        txTemp1.dataAccounts().register(dataAccount.getIdentity());

        txTemp1.dataAccount(dataAccount.getAddress()).setText(dataKey, dataVal, -1);

        PreparedTransaction prepTx1 = txTemp1.prepare();

        prepTx1.sign(keyPair);

        TransactionResponse transactionResponse1 = prepTx1.commit();

        //The second transaction success
        assertTrue(transactionResponse1.isSuccess());

    }

}
