/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: test.com.jd.blockchain.sdk.test.SDK_GateWay_InsertData_Test
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/9/4 上午11:06
 * Description: 插入数据测试
 */
package test.com.jd.blockchain.sdk.test;

import com.jd.blockchain.ledger.*;
import com.jd.blockchain.sdk.client.ClientOperationUtil;
import org.apache.commons.codec.binary.Hex;
import org.junit.Before;
import org.junit.Test;

import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.asymmetric.AsymmetricCryptography;
import com.jd.blockchain.crypto.hash.HashDigest;
import com.jd.blockchain.crypto.impl.AsymmtricCryptographyImpl;
import com.jd.blockchain.sdk.BlockchainService;
import com.jd.blockchain.sdk.client.GatewayServiceFactory;


/**
 * 插入数据测试
 * @author shaozhuguang
 * @create 2018/9/4
 * @since 1.0.0
 */

public class SDK_GateWay_Query_Test_ {

    private BlockchainKeyPair CLIENT_CERT = null;

    private String GATEWAY_IPADDR = null;

    private int GATEWAY_PORT;

    private boolean SECURE;

    private BlockchainService service;

    @Before
    public void init() {
        CLIENT_CERT = BlockchainKeyGenerator.getInstance().generate(CryptoAlgorithm.ED25519);
        GATEWAY_IPADDR = "127.0.0.1";
        GATEWAY_PORT = 8081;
        SECURE = false;
        GatewayServiceFactory serviceFactory = GatewayServiceFactory.connect(GATEWAY_IPADDR, GATEWAY_PORT, SECURE,
                CLIENT_CERT);
        service = serviceFactory.getBlockchainService();
    }

