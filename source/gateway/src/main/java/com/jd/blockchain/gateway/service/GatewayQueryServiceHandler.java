package com.jd.blockchain.gateway.service;

import com.jd.blockchain.consensus.ConsensusProvider;
import com.jd.blockchain.consensus.ConsensusProviders;
import com.jd.blockchain.consensus.ConsensusSettings;
import com.jd.blockchain.consensus.bftsmart.BftsmartConsensusProvider;
import com.jd.blockchain.consensus.mq.MsgQueueConsensusProvider;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.gateway.PeerService;
import com.jd.blockchain.gateway.decompiler.utils.DecompilerUtils;
import com.jd.blockchain.ledger.ContractInfo;
import com.jd.blockchain.ledger.LedgerMetadata;
import com.jd.blockchain.ledger.ParticipantNode;
import com.jd.blockchain.sdk.ContractSettings;
import com.jd.blockchain.sdk.LedgerInitSettings;
import com.jd.blockchain.utils.QueryUtil;
import com.jd.blockchain.utils.codec.HexUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Arrays;


/**
 * @Author zhaogw
 * @Date 2019/2/22 10:39
 */
@Component
public class GatewayQueryServiceHandler implements GatewayQueryService {

    @Autowired
    private PeerService peerService;

    @Override
    public HashDigest[] getLedgersHash(int fromIndex, int count) {
        HashDigest ledgersHash[] = peerService.getQueryService().getLedgerHashs();
        int indexAndCount[] = QueryUtil.calFromIndexAndCount(fromIndex,count,ledgersHash.length);
        HashDigest ledgersHashNew[] = Arrays.copyOfRange(ledgersHash,indexAndCount[0],indexAndCount[0]+indexAndCount[1]);
        return ledgersHashNew;
    }

    @Override
    public ParticipantNode[] getConsensusParticipants(HashDigest ledgerHash, int fromIndex, int count) {
        ParticipantNode participantNode[] = peerService.getQueryService().getConsensusParticipants(ledgerHash);
        int indexAndCount[] = QueryUtil.calFromIndexAndCount(fromIndex,count,participantNode.length);
        ParticipantNode participantNodesNew[] = Arrays.copyOfRange(participantNode,indexAndCount[0],indexAndCount[0]+indexAndCount[1]);
        return participantNodesNew;
    }

    @Override
    public LedgerInitSettings getLedgerInitSettings(HashDigest ledgerHash) {

        ParticipantNode[] participantNodes = peerService.getQueryService().getConsensusParticipants(ledgerHash);

        LedgerMetadata ledgerMetadata = peerService.getQueryService().getLedgerMetadata(ledgerHash);

        return initLedgerInitSettings(participantNodes, ledgerMetadata);
    }

    @Override
    public ContractSettings getContractSettings(HashDigest ledgerHash, String address) {
        ContractInfo contractInfo = peerService.getQueryService().getContract(ledgerHash, address);
        return contractSettings(contractInfo);
    }

    private ContractSettings contractSettings(ContractInfo contractInfo) {
        ContractSettings contractSettings = new ContractSettings(contractInfo.getAddress(), contractInfo.getPubKey(), contractInfo.getRootHash());
        byte[] chainCodeBytes = contractInfo.getChainCode();
        // 将反编译chainCode
        String mainClassJava = DecompilerUtils.decompileMainClassFromBytes(chainCodeBytes);
        contractSettings.setChainCode(mainClassJava);
        return contractSettings;
    }

    /**
     * 初始化账本配置
     *
     * @param participantNodes
     *     参与方列表
     * @param ledgerMetadata
     *     账本元数据
     * @return
     */
    private LedgerInitSettings initLedgerInitSettings(ParticipantNode[] participantNodes, LedgerMetadata ledgerMetadata) {
        LedgerInitSettings ledgerInitSettings = new LedgerInitSettings();

        // 设置参与方
        ledgerInitSettings.setParticipantNodes(participantNodes);

        // 设置共识设置
        ledgerInitSettings.setConsensusSettings(initConsensusSettings(ledgerMetadata));

        // 设置参与方根Hash
        ledgerInitSettings.setParticipantsHash(ledgerMetadata.getParticipantsHash());

        // 设置算法配置
        ledgerInitSettings.setCryptoSetting(ledgerMetadata.getSetting().getCryptoSetting());

        // 设置种子
        ledgerInitSettings.setSeed(initSeed(ledgerMetadata.getSeed()));

        // 设置共识协议
        ledgerInitSettings.setConsensusProtocol(ledgerMetadata.getSetting().getConsensusProvider());

        return ledgerInitSettings;
    }

    /**
     * 初始化账本种子信息
     *
     * @param seedBytes
     *     种子的字节数组显示
     * @return
     *     种子以十六进制方式显示，为方便阅读，每隔八个字符中间以"-"分割
     */
    private String initSeed(byte[] seedBytes) {
        String seedString = HexUtils.encode(seedBytes);
        // 每隔八个字符中加入一个一个横线
        StringBuffer seed = new StringBuffer();

        for( int i = 0; i < seedString.length(); i++) {
            char c = seedString.charAt(i);
            if (i != 0 && i % 8 == 0) {
                seed.append("-");
            }
            seed.append(c);
        }

        return seed.toString();
    }

    /**
     * 初始化共识配置
     *
     * @param ledgerMetadata
     *     账本元数据
     * @return
     */
    private ConsensusSettings initConsensusSettings(LedgerMetadata ledgerMetadata) {
        String consensusProvider = ledgerMetadata.getSetting().getConsensusProvider();
        ConsensusProvider provider = ConsensusProviders.getProvider(consensusProvider);
        byte[] consensusSettingsBytes = ledgerMetadata.getSetting().getConsensusSetting().toBytes();
        return provider.getSettingsFactory().getConsensusSettingsEncoder().decode(consensusSettingsBytes);
    }
}