    @Test
    public void query_Test() {

        // Get First Ledger
        HashDigest ledgerHash = service.getLedgerHashs()[0];
        System.out.println("ledgerHash=" + ledgerHash.toBase58());

        // Show Ledger Info
        LedgerInfo ledgerInfo = service.getLedger(ledgerHash);

        // Get highest block height
        long latestBlockHeight = ledgerInfo.getLatestBlockHeight();

        // Get highest block hash
        HashDigest latestBlockHash = ledgerInfo.getLatestBlockHash();

        System.out.println("latestBlockHeight=" + latestBlockHeight);

        System.out.println("latestBlockHash=" + latestBlockHash.toBase58());

        System.out.println("LedgerHash=" + ledgerInfo.getHash().toBase58());

        // Get newest block
        LedgerBlock latestBlock = service.getBlock(ledgerHash, latestBlockHeight);

        System.out.println("latestBlock.Hash=" + latestBlock.getHash());

        // Get total contract size
        long count = service.getContractCount(ledgerHash, latestBlockHeight);

        System.out.println("contractCount=" + count);

        count = service.getContractCount(ledgerHash, latestBlockHash);

        System.out.println("contractCount=" + count);

        if (count != 0) {
            AccountHeader[] accountHeaders = service.getContractAccounts(ledgerHash, 0, (int)count);
            for (AccountHeader accountHeader : accountHeaders) {
                String contractAddress = accountHeader.getAddress().toBase58();
                System.out.println("Contract address = " + contractAddress);
                // Get one contract by contract address
                AccountHeader contract = service.getContract(ledgerHash, contractAddress);
            }
        }

        // Get other block info
        LedgerBlock block = service.getBlock(ledgerHash, latestBlockHeight - 1);
        System.out.println("block.Hash=" + block.getHash());

        // Get Total DataAccount Size
        count = service.getDataAccountCount(ledgerHash, latestBlockHeight);

        System.out.println("dataAccountCount=" + count);

        count = service.getDataAccountCount(ledgerHash, latestBlockHash);

        System.out.println("dataAccountCount=" + count);

        String queryDataAccountAddress = null;

        if (count > 0) {
            AccountHeader[] accountHeaders = service.getDataAccounts(ledgerHash, 0, (int)count);
            for (AccountHeader accountHeader : accountHeaders) {
                String dataAccountAddress = accountHeader.getAddress().toBase58();
                System.out.println("DataAccount address = " + dataAccountAddress);
                // Get one Data Account by address
                AccountHeader dataAccount = service.getDataAccount(ledgerHash, dataAccountAddress);
                queryDataAccountAddress = dataAccountAddress;
            }
        }

        // Get total transaction size
        count = service.getTransactionCount(ledgerHash, latestBlockHash);
        System.out.println("transactionCount=" + count);

        count = service.getTransactionCount(ledgerHash, latestBlockHeight);
        System.out.println("transactionCount=" + count);

        // Get transaction list
        LedgerTransaction[] txList = service.getTransactions(ledgerHash, 0, 0, 100);
        for (LedgerTransaction ledgerTransaction : txList) {
            System.out.println("transaction.executionState=" + ledgerTransaction.getExecutionState());
//            System.out.println("transaction.hash=" + ledgerTransaction.getHash().toBase58());
            TransactionContent txContent = ledgerTransaction.getTransactionContent();
            System.out.println("transactionContent.hash=" + txContent.getHash().toBase58());
            Operation[] operations = txContent.getOperations();
            if (operations != null && operations.length > 0) {
                for (Operation operation : operations) {
                    operation = ClientOperationUtil.read(operation);
                    if (operation instanceof  DataAccountRegisterOperation) {
                        DataAccountRegisterOperation daro = (DataAccountRegisterOperation) operation;
                        BlockchainIdentity blockchainIdentity = daro.getAccountID();
                        System.out.println("register account = " + blockchainIdentity.getAddress().toBase58());
                    } else if (operation instanceof UserRegisterOperation) {
                        UserRegisterOperation uro = (UserRegisterOperation) operation;
                        BlockchainIdentity blockchainIdentity = uro.getUserID();
                        System.out.println("register user = " + blockchainIdentity.getAddress().toBase58());
                    } else if (operation instanceof LedgerInitOperation) {

                        LedgerInitOperation ledgerInitOperation = (LedgerInitOperation)operation;
                        LedgerInitSetting ledgerInitSetting = ledgerInitOperation.getInitSetting();

                        System.out.println(Hex.encodeHexString(ledgerInitSetting.getLedgerSeed()));
                        System.out.println(ledgerInitSetting.getConsensusProvider());
                        System.out.println(ledgerInitSetting.getConsensusSettings().toBase58());

                        ParticipantNode[] participantNodes = ledgerInitSetting.getConsensusParticipants();
                        if (participantNodes != null && participantNodes.length > 0) {
                            for (ParticipantNode participantNode : participantNodes) {
                                System.out.println("participantNode.id=" + participantNode.getId());
                                System.out.println("participantNode.name=" + participantNode.getName());
                                System.out.println("participantNode.address=" + participantNode.getAddress());
                                System.out.println("participantNode.pubKey=" + participantNode.getPubKey().toBase58());
                            }
                        }

                    } else if (operation instanceof ContractCodeDeployOperation) {
                        ContractCodeDeployOperation ccdo = (ContractCodeDeployOperation) operation;
                        BlockchainIdentity blockchainIdentity = ccdo.getContractID();
                        System.out.println("deploy contract = " + blockchainIdentity.getAddress());
                    } else if (operation instanceof ContractEventSendOperation) {
                        ContractEventSendOperation ceso = (ContractEventSendOperation) operation;
                        System.out.println("event = " + ceso.getEvent());
                        System.out.println("execute contract address = " + ceso.getContractAddress().toBase58());
                    } else if (operation instanceof DataAccountKVSetOperation) {
                        DataAccountKVSetOperation.KVWriteEntry[] kvWriteEntries =
                                ((DataAccountKVSetOperation) operation).getWriteSet();
                        if (kvWriteEntries != null && kvWriteEntries.length > 0) {
                            for (DataAccountKVSetOperation.KVWriteEntry kvWriteEntry : kvWriteEntries) {
                                System.out.println("writeSet.key=" + kvWriteEntry.getKey());
                                BytesValue bytesValue = kvWriteEntry.getValue();
                                DataType dataType = bytesValue.getType();
                                Object showVal = ClientOperationUtil.readValueByBytesValue(bytesValue);
                                System.out.println("writeSet.value=" + showVal);
                                System.out.println("writeSet.type=" + dataType);
                                System.out.println("writeSet.version=" + kvWriteEntry.getExpectedVersion());
                            }
                        }
                    }
                }
            }
        }

        // Get txs by block height
        txList = service.getTransactions(ledgerHash, latestBlockHash, 0, 100);
        for (LedgerTransaction ledgerTransaction : txList) {
            System.out.println("ledgerTransaction.Hash=" + ledgerTransaction.getHash());
        }


        // Get total ParticipantNode array
        ParticipantNode[] participants = service.getConsensusParticipants(ledgerHash);
        for (ParticipantNode participant : participants) {
            System.out.println("participant.name=" + participant.getName());
//            System.out.println(participant.getConsensusAddress());
//            System.out.println("participant.host=" + participant.getConsensusAddress().getHost());
            System.out.println("participant.getPubKey=" + participant.getPubKey());
            System.out.println("participant.getKeyType=" + participant.getPubKey().getKeyType());
            System.out.println("participant.getRawKeyBytes=" + participant.getPubKey().getRawKeyBytes());
            System.out.println("participant.algorithm=" + participant.getPubKey().getAlgorithm());
        }

        // Get total kvs
        KVDataEntry[] kvData = service.getDataEntries(ledgerHash, queryDataAccountAddress, 0, 100);
        if (kvData != null && kvData.length > 0) {
            for (KVDataEntry kvDatum : kvData) {
                System.out.println("kvData.key=" + kvDatum.getKey());
                System.out.println("kvData.version=" + kvDatum.getVersion());
                System.out.println("kvData.type=" + kvDatum.getType());
                System.out.println("kvData.value=" + kvDatum.getValue());

                // Get one kvData by key
                KVDataEntry[] kvDataEntries = service.getDataEntries(ledgerHash,
                        queryDataAccountAddress, kvDatum.getKey());

                for (KVDataEntry kv : kvDataEntries) {
                    System.out.println("kv.key=" + kv.getKey());
                    System.out.println("kv.version=" + kv.getVersion());
                    System.out.println("kv.type=" + kv.getType());
                    System.out.println("kv.value=" + kv.getValue());
                }
            }
        }
    }
}